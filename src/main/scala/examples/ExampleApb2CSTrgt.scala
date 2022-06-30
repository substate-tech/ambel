// See README.md for license details.
package ambel

import chisel3._
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}

/** =ExampleApb2CSTrgt=
  *
  * Example wrapper for Apb2CSTrgt Module, parameterized with src/main/json/Example.json
  * which connects the MixedVec IO on the parameterized Module to IO using pre-generated
  * and auto-generated Bundles from src/main/scala/examples/Example.scala
  *
  */
class ExampleApb2CSTrgt() extends Module {
  val ADDR_W = 32
  val DATA_W = 32

  val t = Module(new Apb2CSTrgt(
    ADDR_W = ADDR_W,
    DATA_W = DATA_W,
    REG_DESC_JSON = "src/main/json/Example.json",
    VERBOSE = true,
    GEN_BUNDLE = false))

  val io = IO(new Bundle {
    val apb2T = new Apb2IO(ADDR_W, DATA_W)
    val rw = Output(new _ExampleRwVec_)
    val ro = Input(new _ExampleRoVec_)
    val wo = Output(new _ExampleWoVec_)
    val wc = Input(new _ExampleWcVec_)
  })

  t.io.apb2T <> io.apb2T

  // Connect RW register outputs
  io.rw.AmbelCtrl_CoreReset        := t.io.rwVec(0)
  io.rw.AmbelFooBar_Foo            := t.io.rwVec(1)
  io.rw.AmbelFooBar_Bar            := t.io.rwVec(2)
  io.rw.AmbelBaz0_BazBits          := t.io.rwVec(3)
  io.rw.AmbelDebugCtrl_Halt        := t.io.rwVec(4)
  io.rw.AmbelBaz1_BazBits          := t.io.rwVec(5)
  io.rw.AmbelBigRegExample_BigBits := t.io.rwVec(6)

  // Connect RO register inputs
  t.io.roVec(0) := io.ro.AmbelRoExample_StatusBits

  // Connect WO register outputs
  io.wo.AmbelDebugCtrl_Step  := t.io.woVec(0)
  io.wo.AmbelWoGobits_GoBits := t.io.woVec(1)

  // Connect W1C register inputs
  t.io.wcVec(0) := io.wc.AmbelW1cStatus_StausBits
}

/** =Verilog generation boiler plate=
  *
  * Run this driver as follows...
  * From within sbt use:
  * {{{
  * runMain ambel.ExampleApb2CSTrgtDriver --target-dir src/main/verilog/examples --log-level info --log-file ExampleApb2CSTrgtDriver.log
  * }}}
  */
// $COVERAGE-OFF$
object ExampleApb2CSTrgtDriver extends App {
  (new ChiselStage).execute(args, Seq(ChiselGeneratorAnnotation(() => new ExampleApb2CSTrgt())))
}
