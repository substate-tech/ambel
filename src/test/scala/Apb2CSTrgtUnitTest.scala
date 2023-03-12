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

  val t = Module(new Apb2CSTrgt(ADDR_W, DATA_W, "src/test/json/32BitRwRegs.json", VERBOSE))

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

  val t = Module(new Apb2CSTrgt(ADDR_W, DATA_W, "src/test/json/8GoBitWoRegs.json", VERBOSE))

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

/** =Apb2CSTrgt8BitROStatusORegsTestWrapper=
  *
  * Wraps instance of Apb2CSTrgt parameterized with 8BitRoStatusRegs.json with
  * register RO Input Vec connected to auto-generated Bundle matching specified
  * bitfield names
  */
class Apb2CSTrgt8BitROStatusORegsTestWrapper(val VERBOSE: Boolean = false) extends Module {
  val ADDR_W = 32
  val DATA_W = 32

  val t = Module(new Apb2CSTrgt(ADDR_W, DATA_W, "src/test/json/8BitRoStatusRegs.json", VERBOSE))

  val io = IO(new Bundle {
    val apb2T = new Apb2IO(ADDR_W, DATA_W)
    val ro = Input(new _8BitRoStatusRegsRoVec_)
  })

  t.io.apb2T <> io.apb2T

  t.io.roVec(0) := io.ro.RegZero_StatusBits
  t.io.roVec(1) := io.ro.RegOne_StatusBits
  t.io.roVec(2) := io.ro.RegTwo_StatusBits
  t.io.roVec(3) := io.ro.RegThree_StatusBits
}

/** =Apb2CSTrgt8BitW1CRegsTestWrapper=
  *
  * Wraps instance of Apb2CSTrgt parameterized with 8BitW1CRegs.json with
  * register W1C Input Vec connected to auto-generated Bundle matching specified
  * bitfield names
  */
class Apb2CSTrgt8BitW1CRegsTestWrapper(val VERBOSE: Boolean = false) extends Module {
  val ADDR_W = 32
  val DATA_W = 32

  val t = Module(new Apb2CSTrgt(ADDR_W, DATA_W, "src/test/json/8BitW1CRegs.json", VERBOSE))

  val io = IO(new Bundle {
    val apb2T = new Apb2IO(ADDR_W, DATA_W)
    val wc = Input(new _8BitW1CRegsWcVec_)
  })

  t.io.apb2T <> io.apb2T

  t.io.wcVec(0) := io.wc.RegZero_StatusBits
  t.io.wcVec(1) := io.wc.RegOne_StatusBits
  t.io.wcVec(2) := io.wc.RegTwo_StatusBits
  t.io.wcVec(3) := io.wc.RegThree_StatusBits
}

/** =Apb2CSTrgtMisalignedRWRegsTestWrapper=
  *
  * Wraps instance of Apb2CSTrgt parameterized with MisalignedRWRegs.json with
  * register W1C Input Vec connected to auto-generated Bundle matching specified
  * bitfield names
  */
class Apb2CSTrgtMisalignedRWRegsTestWrapper(val VERBOSE: Boolean = false) extends Module {
  val ADDR_W = 32
  val DATA_W = 32

  val t = Module(new Apb2CSTrgt(ADDR_W, DATA_W, "src/test/json/MisalignedRWRegs.json", VERBOSE))

  val io = IO(new Bundle {
    val apb2T = new Apb2IO(ADDR_W, DATA_W)
    val rw = Output(new _MisalignedRWRegsRwVec_)
  })

  t.io.apb2T <> io.apb2T

  io.rw.RegZero_Nibble            := t.io.rwVec(0)
  io.rw.RegZero_MisalignedByte0   := t.io.rwVec(1)
  io.rw.RegZero_MisalignedByte1   := t.io.rwVec(2)
  io.rw.RegZero_RestOfBits        := t.io.rwVec(3)
  io.rw.RegTwo_ThreeByteBitfield  := t.io.rwVec(4)
  io.rw.RegTwo_TopByte            := t.io.rwVec(5)
  io.rw.RegThree_Nibble           := t.io.rwVec(6)
  io.rw.RegThree_MisalignedByte0  := t.io.rwVec(7)
  io.rw.RegThree_MisalignedByte1  := t.io.rwVec(8)
  io.rw.RegThree_RestOfBits       := t.io.rwVec(9)
  io.rw.RegFour_ThreeByteBitfield := t.io.rwVec(10)
  io.rw.RegFour_TopByte           := t.io.rwVec(11)
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
class Apb2CSTrgtUnitTester extends AmbaUnitTester {
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
    * Apb2CSTrgt instance DUT. These have been pre-generated in a separate step
    */
  val ADDR_W = 32
  val DATA_W = 32

  it should "write then read consecutive addresses of register file APB target back to back" in {
    test(new Apb2CSTrgt(ADDR_W, DATA_W, "src/test/json/RegFile.json")).withAnnotations(annos) { dut =>
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
    test(new Apb2CSTrgt(ADDR_W, DATA_W, "src/test/json/RegFile.json")).withAnnotations(annos) { dut =>
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
    test(new Apb2CSTrgt(ADDR_W, DATA_W, "src/test/json/RegFile.json")).withAnnotations(annos) { dut =>
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


  it should "test RO bits exhaustively" in {
    test(new Apb2CSTrgt8BitROStatusORegsTestWrapper(_verbose)).withAnnotations(annos) { dut =>
      dut.clock.step(4)

      val NUM_REGS = dut.t.NUM_REGS

      // Initialize status to zeros
      dut.io.ro.RegZero_StatusBits.poke(0.U)
      dut.io.ro.RegOne_StatusBits.poke(0.U)
      dut.io.ro.RegTwo_StatusBits.poke(0.U)
      dut.io.ro.RegThree_StatusBits.poke(0.U)

      // For each register...
      for (r <- 0 until NUM_REGS) {
        val addr = r << 2

        // Check initial status values
        ApbReadExpect(dut.io.apb2T, dut.clock, addr, 0)

        // Set status to random 8 bit values, read back and check
        for (i <- 0 until 10) {
          val data = rand.nextInt & 0xff

          r match {
            case 0 => dut.io.ro.RegZero_StatusBits.poke(data.U(8.W))
            case 1 => dut.io.ro.RegOne_StatusBits.poke(data.U(8.W))
            case 2 => dut.io.ro.RegTwo_StatusBits.poke(data.U(8.W))
            case 3 => dut.io.ro.RegThree_StatusBits.poke(data.U(8.W))
          }

          val rdData = data & 0xff
          ApbReadExpect(dut.io.apb2T, dut.clock, addr, rdData)
        }
      }

      // Read back final values (set above), then write 0s to RO registers and check
      // read values have not changed
      val dataSeq = new ListBuffer[UInt]

      for (r <- 0 until NUM_REGS) {
        val addr = r << 2
        dataSeq += ApbRead(dut.io.apb2T, dut.clock, addr)
      }

      for (r <- 0 until NUM_REGS) {
        val addr = r << 2
        ApbWriteStrb(dut.io.apb2T, dut.clock, addr, 0, 0x1)
      }

      val dataExp = dataSeq.toList

      for (r <- 0 until NUM_REGS) {
        val addr = r << 2
        ApbReadExpect(dut.io.apb2T, dut.clock, addr, dataExp(r).litValue.toInt)
      }

      dut.clock.step(4)
    }
  }

  it should "test W1C bits exhaustively" in {
    test(new Apb2CSTrgt8BitW1CRegsTestWrapper(_verbose)).withAnnotations(annos) { dut =>
      dut.clock.step(4)

      val NUM_REGS = dut.t.NUM_REGS

      // For each register...
      for (r <- 0 until NUM_REGS) {
        val addr = r << 2

        // Check initial status values
        ApbReadExpect(dut.io.apb2T, dut.clock, addr, 0)

        // Set status to all ones and check
        val data = 0xff

        r match {
          case 0 => dut.io.wc.RegZero_StatusBits.poke(data.U(8.W))
          case 1 => dut.io.wc.RegOne_StatusBits.poke(data.U(8.W))
          case 2 => dut.io.wc.RegTwo_StatusBits.poke(data.U(8.W))
          case 3 => dut.io.wc.RegThree_StatusBits.poke(data.U(8.W))
        }

        dut.clock.step()

        r match {
          case 0 => dut.io.wc.RegZero_StatusBits.poke(0.U(8.W))
          case 1 => dut.io.wc.RegOne_StatusBits.poke(0.U(8.W))
          case 2 => dut.io.wc.RegTwo_StatusBits.poke(0.U(8.W))
          case 3 => dut.io.wc.RegThree_StatusBits.poke(0.U(8.W))
        }

        ApbReadExpect(dut.io.apb2T, dut.clock, addr, 0xff)

        // Clear each bit individually, checking as we go
        var dataExp = 0xff
        for (i <- 0 to 7) {
          ApbWriteStrb(dut.io.apb2T, dut.clock, addr, 0x1 << i, 0x1)
          dataExp &= ~(0x1 << i)
          ApbReadExpect(dut.io.apb2T, dut.clock, addr, dataExp)
        }

        // Set status to all ones and check writing zeros has no effect
        r match {
          case 0 => dut.io.wc.RegZero_StatusBits.poke(data.U(8.W))
          case 1 => dut.io.wc.RegOne_StatusBits.poke(data.U(8.W))
          case 2 => dut.io.wc.RegTwo_StatusBits.poke(data.U(8.W))
          case 3 => dut.io.wc.RegThree_StatusBits.poke(data.U(8.W))
        }

        dut.clock.step()

        r match {
          case 0 => dut.io.wc.RegZero_StatusBits.poke(0.U(8.W))
          case 1 => dut.io.wc.RegOne_StatusBits.poke(0.U(8.W))
          case 2 => dut.io.wc.RegTwo_StatusBits.poke(0.U(8.W))
          case 3 => dut.io.wc.RegThree_StatusBits.poke(0.U(8.W))
        }

        dataExp = data
        ApbReadExpect(dut.io.apb2T, dut.clock, addr, dataExp)
        ApbWriteStrb(dut.io.apb2T, dut.clock, addr, 0x00, 0x1)
        ApbReadExpect(dut.io.apb2T, dut.clock, addr, dataExp)
      }

      dut.clock.step(4)
    }
  }

  it should "test pStrb functionality" in {
    test(new Apb2CSTrgtMisalignedRWRegsTestWrapper(_verbose)).withAnnotations(annos) { dut =>
      dut.clock.step(4)

      // pStrb is implemented as follows: pStrb bits are used to mask or enable
      // writes to individual bytes of bit fields. However, if a bit field straddles
      // two or more byte lanes and not ALL the corresponding bits of pStrb are set
      // then the bit field is not written (at all) and pSlvErr is signalled.


      // REG_ZERO and REG_THREE are:
      // "typeRef": "REG_RW_WITH_BITFIELDS_STRADDLING_BYTE_BOUNDARIES_0",
      // "fields": [
      //   {"bits": [3, 0], "name": "NIBBLE", "mode": "RW", "resetVal": 0},
      //   {"bits": [11, 4], "name": "MISALIGNED_BYTE_0", "mode": "RW", "resetVal": 0},
      //   {"bits": [19, 12], "name": "MISALIGNED_BYTE_1", "mode": "RW", "resetVal": 0},
      //   {"bits": [31, 20], "name": "REST_OF_BITS", "mode": "RW", "resetVal": 0}
      val even_regs = 0 :: 8 :: Nil

      for (r <- even_regs) {
        // Write to first byte hitting NIBBLE and bottom of MISALIGNED_BYTE_0 which
        // straddles bytes 0 and 1. Expect NIBBLE to be written but not MISALIGNED_BYTE_0.
        // Expect pSlvErr to be signalled due to the attempted partial write.
        ApbWriteStrb(dut.io.apb2T, dut.clock, r, 0xff, 0x1)
        ApbExpectSlvErr(dut.io.apb2T)
        ApbReadExpect(dut.io.apb2T, dut.clock, r, 0xf)

        r match {
          case 0 => {
            dut.io.rw.RegZero_Nibble.expect(0xf.U)
            dut.io.rw.RegZero_MisalignedByte0.expect(0x00.U)
          }
          case 8 => {
            dut.io.rw.RegThree_Nibble.expect(0xf.U)
            dut.io.rw.RegThree_MisalignedByte0.expect(0x00.U)
          }
        }

        // Write to first two bytes hitting NIBBLE and MISALIGNED_BYTE_0 which
        // straddles bytes 0 and 1 and also MISALIGNED_BYTE_1 which straddles bytes 1
        // and 2. Expect NIBBLE and MISALIGNED_BYTE_0 to be written but not
        // MISALIGNED_BYTE_1.
        // Expect pSlvErr to be signalled due to the attempted partial write.
        ApbWriteStrb(dut.io.apb2T, dut.clock, r, 0xfff0, 0x3)
        ApbExpectSlvErr(dut.io.apb2T)
        ApbReadExpect(dut.io.apb2T, dut.clock, r, 0x0ff0)

        r match {
          case 0 => {
            dut.io.rw.RegZero_Nibble.expect(0x0.U)
            dut.io.rw.RegZero_MisalignedByte0.expect(0xff.U)
            dut.io.rw.RegZero_MisalignedByte1.expect(0x00.U)
          }
          case 8 => {
            dut.io.rw.RegThree_Nibble.expect(0x0.U)
            dut.io.rw.RegThree_MisalignedByte0.expect(0xff.U)
            dut.io.rw.RegThree_MisalignedByte1.expect(0x00.U)
          }
        }

        // Write to top two bytes hitting MISALIGNED_BYTE_1 which
        // straddles bytes 1 and 2, and REST_OF_BITS. Expect REST_OF_BITS
        // to be written but not MISALIGNED_BYTE_1.
        // Expect pSlvErr to be signalled due to the attempted partial write.
        ApbWriteStrb(dut.io.apb2T, dut.clock, r, 0xffffffff, 0xc)
        ApbExpectSlvErr(dut.io.apb2T)
        ApbReadExpect(dut.io.apb2T, dut.clock, r, 0xfff00ff0)

        r match {
          case 0 => {
            dut.io.rw.RegZero_Nibble.expect(0x0.U)
            dut.io.rw.RegZero_MisalignedByte0.expect(0xff.U)
            dut.io.rw.RegZero_MisalignedByte1.expect(0x00.U)
            dut.io.rw.RegZero_RestOfBits.expect(0xfff.U)
          }
          case 8 => {
            dut.io.rw.RegThree_Nibble.expect(0x0.U)
            dut.io.rw.RegThree_MisalignedByte0.expect(0xff.U)
            dut.io.rw.RegThree_MisalignedByte1.expect(0x00.U)
            dut.io.rw.RegZero_RestOfBits.expect(0xfff.U)
          }
        }

      }

      // REG_TWO and REG_FOUR are:
      // "typeRef": "REG_RW_WITH_BITFIELDS_STRADDLING_BYTE_BOUNDARIES_1",
      // "width": 32,
      // "fields": [
      //  {"bits": [23, 0], "name": "THREE_BYTE_BITFIELD", "mode": "RW", "resetVal": 0},
      //  {"bits": [31, 24], "name": "TOP_BYTE", "mode": "RW", "resetVal": 0}
      val odd_regs = 4 :: 12 :: Nil

      for (r <- odd_regs) {
        // Write to top two bytes and bottom two bytes expect only TOP_BYTE to be written
        // and never THREE_BYTE_BITFIELD
        // Expect pSlvErr to be signalled due to the attempted partial write.
        ApbWriteStrb(dut.io.apb2T, dut.clock, r, 0xffff, 0x3)
        ApbExpectSlvErr(dut.io.apb2T)
        ApbReadExpect(dut.io.apb2T, dut.clock, r, 0x00000000)

        r match {
          case 4 => {
            dut.io.rw.RegTwo_ThreeByteBitfield.expect(0x000000.U)
            dut.io.rw.RegTwo_TopByte.expect(0x00.U)
          }
          case 12 => {
            dut.io.rw.RegFour_ThreeByteBitfield.expect(0x000000.U)
            dut.io.rw.RegFour_TopByte.expect(0x00.U)
          }
        }

        ApbWriteStrb(dut.io.apb2T, dut.clock, r, 0xffff0000, 0xc)
        ApbExpectSlvErr(dut.io.apb2T)
        ApbReadExpect(dut.io.apb2T, dut.clock, r, 0xff000000)

        r match {
          case 4 => {
            dut.io.rw.RegTwo_ThreeByteBitfield.expect(0x000000.U)
            dut.io.rw.RegTwo_TopByte.expect(0xff.U)
          }
          case 12 => {
            dut.io.rw.RegFour_ThreeByteBitfield.expect(0x000000.U)
            dut.io.rw.RegFour_TopByte.expect(0xff.U)
          }
        }
      }

      // Test access with all pStrb bits set
      val all_regs = 0 :: 4 :: 8 :: 12 :: Nil
      for (r <- all_regs) {
        val data = rand.nextInt
        ApbWriteStrb(dut.io.apb2T, dut.clock, r, data, 0xf)
        ApbExpectNoSlvErr(dut.io.apb2T)
        ApbReadExpect(dut.io.apb2T, dut.clock, r, data)
      }
      dut.clock.step(4)
    }
  }
}
