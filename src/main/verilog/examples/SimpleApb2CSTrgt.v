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
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
  reg [31:0] _RAND_4;
  reg [31:0] _RAND_5;
  reg [31:0] _RAND_6;
`endif // RANDOMIZE_REG_INIT
  reg [7:0] SimpleRw_RwBits; // @[Apb2CSTrgt.scala 322:53]
  reg [7:0] SimpleRoWo_WoBits; // @[Apb2CSTrgt.scala 341:49]
  reg [2:0] pAddrFF; // @[Apb2CSTrgt.scala 430:26]
  reg  pWriteFF; // @[Apb2CSTrgt.scala 431:26]
  reg  pReadyFF; // @[Apb2CSTrgt.scala 432:26]
  reg [31:0] pRDataFF; // @[Apb2CSTrgt.scala 433:26]
  reg  pSlvErrFF; // @[Apb2CSTrgt.scala 434:26]
  wire  _GEN_1 = io_apb2T_req_pSel & ~io_apb2T_req_pEnable & io_apb2T_req_pWrite; // @[Apb2CSTrgt.scala 439:52 441:14 448:14]
  wire  _GEN_2 = io_apb2T_req_pSel & ~io_apb2T_req_pEnable ? io_apb2T_req_pWrite : pReadyFF; // @[Apb2CSTrgt.scala 439:52 442:14 432:26]
  wire  regIndex = pAddrFF[2]; // @[Apb2CSTrgt.scala 452:23]
  wire  _T_2 = ~regIndex; // @[Apb2CSTrgt.scala 460:22]
  wire  fieldPStrbBits_0 = io_apb2T_req_pStrb[0]; // @[Apb2CSTrgt.scala 482:39]
  wire [31:0] _GEN_4 = fieldPStrbBits_0 ? io_apb2T_req_pWData : {{24'd0}, SimpleRw_RwBits}; // @[Apb2CSTrgt.scala 484:53 500:23 322:53]
  wire  _GEN_5 = fieldPStrbBits_0 ? 1'h0 : fieldPStrbBits_0; // @[Apb2CSTrgt.scala 457:15 484:53]
  wire [31:0] _GEN_6 = ~regIndex ? _GEN_4 : {{24'd0}, SimpleRw_RwBits}; // @[Apb2CSTrgt.scala 460:31 322:53]
  wire  _GEN_7 = ~regIndex & _GEN_5; // @[Apb2CSTrgt.scala 457:15 460:31]
  wire  fieldPStrbBits_0_1 = io_apb2T_req_pStrb[1]; // @[Apb2CSTrgt.scala 482:39]
  wire  _GEN_8 = fieldPStrbBits_0_1 | _GEN_7; // @[Apb2CSTrgt.scala 502:59 504:25]
  wire [23:0] _GEN_9 = fieldPStrbBits_0_1 ? io_apb2T_req_pWData[31:8] : {{16'd0}, SimpleRoWo_WoBits}; // @[Apb2CSTrgt.scala 484:53 500:23 341:49]
  wire  _GEN_10 = fieldPStrbBits_0_1 ? _GEN_7 : _GEN_8; // @[Apb2CSTrgt.scala 484:53]
  wire [23:0] _GEN_11 = regIndex ? _GEN_9 : {{16'd0}, SimpleRoWo_WoBits}; // @[Apb2CSTrgt.scala 460:31 341:49]
  wire [31:0] _GEN_15 = pWriteFF ? _GEN_6 : {{24'd0}, SimpleRw_RwBits}; // @[Apb2CSTrgt.scala 454:19 322:53]
  wire [23:0] _GEN_16 = pWriteFF ? _GEN_11 : 24'h0; // @[Apb2CSTrgt.scala 454:19 514:70]
  wire [7:0] _GEN_17 = _T_2 ? SimpleRw_RwBits : 8'h0; // @[Apb2CSTrgt.scala 540:15 545:31 549:18]
  wire [15:0] shiftedBits_1 = {SimpleRoWo_WoBits, 8'h0}; // @[Apb2CSTrgt.scala 548:80]
  wire [15:0] _GEN_22 = {{8'd0}, io_roVec_0}; // @[Apb2CSTrgt.scala 549:46]
  wire [15:0] _pRDataFF_T = _GEN_22 | shiftedBits_1; // @[Apb2CSTrgt.scala 549:46]
  wire [15:0] _GEN_18 = regIndex ? _pRDataFF_T : {{8'd0}, _GEN_17}; // @[Apb2CSTrgt.scala 545:31 549:18]
  wire  _GEN_20 = ~pReadyFF | _GEN_2; // @[Apb2CSTrgt.scala 538:20 541:15]
  wire [31:0] _GEN_23 = reset ? 32'h0 : _GEN_15; // @[Apb2CSTrgt.scala 322:{53,53}]
  wire [23:0] _GEN_24 = reset ? 24'h0 : _GEN_16; // @[Apb2CSTrgt.scala 341:{49,49}]
  assign io_apb2T_rsp_pReady = pReadyFF; // @[Apb2CSTrgt.scala 557:24]
  assign io_apb2T_rsp_pRData = pRDataFF; // @[Apb2CSTrgt.scala 558:24]
  assign io_apb2T_rsp_pSlvErr = pSlvErrFF; // @[Apb2CSTrgt.scala 559:24]
  assign io_rwVec_0 = SimpleRw_RwBits; // @[Apb2CSTrgt.scala 411:19]
  assign io_woVec_0 = SimpleRoWo_WoBits; // @[Apb2CSTrgt.scala 425:19]
  always @(posedge clock) begin
    SimpleRw_RwBits <= _GEN_23[7:0]; // @[Apb2CSTrgt.scala 322:{53,53}]
    SimpleRoWo_WoBits <= _GEN_24[7:0]; // @[Apb2CSTrgt.scala 341:{49,49}]
    if (reset) begin // @[Apb2CSTrgt.scala 430:26]
      pAddrFF <= 3'h0; // @[Apb2CSTrgt.scala 430:26]
    end else if (io_apb2T_req_pSel & ~io_apb2T_req_pEnable) begin // @[Apb2CSTrgt.scala 439:52]
      pAddrFF <= io_apb2T_req_pAddr; // @[Apb2CSTrgt.scala 440:14]
    end
    if (reset) begin // @[Apb2CSTrgt.scala 431:26]
      pWriteFF <= 1'h0; // @[Apb2CSTrgt.scala 431:26]
    end else if (pWriteFF) begin // @[Apb2CSTrgt.scala 454:19]
      pWriteFF <= 1'h0; // @[Apb2CSTrgt.scala 456:15]
    end else begin
      pWriteFF <= _GEN_1;
    end
    pReadyFF <= reset | _GEN_20; // @[Apb2CSTrgt.scala 432:{26,26}]
    if (reset) begin // @[Apb2CSTrgt.scala 433:26]
      pRDataFF <= 32'h0; // @[Apb2CSTrgt.scala 433:26]
    end else if (~pReadyFF) begin // @[Apb2CSTrgt.scala 538:20]
      pRDataFF <= {{16'd0}, _GEN_18};
    end
    if (reset) begin // @[Apb2CSTrgt.scala 434:26]
      pSlvErrFF <= 1'h0; // @[Apb2CSTrgt.scala 434:26]
    end else if (~pReadyFF) begin // @[Apb2CSTrgt.scala 538:20]
      pSlvErrFF <= 1'h0; // @[Apb2CSTrgt.scala 542:15]
    end else if (pWriteFF) begin // @[Apb2CSTrgt.scala 454:19]
      if (regIndex) begin // @[Apb2CSTrgt.scala 460:31]
        pSlvErrFF <= _GEN_10;
      end else begin
        pSlvErrFF <= _GEN_7;
      end
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  SimpleRw_RwBits = _RAND_0[7:0];
  _RAND_1 = {1{`RANDOM}};
  SimpleRoWo_WoBits = _RAND_1[7:0];
  _RAND_2 = {1{`RANDOM}};
  pAddrFF = _RAND_2[2:0];
  _RAND_3 = {1{`RANDOM}};
  pWriteFF = _RAND_3[0:0];
  _RAND_4 = {1{`RANDOM}};
  pReadyFF = _RAND_4[0:0];
  _RAND_5 = {1{`RANDOM}};
  pRDataFF = _RAND_5[31:0];
  _RAND_6 = {1{`RANDOM}};
  pSlvErrFF = _RAND_6[0:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module SimpleApb2CSTrgt(
  input         clock,
  input         reset,
  input  [31:0] io_apb2T_req_pAddr,
  input  [2:0]  io_apb2T_req_pProt,
  input         io_apb2T_req_pSel,
  input         io_apb2T_req_pEnable,
  input         io_apb2T_req_pWrite,
  input  [31:0] io_apb2T_req_pWData,
  input  [3:0]  io_apb2T_req_pStrb,
  output        io_apb2T_rsp_pReady,
  output [31:0] io_apb2T_rsp_pRData,
  output        io_apb2T_rsp_pSlvErr,
  output [7:0]  io_rw_SimpleRw_RwBits,
  input  [7:0]  io_ro_SimpleRoWo_RoBits,
  output [7:0]  io_wo_SimpleRoWo_WoBits
);
  wire  t_clock; // @[SimpleApb2CSTrgt.scala 18:17]
  wire  t_reset; // @[SimpleApb2CSTrgt.scala 18:17]
  wire [2:0] t_io_apb2T_req_pAddr; // @[SimpleApb2CSTrgt.scala 18:17]
  wire  t_io_apb2T_req_pSel; // @[SimpleApb2CSTrgt.scala 18:17]
  wire  t_io_apb2T_req_pEnable; // @[SimpleApb2CSTrgt.scala 18:17]
  wire  t_io_apb2T_req_pWrite; // @[SimpleApb2CSTrgt.scala 18:17]
  wire [31:0] t_io_apb2T_req_pWData; // @[SimpleApb2CSTrgt.scala 18:17]
  wire [3:0] t_io_apb2T_req_pStrb; // @[SimpleApb2CSTrgt.scala 18:17]
  wire  t_io_apb2T_rsp_pReady; // @[SimpleApb2CSTrgt.scala 18:17]
  wire [31:0] t_io_apb2T_rsp_pRData; // @[SimpleApb2CSTrgt.scala 18:17]
  wire  t_io_apb2T_rsp_pSlvErr; // @[SimpleApb2CSTrgt.scala 18:17]
  wire [7:0] t_io_rwVec_0; // @[SimpleApb2CSTrgt.scala 18:17]
  wire [7:0] t_io_roVec_0; // @[SimpleApb2CSTrgt.scala 18:17]
  wire [7:0] t_io_woVec_0; // @[SimpleApb2CSTrgt.scala 18:17]
  Apb2CSTrgt t ( // @[SimpleApb2CSTrgt.scala 18:17]
    .clock(t_clock),
    .reset(t_reset),
    .io_apb2T_req_pAddr(t_io_apb2T_req_pAddr),
    .io_apb2T_req_pSel(t_io_apb2T_req_pSel),
    .io_apb2T_req_pEnable(t_io_apb2T_req_pEnable),
    .io_apb2T_req_pWrite(t_io_apb2T_req_pWrite),
    .io_apb2T_req_pWData(t_io_apb2T_req_pWData),
    .io_apb2T_req_pStrb(t_io_apb2T_req_pStrb),
    .io_apb2T_rsp_pReady(t_io_apb2T_rsp_pReady),
    .io_apb2T_rsp_pRData(t_io_apb2T_rsp_pRData),
    .io_apb2T_rsp_pSlvErr(t_io_apb2T_rsp_pSlvErr),
    .io_rwVec_0(t_io_rwVec_0),
    .io_roVec_0(t_io_roVec_0),
    .io_woVec_0(t_io_woVec_0)
  );
  assign io_apb2T_rsp_pReady = t_io_apb2T_rsp_pReady; // @[SimpleApb2CSTrgt.scala 31:14]
  assign io_apb2T_rsp_pRData = t_io_apb2T_rsp_pRData; // @[SimpleApb2CSTrgt.scala 31:14]
  assign io_apb2T_rsp_pSlvErr = t_io_apb2T_rsp_pSlvErr; // @[SimpleApb2CSTrgt.scala 31:14]
  assign io_rw_SimpleRw_RwBits = t_io_rwVec_0; // @[SimpleApb2CSTrgt.scala 34:25]
  assign io_wo_SimpleRoWo_WoBits = t_io_woVec_0; // @[SimpleApb2CSTrgt.scala 40:27]
  assign t_clock = clock;
  assign t_reset = reset;
  assign t_io_apb2T_req_pAddr = io_apb2T_req_pAddr[2:0]; // @[SimpleApb2CSTrgt.scala 31:14]
  assign t_io_apb2T_req_pSel = io_apb2T_req_pSel; // @[SimpleApb2CSTrgt.scala 31:14]
  assign t_io_apb2T_req_pEnable = io_apb2T_req_pEnable; // @[SimpleApb2CSTrgt.scala 31:14]
  assign t_io_apb2T_req_pWrite = io_apb2T_req_pWrite; // @[SimpleApb2CSTrgt.scala 31:14]
  assign t_io_apb2T_req_pWData = io_apb2T_req_pWData; // @[SimpleApb2CSTrgt.scala 31:14]
  assign t_io_apb2T_req_pStrb = io_apb2T_req_pStrb; // @[SimpleApb2CSTrgt.scala 31:14]
  assign t_io_roVec_0 = io_ro_SimpleRoWo_RoBits; // @[SimpleApb2CSTrgt.scala 37:17]
endmodule
