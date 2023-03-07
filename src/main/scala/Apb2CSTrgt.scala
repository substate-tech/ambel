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
import scala.math.pow
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Map
import scala.collection.immutable.ListMap
import chisel3._
import chisel3.util.{log2Ceil, MixedVec}
import chisel3.experimental.BundleLiterals._
import chisel3.stage.ChiselGeneratorAnnotation
import circt.stage.ChiselStage
import io.circe._, io.circe.parser._
import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.auto._

/** =Apb2CSTrgt=
  *
  * Basic APB2 Control/Status register target generator with Control/Status
  * register map supplied via a simple JSON description.
  *
  * The following register bit field types are supported:
  *
  * ==RW register bits==
  * Read-write. Generally these are static configuration bit fields and are
  * connected to Outputs. The register bits can only be set or cleared by
  * writing to the register.
  *
  * ==RO register bits==
  * Read-only. These are connected to Inputs which should be driven by registered
  * external status signals from the design instantiating Apb2CSTrgt. Writing to
  * these registers has no effect.
  *
  * ==WO register bits==
  * Write-only. These are connected to Outputs but after being written to '1' they
  * are always set back to to '0' on the following clock cycle, so writing a '1' to
  * a WO register bit will create a single-cycle pulse on the corresponding Output.
  * This bit field mode can be used to implement 'go bits' which trigger some
  * event elsewhere in the design instantiating Apb2CSTrgt.  Writing a '0' has no
  * effect, reads are always '0'.
  *
  * ==W1C register bits==
  * Write-1-to-clear. These are connected to Inputs on which a single cycle pulse
  * will set the corresponding register bit to '1'. Writing a '1' to the same bit
  * will clear it. Writing a '0' has no effect, regardless of the current value of
  * the bit. This bit field mode is the one to use for interrupt status registers.
  * Interrupt enable/mask registers should be implemented using RW bit fields
  * with the enable/mask logic implemented externally in the design instantiating
  * Apb2CSTrgt.
  *
  * @note pStrb is implemented as follows: pStrb bits are used to mask or enable
  * writes to individual bytes of bit fields. However, if a bit field straddles
  * two or more byte lanes and not ALL the corresponding bits of pStrb are set
  * then the bit field is not written (at all) and pSlvErr is signalled. In other
  * words, partial writes to bitfields are not supported; either the whole bit
  * field is written, when ALL corresponding pStrb bits are set, or the bit field
  * is not written at all. Writing to a register with NONE of the pStrb bits
  * corresponding to a given bit field set is OK and is NOT an error condition.
  * This is a design decision. The AMBA APB2 spec only discusses the pStrb bits in the
  * context of the validity of the byte lanes of the write data bus.
  *
  * The JSON register description specifies the width of each register type. All
  * registers in a given JSON must have width >= DATA_W and if width is > DATA_W
  * then it must be power of two a multiple of DATA_W. i.e. 64 bit registers are
  * supported with 32 bit access but 96 bit registers are not supported and 16 bit
  * registers are not supported with 32 bit access, for example.
  *
  * If a register wider than DATA_W specifies a field which straddles the DATA_W
  * boundary then it is broken into two (or more) pieces, for DATA_W access, which
  * are concatenated together.
  *
  * @param DATA_W the width of the APB2 data bus in bits
  * @param REG_DESC_JSON a string giving the path to the register description JSON
  * to be generated
  * @param VERBOSE enables verbose output during generation
  * @param GEN_MODULE enables generation of a wrapper Module which uses generated
  * Bundles suitable for connection to the generated MixedVec IOs. The signal names
  * used in the Bundles match their corresponding register and bit field names, as
  * specified in the JSON. They are declared in the same order as the entries of
  * the corresponding MixedVecs and connected in order
  * @todo implement pProt
  * @todo implement check that there are no spaces in register or regType names in JSON
  */
class Apb2CSTrgt(
  ADDR_W: Int = 32,
  DATA_W: Int = 32,
  REG_DESC_JSON: String,
  VERBOSE: Boolean = false,
  GEN_MODULE: Boolean = false) extends Module {
  val NUM_BYTE = DATA_W/8
  val NUM_BITS_SHIFT = log2Ceil(NUM_BYTE) // Number of bits to shift right address to index registers

  // Decode JSON register map
  case class Register(offset: Int, name: String, typeRef: String, comment: Option[String])
  case class BitField(bits: List[Int], name: String, mode: Option[String], resetVal: Option[Int], comment: Option[String])
  case class RegisterType(typeRef: String, width: Int, fields: List[BitField], comment: Option[String])
  case class RegisterDesc(regMap: List[Register], regTypes: List[RegisterType])

  // Register attributes - offset, width and typeRef
  case class RegisterAttr(offset: Int, width: Int, typeRef: String)

  // Register bits - name and bitfield list
  case class RegisterBits(name: String, fields: List[BitField])

  object RegisterDescDecoder {
    def getReg(args: Array[String]): Option[RegisterDesc] = {

      val decodingResult = parser.decode[RegisterDesc](args(0))

      decodingResult match {
        case Right(decodingResult) => Some(decodingResult)
        case Left(error) => {
          println(error.getMessage())
          None
        }
      }
    }
  }

  def prettyPrintReg(r: Register) {
    print(f"h${r.offset}%04x: ${r.name}  (${r.typeRef})\n")
  }

  def prettyPrintBitField(f: BitField) {
    print(f"  ${f.bits.head}%2d:${f.bits.last}%2d  ${f.name}")
    f.mode match {
      case Some(mode) => print(f", $mode")
      case None =>
    }
    f.resetVal match {
      case Some(value) => print(f", $value")
      case None =>
    }
    f.comment match {
      case Some(comment) => print(f"  ($comment)")
      case None =>
    }
    print("\n")
  }

  def prettyPrintRegType(t: RegisterType) {
    print(f"${t.typeRef}, ${t.width} bits")
    t.comment match {
      case Some(comment) => print(f"  (${comment})\n")
      case None => print(f"\n")
    }
    t.fields.map(prettyPrintBitField)
  }

  def printRegMap(m: RegisterDesc) {
    m.regMap.map(prettyPrintReg)
    m.regTypes.map(prettyPrintRegType)
  }

  val jsonString = Source.fromFile(REG_DESC_JSON).getLines.mkString.stripMargin

  val regDesc = RegisterDescDecoder.getReg(Array(jsonString))

  regDesc match {
    case Some(regs) => {
      printRegMap(regs)
    }
    case None =>
  }

  val names: List[String] = regDesc match {
    case Some(r) => for (m <- r.regMap) yield m.name
    case None => List("Empty")
  }

  val offsets: List[Int] = regDesc match {
    case Some(r) => for (m <- r.regMap) yield m.offset
    case None => List(0)
  }

  val types: List[String] = regDesc match {
    case Some(r) => for (t <- r.regTypes) yield t.typeRef
    case None => List("Empty")
  }

  val widths: List[Int] = regDesc match {
    case Some(r) => for (t <- r.regTypes) yield t.width
    case None => List(0)
  }

  val typeWidthMap = (types zip widths).toMap

  val attributes: List[RegisterAttr] = regDesc match {
    case Some(r) => for (m <- r.regMap) yield
      RegisterAttr(offset = m.offset, typeWidthMap(m.typeRef), typeRef = m.typeRef)
    case None => List(
      RegisterAttr(offset = 0, width = 0, typeRef = "NONE")
    )
  }

  val fields: List[List[BitField]] = regDesc match {
    case Some(r) => for (t <- r.regTypes) yield t.fields
    case None => List(List(
      BitField(bits = List(0,0),
        name = "NONE", mode = Some("RW"),
        resetVal = Some(0), comment = Some("No comment")
      )
    ))
  }


  // Run basic register description checks and derive basic parameters
  // 1. Sort the attributes List by offset (i.e. put the registers into order)
  val sorted: List[RegisterAttr] = attributes.sortWith(_.offset < _.offset)
  println(sorted)

  // 1. Check alignment: offset should be on a width / 8 boundary
  var bytes: Int = 0
  for (r <- attributes) {
    assert ((r.offset % (r.width / 8)) == 0)
    bytes += r.width / 8
  }

  // 2. Determine number of (possible) DATA_W registers from maximum offset
  // used in the register description
  val NUM_REGS = bytes >> NUM_BITS_SHIFT
  val REQD_W = log2Ceil(NUM_REGS * NUM_BYTE)
  val MAX_REGS = pow(2, REQD_W).toInt >> NUM_BITS_SHIFT

  // 3. Check that registers do not overlap
  for (i <- 0 until sorted.size - 1) {
    assert (sorted(i).offset + sorted(i).width / 8 <= sorted(i+1).offset)
  }

  val regMap = (names zip attributes).toMap
  val offNameMap = (offsets zip names).toMap
  val typeFieldMap = (types zip fields).toMap


  if (VERBOSE) {
    println(f"NUM_REGS = ${NUM_REGS}, REQD_W = ${REQD_W}, MAX_REGS = ${MAX_REGS}")
  }

  val namesAndBits: List[RegisterBits] = regDesc match {
    case Some(r) => for (m <- r.regMap) yield
      RegisterBits(name = m.name, fields = typeFieldMap(m.typeRef))
    case None => List(
      RegisterBits(
        name = "NONE",
        fields = List(BitField(bits = List(0,0),
          name = "NONE", mode = Some("RW"),
          resetVal = Some(0), comment = Some("No comment")
        ))
      )
    )
  }

  // Build ordered maps of different register bit field categories
  var rwRegBits = ListMap[String, Int]()
  var roRegBits = ListMap[String, Int]()
  var woRegBits = ListMap[String, Int]()
  var wcRegBits = ListMap[String, Int]()
  for (r <- namesAndBits) {
    for (f <- r.fields) {
      val width = f.bits.head - f.bits.last + 1
      val name = r.name.toLowerCase + "_" + f.name.toLowerCase
      if (f.mode == Some("RW")) {
        rwRegBits += (name -> width)
      }
      if (f.mode == Some("RO")) {
        roRegBits += (name -> width)
      }
      if (f.mode == Some("WO")) {
        woRegBits += (name -> width)
      }
      if (f.mode == Some("W1C")) {
        wcRegBits += (name -> width)
      }
    }
  }

  // NOTE suggestName doesn't actually work in IO Bundles. unclear whether it ever will, but if
  // it was possible we'd use it here, negating the requirement for the GEN_MODULE functionality
  val io = IO(new Bundle {
    val apb2T = new Apb2IO(ADDR_W, DATA_W)
    val rwVec = Output(MixedVec((rwRegBits map { case (k, v) => UInt(v.W).suggestName(k) }).toList))
    val roVec = Input(MixedVec((roRegBits map { case (k, v) => UInt(v.W).suggestName(k) }).toList))
    val woVec = Output(MixedVec((woRegBits map { case (k, v) => UInt(v.W).suggestName(k) }).toList))
    val wcVec = Input(MixedVec((wcRegBits map { case (k, v) => UInt(v.W).suggestName(k) }).toList))
  })

  // Build a 2D array of all registers and their RegInit() bit fields with
  // JSON specified reset values and widths. No RegInit() for RESERVED
  // bit fields which are read-only zeros. Each RegInit() is part of a tuple
  // which also holds its position and its mode
  case class BitFieldDetails(reg: UInt, pos: Int, width: Int, mode: String, name: String)
  val regArr = Array.ofDim[ArrayBuffer[BitFieldDetails]](MAX_REGS)

  // Takes a snake_case or SNAKE_CASE string name and converts to CamelCase
  def toCamelCase(s: String): String = {
    val pieces = s.split('_')
    val camelArr: Array[String] = for (p <- pieces) yield p.toLowerCase.capitalize
    val camel: String = camelArr.mkString
    return camel
  }

  for (i <- 0 until MAX_REGS) {
    regArr(i) = ArrayBuffer[BitFieldDetails]()
    val offset = i << NUM_BITS_SHIFT
    if (offNameMap.contains(offset)) {
      for (f <- typeFieldMap(regMap(offNameMap(offset)).typeRef)) {
        if (f.name != "RESERVED") {
          val width = f.bits.head - f.bits.last + 1
          val pos = f.bits.last
          val regName = offNameMap(offset)
          val fieldName = f.name.toLowerCase
          val name = toCamelCase(regName) + "_" + toCamelCase(fieldName)
          if (f.mode == Some("RW")) {
            // For RW bit fields use reset value of 0 assumed if none specified
            if (VERBOSE) {
              println(f"Creating RW register bit field ${regName}.${f.name} @offset h${offset}%04x")
            }
            f.resetVal match {
              case Some(value) => {
                regArr(i) += BitFieldDetails(RegInit(value.U(width.W)).suggestName(name), pos, width, "RW", name)
              }
              case None => {
                regArr(i) += BitFieldDetails(RegInit(0.U(width.W)).suggestName(name), pos, width, "RW", name)
              }
            }
          }
          if (f.mode == Some("RO")) {
            // For RO bit fields declare Wires rather than registers
            if (VERBOSE) {
              println(f"Found RO register bit field ${regName}.${f.name} @offset h${offset}%04x")
            }
            regArr(i) += BitFieldDetails(Wire(UInt(width.W)).suggestName(name), pos, width, "RO", name)
          }
          if (f.mode == Some("WO")) {
            // For WO bit fields reset value of 0
            if (VERBOSE) {
              println(f"Creating WO register bit field ${regName}.${f.name} @offset h${offset}%04x")
            }
            regArr(i) += BitFieldDetails(RegInit(0.U(width.W)).suggestName(name), pos, width, "WO", name)
          }
          if (f.mode == Some("W1C")) {
            // For W1C bit fields reset value of 0
            if (VERBOSE) {
              println(f"Creating W1C register bit field ${regName}.${f.name} @offset h${offset}%04x")
            }
            regArr(i) += BitFieldDetails(RegInit(0.U(width.W)).suggestName(name), pos, width, "W1C", name)
          }
        }
      }
    } else {
      // No register defined at this offset (unmapped) - mark a not applicable
      // and create a single WireDefault() bit field with a value of 0
      if (VERBOSE) {
        println(f"No register @offset h${offset}%04x")
      }
      regArr(i) += BitFieldDetails(WireDefault(0.U(DATA_W.W)), 0, DATA_W, "N/A", "None")
    }
  }

  // Connect different register bit field categories to MixedVec IOs
  // NOTE the Vecs are declared in same order as registers are declared
  // in JSON description therefore iterate over names and index regArr with
  // corresponding offset. Simultaneously we generate Chisel Bundles with
  // signal names suitable for connection to the MixedVecs. These are
  // written to file if (GEN_MODULE)
  val rwBundleBuffer = new ListBuffer[String]()
  val roBundleBuffer = new ListBuffer[String]()
  val woBundleBuffer = new ListBuffer[String]()
  val wcBundleBuffer = new ListBuffer[String]()

  val rwConnectBuffer = new ListBuffer[String]()
  val roConnectBuffer = new ListBuffer[String]()
  val woConnectBuffer = new ListBuffer[String]()
  val wcConnectBuffer = new ListBuffer[String]()

  val rwIt = io.rwVec.iterator
  val roIt = io.roVec.iterator
  val woIt = io.woVec.iterator
  val wcIt = io.wcVec.iterator

  val chisel3ModuleFilePath = REG_DESC_JSON.replaceAll("json", "scala").replaceAll("\\.scala", "Apb2T.scala")
  val chisel3ModuleFileName = chisel3ModuleFilePath.split('/').last
  val chisel3ModuleName = chisel3ModuleFileName.split('.').head

  val bundlePrefix = chisel3ModuleFileName.split('.').head.capitalize

  if (rwIt.nonEmpty) {
    rwBundleBuffer += f"class _${bundlePrefix}RwVec_ extends Bundle {\n"
    rwConnectBuffer += f"\n  // Connect RW bit-field outputs\n"
  }
  if (roIt.nonEmpty) {
    roBundleBuffer += f"class _${bundlePrefix}RoVec_ extends Bundle {\n"
    roConnectBuffer += f"\n  // Connect RO bit-field inputs\n"
  }
  if (woIt.nonEmpty) {
    woBundleBuffer += f"class _${bundlePrefix}WoVec_ extends Bundle {\n"
    woConnectBuffer += f"\n  // Connect WO bit-field Outputs\n"
  }
  if (wcIt.nonEmpty) {
    wcBundleBuffer += f"class _${bundlePrefix}WcVec_ extends Bundle {\n"
    wcConnectBuffer += f"\n  // Connect W1C bit-field Inputs\n"
  }

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

  var rwIdx, roIdx, woIdx, wcIdx: Int = 0
  for (r <- names) {
    for (f <- regArr(regMap(r).offset >> NUM_BITS_SHIFT).iterator) {
      if (f.mode == "RW") {
        rwConnectBuffer += writeBundleConnect(f, rwIdx)
        rwIdx = rwIdx + 1
        rwBundleBuffer += writeBundleMember(f)
        rwIt.next := f.reg
      }
      if (f.mode == "RO") {
        roConnectBuffer += writeBundleConnect(f, roIdx)
        roIdx = roIdx + 1
        roBundleBuffer += writeBundleMember(f)
        f.reg := roIt.next
      }
      if (f.mode == "WO") {
        woConnectBuffer += writeBundleConnect(f, woIdx)
        woIdx = woIdx + 1
        woBundleBuffer += writeBundleMember(f)
        woIt.next := f.reg
      }
    }
  }

  val pAddrFF   = RegInit(0.U)
  val pWriteFF  = RegInit(false.B)
  val pReadyFF  = RegInit(true.B)
  val pRDataFF  = RegInit(0.U(DATA_W.W))
  val pSlvErrFF = RegInit(false.B)

  val regIndex   = Wire(Bits((REQD_W - NUM_BITS_SHIFT).W))
  val regAliasFF = RegInit(false.B)

  // Access detect
  when (io.apb2T.req.pSel & !io.apb2T.req.pEnable) {
    // Capture address bits required to index defined registers
    pAddrFF  :=  io.apb2T.req.pAddr(REQD_W, 0)
    pWriteFF :=  io.apb2T.req.pWrite
    pReadyFF :=  io.apb2T.req.pWrite // Always one wait state for reads

    // Check for any address bits set above the required maximum offset of the
    // defined register map which could alias down, prevent write, respond with pSlvErr
    regAliasFF := (io.apb2T.req.pAddr >> REQD_W).orR
  }.otherwise {
    pWriteFF := false.B
  }

  // Decode address to index registers
  regIndex := pAddrFF >> NUM_BITS_SHIFT

  when (pWriteFF) {
    // Write process
    pWriteFF  := false.B
    pSlvErrFF := false.B

    when (regAliasFF) {
      pSlvErrFF := true.B
    }.otherwise {

      for (i <- 0 until MAX_REGS) {
        when (regIndex === i.U) {
          // Write data to the individual writable bit fields of the
          // register and signal error if the register is unmapped
          for (f <- regArr(i)) {
            if (f.mode == "RW" || f.mode == "WO" || f.mode == "W1C") {
              // Write strobes: pStrb bits are used to mask or enable writes to individual
              // bytes of bit fields. However, if a bit field straddles two or more byte lanes
              // and not ALL the corresponding bits of pStrb are set then the bit field is not
              // written (at all) and pSlvErr is signalled.
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
              val fieldPStrbBits = for {
                b <- 0 until NUM_BYTE if (f.pos < ((b + 1) * 8)) && ((f.pos + f.width - b * 8) > 0)
              } yield io.apb2T.req.pStrb(b)

              when (fieldPStrbBits.reduceLeft(_ & _)) {
                // ALL bits of pStrb covering the bit field are set
                if (f.mode == "W1C") {
                  // Write-1-to-clear: create a Bool Vec of the register bit field
                  // and zip it with a Bool Vec of the write data clear each bit
                  // of the bit field individually
                  val nxtBits = VecInit(f.reg.asBools)
                  val clrBits = VecInit((io.apb2T.req.pWData >> f.pos).asBools)
                  for ((nxt, clr) <- (nxtBits zip clrBits).toMap) {
                    when (clr) {
                      nxt := false.B
                    }
                  }
                  f.reg := nxtBits.asUInt
                } else {
                  // f.mode == "RW" || f.mode == "WO"
                  f.reg := io.apb2T.req.pWData >> f.pos
                }
              }.elsewhen (fieldPStrbBits.reduceLeft(_ | _)) {
                // SOME but not ALL bits of pStrb covering the bit field are set
                pSlvErrFF := true.B
              }
            } else if (f.mode == "N/A") {
              pSlvErrFF := true.B
            }
          }
        }
      }
    }
  }.otherwise {
    // Clear all WO register bit fields
    regArr.foreach(r => { r.foreach(f => { if (f.mode == "WO") f.reg := 0.U }) })

    // Update all W1C register bit fields
    regArr.foreach(r => {
      r.foreach(f => {
        if (f.mode == "W1C") {
          wcConnectBuffer += writeBundleConnect(f, wcIdx)
          wcIdx = wcIdx + 1
          wcBundleBuffer += writeBundleMember(f)

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
  }

  when (!pReadyFF) {
    // Read process
    pRDataFF  := 0.U
    pReadyFF  := true.B

    when (regAliasFF) {
      pSlvErrFF := true.B
    }.otherwise {
      pSlvErrFF := false.B
    }

    for (i <- 0 until MAX_REGS) {
      when (regIndex === i.U) {
        // Shift the individual bit fields of the indexed register into position and OR
        // to be read together and signal error if the register is unmapped
        val shiftedBits: List[UInt] = for (f <- regArr(i).toList) yield (f.reg << f.pos)
        pRDataFF := shiftedBits.reduceLeft(_ | _)
        for (f <- regArr(i)) {
          if (f.mode == "N/A") pSlvErrFF := true.B
        }
      }
    }
  }

  io.apb2T.rsp.pReady  := pReadyFF
  io.apb2T.rsp.pRData  := pRDataFF
  io.apb2T.rsp.pSlvErr := pSlvErrFF

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
    pw.write( "import chisel3.stage.ChiselGeneratorAnnotation\n")
    pw.write( "import circt.stage.ChiselStage\n\n")
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

    pw.append(f"""\n/** =Wrapper Module for Apb2CSTrgt(REG_DESC_JSON="${REG_DESC_JSON}")=\n""")
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

/** =Verilog generation boiler plate=
  *
  * Run this driver as follows...
  * From within sbt use:
  * {{{
  * runMain ambel.Apb2CSTrgtDriver --target-dir src/main/verilog --log-level info --log-file Apb2CSTrgtDriver.log
  * }}}
  */
// $COVERAGE-OFF$
object Apb2CSTrgtDriver extends App {
  (new ChiselStage).execute(args, Seq(ChiselGeneratorAnnotation(() => new Apb2CSTrgt(32, 32, "src/main/json/Simple.json", true, true))))
}
