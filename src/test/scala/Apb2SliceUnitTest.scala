// See README.md for license details.
package ambel

import scala.collection.mutable.ListBuffer
import chisel3._
import chisel3.util._
import chiseltest._

/** =Apb2Slice Test Harness=
  *
  * Connects Apb2Slice DUT to Apb2RegFile instance
  */
class Apb2SliceTestHarness extends Module {
  val dataWidth = 32
  val nByte = dataWidth/8
  val nRegs = 4
  val addrWidth = log2Ceil(nRegs * nByte)
  val io = IO(new Apb2IO(addrWidth, dataWidth))

  val Apb2Slice_i = Module(new Apb2Slice(addrWidth, dataWidth))
  val Apb2Trgt_i = Module(new Apb2RegFile(nRegs, dataWidth))

  Apb2Slice_i.io.apb2t <> io
  Apb2Slice_i.io.apb2i <> Apb2Trgt_i.io.apb2T
}

/** =Apb2Slice Unit Tester=
  * Run this Specification as follows...
  * From within sbt use:
  * {{{
  * testOnly ambel.Apb2SliceUnitTester
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly ambel.Apb2SliceUnitTester'
  * }}}
  * For VCD dump use:
  * {{{
  * testOnly ambel.Apb2SliceUnitTester -- -DwriteVcd=1
  * }}}
  */
class Apb2SliceUnitTester extends AmbaUnitTester {
  behavior of "Apb2SliceTestHarness"

  /**
    * Test cases
    */
  it should "write then read consecutive addresses back to back" in {
    test(new Apb2SliceTestHarness).withAnnotations(annos) { dut =>
      dut.clock.step(4)

      for (i <- 0 until 4) {
        val addr = i << 2
        val data = 0xff << i*8
        ApbWriteStrb(dut.io, dut.clock, addr, data, 0xf)
        ApbReadExpect(dut.io, dut.clock, addr, data)
      }

      dut.clock.step(4)
    }
  }

  it should "write all addresses in sequence then read all back" in {
    test(new Apb2SliceTestHarness).withAnnotations(annos) { dut =>
      dut.clock.step(4)

      val dataSeq = new ListBuffer[Int]
      for (i <- 0 until 4) {
        val addr = i << 2
        val data = rand.nextInt
        dataSeq += data
        ApbWriteStrb(dut.io, dut.clock, addr, data, 0xf)
      }

      dut.clock.step(2)

      val dataExp = dataSeq.toList
      for (i <- 0 until 4) {
        val addr = i << 2
        ApbReadExpect(dut.io, dut.clock, addr, dataExp(i))
      }

      dut.clock.step(4)
    }
  }

  it should "test write strobes" in {
    test(new Apb2SliceTestHarness).withAnnotations(annos) { dut =>
      dut.clock.step(4)

      val addr = 0
      val data = 0xabbaface
      for (i <- 0 until 4) {
        ApbWriteStrb(dut.io, dut.clock, addr, 0, 0xf)
        ApbReadExpect(dut.io, dut.clock, addr, 0)
        ApbWriteStrb(dut.io, dut.clock, addr, data, (1 << i))
        ApbReadExpect(dut.io, dut.clock, addr, (data & (0xff << (i*8))))
      }

      dut.clock.step(4)
    }
  }
}
