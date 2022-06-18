// See README.md for license details.
package ambel

import chisel3._
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}

/** =SimpleApb2CSTrgt=
  *
  * Simple wrapper for Apb2CSTrgt Module, parameterized with src/main/json/Simple.json
  * which connects the MixedVec IO on the parameterized Module to IO using pre-generated
  * and auto-generated Bundles from src/main/scala/examples/Simple.scala
  *
  */
class SimpleApb2CSTrgt() extends Module {
  val ADDR_W = 32
  val DATA_W = 32

  val t = Module(new Apb2CSTrgt(
    DATA_W = DATA_W,
    REG_DESC_JSON = "src/main/json/Simple.json",
    VERBOSE = true,
    GEN_BUNDLE = false))

  val io = IO(new Bundle {
    val apb2T = new Apb2IO(ADDR_W, DATA_W)
    val rw = Output(new _SimpleRwVec_)
    val ro = Input(new _SimpleRoVec_)
    val wo = Output(new _SimpleWoVec_)
  })

  t.io.apb2T <> io.apb2T

  // Connect RW bit-field outputs
  io.rw.SimpleRw_RwBits := t.io.rwVec(0)

  // Connect RO bit-field outputs
  t.io.roVec(0) := io.ro.SimpleRoWo_RoBits

  // Connect WO bit-field outputs
  io.wo.SimpleRoWo_WoBits := t.io.woVec(0)
}

/** =Verilog generation boiler plate=
  *
  * Run this driver as follows...
  * From within sbt use:
  * {{{
  * runMain ambel.SimpleApb2CSTrgtDriver --target-dir src/main/verilog/examples --log-level info --log-file SimpleApb2CSTrgtDriver.log
  * }}}
  */
// $COVERAGE-OFF$
object SimpleApb2CSTrgtDriver extends App {
  (new ChiselStage).execute(args, Seq(ChiselGeneratorAnnotation(() => new SimpleApb2CSTrgt())))
}
