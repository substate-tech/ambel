// See README.md for license details.
package ambel

import scala.collection.mutable.ListBuffer
import chisel3._
import chisel3.util._
import chiseltest._

/** =Apb2RegFile Unit Tester=
  * Run this Specification as follows...
  * From within sbt use:
  * {{{
  * testOnly ambel.Apb2RegFileUnitTester
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly ambel.Apb2RegFileUnitTester'
  * }}}
  * For VCD dump use:
  * {{{
  * testOnly ambel.Apb2RegFileUnitTester -- -DwriteVcd=1
  * }}}
  */
class Apb2RegFileUnitTester extends AmbelUnitTester {
  behavior of "Apb2RegFile"

  /**
    * Test cases
    */
  val NUM_REGS = 16
  val DATA_W = 32

  it should "write then read consecutive addresses back to back" in {
    test(new Apb2RegFile(NUM_REGS, DATA_W)) { dut =>
      dut.clock.step(4)

      for (i <- 0 until NUM_REGS) {
        val addr = i << 2
        val data = 0xff << i*8
        ApbWriteStrb(dut.io, dut.clock, addr, data, 0xf)
        ApbReadExpect(dut.io, dut.clock, addr, data)
      }

      dut.clock.step(4)
    }
  }

  it should "write all addresses in sequence then read all back" in {
    test(new Apb2RegFile(NUM_REGS, DATA_W)) { dut =>
      dut.clock.step(4)

      val dataSeq = new ListBuffer[Int]
      for (i <- 0 until NUM_REGS) {
        val addr = i << 2
        val data = rand.nextInt
        dataSeq += data
        ApbWriteStrb(dut.io, dut.clock, addr, data, 0xf)
      }

      dut.clock.step(2)

      val dataExp = dataSeq.toList
      for (i <- 0 until NUM_REGS) {
        val addr = i << 2
        ApbReadExpect(dut.io, dut.clock, addr, dataExp(i))
      }

      dut.clock.step(4)
    }
  }

  it should "test write strobes" in {
    test(new Apb2RegFile(NUM_REGS, DATA_W)) { dut =>
      dut.clock.step(4)

      val NUM_BYTE = dut.NUM_BYTE

      val addr = 0
      val data = 0xabbaface
      for (i <- 0 until NUM_REGS) {
        ApbWriteStrb(dut.io, dut.clock, addr, 0, 0xf)
        ApbReadExpect(dut.io, dut.clock, addr, 0)
        var dataExp = 0x0
        for (b <- 0 until NUM_BYTE) {
          dataExp = dataExp | data & (0xff << (b*8))
          ApbWriteStrb(dut.io, dut.clock, addr, data, (1 << b))
          ApbReadExpect(dut.io, dut.clock, addr, dataExp)
        }
      }

      dut.clock.step(4)
    }
  }
}
