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

import scala.io.Source
import chisel3._
import chisel3.util._
import chisel3.experimental.BundleLiterals._
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}

/** =Apb2CSTrgt=
  *
  * AMBA APB2 protocol Control/Status register target (slave) generator
  *
  * @param ADDR_W the width of the address bus in bits
  * @param DATA_W the width of the data bus in bits
  * @param REG_DESC_JSON the path to the register description JSON file
  * @param GEN_MODULE enables generation of a wrapper Module which uses generated
  *        Bundles suitable for connection to the generated MixedVec IOs. The signal
  *        names used in the Bundles match their corresponding register and bit field
  *        names, as specified in the JSON. They are declared in the same order as
  *        the entries of the corresponding MixedVec and connected in order
  * @param VERBOSE enables verbose output during generation
  *
  * @todo implement pProt
  */
class Apb2CSTrgt(
  ADDR_W: Int = 32,
  DATA_W: Int = 32,
  REG_DESC_JSON: String,
  GEN_MODULE: Boolean = false,
  VERBOSE: Boolean = false) extends Module {

  // Parse RW, RO, WO and WC register bits from register description JSON to generate required IO
  val RegDescDecoder = new RegisterDescDecoder
  val jsonString = Source.fromFile(REG_DESC_JSON).getLines.mkString.stripMargin
  val regDesc: Option[RegisterDesc] = RegDescDecoder.getReg(Array(jsonString))

  regDesc match {
    case Some(regs) => {
      RegDescDecoder.prettyPrint(regs)
    }
    case None =>
  }

  val regBits = new RegisterBitLists(regDesc, DATA_W)

  // NOTE suggestName doesn't actually work in IO Bundles. Unclear whether it ever will, but if
  // it was possible we'd use it here, negating the requirement for the GEN_MODULE functionality
  val io = IO(new Bundle {
    val apb2T = new Apb2IO(ADDR_W, DATA_W)
    val rwVec = Output(MixedVec((regBits.rwRegBits map { case (k, v) => UInt(v.W).suggestName(k) }).toList))
    val roVec = Input(MixedVec((regBits.roRegBits map { case (k, v) => UInt(v.W).suggestName(k) }).toList))
    val woVec = Output(MixedVec((regBits.woRegBits map { case (k, v) => UInt(v.W).suggestName(k) }).toList))
    val wcVec = Input(MixedVec((regBits.wcRegBits map { case (k, v) => UInt(v.W).suggestName(k) }).toList))
  })

  // Instantiate and parameterize generic control and status register target
  val t = Module(new GenCSTrgt(
    ADDR_W, DATA_W,
    REG_DESC_JSON,
    GEN_MODULE,
    VERBOSE))

  val pAddrFF   = RegInit(0.U)
  val pWriteFF  = RegInit(false.B)
  val pStrbFF   = RegInit(0.U)
  val pReadyFF  = RegInit(true.B)
  val pRDataFF  = RegInit(0.U)
  val pSlvErrFF = RegInit(false.B)

  val NUM_BYTE = t.NUM_BYTE
  val NUM_BITS_SHIFT = t.NUM_BITS_SHIFT
  val NUM_REGS = t.NUM_REGS
  val REQD_W = t.REQD_W

  // APB protocol: access detect
  when (io.apb2T.req.pSel & !io.apb2T.req.pEnable) {
    // Capture address bits required to index defined registers
    pAddrFF  := io.apb2T.req.pAddr
    pWriteFF := io.apb2T.req.pWrite
    pStrbFF  := io.apb2T.req.pStrb
    pReadyFF := io.apb2T.req.pWrite // Always one wait state for reads
  }.otherwise {
    //pAddrFF  := 0.U
    pReadyFF := true.B
    pWriteFF := false.B
  }

  // Decode address to index registers
  t.io.addr := pAddrFF

  // Write
  t.io.write     := pWriteFF
  t.io.writeStrb := pStrbFF
  t.io.writeData := io.apb2T.req.pWData

  // Read (data ready in next cycle)
  t.io.read := !pReadyFF

  // Respond
  io.apb2T.rsp.pReady  := pReadyFF
  io.apb2T.rsp.pRData  := t.io.readData
  io.apb2T.rsp.pSlvErr := t.io.error

  io.rwVec   := t.io.rwVec
  t.io.roVec := io.roVec
  io.woVec   := t.io.woVec
  t.io.wcVec := io.wcVec
}

/** =Verilog generation boiler plate=
  *
  * Run this driver as follows...
  * From within sbt use:
  * {{{
  * runMain ambel.Apb2CSTrgtDriver --target-dir src/main/verilog --log-level info --log-file Apb2CSTrgtDriver.log
  * }}}
  */
// $COVERAGE-OFF$
object Apb2CSTrgtDriver extends App {
  (new ChiselStage).execute(args, Seq(ChiselGeneratorAnnotation(() => new Apb2CSTrgt(32, 32, "src/main/json/Simple.json", true, true))))
}
