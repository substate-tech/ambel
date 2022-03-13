// See README.md for license details.
package ambel

import chisel3._

/** =Bundles for Connection to Apb2CSTrgt(REG_DESC_JSON="src/main/json/Example.json")
  *
  * THIS IS AUTO-GENERATED CODE - DO NOT MODIFY BY HAND!
  */
class _ExampleRwVec_ extends Bundle {
  val AmbelCtrl_CoreReset = Bool()
  val AmbelFooBar_Foo = UInt(16.W)
  val AmbelFooBar_Bar = UInt(16.W)
  val AmbelBaz0_BazBits = UInt(32.W)
  val AmbelDebugCtrl_Halt = Bool()
  val AmbelBaz1_BazBits = UInt(32.W)
  val AmbelBigRegExample_BigBits = UInt(64.W)
}
class _ExampleRoVec_ extends Bundle {
  val AmbelRoExample_StatusBits = UInt(8.W)
}
class _ExampleWoVec_ extends Bundle {
  val AmbelDebugCtrl_Step = Bool()
  val AmbelWoGobits_GoBits = UInt(8.W)
}
class _ExampleWcVec_ extends Bundle {
  val AmbelW1cStatus_StausBits = UInt(16.W)
}
