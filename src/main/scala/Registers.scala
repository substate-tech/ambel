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

/** =RegisterBitLists=
  *
  * ListMaps of RW, RO, WO and WC register bits suitable for generation of Control/Status
  * register target IOs
  */
class RegisterBitLists() {

  var rwRegBits:ListMap[String, Int] = ListMap[String, Int]()
  var roRegBits:ListMap[String, Int] = ListMap[String, Int]()
  var woRegBits:ListMap[String, Int] = ListMap[String, Int]()
  var wcRegBits:ListMap[String, Int] = ListMap[String, Int]()

  def this(regDesc: Option[RegisterDesc], dataWidth: Int) {
    this()

    val types = regDesc match {
      case Some(r) => for (t <- r.regTypes) yield t.typeRef
      case None => List("Empty")
    }

    val fields = regDesc match {
      case Some(r) => for (t <- r.regTypes) yield t.fields
      case None => List(List(
        BitField(bits = List(0,0),
          name = "NONE", mode = Some("RW"),
          resetVal = Some(0), comment = Some("No comment")
        )
      ))
    }

    val typeFieldMap = (types zip fields).toMap
    val namesAndBits = regDesc match {
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

    for (r <- namesAndBits) {
      for (f <- r.fields) {
        val width = f.bits.head - f.bits.last + 1
        val name = r.name.toLowerCase + "_" + f.name.toLowerCase
        if (f.mode == Some("RW")) {
          this.rwRegBits += (name -> width)
        }
        if (f.mode == Some("RO")) {
          this.roRegBits += (name -> width)
        }
        if (f.mode == Some("WO")) {
          this.woRegBits += (name -> width)
        }
        if (f.mode == Some("W1C")) {
          this.wcRegBits += (name -> width)
        }
      }
    }
  }
}

/** =RegisterElements=
  *
  * Lists of separated register description elements ultimately used to build
  * an array of registers suitable for hardware generation
  *
  * @todo implement check that there are no spaces in register or regType names in JSON
  */
class RegisterElements() {

  var numByte: Int = 0
  var numBitsShift: Int = 0
  var numRegs: Int = 0
  var requiredWidth: Int = 0
  var maxRegs: Int = 0

  var names: List[String] = List("Empty")
  var offsets: List[Int] = List(0)
  var types: List[String] = List("Undefined")
  var widths: List[Int] = List(0)
  var attributes: List[RegisterAttr] = List(RegisterAttr(0, 0, "Undefined"))
  var fields: List[List[BitField]] = List(List(BitField(List(0,0), "Undefined")))

  var regMap:Map[String, RegisterAttr] = Map[String, RegisterAttr]()

  var regArray: Array[ArrayBuffer[BitFieldDetails]] = Array[ArrayBuffer[BitFieldDetails]]()

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

    // 3. Determine number of (possible) dataWidth registers from maximum offset
    // used in the register description
    this.numByte = dataWidth/8
    this.numBitsShift = log2Ceil(numByte) // Number of bits to shift right address to index registers
    this.numRegs = bytes >> numBitsShift
    this.requiredWidth = log2Ceil(numRegs * numByte)
    this.maxRegs = pow(2, requiredWidth).toInt >> numBitsShift

    // 4. Check that registers do not overlap
    for (i <- 0 until sorted.size - 1) {
      assert (sorted(i).offset + sorted(i).width / 8 <= sorted(i+1).offset)
    }

    val typeFieldMap = (types zip fields).toMap

    // Build a 2D array of all registers and their RegInit() bit fields with
    // JSON specified reset values and widths. No RegInit() for RESERVED
    // bit fields which are read-only zeros. Each RegInit() is part of a
    // class (BitFieldDetails) which also holds its position and its mode
    this.regArray = Array.ofDim[ArrayBuffer[BitFieldDetails]](this.maxRegs)

    this.regMap = (names zip attributes).toMap

    val offNameMap = (offsets zip names).toMap

    for (i <- 0 until this.maxRegs) {
      this.regArray(i) = ArrayBuffer[BitFieldDetails]()
      val offset = i << numBitsShift
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
              println(f"Creating RW register bit field ${regName}.${f.name} @offset h${offset}%04x")
              f.resetVal match {
                case Some(value) => {
                  this.regArray(i) += BitFieldDetails(RegInit(value.U(width.W)).suggestName(name), pos, width, "RW", name)
                }
                case None => {
                  this.regArray(i) += BitFieldDetails(RegInit(0.U(width.W)).suggestName(name), pos, width, "RW", name)
                }
              }
            }
            if (f.mode == Some("RO")) {
              // For RO bit fields declare Wires rather than registers
              println(f"Found RO register bit field ${regName}.${f.name} @offset h${offset}%04x")
              this.regArray(i) += BitFieldDetails(Wire(UInt(width.W)).suggestName(name), pos, width, "RO", name)
            }
            if (f.mode == Some("WO")) {
              // For WO bit fields reset value of 0
              println(f"Creating WO register bit field ${regName}.${f.name} @offset h${offset}%04x")
              this.regArray(i) += BitFieldDetails(RegInit(0.U(width.W)).suggestName(name), pos, width, "WO", name)
            }
            if (f.mode == Some("W1C")) {
              // For W1C bit fields reset value of 0
              println(f"Creating W1C register bit field ${regName}.${f.name} @offset h${offset}%04x")
              this.regArray(i) += BitFieldDetails(RegInit(0.U(width.W)).suggestName(name), pos, width, "W1C", name)
            }
          }
        }
      } else {
        // No register defined at this offset (unmapped) - mark a not applicable
        // and create a single WireDefault() bit field with a value of 0
        println(f"No register @offset h${offset}%04x")
        this.regArray(i) += BitFieldDetails(WireDefault(0.U(dataWidth.W)), 0, dataWidth, "N/A", "None")
      }
    }

  }

  def toCamelCase(s: String): String = {
    val pieces = s.split('_')
    val camelArr: Array[String] = for (p <- pieces) yield p.toLowerCase.capitalize
    val camel: String = camelArr.mkString
    return camel
  }
}
