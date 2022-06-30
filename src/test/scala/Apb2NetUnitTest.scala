// See README.md for license details.
package ambel

import scala.collection.mutable.ListBuffer
import chisel3._
import chisel3.util._
import chiseltest._

/** =Apb2Net Test Harness=
  *
  * Connects Apb2Net DUT to (Seq) array of Module Apb2RegFile instances
  */
class Apb2NetTestHarness(
  val NUM_INIT: Int = 1, val NUM_TARG: Int = 2,
  val TARGET_SIZES: Array[Int] = Array(1,1)) extends Module {

  val DATA_W = 32
  val NUM_REGS = 16
  val ADDR_W = 32
  val io = IO(new Bundle {
    val apb2i = Vec(NUM_INIT, new Apb2IO(ADDR_W, DATA_W))
  })

  val Apb2Net_i = Module(new Apb2Net(NUM_INIT=NUM_INIT, NUM_TARG=NUM_TARG, TARGET_SIZES=TARGET_SIZES))
  val Apb2TrgtArr_i = Seq.fill(NUM_TARG)(Module(new Apb2RegFile(NUM_REGS, DATA_W)))

  Apb2Net_i.io.apb2i <> io.apb2i

  for (t <- 0 until NUM_TARG) {
    Apb2TrgtArr_i(t).io.apb2T <> Apb2Net_i.io.apb2t(t)
  }
}

/** =Apb2Net Unit Tester=
  * Run this Specification as follows...
  * From within sbt use:
  * {{{
  * testOnly ambel.Apb2NetUnitTester
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly ambel.Apb2NetUnitTester'
  * }}}
  * For VCD dump use:
  * {{{
  * testOnly ambel.Apb2NetUnitTester -- -DwriteVcd=1
  * }}}
  */
class Apb2NetUnitTester extends AmbelUnitTester {
  behavior of "Apb2NetTestHarness"

  /**
    * Test cases
    */
  it should "test each initiator can write/read back each target" in {
    test(new Apb2NetTestHarness(NUM_INIT=2, NUM_TARG=2, TARGET_SIZES=Array(1,1))).withAnnotations(annos) { dut =>

      dut.clock.step(4)

      // Discover APB network topology
      val DATA_W = dut.DATA_W
      val NUM_REGS = dut.NUM_REGS
      val NUM_INIT = dut.NUM_INIT
      val NUM_TARG = dut.NUM_TARG

      val BASE_ADDR = dut.Apb2Net_i.BASE_ADDR
      val GRANULE_SIZE_K = dut.Apb2Net_i.GRANULE_SIZE_K
      val TARGET_SIZES = dut.Apb2Net_i.TARGET_SIZES

      val targetBases: Array[Int] = new Array[Int](NUM_TARG+1)

      targetBases(0) = BASE_ADDR
      for (t <- 1 until NUM_TARG) {
        targetBases(t) = targetBases(t-1) + TARGET_SIZES(t-1) * GRANULE_SIZE_K * 1024
      }

      // Test access to each register in each target from each initiator
      for (i <- 0 until NUM_INIT) {
        for (t <- 0 until NUM_TARG) {
          for (r <- 0 until NUM_REGS) {
            val data = rand.nextInt
            val addr = targetBases(t) + r * DATA_W / 8
            ApbWriteStrb(dut.io.apb2i(i), dut.clock, addr, data, 0xf)
            ApbReadExpect(dut.io.apb2i(i), dut.clock, addr, data)
          }
        }
      }

      dut.clock.step(4)
    }
  }

  it should "arbitrate between from two initiators writing to one target" in {
    test(new Apb2NetTestHarness(NUM_INIT=2, NUM_TARG=1, TARGET_SIZES=Array(1))).withAnnotations(annos) { dut =>

      dut.clock.step(4)

      // Discover APB network topology
      val DATA_W = dut.DATA_W
      val NUM_REGS = dut.NUM_REGS
      val NUM_INIT = dut.NUM_INIT
      val NUM_TARG = dut.NUM_TARG
      val BASE_ADDR = dut.Apb2Net_i.BASE_ADDR

      // Have initiator 0 write even registers while initator 1 writes odd registers
      val evenDataSeq = new ListBuffer[Int]
      val oddDataSeq = new ListBuffer[Int]
      fork {
        for (r <- 0 until NUM_REGS by 2) {
          val data = rand.nextInt
          val addr = BASE_ADDR + r * DATA_W / 8
          evenDataSeq += data
          ApbWriteStrb(dut.io.apb2i(0), dut.clock, addr, data, 0xf)
        }
      }.fork {
        for (r <- 1 until NUM_REGS by 2) {
          val data = rand.nextInt
          val addr = BASE_ADDR + r * DATA_W / 8
          oddDataSeq += data
          ApbWriteStrb(dut.io.apb2i(1), dut.clock, addr, data, 0xf)
        }
      }.join

      // Build expected, ordered data sequence
      val dataSeq = new ListBuffer[Int]
      for (i <- 0 until NUM_REGS/2) {
        dataSeq += evenDataSeq(i)
        dataSeq += oddDataSeq(i)
      }

      // Read all registers back in order via each initiator in turn
      for (r <- 0 until NUM_REGS) {
        val addr = BASE_ADDR + r * DATA_W / 8
        ApbReadExpect(dut.io.apb2i(0), dut.clock, addr, dataSeq(r))
      }
      for (r <- 0 until NUM_REGS) {
        val addr = BASE_ADDR + r * DATA_W / 8
        ApbReadExpect(dut.io.apb2i(1), dut.clock, addr, dataSeq(r))
      }

      dut.clock.step(4)
    }
  }
}
