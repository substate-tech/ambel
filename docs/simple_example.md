The following JSON file [versioned here](src/main/json/Simple.json) can be used to parameterize Apb2CSTrgt to implement an extremely simple Module with two 32-bit registers. The first has a single 8-bit read-write bit-field (the rest of the bits are marked as RESERVED, will not be writable and will read zero). The second register has two 8 bit fields, one is read-only, one is write-only. 
```JSON
{
 "regMap": [
  {
   "offset": 0,
   "name": "SIMPLE_RW", "typeRef": "SIMPLE_RW8B",
   "comment": "Simple read/write register"
  },
  {
   "offset": 4,
   "name": "SIMPLE_RO_WO", "typeRef": "SIMPLE_RO8B_WO8B",
   "comment": "Simple multi-mode register (one read-only bit field, one write-only bit-field)"
  }
 ],
 "regTypes": [
  {
   "typeRef": "SIMPLE_RW8B",
   "width": 32,
   "fields": [
    {"bits": [ 7, 0], "name": "RW_BITS", "mode": "RW", "resetVal": 0, "comment": "Example RW bit-field"},
    {"bits": [31, 8], "name": "RESERVED"}
   ],
   "comment": "Simple read-write register with single 8-bit field"
  },
  {
   "typeRef": "SIMPLE_RO8B_WO8B",
   "width": 32,
   "fields": [
    {"bits": [ 7, 0], "name": "RO_BITS", "mode": "RO", "resetVal": 0, "comment": "Example RO bit-field"},
    {"bits": [15, 8], "name": "WO_BITS", "mode": "WO", "resetVal": 0, "comment": "Example WO bit-field"},
    {"bits": [31,16], "name": "RESERVED"}
   ],
   "comment": "Simple register with an 8-bit read-only field and an 8-bit write-only field"
  }
 ]
}
```

Generating the Verilog for this parameterization will produce a module ([versioned here](src/main/verilog/examples/SimpleApb2T.v)) with the following interface.

Below the clock and reset, and the standard APB2 IOs we see three extra IOs, one output which will reflect the value of the read-write register, one input which is readable via the read-only register, and one output which will propagate any non-zero value written to the write-only register for one clock cycle before returning to zero again. Simple!
```Verilog
module Apb2CSTrgt(
  input         clock,
  input         reset,
  input  [2:0]  io_apb2T_req_pAddr,
  input         io_apb2T_req_pSel,
  input         io_apb2T_req_pEnable,
  input         io_apb2T_req_pWrite,
  input  [31:0] io_apb2T_req_pWData,
  input  [3:0]  io_apb2T_req_pStrb,
  output        io_apb2T_rsp_pReady,
  output [31:0] io_apb2T_rsp_pRData,
  output        io_apb2T_rsp_pSlvErr,
  output [7:0]  io_rwVec_0,
  input  [7:0]  io_roVec_0,
  output [7:0]  io_woVec_0
);
```
Running generation with the parameter `GEN_MODULE = true` will also generate Chisel Bundles and a wrapper Module [versioned here](src/main/scala/examples/SimpleApb2T.scala), which looks like this:
```scala
package ambel

import chisel3._
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}

/** =Bundles for Connection to Apb2CSTrgt(REG_DESC_JSON="src/main/json/Simple.json")=
  *
  * THIS IS AUTO-GENERATED CODE - DO NOT MODIFY BY HAND!
  */
class _SimpleApb2TRwVec_ extends Bundle {
  val SimpleRw_RwBits = UInt(8.W)
}
class _SimpleApb2TRoVec_ extends Bundle {
  val SimpleRoWo_RoBits = UInt(8.W)
}
class _SimpleApb2TWoVec_ extends Bundle {
  val SimpleRoWo_WoBits = UInt(8.W)
}

/** =Wrapper Module for Apb2CSTrgt(REG_DESC_JSON="src/main/json/Simple.json")=
  * Uses Bundles above on IO and makes ordered connection to MixedVec IO on
  * Apb2CSTrgt instance
  *
  * THIS IS AUTO-GENERATED CODE - DO NOT MODIFY BY HAND!
  */
class SimpleApb2T() extends Module {
  val ADDR_W = 32
  val DATA_W = 32

  val t = Module(new Apb2CSTrgt(
    ADDR_W = ADDR_W,
    DATA_W = DATA_W,
    REG_DESC_JSON = "src/main/json/Simple.json"))

  val io = IO(new Bundle {
    val apb2T = new Apb2IO(ADDR_W, DATA_W)
    val rw = Output(new _SimpleApb2TRwVec_)
    val ro = Input(new _SimpleApb2TRoVec_)
    val wo = Output(new _SimpleApb2TWoVec_)
  })

  // Connect APB2 target interface
  t.io.apb2T <> io.apb2T

  // Connect RW bit-field outputs
  io.rw.SimpleRw_RwBits := t.io.rwVec(0)

  // Connect RO bit-field inputs
  t.io.roVec(0) := io.ro.SimpleRoWo_RoBits

  // Connect WO bit-field Outputs
  io.wo.SimpleRoWo_WoBits := t.io.woVec(0)
}

object SimpleApb2TDriver extends App {
  (new ChiselStage).execute(args, Seq(ChiselGeneratorAnnotation(() => new SimpleApb2T())))
}
```
