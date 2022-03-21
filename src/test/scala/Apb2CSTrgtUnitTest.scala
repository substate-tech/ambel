// See README.md for license details.
package ambel

import scala.collection.mutable.ListBuffer
import chisel3._
import chisel3.util._
import chiseltest._

/** =Apb2CSTrgt32BitRWRegsTestWrapper=
  *
  * Wraps instance of Apb2CSTrgt parameterized with 32BitRwRegs.json with
  * register RW Output Vec connected to auto-generated Bundle matching specified
  * bitfield names
  */
class Apb2CSTrgt32BitRWRegsTestWrapper(val VERBOSE: Boolean = false) extends Module {
  val ADDR_W = 32
  val DATA_W = 32

  val t = Module(new Apb2CSTrgt(32, "src/test/json/32BitRwRegs.json", VERBOSE))

  val io = IO(new Bundle {
    val apb2T = new Apb2IO(ADDR_W, DATA_W)
    val rw = Output(new _32BitRwRegsRwVec_)
  })

  t.io.apb2T <> io.apb2T

  io.rw.RegZero_Word       := t.io.rwVec(0)
  io.rw.RegOne_LowerHalf   := t.io.rwVec(1)
  io.rw.RegOne_UpperHalf   := t.io.rwVec(2)
  io.rw.RegTwo_ByteZero    := t.io.rwVec(3)
  io.rw.RegTwo_ByteOne     := t.io.rwVec(4)
  io.rw.RegTwo_ByteTwo     := t.io.rwVec(5)
  io.rw.RegTwo_ByteThree   := t.io.rwVec(6)
  io.rw.RegThree_ByteZero  := t.io.rwVec(7)
  io.rw.RegThree_ByteOne   := t.io.rwVec(8)
  io.rw.RegThree_ByteTwo   := t.io.rwVec(9)
  io.rw.RegThree_ByteThree := t.io.rwVec(10)
}

/** =Apb2CSTrgt8GoBitWoRegsTestWrapper=
  *
  * Wraps instance of Apb2CSTrgt parameterized with 8GoBitWoRegs.json with
  * register WO Output Vec connected to auto-generated Bundle matching specified
  * bitfield names
  */
class Apb2CSTrgt8GoBitWORegsTestWrapper(val VERBOSE: Boolean = false) extends Module {
  val ADDR_W = 32
  val DATA_W = 32

  val t = Module(new Apb2CSTrgt(32, "src/test/json/8GoBitWoRegs.json", VERBOSE))

  val io = IO(new Bundle {
    val apb2T = new Apb2IO(ADDR_W, DATA_W)
    val wo = Output(new _8GoBitWoRegsWoVec_)
  })

  t.io.apb2T <> io.apb2T

  io.wo.RegZero_GoBits  := t.io.woVec(0)
  io.wo.RegOne_GoBits   := t.io.woVec(1)
  io.wo.RegTwo_GoBits   := t.io.woVec(2)
  io.wo.RegThree_GoBits := t.io.woVec(3)
}

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
    *
    * NOTE that the tests using src/test/json/RegFile.json do not check RW register IO,
    * they only peek/poke/expect the APB2 target interface. They can therefore use
    * the parameterized Apb2CSTrgt Module as the DUT directly.
    *
    * Tests using other JSON register descriptions use the *TestWrapper Modules above
    * which wrap a Module with IOs using the generated Bundles for the parameterized
    * Apb2CSTrgt instance DUT. These have been pre-generated in a separate step, and
    * versioned, by elaboration of the parameterized Apb2CSTrgt Module with the
    * corresponding JSON file and GEN_BUNDLE=true.
    */
  val DATA_W = 32

  it should "write then read consecutive addresses of register file APB target back to back" in {
    test(new Apb2CSTrgt(DATA_W, "src/test/json/RegFile.json")).withAnnotations(annos) { dut =>
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
    test(new Apb2CSTrgt(DATA_W, "src/test/json/RegFile.json")).withAnnotations(annos) { dut =>
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
    test(new Apb2CSTrgt(DATA_W, "src/test/json/RegFile.json")).withAnnotations(annos) { dut =>
      dut.clock.step(4)

      val NUM_REGS = dut.NUM_REGS
      val NUM_BYTE = dut.NUM_BYTE

      // Zero each register then write following 32 bit word (val data)
      // one byte at a time, checking the accumulated written data after
      // each write
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
    test(new Apb2CSTrgt32BitRWRegsTestWrapper(_verbose)).withAnnotations(annos) { dut =>
      dut.clock.step(4)

      val NUM_REGS = dut.t.NUM_REGS
      val NUM_BYTE = dut.t.NUM_BYTE

      // Test word access to all registers
      {
        val addr = 0 << 2
        val data = rand.nextInt
        ApbWriteStrb(dut.io.apb2T, dut.clock, addr, data, 0xf)

        val dataStr = f"h${data}%08x"
        dut.io.rw.RegZero_Word.expect(dataStr.U)

        ApbReadExpect(dut.io.apb2T, dut.clock, addr, data)
      }

      // Test half word access to registers which support it
      var rdData = 0
      for (i <- 0 until NUM_BYTE / 2) {
        val addr = 4 + (i << 1)
        val data = rand.nextInt
        ApbWriteStrb(dut.io.apb2T, dut.clock, addr, data, 0x3 << (i * NUM_BYTE / 2))

        val dataStr = f"h${(data >> (i * NUM_BYTE / 2 * 8)) & 0xffff}%08x"
        if (i == 0) {
          dut.io.rw.RegOne_LowerHalf.expect(dataStr.U)
        } else {
          dut.io.rw.RegOne_UpperHalf.expect(dataStr.U)
        }

        rdData |= data & (0xffff << (i * NUM_BYTE / 2 * 8))
        ApbReadExpect(dut.io.apb2T, dut.clock, addr, rdData)
      }

      // Test byte access to registers which support it
      rdData = 0
      for (i <- 0 until NUM_BYTE) {
        val addr = 8 + i
        val data = rand.nextInt
        ApbWriteStrb(dut.io.apb2T, dut.clock, addr, data, 0x1 << i)

        val dataStr = f"h${(data >> (i * 8)) & 0xff}%08x"
        i match {
          case 0 => dut.io.rw.RegTwo_ByteZero.expect(dataStr.U)
          case 1 => dut.io.rw.RegTwo_ByteOne.expect(dataStr.U)
          case 2 => dut.io.rw.RegTwo_ByteTwo.expect(dataStr.U)
          case 3 => dut.io.rw.RegTwo_ByteThree.expect(dataStr.U)
        }

        rdData |= data & (0xff << (i * 8))
        ApbReadExpect(dut.io.apb2T, dut.clock, addr, rdData)
      }

      rdData = 0
      for (i <- 0 until NUM_BYTE) {
        val addr = 12 + i
        val data = rand.nextInt
        ApbWriteStrb(dut.io.apb2T, dut.clock, addr, data, 0x1 << i)

        val dataStr = f"h${(data >> (i * 8)) & 0xff}%08x"
        i match {
          case 0 => dut.io.rw.RegThree_ByteZero.expect(dataStr.U)
          case 1 => dut.io.rw.RegThree_ByteOne.expect(dataStr.U)
          case 2 => dut.io.rw.RegThree_ByteTwo.expect(dataStr.U)
          case 3 => dut.io.rw.RegThree_ByteThree.expect(dataStr.U)
        }

        rdData |= data & (0xff << (i * 8))
        ApbReadExpect(dut.io.apb2T, dut.clock, addr, rdData)
      }

      dut.clock.step(4)
    }
  }

  it should "test WO go bits exhaustively" in {
    test(new Apb2CSTrgt8GoBitWORegsTestWrapper(_verbose)).withAnnotations(annos) { dut =>
      dut.clock.step(4)

      val NUM_REGS = dut.t.NUM_REGS

      // Test that these read zero
      for (i <- 0 until NUM_REGS) {
        val addr = i << 2
        ApbReadExpect(dut.io.apb2T, dut.clock, addr, 0)
      }

      // Test that writing each bit causes a single-cycle pulse on the
      // corresponding output bit and, in parallel, that the register
      // reads zero after writing
      for (i <- 0 until NUM_REGS) {
        val addr = i << 2

        val regNameRef: UInt = i match {
            case 0 => dut.io.wo.RegZero_GoBits
            case 1 => dut.io.wo.RegOne_GoBits
            case 2 => dut.io.wo.RegTwo_GoBits
            case 3 => dut.io.wo.RegThree_GoBits
        }

        for (bit <- 0 until 8) {
          val data = 1 << bit
          ApbWriteStrb(dut.io.apb2T, dut.clock, addr, data, 0x1)
          val dataStr = f"h${data}%08x"

          fork {
            regNameRef.expect(dataStr.U)
            dut.clock.step()
            regNameRef.expect(0.U)
          }.fork {
            ApbReadExpect(dut.io.apb2T, dut.clock, addr, 0)
          }.join
        }
      }

      dut.clock.step(4)
    }
  }
}
