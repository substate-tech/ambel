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

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map
import scala.collection.immutable.ListMap
import scala.math.pow
import chisel3._
import chisel3.util._
import io.circe._, io.circe.parser._
import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.auto._

/** =Register=
  */
case class Register(offset: Int, name: String, typeRef: String, comment: Option[String] = None)

/** =BitField=
  */
case class BitField(bits: List[Int], name: String, mode: Option[String] = None, resetVal: Option[Int] = None, comment: Option[String] = None)

/** =RegisterType=
  *
  * Register type: type reference string, register width in bits and List of BitFields,
  * optional description comment
  */
case class RegisterType(typeRef: String, width: Int, fields: List[BitField], comment: Option[String] = None)

/** =RegisterDesc=
  *
  * Register description: Register map List and register type List
  */
case class RegisterDesc(regMap: List[Register], regTypes: List[RegisterType])

/** =RegisterAttr=
  */
case class RegisterAttr(offset: Int, width: Int, typeRef: String)

/** =RegisterAttr=
  *
  * Register attributes: Name and BitField List
  */
case class RegisterBits(name: String, fields: List[BitField])

/** =BitFieldDetails=
  *
  * class holding the actual bits (i.e. hardware) for a single bitfield of a register
  * along with its position, width, mode and name
  */
case class BitFieldDetails(reg: UInt, pos: Int, width: Int, mode: String, name: String)

class RegisterDescDecoder {
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

  def prettyPrint(r: Register) {
    print(f"h${r.offset}%04x: ${r.name}  (${r.typeRef})\n")
  }

  def prettyPrint(f: BitField) {
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

  def prettyPrint(t: RegisterType) {
    print(f"${t.typeRef}, ${t.width} bits")
    t.comment match {
      case Some(comment) => print(f"  (${comment})\n")
      case None => print(f"\n")
    }
    t.fields.map(prettyPrint)
  }

  def prettyPrint(m: RegisterDesc) {
    m.regMap.map(prettyPrint)
    m.regTypes.map(prettyPrint)
  }
}

/** =RegisterElements=
  *
  * Lists of separated register description elements
  */
class RegisterElements() {

  var NUM_REGS: Int = 0
  var REQD_W: Int = 0
  var MAX_REGS: Int = 0

  var names: List[String] = List("Empty")
  var offsets: List[Int] = List(0)
  var types: List[String] = List("Undefined")
  var widths: List[Int] = List(0)
  var attributes: List[RegisterAttr] = List(RegisterAttr(0, 0, "Undefined"))
  var fields: List[List[BitField]] = List(List(BitField(List(0,0), "Undefined")))
  var namesAndBits: List[RegisterBits] = List(RegisterBits("None", List(BitField(List(0,0), "Undefined"))))

  def this(regDesc: Option[RegisterDesc], dataWidth: Int) {
    this()
    this.names = regDesc match {
      case Some(r) => for (m <- r.regMap) yield m.name
      case None => List("Empty")
    }

    this.offsets = regDesc match {
      case Some(r) => for (m <- r.regMap) yield m.offset
      case None => List(0)
    }

    this.types = regDesc match {
      case Some(r) => for (t <- r.regTypes) yield t.typeRef
      case None => List("Empty")
    }

    this.widths = regDesc match {
      case Some(r) => for (t <- r.regTypes) yield t.width
      case None => List(0)
    }

    val typeWidthMap = (types zip widths).toMap

    this.attributes = regDesc match {
      case Some(r) => for (m <- r.regMap) yield {
        RegisterAttr(offset = m.offset, typeWidthMap(m.typeRef), typeRef = m.typeRef)
      }
      case None => List(
        RegisterAttr(offset = 0, width = 0, typeRef = "NONE")
      )
    }

    this.fields = regDesc match {
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

    // 2. Check alignment: offset should be on a width / 8 boundary
    var bytes: Int = 0
    for (r <- attributes) {
      assert ((r.offset % (r.width / 8)) == 0)
      bytes += r.width / 8
    }

    // 3. Determine number of (possible) DATA_W registers from maximum offset
    // used in the register description
    val NUM_BYTE = dataWidth/8
    val NUM_BITS_SHIFT = log2Ceil(NUM_BYTE) // Number of bits to shift right address to index registers

    this.NUM_REGS = bytes >> NUM_BITS_SHIFT
    this.REQD_W = log2Ceil(NUM_REGS * NUM_BYTE)
    this.MAX_REGS = pow(2, REQD_W).toInt >> NUM_BITS_SHIFT

    // 4. Check that registers do not overlap
    for (i <- 0 until sorted.size - 1) {
      assert (sorted(i).offset + sorted(i).width / 8 <= sorted(i+1).offset)
    }

    val typeFieldMap = (types zip fields).toMap

    namesAndBits = regDesc match {
      case Some(r) => for (m <- r.regMap) yield {
        RegisterBits(name = m.name, fields = typeFieldMap(m.typeRef))
      }
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

  }
}

/** =GenCSTrgt=
  *
  * Generic (bus protocol agnostic) Control/Status register target generator
  *
  * @param DATA_W the width of the data bus in bits
  * @param MAX_REGS the maximum number of registers that can be indexed
  * @param NAMES List of register names
  * @param INDEX_MAP Map of numeric register indices, indexed by name
  * @param RW_FIELD_MAP ordered map of RW bit field names and widths
  * @param RO_FIELD_MAP ordered map of RO bit field names and widths
  * @param WO_FIELD_MAP ordered map of WO bit field names and widths
  * @param WC_FIELD_MAP ordered map of WC bit field names and widths
  * @param REG_ARR register description array, may be indexed by INDEX_MAP(name)
  * @param VERBOSE enables verbose output during generation
  * @param GEN_MODULE enables generation of a wrapper Module which uses generated
  * Bundles suitable for connection to the generated MixedVec IOs. The signal names
  * used in the Bundles match their corresponding register and bit field names, as
  * specified in the JSON. They are declared in the same order as the entries of
  * the corresponding MixedVecs and connected in order
  */
class GenCSTrgt(
  DATA_W: Int,
  MAX_REGS: Int,
  NAMES: List[String],
  INDEX_MAP: Map[String, Int],
  RW_FIELD_MAP: ListMap[String, Int],
  RO_FIELD_MAP: ListMap[String, Int],
  WO_FIELD_MAP: ListMap[String, Int],
  WC_FIELD_MAP: ListMap[String, Int],
  REG_ARR: Array[ArrayBuffer[BitFieldDetails]],
  VERBOSE: Boolean = false) extends Module {
  assert(DATA_W <= 32)
  assert(DATA_W % 8 == 0)

  val INDEX_W = log2Ceil(MAX_REGS)
  val NUM_BYTE = DATA_W/8

  val io = IO(new Bundle {
    val index     = Input(UInt(INDEX_W.W))
    val write     = Input(Bool())
    val writeStrb = Input(UInt(NUM_BYTE.W))
    val writeData = Input(UInt(DATA_W.W))
    val read      = Input(Bool())
    val readData  = Output(UInt(DATA_W.W))
    val error     = Output(Bool())
    // NOTE suggestName doesn't actually work in IO Bundles. unclear whether it ever will, but if
    // it was possible we'd use it here, negating the requirement for the GEN_MODULE functionality
    val rwVec     = Output(MixedVec((RW_FIELD_MAP map { case (k, v) => UInt(v.W).suggestName(k) }).toList))
    val roVec     = Input(MixedVec((RO_FIELD_MAP map { case (k, v) => UInt(v.W).suggestName(k) }).toList))
    val woVec     = Output(MixedVec((WO_FIELD_MAP map { case (k, v) => UInt(v.W).suggestName(k) }).toList))
    val wcVec     = Input(MixedVec((WC_FIELD_MAP map { case (k, v) => UInt(v.W).suggestName(k) }).toList))
  })

  // Connect different register bit field categories to MixedVec IOs
  // NOTE the Vecs are declared in same order as registers are declared
  // therefore we can iterate over names and index REG_ARR accordingly
  val rwIt = io.rwVec.iterator
  val roIt = io.roVec.iterator
  val woIt = io.woVec.iterator
  val wcIt = io.wcVec.iterator

  for (r <- NAMES) {
    for (f <- REG_ARR(INDEX_MAP(r)).iterator) {
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

  val errorFF = RegInit(false.B)

  when (io.write) {
    // Write process
    errorFF := false.B

    for (i <- 0 until MAX_REGS) {
      when (io.index === i.U) {
        // Write data to the individual writable bit fields of the
        // register and signal error if the register is unmapped
        for (f <- REG_ARR(i)) {
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
              // ALL bits of pStrb covering the bit field are set
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
              errorFF := true.B
            }
          } else if (f.mode == "N/A") {
            errorFF := true.B
          }
        }
      }
    }
  }.otherwise {
    // Clear all WO register bit fields
    REG_ARR.foreach(r => { r.foreach(f => { if (f.mode == "WO") f.reg := 0.U }) })

    // Update all W1C register bit fields
    REG_ARR.foreach(r => {
      r.foreach(f => {
        if (f.mode == "W1C") {
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

  val readDataFF = RegInit(0.U(DATA_W.W))

  when (io.read) {
    // Read process
    readDataFF := 0.U

    for (i <- 0 until MAX_REGS) {
      when (io.index === i.U) {
        // Shift the individual bit fields of the indexed register into position and OR
        // to be read together and signal error if the register is unmapped
        val shiftedBits: List[UInt] = for (f <- REG_ARR(i).toList) yield (f.reg << f.pos)
        readDataFF := shiftedBits.reduceLeft(_ | _)
        for (f <- REG_ARR(i)) {
          if (f.mode == "N/A") errorFF := true.B
        }
      }
    }
  }

  io.readData := readDataFF
  io.error := errorFF
}
