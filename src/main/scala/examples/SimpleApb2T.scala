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
import chisel3.stage.ChiselGeneratorAnnotation
import circt.stage.ChiselStage

/** =Bundles for Connection to Apb2CSTrgt(REG_DESC_JSON="src/main/json/Simple.json")=
  *
  * THIS IS AUTO-GENERATED CODE - DO NOT MODIFY BY HAND!
  */
class _SimpleApb2TRwVec_ extends Bundle {
  val SimpleRw_RwBits = UInt(8.W)
}
class _SimpleApb2TRoVec_ extends Bundle {
  val SimpleRoWo_RoBits = UInt(8.W)
}
class _SimpleApb2TWoVec_ extends Bundle {
  val SimpleRoWo_WoBits = UInt(8.W)
}

/** =Wrapper Module for Apb2CSTrgt(REG_DESC_JSON="src/main/json/Simple.json")=
  * Uses Bundles above on IO and makes ordered connection to MixedVec IO on
  * Apb2CSTrgt instance
  *
  * THIS IS AUTO-GENERATED CODE - DO NOT MODIFY BY HAND!
  */
class SimpleApb2T() extends Module {
  val ADDR_W = 32
  val DATA_W = 32

  val t = Module(new Apb2CSTrgt(
    ADDR_W = ADDR_W,
    DATA_W = DATA_W,
    REG_DESC_JSON = "src/main/json/Simple.json"))

  val io = IO(new Bundle {
    val apb2T = new Apb2IO(ADDR_W, DATA_W)
    val rw = Output(new _SimpleApb2TRwVec_)
    val ro = Input(new _SimpleApb2TRoVec_)
    val wo = Output(new _SimpleApb2TWoVec_)
  })

  // Connect APB2 target interface
  t.io.apb2T <> io.apb2T

  // Connect RW bit-field outputs
  io.rw.SimpleRw_RwBits := t.io.rwVec(0)

  // Connect RO bit-field inputs
  t.io.roVec(0) := io.ro.SimpleRoWo_RoBits

  // Connect WO bit-field Outputs
  io.wo.SimpleRoWo_WoBits := t.io.woVec(0)
}

object SimpleApb2TDriver extends App {
  (new ChiselStage).execute(args, Seq(ChiselGeneratorAnnotation(() => new SimpleApb2T())))
}
