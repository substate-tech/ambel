// Copyright 2022 Richard James Richmond
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package ambel

import java.io._
import scala.io.Source
import scala.collection.mutable.ListBuffer
import chisel3._
import chisel3.util._

/** =GenCSTrgt=
  *
  * Generic (bus protocol agnostic) Control/Status register target generator
  *
  * @param ADDR_W the width of the address bus in bits
  * @param DATA_W the width of the data bus in bits
  * @param REG_DESC_JSON the path to the register description JSON file
  * @param GEN_MODULE enables generation of a wrapper Module which uses generated
  *        Bundles suitable for connection to the generated MixedVec IOs. The signal
  *        names used in the Bundles match their corresponding register and bit field
  *        names, as specified in the JSON. They are declared in the same order as
  *        the entries of the corresponding MixedVec and connected in order
  * @param VERBOSE enables verbose output during generation
  */
class GenCSTrgt(
  ADDR_W: Int,
  DATA_W: Int,
  REG_DESC_JSON: String = "",
  GEN_MODULE: Boolean = false,
  VERBOSE: Boolean = false) extends Module {
  assert(DATA_W <= 32)
  assert(DATA_W % 8 == 0)

  // Parse register description JSON
  val RegDescDecoder = new RegisterDescDecoder
  val jsonString = Source.fromFile(REG_DESC_JSON).getLines.mkString.stripMargin
  val regDesc: Option[RegisterDesc] = RegDescDecoder.getReg(Array(jsonString))

  regDesc match {
    case Some(regs) => {
      RegDescDecoder.prettyPrint(regs)
    }
    case None =>
  }

  val regBits = new RegisterBitLists(regDesc, DATA_W)
  val regElements = new RegisterElements(regDesc, DATA_W)

  val NUM_BYTE = regElements.numByte
  val NUM_BITS_SHIFT = regElements.numBitsShift
  val NUM_REGS = regElements.numRegs
  val REQD_W = regElements.requiredWidth
  val INDEX_W = log2Ceil(regElements.maxRegs)

  val io = IO(new Bundle {
    val addr      = Input(UInt((ADDR_W - NUM_BITS_SHIFT).W))
    val write     = Input(Bool())
    val writeStrb = Input(UInt(NUM_BYTE.W))
    val writeData = Input(UInt(DATA_W.W))
    val read      = Input(Bool())
    val readData  = Output(UInt(DATA_W.W))
    val error     = Output(Bool())
    // NOTE suggestName doesn't actually work in IO Bundles. unclear whether it ever will, but if
    // it was possible we'd use it here, negating the requirement for the GEN_MODULE functionality
    val rwVec     = Output(MixedVec((regBits.rwRegBits map { case (k, v) => UInt(v.W).suggestName(k) }).toList))
    val roVec     = Input(MixedVec((regBits.roRegBits map { case (k, v) => UInt(v.W).suggestName(k) }).toList))
    val woVec     = Output(MixedVec((regBits.woRegBits map { case (k, v) => UInt(v.W).suggestName(k) }).toList))
    val wcVec     = Input(MixedVec((regBits.wcRegBits map { case (k, v) => UInt(v.W).suggestName(k) }).toList))
  })

  // Connect different register bit field categories to MixedVec IOs
  // NOTE the Vecs are declared in same order as registers are declared
  // therefore we can iterate over names and index regArray accordingly
  val rwIt = io.rwVec.iterator
  val roIt = io.roVec.iterator
  val woIt = io.woVec.iterator
  val wcIt = io.wcVec.iterator

  for (r <- regElements.names) {
    for (f <- regElements.regArray(regElements.regMap(r).offset >> NUM_BITS_SHIFT).iterator) {
      if (f.mode == "RW") {
        rwIt.next := f.reg
      }
      if (f.mode == "RO") {
        f.reg := roIt.next
      }
      if (f.mode == "WO") {
        woIt.next := f.reg
      }
    }
  }

  // Generate Chisel Bundles with signal names suitable for connection to the MixedVec IOs.
  // These are written to file if (GEN_MODULE)
  val rwBundleBuffer = new ListBuffer[String]()
  val roBundleBuffer = new ListBuffer[String]()
  val woBundleBuffer = new ListBuffer[String]()
  val wcBundleBuffer = new ListBuffer[String]()

  val rwConnectBuffer = new ListBuffer[String]()
  val roConnectBuffer = new ListBuffer[String]()
  val woConnectBuffer = new ListBuffer[String]()
  val wcConnectBuffer = new ListBuffer[String]()

  def writeBundleMember(f: BitFieldDetails): String = {
    var s = f"  val ${f.name} = "
    if (f.width > 1)
      s = s + f"UInt(${f.width}.W)\n"
    else
      s = s + f"Bool()\n"
    return s
  }

  def writeBundleConnect(f: BitFieldDetails, index: Int): String = {
    var s: String = ""
    if (f.mode == "RW")
      s = f"  io.rw.${f.name} := t.io.rwVec(${index})\n"
    else if (f.mode == "RO")
      s = f"  t.io.roVec(${index}) := io.ro.${f.name}\n"
    else if (f.mode == "WO")
      s = f"  io.wo.${f.name} := t.io.woVec(${index})\n"
    else if (f.mode == "W1C")
      s = f"  t.io.wcVec(${index}) := io.wc.${f.name}\n"

    if (VERBOSE) {
      println(f"Connecting ${f.mode} bit field ${f.name} to IO Bundle")
    }

    return s
  }

  val chisel3ModuleFilePath = REG_DESC_JSON.replaceAll("json", "scala").replaceAll("\\.scala", "Apb2T.scala")
  val chisel3ModuleFileName = chisel3ModuleFilePath.split('/').last
  val chisel3ModuleName = chisel3ModuleFileName.split('.').head

  val bundlePrefix = chisel3ModuleFileName.split('.').head.capitalize

  if (io.rwVec.nonEmpty) {
    rwBundleBuffer += f"class _${bundlePrefix}RwVec_ extends Bundle {\n"
    rwConnectBuffer += f"\n  // Connect RW bit-field outputs\n"
  }
  if (io.roVec.nonEmpty) {
    roBundleBuffer += f"class _${bundlePrefix}RoVec_ extends Bundle {\n"
    roConnectBuffer += f"\n  // Connect RO bit-field inputs\n"
  }
  if (io.woVec.nonEmpty) {
    woBundleBuffer += f"class _${bundlePrefix}WoVec_ extends Bundle {\n"
    woConnectBuffer += f"\n  // Connect WO bit-field Outputs\n"
  }
  if (io.wcVec.nonEmpty) {
    wcBundleBuffer += f"class _${bundlePrefix}WcVec_ extends Bundle {\n"
    wcConnectBuffer += f"\n  // Connect W1C bit-field Inputs\n"
  }

  var rwIdx, roIdx, woIdx, wcIdx: Int = 0
  for (r <- regElements.names) {
    for (f <- regElements.regArray(regElements.regMap(r).offset >> NUM_BITS_SHIFT).iterator) {
      if (f.mode == "RW") {
        rwConnectBuffer += writeBundleConnect(f, rwIdx)
        rwBundleBuffer += writeBundleMember(f)
        rwIdx = rwIdx + 1
      }
      if (f.mode == "RO") {
        roConnectBuffer += writeBundleConnect(f, roIdx)
        roBundleBuffer += writeBundleMember(f)
        roIdx = roIdx + 1
      }
      if (f.mode == "WO") {
        woConnectBuffer += writeBundleConnect(f, woIdx)
        woBundleBuffer += writeBundleMember(f)
        woIdx = woIdx + 1
      }
    }
  }

  // Check for any address bits set above the required maximum offset of the
  // defined register map which could alias down, prevent write, respond with error
  val regAlias = WireDefault((io.addr >> REQD_W).orR)
  val regIndex = WireDefault(io.addr(REQD_W-1, 0) >> NUM_BITS_SHIFT)

  // Generate core control and status register access logic
  val writeErrorFF = RegInit(false.B)

  when (io.write & !regAlias) {
    // Write process
    writeErrorFF := false.B

    for (i <- 0 until regElements.maxRegs) {
      when (regIndex === i.U) {
        // Write data to the individual writable bit fields of the
        // register and signal error if the register is unmapped
        for (f <- regElements.regArray(i)) {
          if (f.mode == "RW" || f.mode == "WO" || f.mode == "W1C") {
            // Write strobes: bits are used to mask or enable writes to individual bytes
            // of bit fields. However, if a bit field straddles two or more byte lanes
            // and not ALL the corresponding strobe bits are set then the bit field is
            // not written (at all) and error is signalled.
            //
            // Following logic extracts only the write strobes that cover the bit field
            // then and ANDs them together
            //
            // b  | range | check
            // ---|-------|---------------------------------
            // 0  |  7:0  | pos < 8  && pos + width -  0 > 0
            // 1  | 15:8  | pos < 16 && pos + width -  8 > 0
            // 2  | 23:16 | pos < 24 && pos + width - 16 > 0
            // 3  | 31:24 | pos < 32 && pos + width - 24 > 0
            //
            val fieldStrbBits = for {
              b <- 0 until NUM_BYTE if (f.pos < ((b + 1) * 8)) && ((f.pos + f.width - b * 8) > 0)
            } yield io.writeStrb(b)

            when (fieldStrbBits.reduceLeft(_ & _)) {
              // ALL bits of writeStrb covering the bit field are set
              if (f.mode == "W1C") {
                // Write-1-to-clear: create a Bool Vec of the register bit field
                // and zip it with a Bool Vec of the write data clear each bit
                // of the bit field individually
                val nxtBits = VecInit(f.reg.asBools)
                val clrBits = VecInit((io.writeData >> f.pos).asBools)
                for ((nxt, clr) <- (nxtBits zip clrBits).toMap) {
                  when (clr) {
                    nxt := false.B
                  }
                }
                f.reg := nxtBits.asUInt
              } else {
                // f.mode == "RW" || f.mode == "WO"
                f.reg := io.writeData >> f.pos
              }
            }.elsewhen (fieldStrbBits.reduceLeft(_ | _)) {
              // SOME but not ALL write strobe bits covering the bit field are set
              writeErrorFF := true.B
            }
          } else if (f.mode == "N/A") {
            writeErrorFF := true.B
          }
        }
      }
    }
  }.otherwise {
    // Clear all WO register bit fields
    regElements.regArray.foreach(r => { r.foreach(f => { if (f.mode == "WO") f.reg := 0.U }) })

    // Update all W1C register bit fields
    regElements.regArray.foreach(r => {
      r.foreach(f => {
        if (f.mode == "W1C") {
          wcConnectBuffer += writeBundleConnect(f, wcIdx)
          wcBundleBuffer += writeBundleMember(f)
          wcIdx = wcIdx + 1

          val nxtBits = VecInit(f.reg.asBools)
          val setBits = VecInit(wcIt.next.asBools)
          for ((nxt, set) <- (nxtBits zip setBits).toMap) {
            when (set) {
              nxt := true.B
            }
          }
          f.reg := nxtBits.asUInt
        }
      })
    })

    // Signal error on address alias, otherwise (if no alias) clear error status
    writeErrorFF := regAlias
  }

  val readDataFF = RegInit(0.U(DATA_W.W))
  val readErrorFF = RegInit(regAlias)

  when (io.read) {
    // Read process
    readDataFF := 0.U

    for (i <- 0 until regElements.maxRegs) {
      when (regIndex === i.U) {
        // Shift the individual bit fields of the indexed register into position and OR
        // to be read together and signal error if the register is unmapped
        val shiftedBits: List[UInt] = for (f <- regElements.regArray(i).toList) yield (f.reg << f.pos)
        readDataFF := shiftedBits.reduceLeft(_ | _)
        for (f <- regElements.regArray(i)) {
          if (f.mode == "N/A") readErrorFF := true.B
        }
      }
    }
  }

  io.readData := readDataFF
  io.error := writeErrorFF | readErrorFF


  // Optional write of generated Bundles and wrapper Module to file
  if (GEN_MODULE) {
    val pw = new PrintWriter(new File(f"${chisel3ModuleFilePath}"))
    println(f"Writing Bundles to ${chisel3ModuleFilePath}")

    pw.write( "//\n// Copyright 2022 Richard James Richmond\n//\n")
    pw.write(f"""// Licensed under the Apache License, Version 2.0 (the "License");\n""")
    pw.write( "// you may not use this file except in compliance with the License.\n")
    pw.write( "// You may obtain a copy of the License at\n//\n")
    pw.write( "//     http://www.apache.org/licenses/LICENSE-2.0\n//\n")
    pw.write( "// Unless required by applicable law or agreed to in writing, software\n")
    pw.write(f"""// distributed under the License is distributed on an "AS IS" BASIS,\n""")
    pw.write( "// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n")
    pw.write( "// See the License for the specific language governing permissions and\n")
    pw.write( "// limitations under the License.\n\n")

    pw.write( "package ambel\n\n")
    pw.write( "import chisel3._\n")
    pw.write( "import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}\n\n")
    pw.write(f"""/** =Bundles for Connection to Apb2CSTrgt(REG_DESC_JSON="${REG_DESC_JSON}")=\n""")
    pw.write( "  *\n  * THIS IS AUTO-GENERATED CODE - DO NOT MODIFY BY HAND!\n  */\n")

    if (rwBundleBuffer.nonEmpty) {
      rwBundleBuffer += "}\n"
    }
    if (roBundleBuffer.nonEmpty) {
      roBundleBuffer += "}\n"
    }
    if (woBundleBuffer.nonEmpty) {
      woBundleBuffer += "}\n"
    }
    if (wcBundleBuffer.nonEmpty) {
      wcBundleBuffer += "}\n"
    }

    rwBundleBuffer.foreach(pw.write)
    roBundleBuffer.foreach(pw.write)
    woBundleBuffer.foreach(pw.write)
    wcBundleBuffer.foreach(pw.write)

    println(f"Writing wrapper Module to ${chisel3ModuleFilePath}")

    pw.append(f"""\n/** =Wrapper Module for Apb2CSTrgt(REG_DESC_JSONy="${REG_DESC_JSON}")=\n""")
    pw.append( "  * Uses Bundles above on IO and makes ordered connection to MixedVec IO on\n")
    pw.append( "  * Apb2CSTrgt instance\n")
    pw.append( "  *\n  * THIS IS AUTO-GENERATED CODE - DO NOT MODIFY BY HAND!\n  */\n")

    pw.append(f"class ${chisel3ModuleName}() extends Module {\n")
    pw.append(f"  val ADDR_W = ${ADDR_W}\n")
    pw.append(f"  val DATA_W = ${DATA_W}\n\n")
    pw.append(f"  val t = Module(new Apb2CSTrgt(\n")
    pw.append(f"    ADDR_W = ADDR_W,\n")
    pw.append(f"    DATA_W = DATA_W,\n")
    pw.append(f"""    REG_DESC_JSON = "${REG_DESC_JSON}"))\n\n""")

    pw.append( "  val io = IO(new Bundle {\n")
    pw.append( "    val apb2T = new Apb2IO(ADDR_W, DATA_W)\n")
    if (rwBundleBuffer.nonEmpty) {
      pw.append(f"    val rw = Output(new _${bundlePrefix}RwVec_)\n")
    }
    if (roBundleBuffer.nonEmpty) {
      pw.append(f"    val ro = Input(new _${bundlePrefix}RoVec_)\n")
    }
    if (woBundleBuffer.nonEmpty) {
      pw.append(f"    val wo = Output(new _${bundlePrefix}WoVec_)\n")
    }
    if (wcBundleBuffer.nonEmpty) {
      pw.append(f"    val wc = Input(new _${bundlePrefix}WcVec_)\n")
    }
    pw.append( "  })\n\n")

    pw.append( "  // Connect APB2 target interface\n")
    pw.append( "  t.io.apb2T <> io.apb2T\n")

    rwConnectBuffer.foreach(pw.write)
    roConnectBuffer.foreach(pw.write)
    woConnectBuffer.foreach(pw.write)
    wcConnectBuffer.foreach(pw.write)

    pw.append( "}\n")

    pw.append(f"\nobject ${chisel3ModuleName}Driver extends App {\n")
    pw.append(f"  (new ChiselStage).execute(args, Seq(ChiselGeneratorAnnotation(() => new ${chisel3ModuleName}())))\n")
    pw.append( "}\n")

    pw.close
  }
}
