// See README.md for license details.
package ambel

import chisel3._

/** =Bundles for Connection to Apb2CSTrgt(REG_DESC_JSON="src/test/json/32BitRwRegs.json")
  *
  * THIS IS AUTO-GENERATED CODE - DO NOT MODIFY BY HAND!
  */
class _32BitRwRegsRwVec_ extends Bundle {
  val RegZero_Word = UInt(32.W)
  val RegOne_LowerHalf = UInt(16.W)
  val RegOne_UpperHalf = UInt(16.W)
  val RegTwo_ByteZero = UInt(8.W)
  val RegTwo_ByteOne = UInt(8.W)
  val RegTwo_ByteTwo = UInt(8.W)
  val RegTwo_ByteThree = UInt(8.W)
  val RegThree_ByteZero = UInt(8.W)
  val RegThree_ByteOne = UInt(8.W)
  val RegThree_ByteTwo = UInt(8.W)
  val RegThree_ByteThree = UInt(8.W)
}
