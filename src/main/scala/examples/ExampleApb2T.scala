//
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

import chisel3._
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}

/** =Bundles for Connection to Apb2CSTrgt(REG_DESC_JSON="src/main/json/Example.json")=
  *
  * THIS IS AUTO-GENERATED CODE - DO NOT MODIFY BY HAND!
  */
class _ExampleApb2TRwVec_ extends Bundle {
  val AmbelCtrl_CoreReset = Bool()
  val AmbelFooBar_Foo = UInt(16.W)
  val AmbelFooBar_Bar = UInt(16.W)
  val AmbelBaz0_BazBits = UInt(32.W)
  val AmbelDebugCtrl_Halt = Bool()
  val AmbelBaz1_BazBits = UInt(32.W)
  val AmbelBigRegExample_BigBits = UInt(64.W)
}
class _ExampleApb2TRoVec_ extends Bundle {
  val AmbelRoExample_StatusBits = UInt(8.W)
}
class _ExampleApb2TWoVec_ extends Bundle {
  val AmbelDebugCtrl_Step = Bool()
  val AmbelWoGobits_GoBits = UInt(8.W)
}
class _ExampleApb2TWcVec_ extends Bundle {
  val AmbelW1cStatus_StausBits = UInt(16.W)
}

/** =Wrapper Module for Apb2CSTrgt(REG_DESC_JSON="src/main/json/Example.json")=
  * Uses Bundles above on IO and makes ordered connection to MixedVec IO on
  * Apb2CSTrgt instance
  *
  * THIS IS AUTO-GENERATED CODE - DO NOT MODIFY BY HAND!
  */
class ExampleApb2T() extends Module {
  val ADDR_W = 32
  val DATA_W = 32

  val t = Module(new Apb2CSTrgt(
    ADDR_W = ADDR_W,
    DATA_W = DATA_W,
    REG_DESC_JSON = "src/main/json/Example.json"))

  val io = IO(new Bundle {
    val apb2T = new Apb2IO(ADDR_W, DATA_W)
    val rw = Output(new _ExampleApb2TRwVec_)
    val ro = Input(new _ExampleApb2TRoVec_)
    val wo = Output(new _ExampleApb2TWoVec_)
    val wc = Input(new _ExampleApb2TWcVec_)
  })

  // Connect APB2 target interface
  t.io.apb2T <> io.apb2T

  // Connect RW bit-field outputs
  io.rw.AmbelCtrl_CoreReset := t.io.rwVec(0)
  io.rw.AmbelFooBar_Foo := t.io.rwVec(1)
  io.rw.AmbelFooBar_Bar := t.io.rwVec(2)
  io.rw.AmbelBaz0_BazBits := t.io.rwVec(3)
  io.rw.AmbelDebugCtrl_Halt := t.io.rwVec(4)
  io.rw.AmbelBaz1_BazBits := t.io.rwVec(5)
  io.rw.AmbelBigRegExample_BigBits := t.io.rwVec(6)

  // Connect RO bit-field inputs
  t.io.roVec(0) := io.ro.AmbelRoExample_StatusBits

  // Connect WO bit-field Outputs
  io.wo.AmbelDebugCtrl_Step := t.io.woVec(0)
  io.wo.AmbelWoGobits_GoBits := t.io.woVec(1)

  // Connect W1C bit-field Inputs
  t.io.wcVec(0) := io.wc.AmbelW1cStatus_StausBits
}

object ExampleApb2TDriver extends App {
  (new ChiselStage).execute(args, Seq(ChiselGeneratorAnnotation(() => new ExampleApb2T())))
}
