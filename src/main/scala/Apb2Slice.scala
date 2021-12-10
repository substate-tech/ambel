// See README.md for license details.
package ambel

import chisel3._
import chisel3.util.isPow2
import chisel3.util.log2Ceil
import chisel3.experimental.BundleLiterals._
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}

/** =APB2 Slice=
  *
  * APB2 bus register slice for pipelining/timing closure.
  *
  * @param addrWidth the width of the APB address bus
  * @param dataWidth the width of the APB data bus
  */
class Apb2Slice(
  val addrWidth: Int = 32, val dataWidth: Int = 32) extends Module {
  assert(dataWidth % 8 == 0)
  assert(addrWidth > log2Ceil(dataWidth/8))
  val strbWidth = dataWidth/8

  val io = IO(new Bundle {
    val apb2t =         new Apb2IO(addrWidth, dataWidth)
    val apb2i = Flipped(new Apb2IO(addrWidth, dataWidth))
  })

  // Bundle-literal initialized Registers of Bundles (implict initialization to zero/false)
  // NOTE wrapping Bundle declaration in WireInit is a work-around for https://github.com/chipsalliance/chisel3/issues/1671
  val reqFF = RegInit(WireInit(new Apb2Req(addrWidth, dataWidth).Lit()))
  val rspFF = RegInit(WireInit(new Apb2Rsp(addrWidth, dataWidth).Lit(_.pReady -> true.B)))

  // Slice works by registering initiator request, forwarding to target, and extending
  // with wait states until targets responds
  //          __    __    __    __    __    __    __
  // clock __|  |__|  |__|  |__|  |__|  |__|  |__|  |__
  //                      _______________________
  // io.apb2t.req.pSel __|                       |_____
  //                            _________________
  // io.apb2t.req.pEnable _____|                 |_____
  //                                        ___________
  // io.apb2t.rsp.pReady XXXXXX|___________|
  //                            ___________
  //       reqFF.pSel _________|           |___________
  //                                  _____
  //       reqFF.pEnable ____________|     |___________
  //                                  _____
  // io.apb2i.rsp.pReady XXXXXXXXXXXX|     |XXXXXXXXXXX
  //                                        ___________
  //        rspFF.pReady XXXXXX|___________|
  //
  when (io.apb2t.req.pSel) {
    when (!io.apb2t.req.pEnable) {
      reqFF.pSel  := true.B
      reqFF.pAddr := io.apb2t.req.pAddr
      reqFF.pProt := io.apb2t.req.pProt

      when (io.apb2t.req.pWrite) {
        reqFF.pWrite := io.apb2t.req.pWrite
        reqFF.pWData := io.apb2t.req.pWData
        reqFF.pStrb  := io.apb2t.req.pStrb
      }.otherwise {
        reqFF.pWrite := false.B
      }
    }.elsewhen (io.apb2i.rsp.pReady & reqFF.pEnable) {
      reqFF.pSel := false.B
    }
  }

  when (reqFF.pSel) {
    when (!reqFF.pEnable) {
      reqFF.pEnable := true.B
      rspFF.pReady  := false.B
      rspFF.pSlvErr := false.B
    }.elsewhen (io.apb2i.rsp.pReady) {
      reqFF.pEnable := false.B
      rspFF.pReady  := true.B
      rspFF.pRData  := io.apb2i.rsp.pRData
      rspFF.pSlvErr := io.apb2i.rsp.pSlvErr
    }
  }

  io.apb2t.rsp.pReady  := rspFF.pReady
  io.apb2t.rsp.pRData  := rspFF.pRData
  io.apb2t.rsp.pSlvErr := rspFF.pSlvErr

  io.apb2i.req.pAddr   := reqFF.pAddr
  io.apb2i.req.pProt   := reqFF.pProt
  io.apb2i.req.pSel    := reqFF.pSel
  io.apb2i.req.pEnable := reqFF.pEnable
  io.apb2i.req.pWrite  := reqFF.pWrite
  io.apb2i.req.pWData  := reqFF.pWData
  io.apb2i.req.pStrb   := reqFF.pStrb
}

/** =Verilog generation boiler plate=
  *
  * Run this driver as follows...
  * From within sbt use:
  * {{{
  * runMain ambel.Apb2SliceDriver --target-dir src/main/verilog --log-level info --log-file Apb2SliceDriver.log
  * }}}
  */
// $COVERAGE-OFF$
object Apb2SliceDriver extends App {
  (new ChiselStage).execute(args, Seq(ChiselGeneratorAnnotation(() => new Apb2Slice())))
}
// $COVERAGE-OFF$
