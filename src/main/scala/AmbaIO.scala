// See README.md for license details.
package ambel

import chisel3._

// $COVERAGE-OFF$
abstract class Apb2Bundle(val ADDR_WIDTH: Int = 32, val DATA_WIDTH: Int = 32) extends Bundle {
  assert(ADDR_WIDTH <= 32)
  assert(DATA_WIDTH <= 32)
  assert(DATA_WIDTH % 8 == 0)
  val STRB_WIDTH = DATA_WIDTH/8
}
// $COVERAGE-ON$

/** =AMBA APB2 IO Bundle=
  *
  * Direction of AMBA APB2 IOs are from target perspective (i.e. request = Input,
  * response = Output)
  *
  * @param ADDR_WIDTH the width of the address bus pAddr
  * @param DATA_WIDTH the width of the data buses pWData and pRData
  */
class Apb2Req(ADDR_WIDTH: Int = 32, DATA_WIDTH: Int = 32) extends Apb2Bundle(ADDR_WIDTH, DATA_WIDTH) {
  val pAddr   = UInt(ADDR_WIDTH.W)
  val pProt   = UInt(3.W)
  val pSel    = Bool()
  val pEnable = Bool()
  val pWrite  = Bool()
  val pWData  = UInt(DATA_WIDTH.W)
  val pStrb   = UInt((STRB_WIDTH).W)
}

class Apb2Rsp(ADDR_WIDTH: Int = 32, DATA_WIDTH: Int = 32) extends Apb2Bundle(ADDR_WIDTH, DATA_WIDTH) {
  val pReady  = Bool()
  val pRData  = UInt(DATA_WIDTH.W)
  val pSlvErr = Bool()
}

class Apb2IO(ADDR_WIDTH: Int = 32, DATA_WIDTH: Int = 32) extends Apb2Bundle(ADDR_WIDTH, DATA_WIDTH) {
  val req = Input(new Apb2Req(ADDR_WIDTH, DATA_WIDTH))
  val rsp = Output(new Apb2Rsp(ADDR_WIDTH, DATA_WIDTH))
}
