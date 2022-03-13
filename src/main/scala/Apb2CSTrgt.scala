// See README.md for license details.
package ambel

import java.io._
import scala.io.Source
import scala.math.pow
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Map
import scala.collection.immutable.ListMap
import chisel3._
import chisel3.util._
import chisel3.experimental.BundleLiterals._
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}
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
  * @param GEN_BUNDLE enables generation of Scala Bundles suitable for connection
  * to the generated MixedVec IOs. The signal names used in the Bundles match their
  * corresponding register and bit field names, as specified in the JSON. They are
  * declared in the same order as the entries of the corresponding MixedVecs and
  * are therefore very simple to connect to create wrapper Modules with named IO
  * for specific (JSON) parameterizations of Apb2CSTrgt
  * @todo implement pProt
  * @todo implement check that there are no spaces in register or regType names in JSON
  */
class Apb2CSTrgt(
  DATA_W: Int = 32,
  REG_DESC_JSON: String,
  VERBOSE: Boolean = false,
  GEN_BUNDLE: Boolean = false) extends Module {
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
  val ADDR_W = log2Ceil(NUM_REGS * NUM_BYTE)
  val MAX_REGS = pow(2, ADDR_W).toInt >> NUM_BITS_SHIFT

  // 3. Check that registers do not overlap
  for (i <- 0 until sorted.size - 1) {
    assert (sorted(i).offset + sorted(i).width / 8 <= sorted(i+1).offset)
  }

  val regMap = (names zip attributes).toMap
  val offNameMap = (offsets zip names).toMap
  val typeFieldMap = (types zip fields).toMap


  if (VERBOSE) {
    println(f"NUM_REGS = ${NUM_REGS}, ADDR_W = ${ADDR_W}, MAX_REGS = ${MAX_REGS}")
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
  // it was possible we'd use it here, negating the requirement for the GEN_BUNDLE functionality
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
  // written to file if (GEN_BUNDLE)
  val rwBundleBuffer = new ListBuffer[String]()
  val roBundleBuffer = new ListBuffer[String]()
  val woBundleBuffer = new ListBuffer[String]()
  val wcBundleBuffer = new ListBuffer[String]()

  val rwIt = io.rwVec.iterator
  val roIt = io.roVec.iterator
  val woIt = io.woVec.iterator
  val wcIt = io.wcVec.iterator

  val chisel3BundleFilePath = REG_DESC_JSON.replaceAll("json", "scala")
  val chisel3BundleFileName = chisel3BundleFilePath.split('/').last
  val bundlePrefix = chisel3BundleFileName.split('.').head.capitalize

  if (rwIt.nonEmpty) {
    rwBundleBuffer += f"class _${bundlePrefix}RwVec_ extends Bundle {\n"
  }
  if (roIt.nonEmpty) {
    roBundleBuffer += f"class _${bundlePrefix}RoVec_ extends Bundle {\n"
  }
  if (woIt.nonEmpty) {
    woBundleBuffer += f"class _${bundlePrefix}WoVec_ extends Bundle {\n"
  }
  if (wcIt.nonEmpty) {
    wcBundleBuffer += f"class _${bundlePrefix}WcVec_ extends Bundle {\n"
  }

  def writeBundleMember(f: BitFieldDetails): String = {
    var s = f"  val ${f.name} = "
    if (f.width > 1)
      s = s + f"UInt(${f.width}.W)\n"
    else
      s = s + f"Bool()\n"
    return s
  }

  for (r <- names) {
    for (f <- regArr(regMap(r).offset >> NUM_BITS_SHIFT).iterator) {
      if (f.mode == "RW") {
        if (VERBOSE) {
          println(f"Connecting RW bit field ${f.name} to IO Bundle rwVec Output")
        }
        rwBundleBuffer += writeBundleMember(f)
        rwIt.next := f.reg
      }
      if (f.mode == "RO") {
        if (VERBOSE) {
          println(f"Connecting RO bit field ${f.name} to IO Bundle roVec Input")
        }
        roBundleBuffer += writeBundleMember(f)
        f.reg := roIt.next
      }
      if (f.mode == "WO") {
        if (VERBOSE) {
          println(f"Connecting WO bit field ${f.name} to IO Bundle woVec Output")
        }
        woBundleBuffer += writeBundleMember(f)
        woIt.next := f.reg
      }
    }
  }

  val pAddrFF   = RegInit(0.U(ADDR_W.W))
  val pWriteFF  = RegInit(false.B)
  val pReadyFF  = RegInit(true.B)
  val pRDataFF  = RegInit(0.U(DATA_W.W))
  val pSlvErrFF = RegInit(false.B)

  val regIndex  = Wire(Bits((ADDR_W - NUM_BITS_SHIFT).W))

  // Access detect
  when (io.apb2T.req.pSel & !io.apb2T.req.pEnable) {
    pAddrFF  :=  io.apb2T.req.pAddr
    pWriteFF :=  io.apb2T.req.pWrite
    pReadyFF :=  io.apb2T.req.pWrite // Always one wait state for reads

    // Debug
    //printf("Access detected:\n")
    //printf("  pAddr = 0x%x, pWrite = %b, pStrb = %b, pWData = 0x%x\n", io.pAddr, io.pWrite, io.pStrb, io.pWData)
  } .otherwise {
    pWriteFF := false.B
  }

  // Decode address to index registers
  regIndex := pAddrFF >> NUM_BITS_SHIFT

  when (pWriteFF) {
    // Write process
    pWriteFF  := false.B
    pSlvErrFF := false.B

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
  }.otherwise {
    // Clear all WO register bit fields
    regArr.foreach(r => { r.foreach(f => { if (f.mode == "WO") f.reg := 0.U }) })

    // Update all W1C register bit fields
    regArr.foreach(r => {
      r.foreach(f => {
        if (f.mode == "W1C") {
          if (VERBOSE) {
            println(f"Connecting W1C bit field ${f.name} to IO Bundle")
          }
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
    pSlvErrFF := false.B

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

  // Optional write of generated Bundles to file
  if (GEN_BUNDLE) {
    val pw = new PrintWriter(new File(f"${chisel3BundleFilePath}"))

    println(f"Writing ${chisel3BundleFilePath}")

    pw.write("// See README.md for license details.\n")
    pw.write("package ambel\n\n")
    pw.write("import chisel3._\n\n")
    pw.write(f"""/** =Bundles for Connection to Apb2CSTrgt(REG_DESC_JSON="${REG_DESC_JSON}")\n""")
    pw.write("  *\n  * THIS IS AUTO-GENERATED CODE - DO NOT MODIFY BY HAND!\n  */\n")

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
  (new ChiselStage).execute(args, Seq(ChiselGeneratorAnnotation(() => new Apb2CSTrgt(32, "src/main/json/Example.json", true, true))))
}
