// See README.md for license details.
package ambel

import chisel3._

/** =Bundles for Connection to Apb2CSTrgt(REG_DESC_JSON="src/main/json/Simple.json")
  *
  * THIS IS AUTO-GENERATED CODE - DO NOT MODIFY BY HAND!
  */
class _SimpleRwVec_ extends Bundle {
  val SimpleRw_RwBits = UInt(8.W)
}
class _SimpleRoVec_ extends Bundle {
  val SimpleRoWo_RoBits = UInt(8.W)
}
class _SimpleWoVec_ extends Bundle {
  val SimpleRoWo_WoBits = UInt(8.W)
}
