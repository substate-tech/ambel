// See README.md for license details.
package ambel

import scala.collection.mutable.ListBuffer
import chisel3._
import chisel3.util._
import chiseltest._

/** =SimpleApb2T Unit Tester=
  * Run this Specification as follows...
  * From within sbt use:
  * {{{
  * testOnly ambel.SimpleApb2TUnitTester
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly ambel.SimpleApb2TUnitTester'
  * }}}
  * For VCD dump use:
  * {{{
  * testOnly ambel.SimpleApb2TUnitTester -- -DwriteVcd=1
  * }}}
  */
class SimpleApb2TUnitTester extends AmbaUnitTester {
  behavior of "SimpleApb2T"

  /**
    * Test cases
    *
    */
  val DATA_W = 32

  it should "write then read back SIMPLE_RW and check IO updates OK" in {
    test(new SimpleApb2T()).withAnnotations(annos) { dut =>
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

  it should "respond with PSLVERR on access to unmapped address" in {
    test(new SimpleApb2T()).withAnnotations(annos) { dut =>
      dut.clock.step(4)

      // Following address is out of range - only 2 registers mapped to lowest offsets (i.e. 0x0 and 0x4)
      val addr = 0x8
      val data = rand.nextInt & 0xff

      // Attempt write out of range - expect PSLVERR
      ApbWriteStrb(dut.io.apb2T, dut.clock, addr, data, 0xf)
      ApbExpectSlvErr(dut.io.apb2T)

      // Read and check reset value of RW register
      ApbReadExpect(dut.io.apb2T, dut.clock, 0x0, 0x0)

      // Attempt read out of range - expect PSLVERR
      ApbRead(dut.io.apb2T, dut.clock, addr)
      ApbExpectSlvErr(dut.io.apb2T)

      dut.clock.step(4)
    }
  }

}
