// See README.md for license details.
package ambel

import scala.collection.mutable.ListBuffer
import chisel3._
import chisel3.util._
import chiseltest._

/** =Apb2CSTrgt Unit Tester=
  * Run this Specification as follows...
  * From within sbt use:
  * {{{
  * testOnly ambel.Apb2CSTrgtUnitTester
  * }}}
  * From a terminal shell use:
  * {{{
  * sbt 'testOnly ambel.Apb2CSTrgtUnitTester'
  * }}}
  * For VCD dump use:
  * {{{
  * testOnly ambel.Apb2CSTrgtUnitTester -- -DwriteVcd=1
  * }}}
  */
class Apb2CSTrgtUnitTester extends AmbelUnitTester {
  behavior of "Apb2CSTrgt"

  /**
    * Test cases
    */
  val DATA_W = 32

  it should "write then read consecutive addresses of register file APB target back to back" in {
    test(new Apb2CSTrgt(DATA_W, "src/test/json/regfile.json")) { dut =>
      dut.clock.step(4)

      val NUM_REGS = dut.NUM_REGS

      for (i <- 0 until NUM_REGS) {
        val addr = i << 2
        val data = 0xff << i*8
        ApbWriteStrb(dut.io.apb2T, dut.clock, addr, data, 0xf)
        ApbReadExpect(dut.io.apb2T, dut.clock, addr, data)
      }

      dut.clock.step(4)
    }
  }

  it should "write all addresses of register file APB target in sequence then read all back" in {
    test(new Apb2CSTrgt(DATA_W, "src/test/json/regfile.json")) { dut =>
      dut.clock.step(4)

      val NUM_REGS = dut.NUM_REGS

      val dataSeq = new ListBuffer[Int]
      for (i <- 0 until NUM_REGS) {
        val addr = i << 2
        val data = rand.nextInt
        dataSeq += data
        ApbWriteStrb(dut.io.apb2T, dut.clock, addr, data, 0xf)
      }

      dut.clock.step(2)

      val dataExp = dataSeq.toList
      for (i <- 0 until NUM_REGS) {
        val addr = i << 2
        ApbReadExpect(dut.io.apb2T, dut.clock, addr, dataExp(i))
      }

      dut.clock.step(4)
    }
  }

  it should "test write strobes of register file APB target" in {
    test(new Apb2CSTrgt(DATA_W, "src/test/json/regfile.json")) { dut =>
      dut.clock.step(4)

      val NUM_REGS = dut.NUM_REGS
      val NUM_BYTE = dut.NUM_BYTE

      val data = 0xabbaface
      for (i <- 0 until NUM_REGS) {
        val addr = i << 2
        ApbWriteStrb(dut.io.apb2T, dut.clock, addr, 0, 0xf)
        ApbReadExpect(dut.io.apb2T, dut.clock, addr, 0)
        var dataExp = 0x0
        for (b <- 0 until NUM_BYTE) {
          dataExp = dataExp | data & (0xff << (b*8))
          ApbWriteStrb(dut.io.apb2T, dut.clock, addr, data, (1 << b))
          ApbReadExpect(dut.io.apb2T, dut.clock, addr, dataExp)
        }
      }

      dut.clock.step(4)
    }
  }

  it should "test word, half word and byte access to 32 bit RW regs exhaustively" in {
    test(new Apb2CSTrgt(DATA_W, "src/test/json/32bitrwregs.json")) { dut =>
      // NOTE 32bitrwregs.json has one register supporting word access only, one
      // supporting half word access and two supporting byte access
      dut.clock.step(4)

      val NUM_REGS = dut.NUM_REGS
      val NUM_BYTE = dut.NUM_BYTE

      // Test word access to all registers
      {
        val addr = 0 << 2
        val data = rand.nextInt
        ApbWriteStrb(dut.io.apb2T, dut.clock, addr, data, 0xf)

        val dataStr = f"h${data}%08x"
        dut.io.rwVec(0).expect(dataStr.U)

        ApbReadExpect(dut.io.apb2T, dut.clock, addr, data)
      }

      var rdData = 0
      for (i <- 0 until NUM_BYTE / 2) {
        val addr = 4 + (i << 1)
        val data = rand.nextInt
        ApbWriteStrb(dut.io.apb2T, dut.clock, addr, data, 0x3 << (i * NUM_BYTE / 2))

        val dataStr = f"h${(data >> (i * NUM_BYTE / 2 * 8)) & 0xffff}%08x"
        dut.io.rwVec(i+1).expect(dataStr.U)

        rdData |= data & (0xffff << (i * NUM_BYTE / 2 * 8))
        ApbReadExpect(dut.io.apb2T, dut.clock, addr, rdData)
      }

      rdData = 0
      for (i <- 0 until NUM_BYTE) {
        val addr = 8 + i
        val data = rand.nextInt
        ApbWriteStrb(dut.io.apb2T, dut.clock, addr, data, 0x1 << i)

        val dataStr = f"h${(data >> (i * 8)) & 0xff}%08x"
        dut.io.rwVec(i+3).expect(dataStr.U)

        rdData |= data & (0xff << (i * 8))
        ApbReadExpect(dut.io.apb2T, dut.clock, addr, rdData)
      }

      rdData = 0
      for (i <- 0 until NUM_BYTE) {
        val addr = 12 + i
        val data = rand.nextInt
        ApbWriteStrb(dut.io.apb2T, dut.clock, addr, data, 0x1 << i)

        val dataStr = f"h${(data >> (i * 8)) & 0xff}%08x"
        dut.io.rwVec(i+7).expect(dataStr.U)

        rdData |= data & (0xff << (i * 8))
        ApbReadExpect(dut.io.apb2T, dut.clock, addr, rdData)
      }

      dut.clock.step(4)
    }
  }
}
