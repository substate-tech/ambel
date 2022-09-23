// See README.md for license details.
package ambel

import chisel3._

/** =Bundles for Connection to Apb2CSTrgt(REG_DESC_JSON="src/test/json/MisalignedRWRegs.json")=
  *
  * THIS IS AUTO-GENERATED CODE - DO NOT MODIFY BY HAND!
  */
class _MisalignedRWRegsRwVec_ extends Bundle {
  val RegZero_Nibble = UInt(4.W)
  val RegZero_MisalignedByte0 = UInt(8.W)
  val RegZero_MisalignedByte1 = UInt(8.W)
  val RegZero_RestOfBits = UInt(12.W)
  val RegTwo_ThreeByteBitfield = UInt(24.W)
  val RegTwo_TopByte = UInt(8.W)
  val RegThree_Nibble = UInt(4.W)
  val RegThree_MisalignedByte0 = UInt(8.W)
  val RegThree_MisalignedByte1 = UInt(8.W)
  val RegThree_RestOfBits = UInt(12.W)
  val RegFour_ThreeByteBitfield = UInt(24.W)
  val RegFour_TopByte = UInt(8.W)
}
