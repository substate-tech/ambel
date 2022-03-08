// See README.md for license details.
package ambel

import chisel3._
import chisel3.util.log2Ceil
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}

/** =Apb2RegFile=
  *
  * APB2 target implementing bank of 4x 32 bit (by default) read/write registers
  *
  * @param NUM_REGS number of registers
  * @param DATA_W width of registers in bits
  */
class Apb2RegFile(NUM_REGS: Int = 4, DATA_W: Int = 32) extends Module {
  val NUM_BYTE = DATA_W/8
  val ADDR_W = log2Ceil(NUM_REGS * NUM_BYTE)
  val NUM_BITS_SHIFT = log2Ceil(NUM_BYTE) // Number of bits to shift right address to index registers

  val io = IO(new Bundle {
    val apb2T = new Apb2IO(ADDR_W, DATA_W)
  })

  // Boiler plating to create [NUM_REGS][NUM_BYTE] array of initialized registers
  val regBankFF = RegInit(VecInit(Seq.fill(NUM_REGS)(VecInit(Seq.fill(NUM_BYTE)(0.U(8.W))))))

  val pAddrFF   = RegInit(0.U(ADDR_W.W))
  val pWriteFF  = RegInit(false.B)
  val pReadyFF  = RegInit(true.B)
  val pRDataFF  = RegInit(0.U(DATA_W.W))
  val pSlvErrFF = RegInit(false.B)

  val regIndex  = Wire(UInt((ADDR_W - NUM_BITS_SHIFT).W))
  val decodeOK  = Wire(Bool())

  // Access detect
  when (io.apb2T.req.pSel & !io.apb2T.req.pEnable) {
    pAddrFF  :=  io.apb2T.req.pAddr
    pWriteFF :=  io.apb2T.req.pWrite
    pReadyFF :=  io.apb2T.req.pWrite // One wait state for reads, none for writes

    // Debug
    //printf("Access detected:\n")
    //printf("  pAddr = 0x%x, pWrite = %b, pStrb = %b, pWData = 0x%x\n", io.apb2T.pAddr, io.apb2T.pWrite, io.apb2T.pStrb, io.apb2T.pWData)
  } .otherwise {
    pWriteFF := false.B
  }

  regIndex := pAddrFF >> NUM_BITS_SHIFT
  decodeOK := regIndex >= 0.U && regIndex < NUM_REGS.U

  when (pWriteFF) {
    // Write process
    pWriteFF  := false.B
    pSlvErrFF := true.B

    when (decodeOK) {
      pSlvErrFF := false.B
      // Iterate over pStrb bits, update bytes of selected register
      for ((bit, n) <- io.apb2T.req.pStrb.asBools.zipWithIndex) {
        when (bit) {
          regBankFF(regIndex)(n) := io.apb2T.req.pWData(n*8+7, n*8)
        }
      }
    }
  } .elsewhen (!pReadyFF) {
    // Read process
    pSlvErrFF := true.B
    pReadyFF  := true.B
    when (decodeOK) {
      pSlvErrFF := false.B
      pRDataFF  := regBankFF(regIndex).asUInt
    }
  }

  io.apb2T.rsp.pReady  := pReadyFF
  io.apb2T.rsp.pRData  := pRDataFF
  io.apb2T.rsp.pSlvErr := pSlvErrFF
}


/** =Verilog generation boiler plate=
  *
  * Run this driver as follows...
  * From within sbt use:
  * {{{
  * runMain ambel.Apb2RegFileDriver --target-dir src/main/verilog --log-level info --log-file Apb2RegFileDriver.log
  * }}}
  */
// $COVERAGE-OFF$
object Apb2RegFileDriver extends App {
  (new ChiselStage).execute(args, Seq(ChiselGeneratorAnnotation(() => new Apb2RegFile(8, 32))))
}
// $COVERAGE-OFF$
