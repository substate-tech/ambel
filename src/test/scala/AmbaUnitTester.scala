// See README.md for license details.
package ambel

import java.io.File
import scala.math._
import scala.util.Random
import org.scalatest._
import chisel3._
import chisel3.util._
import chiseltest._

/** =AMBA Unit Tester Base Class=
  *
  * @todo use NUM_NIBBLE to format addr/data strings
  */
abstract class AmbelUnitTester(DATA_W: Int = 32) extends BaseUnitTester {
  val NUM_BYTE = DATA_W/8
  val NUM_NIBBLE = NUM_BYTE * 2

  /** =ApbXfer()=
    *
    * Runs basic APB protocol regardless of whether read or write
    *
    * @note clock.step() is required following the call to complete
    * transaction. Keeping this external to ApbXfer allows back-to-back
    * sequences to be tested.
    */
  def ApbXfer(t: Apb2IO, pclk: Clock) = {
    t.req.pSel.poke(true.B)
    pclk.step()
    t.req.pEnable.poke(true.B)
    do {
      pclk.step()
    } while (t.rsp.pReady.peek().litValue == 0)
    t.req.pSel.poke(false.B)
    t.req.pEnable.poke(false.B)
    t.req.pWrite.poke(false.B)
  }

  /** Following functions implement reads and writes using ApbXfer()
    */
  def ApbRead(t: Apb2IO, pclk: Clock, pAddr: Int): UInt = {
    val pAddrStr: String = f"h${pAddr}%08x"

    t.req.pAddr.poke(pAddrStr.U)
    t.req.pWData.poke(0.U)
    t.req.pWrite.poke(false.B)
    t.req.pStrb.poke(0.U)
    ApbXfer(t, pclk)
    t.rsp.pRData.peek()
    t.rsp.pSlvErr.peek()
  }

  def ApbReadExpect(t: Apb2IO, pclk: Clock, pAddr: Int, pRData: Int) = {
    val pAddrStr: String = f"h${pAddr}%08x"
    val pRDataStr: String = f"h${pRData}%08x"

    t.req.pAddr.poke(pAddrStr.U)
    t.req.pWData.poke(0.U)
    t.req.pWrite.poke(false.B)
    t.req.pStrb.poke(0.U)
    ApbXfer(t, pclk)
    t.rsp.pRData.expect(pRDataStr.U)
    t.rsp.pSlvErr.expect(false.B)
  }

  def ApbExpectSlvErr(t: Apb2IO) = {
    t.rsp.pSlvErr.expect(true.B)
  }

  def ApbWriteStrb(t: Apb2IO, pclk: Clock, pAddr: Int, pWData: Int, pStrb: Int) = {
    val pAddrStr: String = f"h${pAddr}%08x"
    val pWDataStr: String = f"h${pWData}%08x"

    t.req.pAddr.poke(pAddrStr.U)
    t.req.pWData.poke(pWDataStr.U)
    t.req.pWrite.poke(true.B)
    t.req.pStrb.poke(pStrb.U)
    ApbXfer(t, pclk)
  }
}
