// See README.md for license details.
package ambel

import scala.io.Source
import scala.math.pow
import scala.collection.mutable.ArrayBuffer
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
  * @note pStrb is implemented as follows. There is no masking of pWriteData with
  * pStrb. However, foreach bitfield of a register being written the implementation
  * checks that the pStrb bits that correspond to that bit field are set. If they
  * are not then pSlvErr is signalled. Again, the bit field is written whether the
  * pStrb bits are set or not. This is a design decision. The AMBA APB2 spec only
  * discusses the pStrb bits in the context of the validity of the byte lanes of
  * the write data bus.
  * @param DATA_W the width of the APB2 data bus in bits
  * @param REG_DESC_JSON a string giving the path to the register description JSON
  * to be generated
  * @todo implement pProt
  */
class Apb2CSTrgt(
  DATA_W: Int = 32,
  REG_DESC_JSON: String) extends Module {
  val NUM_BYTE = DATA_W/8
  val NUM_BITS_SHIFT = log2Ceil(NUM_BYTE) // Number of bits to shift right address to index registers

  // Decode JSON register map
  case class Register(offset: Int, name: String, typeRef: String, comment: Option[String])
  case class BitField(bits: List[Int], name: String, mode: Option[String], resetVal: Option[Int], comment: Option[String])
  case class RegisterType(typeRef: String, fields: List[BitField], comment: Option[String])
  case class RegisterDesc(regMap: List[Register], regTypes: List[RegisterType])

  // Register attributes - offset and typeRef
  case class RegisterAttr(offset: Int, typeRef: String)

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
    print(f"${t.typeRef}")
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

  val attributes: List[RegisterAttr] = regDesc match {
    case Some(r) => for (m <- r.regMap) yield
      new RegisterAttr(offset = m.offset, typeRef = m.typeRef)
    case None => List(
      new RegisterAttr(offset = 0, typeRef = "NONE")
    )
  }

  val types: List[String] = regDesc match {
    case Some(r) => for (t <- r.regTypes) yield t.typeRef
    case None => List("Empty")
  }

  val fields: List[List[BitField]] = regDesc match {
    case Some(r) => for (t <- r.regTypes) yield t.fields
    case None => List(List(
      new BitField(bits = List(0,0),
        name = "NONE", mode = Some("RW"),
        resetVal = Some(0), comment = Some("No comment")
      )
    ))
  }
  // println(names)
  // println(offsets)
  // println(attributes)
  // println(types)
  // println(fields)

  val regMap = (names zip attributes).toMap
  val offNameMap = (offsets zip names).toMap
  val typeFieldMap = (types zip fields).toMap
  // println(regMap)
  // println(offNameMap)
  // println(typeFieldMap)

  // Determine number of (possible) registers from maximum offset used in the
  // register description
  val NUM_REGS = (offsets.max >> NUM_BITS_SHIFT) + 1
  val ADDR_W = log2Ceil(NUM_REGS * NUM_BYTE)
  val MAX_REGS = pow(2, ADDR_W).toInt >> NUM_BITS_SHIFT

  println(f"NUM_REGS = ${NUM_REGS}, ADDR_W = ${ADDR_W}, MAX_REGS = ${MAX_REGS}")

  val namesAndBits: List[RegisterBits] = regDesc match {
    case Some(r) => for (m <- r.regMap) yield
      new RegisterBits(name = m.name, fields = typeFieldMap(m.typeRef))
    case None => List(
      new RegisterBits(
        name = "NONE",
        fields = List(new BitField(bits = List(0,0),
          name = "NONE", mode = Some("RW"),
          resetVal = Some(0), comment = Some("No comment")
        ))
      )
    )
  }
  // println(namesAndBits)

  // Build ordered maps of different register bit field categories
  var rwRegBits = ListMap[String, Int]()
  var roRegBits = ListMap[String, Int]()
  var woRegBits = ListMap[String, Int]()
  var wcRegBits = ListMap[String, Int]()
  for (r <- namesAndBits) {
    for (f <- r.fields) {
      val width: Int = f.bits.head - f.bits.last + 1
      val name: String = r.name.toLowerCase + "_" + f.name.toLowerCase
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
  // println(rwRegBits)
  // println(roRegBits)
  // println(woRegBits)
  // println(wcRegBits)

  // NOTE suggestName doesn't actually work in IO Bundles yet
  val rwStaticCfgSignalList = rwRegBits.keys.toList
  val roSignalList = roRegBits.keys.toList
  val io = IO(new Bundle {
    val apb2T = new Apb2IO(ADDR_W, DATA_W)
    val rwVec = Output(MixedVec((rwRegBits map { case (k, v) => UInt(v.W).suggestName(k) }).toList))
    val roVec = Input(MixedVec((roRegBits map { case (k, v) => UInt(v.W).suggestName(k) }).toList))
    val woVec = Output(MixedVec((woRegBits map { case (k, v) => UInt(v.W).suggestName(k) }).toList))
    val wcVec = Input(MixedVec((wcRegBits map { case (k, v) => UInt(v.W).suggestName(k) }).toList))
  })

  // Build a 2D array of registers and their RegInit() bit fields with
  // JSON specified reset values and widths. No RegInit() for RESERVED
  // bit fields which are read-only zeros. Each RegInit() is part of a tuple
  // which also holds its position and its mode
  val regArr = Array.ofDim[ArrayBuffer[(UInt, Int, Int, String)]](MAX_REGS)

  for (i <- 0 until MAX_REGS) {
    regArr(i) = ArrayBuffer[(UInt, Int, Int, String)]()
    val offset = i << NUM_BITS_SHIFT
    if (offNameMap.contains(offset)) {
      for (f <- typeFieldMap(regMap(offNameMap(offset)).typeRef)) {
        if (f.name != "RESERVED") {
          val width: Int = f.bits.head - f.bits.last + 1
          val pos: Int = f.bits.last
          val regName: String = offNameMap(offset)
          if (f.mode == Some("RW")) {
            // For RW bit fields use reset value of 0 assumed if none specified
            println(f"Creating RW register bit field ${regName}.${f.name} @offset h${offset}%04x")
            f.resetVal match {
              case Some(value) => {
                regArr(i) += new Tuple4(RegInit(value.U(width.W)).suggestName(name.toLowerCase), pos, width, "RW")
              }
              case None => {
                regArr(i) += new Tuple4(RegInit(0.U(width.W)).suggestName(name.toLowerCase), pos, width, "N/A")
              }
            }
          }
          if (f.mode == Some("RO")) {
            // For RO bit fields declare Wires rather than registers
            println(f"Found RO register bit field ${regName}.${f.name} @offset h${offset}%04x")
            regArr(i) += new Tuple4(Wire(UInt(width.W)).suggestName(name.toLowerCase), pos, width, "RO")
          }
          if (f.mode == Some("WO")) {
            // For WO bit fields reset value of 0
            println(f"Creating WO register bit field ${regName}.${f.name} @offset h${offset}%04x")
            regArr(i) += new Tuple4(RegInit(0.U(width.W)).suggestName(name.toLowerCase), pos, width, "WO")
          }
          if (f.mode == Some("W1C")) {
            // For W1C bit fields reset value of 0
            println(f"Creating W1C register bit field ${regName}.${f.name} @offset h${offset}%04x")
            regArr(i) += new Tuple4(RegInit(0.U(width.W)).suggestName(name.toLowerCase), pos, width, "W1C")
          }
        }
      }
    } else {
      // No register defined at this offset (unmapped) - mark a not applicable
      // and create a single WireDefault() bit field with a value of 0
      println(f"No register @offset h${offset}%04x")
      regArr(i) += new Tuple4(WireDefault(0.U(DATA_W.W)), 0, DATA_W, "N/A")
    }
  }

  // Connect different register bit fields categories to MixedVec IOs
  // NOTE the Vecs are declared in same order as registers are declared
  // in JSON description therefore iterate over names and index regArr with
  // corresponding offset
  val rwIt = io.rwVec.iterator
  val roIt = io.roVec.iterator
  val woIt = io.woVec.iterator
  val wcIt = io.wcVec.iterator
  for (r <- names) {
    for (f <- regArr(regMap(r).offset >> NUM_BITS_SHIFT).iterator) {
      if (f._4 == "RW") {
        println(f"Connecting RW bit field to IO Bundle") // ${r}.${f.name}[${f.bits.head}%d:${f.bits.last}%d]
        rwIt.next := f._1
      }
      if (f._4 == "RO") {
        println(f"Connecting RO bit field to IO Bundle") // ${r}.${f.name}[${f.bits.head}%d:${f.bits.last}%d]
        f._1 := roIt.next
      }
      if (f._4 == "WO") {
        println(f"Connecting WO bit field to IO Bundle") // ${r}.${f.name}[${f.bits.head}%d:${f.bits.last}%d]
        woIt.next := f._1
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
          if (f._4 == "RW" || f._4 == "WO" || f._4 == "W1C") {
            // Write strobes: pStrb bits are used to mask or enable writes to individual
            // bytes of bit fields. However, if a bit field straddles two or more byte lanes
            // and not ALL the corresponding bits of pStrb are set then the bit field is not
            // written and pSlvErr is signalled.
            //
            // Following logic finds the write strobes that cover the bit field and ANDs them
            // b  | range | check
            // ---|-------|---------------------------------
            // 0  | 7:0   | pos < 8  && pos + width -  0 > 0
            // 1  | 15:8  | pos < 16 && pos + width -  8 > 0
            // 2  | 23:16 | pos < 24 && pos + width - 16 > 0
            // 3  | 31:24 | pos < 32 && pos + width - 24 > 0
            val fieldPStrbBits = for {
              b <- 0 until NUM_BYTE if (f._2 < ((b + 1) * 8)) && ((f._2 + f._3 - b * 8) > 0)
            } yield io.apb2T.req.pStrb(b)

            when (fieldPStrbBits.reduceLeft(_ & _)) {
              if (f._4 == "W1C") {
                // Write-1-to-clear: create a Bool Vec of the register bit field
                // and zip it with a Bool Vec of the write data clear each bit
                // of the bit field individually
                val nxtBits = VecInit(f._1.asBools)
                val clrBits = VecInit((io.apb2T.req.pWData >> f._2).asBools)
                for ((nxt, clr) <- (nxtBits zip clrBits).toMap) {
                  when (clr) {
                    nxt := false.B
                  }
                }
                f._1 := nxtBits.asUInt
              } else {
                // f._4 == "RW" || f._4 == "WO"
                f._1 := io.apb2T.req.pWData >> f._2
              }
            }.otherwise {
              pSlvErrFF := true.B
            }
          } else if (f._4 == "N/A") {
            pSlvErrFF := true.B
          }
        }
      }
    }
  }.otherwise {
    // Clear all WO register bit fields
    regArr.foreach(r => { r.foreach(f => { if (f._4 == "WO") f._1 := 0.U }) })

    // Update all W1C register bit fields
    regArr.foreach(r => {
      r.foreach(f => {
        if (f._4 == "W1C") {
          println(f"Connecting W1C bit field to IO Bundle") // ${r}.${f.name}[${f.bits.head}%d:${f.bits.last}%d]
          val nxtBits = VecInit(f._1.asBools)
          val setBits = VecInit(wcIt.next.asBools)
          for ((nxt, set) <- (nxtBits zip setBits).toMap) {
            when (set) {
              nxt := true.B
            }
          }
          f._1 := nxtBits.asUInt
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
        val shiftedBits: List[UInt] = for (f <- regArr(i).toList) yield (f._1 << f._2)
        pRDataFF := shiftedBits.reduceLeft(_ | _)
        for (f <- regArr(i)) {
          if (f._4 == "N/A") pSlvErrFF := true.B
        }
      }
    }
  }

  io.apb2T.rsp.pReady  := pReadyFF
  io.apb2T.rsp.pRData  := pRDataFF
  io.apb2T.rsp.pSlvErr := pSlvErrFF
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
  (new ChiselStage).execute(args, Seq(ChiselGeneratorAnnotation(() => new Apb2CSTrgt(32, "src/test/json/test.json"))))
}
