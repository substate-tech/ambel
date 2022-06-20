// See README.md for license details.
package ambel

import scala.collection.mutable.ListBuffer
import chisel3._
import chisel3.util._
import chiseltest._

/** =SimpleApb2CSTrgt Unit Tester=
  * Run this Specification as follows...
  * From within sbt use:
  * {{{
  * testOnly ambel.SimpleApb2CSTrgtUnitTester
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly ambel.SimpleApb2CSTrgtUnitTester'
  * }}}
  * For VCD dump use:
  * {{{
  * testOnly ambel.SimpleApb2CSTrgtUnitTester -- -DwriteVcd=1
  * }}}
  */
class SimpleApb2CSTrgtUnitTester extends AmbelUnitTester {
  behavior of "SimpleApb2CSTrgt"

  /**
    * Test cases
    *
    */
  val DATA_W = 32

  it should "write then read back SIMPLE_RW and check IO updates OK" in {
    test(new SimpleApb2CSTrgt()).withAnnotations(annos) { dut =>
      dut.clock.step(4)

      for (i <- 0 until 10) {
        val addr = 0
        val data = rand.nextInt & 0xff
        ApbWriteStrb(dut.io.apb2T, dut.clock, 0, data, 0xf)

        val dataStr = f"h${data}%08x"
        dut.io.rw.SimpleRw_RwBits.expect(dataStr.U)

        ApbReadExpect(dut.io.apb2T, dut.clock, addr, data)
      }

      dut.clock.step(4)
    }
  }

}
