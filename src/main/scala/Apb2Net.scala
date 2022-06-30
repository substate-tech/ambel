// See README.md for license details.
package ambel

import chisel3._
import chisel3.util.isPow2
import chisel3.util.log2Ceil
import chisel3.util.DecoupledIO
import chisel3.util.Arbiter
import chisel3.util.switch
import chisel3.util.is
import chisel3.experimental.BundleLiterals._
import chisel3.experimental.ChiselEnum
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}

/** =Apb2ReqCtrl Bundle=
  *
  * Apb2ReqCtrl is the same as Apb2Req Bundle but without pSel.  This Bundle is
  * used below in DecoupledIO where the pSel signal from Apb2Req Bundle is used
  * to drive valid.
  *
  * @param DATA_W the width of the APB data bus in bits
  * @param ADDR_W the width of the APB address bus in bits
  */
class Apb2ReqCtrl(ADDR_W: Int = 32, DATA_W: Int = 32) extends Apb2Bundle(ADDR_W, DATA_W) {
  val pAddr   = UInt(ADDR_W.W)
  val pProt   = UInt(3.W)
  val pEnable = Bool()
  val pWrite  = Bool()
  val pWData  = UInt(DATA_W.W)
  val pStrb   = UInt((STRB_WIDTH).W)
}

/** =APB2 Network=
  *
  * Parameterizable number of initiators and memory mapped targets.  Arbitration between
  * initiators for access to targets.  Default parameterization connects one APB initiator
  * to two APB targets.  Cross-bar network topology.  To be clear, this Module drives its
  * targets and responds to its initiators, but where it is instantiated its initators are
  * targets and its targets are initiators.
  *
  * @note targets are assumed to be contiguously mapped from the base (with no gaps in map,
  * i.e. if there are two targets of size 4kB and 8kB, respectively, and the base address
  * of the first is @0x0 then the base address of the second is implicitly @4kB)
  *
  * @note this module is not optimized for throughput, but for easy timing closure and
  * low power
  *
  * @param BASE_ADDR base address of the network in bytes (must be multiple of GRANULE_SIZE_K)
  * @param GRANULE_SIZE_K minimum size of address space of one APB target (kB >= 4)
  * @param TARGET_SIZES array of target sizes (multiples of GRANULE_SIZE_K)
  * @param NUM_INIT number of initiators
  * @param NUM_TARG number of targets
  * @param DATA_W the width of the APB data buses in bits
  * @param ADDR_W the width of the APB address buses in bits
  * @param PIPE_CTRL adds pipeline register stages as follows 0: none, 1: after arbitration, 2: for each target
  * @todo implement PIPE_CTRL as described
  */
class Apb2Net(
  val BASE_ADDR: Int = 0, val GRANULE_SIZE_K: Int = 4,
  val NUM_INIT: Int = 1, val NUM_TARG: Int = 2,
  val TARGET_SIZES: Array[Int] = new Array[Int](2),
  val DATA_W: Int = 32, val ADDR_W: Int = 32,
  val PIPE_CTRL: Int = 0) extends Module {
  require(isPow2(GRANULE_SIZE_K))
  require(TARGET_SIZES.length == NUM_TARG)
  require(GRANULE_SIZE_K >= 4)
  require(DATA_W % 8 == 0)
  val NUM_BYTE = DATA_W / 8
  val APB_ALGN = log2Ceil(NUM_BYTE)
  val SEL_ALGN = log2Ceil(GRANULE_SIZE_K * 1024)

  // Array of target spaces in units of GRANULE_SIZE_K
  println(f"Apb2Net(): BASE_ADDR=${BASE_ADDR}, GRANULE_SIZE_K=${GRANULE_SIZE_K}")
  println(f"           NUM_INIT=${NUM_INIT}, NUM_TARG=${NUM_TARG}")
  print(f"           TARGET_BASES=(")
  val targetSpace: Array[Int] = new Array[Int](NUM_TARG+1)
  targetSpace(0) = BASE_ADDR / (GRANULE_SIZE_K * 1024)
  for (t <- 1 to NUM_TARG) {
    // @note we iterate to NUM_TARG+1 here to define top of range for highest order target
    targetSpace(t) = targetSpace(t-1) + TARGET_SIZES(t-1)
  }
  for (t <- 0 until NUM_TARG) {
    // Convert spaces to base addresses
    print(f"h${targetSpace(t)* GRANULE_SIZE_K * 1024}%08x")
    if (t == NUM_TARG-1) println(")") else print(", ")
  }
  println(f"           DATA_W=${DATA_W}, ADDR_W=${ADDR_W}")
  println(f"           PIPE_CTRL=${PIPE_CTRL}")

  val io = IO(new Bundle {
    val apb2i =         Vec(NUM_INIT, new Apb2IO(ADDR_W, DATA_W))
    val apb2t = Flipped(Vec(NUM_TARG, new Apb2IO(ADDR_W, DATA_W)))
  })

  // Create Vec of DecoupledIO of Apb2ReqCtrl for arbitration, connecting valid
  // to pSel
  val apb2ReqVec = Wire(Flipped(Vec(NUM_INIT, new DecoupledIO(new Apb2ReqCtrl(ADDR_W, DATA_W)))))
  val apb2Choice = Wire(new DecoupledIO(new Apb2ReqCtrl(ADDR_W, DATA_W)))
  val chosen = Wire(UInt(log2Ceil(NUM_INIT).W))

  val reqVec = Wire(Vec(NUM_INIT, Bool()))
  for (i <- 0 until NUM_INIT) {
    apb2ReqVec(i).bits.pAddr   := io.apb2i(i).req.pAddr
    apb2ReqVec(i).bits.pProt   := io.apb2i(i).req.pProt
    apb2ReqVec(i).bits.pEnable := io.apb2i(i).req.pEnable
    apb2ReqVec(i).bits.pWrite  := io.apb2i(i).req.pWrite
    apb2ReqVec(i).bits.pWData  := io.apb2i(i).req.pWData
    apb2ReqVec(i).bits.pStrb   := io.apb2i(i).req.pStrb
    apb2ReqVec(i).valid        := io.apb2i(i).req.pSel
    reqVec(i)                  := io.apb2i(i).req.pSel
  }

  // ArbInst is combinatorial and unfair, favouring the lower order requests
  val ArbInst = Module(new Arbiter(new Apb2ReqCtrl(ADDR_W, DATA_W), NUM_INIT))
  ArbInst.io.in <> apb2ReqVec
  ArbInst.io.out <> apb2Choice
  ArbInst.io.chosen <> chosen

  // Simple FSM intercepts arbitration winner (chosen) and forwards transaction
  // to selected target, re-producing the pEnable LOW/HIGH setup/access phases
  object Apb2NetStateEnum extends ChiselEnum {
    val S_IDLE, S_PSEL, S_PEN, S_DONE = Value
  }

  import Apb2NetStateEnum._

  val stateFF = RegInit(S_IDLE)

  // Registered request from chosen initiator
  val apb2InitChoiceReqFF = RegInit(WireInit(new Apb2Req(ADDR_W, DATA_W).Lit()))

  // Detect active request
  val activeReq = WireDefault(reqVec.asUInt.orR)

  // pReady to each initiator, by default not ready!
  val pReadyFF = RegInit(VecInit(Seq.fill(NUM_INIT)(false.B)))

  // Registered response from selected target
  val apb2TargetSelRspFF = RegInit(WireInit(new Apb2Rsp(ADDR_W, DATA_W).Lit()))

  // Decode target selection for the chosen initiator
  val apb2TargetSel = WireDefault(apb2InitChoiceReqFF.pAddr(ADDR_W-1, SEL_ALGN) )

  // Connect selected target response to chosen initiator (defaulting to target zero)
  val apb2ChoiceRsp = WireDefault(new Apb2Rsp(ADDR_W, DATA_W), io.apb2t(0).rsp)

  for (t <- 0 until NUM_TARG) {
    when (targetMatch(apb2TargetSel, targetSpace(t), targetSpace(t+1))) {
      apb2ChoiceRsp.pReady := io.apb2t(t).rsp.pReady
      apb2ChoiceRsp.pRData := io.apb2t(t).rsp.pRData
      apb2ChoiceRsp.pSlvErr := io.apb2t(t).rsp.pSlvErr
    }
  }

  // Arbiter ready/vaild handshake
  apb2Choice.ready := stateFF === S_DONE

  switch (stateFF) {
    is (S_IDLE) {
      when (activeReq) {
        stateFF  := S_PSEL
        apb2InitChoiceReqFF.pAddr   := apb2Choice.bits.pAddr
        apb2InitChoiceReqFF.pProt   := apb2Choice.bits.pProt
        apb2InitChoiceReqFF.pSel    := true.B
        apb2InitChoiceReqFF.pEnable := false.B
        apb2InitChoiceReqFF.pWrite  := apb2Choice.bits.pWrite
        apb2InitChoiceReqFF.pWData  := apb2Choice.bits.pWData
        apb2InitChoiceReqFF.pStrb   := apb2Choice.bits.pStrb
      }
    }
    is (S_PSEL) {
      // From the AMBA APB Protocol Specification:
      // The enable signal, PENABLE, is asserted in the ACCESS state. The address,
      // write, select, and write data signals must remain stable during the transition
      // from the SETUP to ACCESS state.
      assert(apb2Choice.bits.pEnable === true.B)
      // pEnable LOW in this state (setup)
      stateFF := S_PEN
      apb2InitChoiceReqFF.pEnable := true.B
    }
    is (S_PEN) {
      // pEnable HIGH in this state (access)
      when (apb2ChoiceRsp.pReady) {
        pReadyFF(chosen) := true.B
        //apb2TargetSelRspFF.pReady UNUSED
        apb2TargetSelRspFF.pRData := apb2ChoiceRsp.pRData
        apb2TargetSelRspFF.pSlvErr := apb2ChoiceRsp.pSlvErr
        stateFF := S_DONE
      }
    }
    is (S_DONE) {
      // Clear pReady and pSlvErr when done
      pReadyFF := Seq.fill(NUM_INIT)(false.B)
      apb2TargetSelRspFF.pSlvErr := false.B

      // Arbitrate again immediately if possible
      when (activeReq) {
        stateFF := S_PSEL
        apb2InitChoiceReqFF.pAddr   := apb2Choice.bits.pAddr
        apb2InitChoiceReqFF.pProt   := apb2Choice.bits.pProt
        apb2InitChoiceReqFF.pSel    := true.B
        apb2InitChoiceReqFF.pEnable := false.B
        apb2InitChoiceReqFF.pWrite  := apb2Choice.bits.pWrite
        apb2InitChoiceReqFF.pWData  := apb2Choice.bits.pWData
        apb2InitChoiceReqFF.pStrb   := apb2Choice.bits.pStrb
      }.otherwise {
        apb2InitChoiceReqFF.pSel := false.B
        stateFF := S_IDLE
      }
    }
  }

  // Test whether target selection is within target range [bot, top)
  def targetMatch(sel: UInt, bot: Int, top: Int): Bool = {
    val inRange = Wire(Bool())
    inRange := (sel >= bot.U && sel < top.U)
    inRange
  }

  // NOTE only propagating controls to selected target to avoid unecessary
  // downstream toggles
  for (t <- 0 until NUM_TARG) {
    // TODO - try WireInit(new Apb2Req(ADDR_W, DATA_W).Lit())
    io.apb2t(t).req.pAddr   := 0.U
    io.apb2t(t).req.pProt   := 0.U
    io.apb2t(t).req.pSel    := false.B
    io.apb2t(t).req.pEnable := false.B
    io.apb2t(t).req.pWrite  := false.B
    io.apb2t(t).req.pWData  := 0.U
    io.apb2t(t).req.pStrb   := 0.U

    when (targetMatch(apb2TargetSel, targetSpace(t), targetSpace(t+1))) {
      io.apb2t(t).req := apb2InitChoiceReqFF
    }
  }

  // Connect pReadyFF to each initiator and connect registered response from
  // selected target to chosen initiator
  for (i <- 0 until NUM_INIT) {
    io.apb2i(i).rsp.pReady := pReadyFF(i)
    io.apb2i(i).rsp.pRData := apb2TargetSelRspFF.pRData
    io.apb2i(i).rsp.pSlvErr := apb2TargetSelRspFF.pSlvErr
  }
}

/** =Verilog generation boiler plate=
  *
  * Run this driver as follows...
  * From within sbt use:
  * {{{
  * runMain ambel.Apb2NetDriver --target-dir src/main/verilog --log-level info --log-file Apb2NetDriver.log
  * }}}
  */
// $COVERAGE-OFF$
object Apb2NetDriver extends App {
  (new ChiselStage).execute(args, Seq(ChiselGeneratorAnnotation(() => new Apb2Net(NUM_INIT=2, NUM_TARG=4, TARGET_SIZES=Array(1,1,1,1)))))
}
// $COVERAGE-OFF$
