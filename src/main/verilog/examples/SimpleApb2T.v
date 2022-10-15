module Apb2CSTrgt(
  input         clock,
  input         reset,
  input  [31:0] io_apb2T_req_pAddr,
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
  reg [31:0] _RAND_7;
`endif // RANDOMIZE_REG_INIT
  reg [7:0] SimpleRw_RwBits; // @[Apb2CSTrgt.scala 335:53]
  reg [7:0] SimpleRoWo_WoBits; // @[Apb2CSTrgt.scala 354:49]
  reg [3:0] pAddrFF; // @[Apb2CSTrgt.scala 470:26]
  reg  pWriteFF; // @[Apb2CSTrgt.scala 471:26]
  reg  pReadyFF; // @[Apb2CSTrgt.scala 472:26]
  reg [31:0] pRDataFF; // @[Apb2CSTrgt.scala 473:26]
  reg  pSlvErrFF; // @[Apb2CSTrgt.scala 474:26]
  reg  regAliasFF; // @[Apb2CSTrgt.scala 477:27]
  wire  _GEN_1 = io_apb2T_req_pSel & ~io_apb2T_req_pEnable & io_apb2T_req_pWrite; // @[Apb2CSTrgt.scala 480:52 483:14 490:14]
  wire  _GEN_2 = io_apb2T_req_pSel & ~io_apb2T_req_pEnable ? io_apb2T_req_pWrite : pReadyFF; // @[Apb2CSTrgt.scala 480:52 484:14 472:26]
  wire  regIndex = pAddrFF[2]; // @[Apb2CSTrgt.scala 476:24 494:12]
  wire  _T_2 = ~regIndex; // @[Apb2CSTrgt.scala 506:24]
  wire  fieldPStrbBits_0 = io_apb2T_req_pStrb[0]; // @[Apb2CSTrgt.scala 528:41]
  wire [31:0] _GEN_5 = fieldPStrbBits_0 ? io_apb2T_req_pWData : {{24'd0}, SimpleRw_RwBits}; // @[Apb2CSTrgt.scala 530:55 546:25 335:53]
  wire  _GEN_6 = fieldPStrbBits_0 ? 1'h0 : fieldPStrbBits_0; // @[Apb2CSTrgt.scala 499:15 530:55]
  wire [31:0] _GEN_7 = ~regIndex ? _GEN_5 : {{24'd0}, SimpleRw_RwBits}; // @[Apb2CSTrgt.scala 506:33 335:53]
  wire  _GEN_8 = ~regIndex & _GEN_6; // @[Apb2CSTrgt.scala 499:15 506:33]
  wire  fieldPStrbBits_0_1 = io_apb2T_req_pStrb[1]; // @[Apb2CSTrgt.scala 528:41]
  wire  _GEN_9 = fieldPStrbBits_0_1 | _GEN_8; // @[Apb2CSTrgt.scala 548:61 550:27]
  wire [23:0] _GEN_10 = fieldPStrbBits_0_1 ? io_apb2T_req_pWData[31:8] : {{16'd0}, SimpleRoWo_WoBits}; // @[Apb2CSTrgt.scala 530:55 546:25 354:49]
  wire  _GEN_11 = fieldPStrbBits_0_1 ? _GEN_8 : _GEN_9; // @[Apb2CSTrgt.scala 530:55]
  wire [23:0] _GEN_12 = regIndex ? _GEN_10 : {{16'd0}, SimpleRoWo_WoBits}; // @[Apb2CSTrgt.scala 506:33 354:49]
  wire  _GEN_13 = regIndex ? _GEN_11 : _GEN_8; // @[Apb2CSTrgt.scala 506:33]
  wire  _GEN_14 = regAliasFF | _GEN_13; // @[Apb2CSTrgt.scala 501:23 502:17]
  wire [31:0] _GEN_15 = regAliasFF ? {{24'd0}, SimpleRw_RwBits} : _GEN_7; // @[Apb2CSTrgt.scala 501:23 335:53]
  wire [23:0] _GEN_16 = regAliasFF ? {{16'd0}, SimpleRoWo_WoBits} : _GEN_12; // @[Apb2CSTrgt.scala 501:23 354:49]
  wire [31:0] _GEN_19 = pWriteFF ? _GEN_15 : {{24'd0}, SimpleRw_RwBits}; // @[Apb2CSTrgt.scala 496:19 335:53]
  wire [23:0] _GEN_20 = pWriteFF ? _GEN_16 : 24'h0; // @[Apb2CSTrgt.scala 496:19 561:70]
  wire [7:0] _GEN_22 = _T_2 ? SimpleRw_RwBits : 8'h0; // @[Apb2CSTrgt.scala 586:15 596:31 600:18]
  wire [15:0] shiftedBits_1 = {SimpleRoWo_WoBits, 8'h0}; // @[Apb2CSTrgt.scala 599:80]
  wire [15:0] _GEN_27 = {{8'd0}, io_roVec_0}; // @[Apb2CSTrgt.scala 600:46]
  wire [15:0] _pRDataFF_T = _GEN_27 | shiftedBits_1; // @[Apb2CSTrgt.scala 600:46]
  wire [15:0] _GEN_23 = regIndex ? _pRDataFF_T : {{8'd0}, _GEN_22}; // @[Apb2CSTrgt.scala 596:31 600:18]
  wire  _GEN_25 = ~pReadyFF | _GEN_2; // @[Apb2CSTrgt.scala 584:20 587:15]
  wire [31:0] _GEN_28 = reset ? 32'h0 : _GEN_19; // @[Apb2CSTrgt.scala 335:{53,53}]
  wire [23:0] _GEN_29 = reset ? 24'h0 : _GEN_20; // @[Apb2CSTrgt.scala 354:{49,49}]
  assign io_apb2T_rsp_pReady = pReadyFF; // @[Apb2CSTrgt.scala 608:24]
  assign io_apb2T_rsp_pRData = pRDataFF; // @[Apb2CSTrgt.scala 609:24]
  assign io_apb2T_rsp_pSlvErr = pSlvErrFF; // @[Apb2CSTrgt.scala 610:24]
  assign io_rwVec_0 = SimpleRw_RwBits; // @[Apb2CSTrgt.scala 453:19]
  assign io_woVec_0 = SimpleRoWo_WoBits; // @[Apb2CSTrgt.scala 465:19]
  always @(posedge clock) begin
    SimpleRw_RwBits <= _GEN_28[7:0]; // @[Apb2CSTrgt.scala 335:{53,53}]
    SimpleRoWo_WoBits <= _GEN_29[7:0]; // @[Apb2CSTrgt.scala 354:{49,49}]
    if (reset) begin // @[Apb2CSTrgt.scala 470:26]
      pAddrFF <= 4'h0; // @[Apb2CSTrgt.scala 470:26]
    end else if (io_apb2T_req_pSel & ~io_apb2T_req_pEnable) begin // @[Apb2CSTrgt.scala 480:52]
      pAddrFF <= io_apb2T_req_pAddr[3:0]; // @[Apb2CSTrgt.scala 482:14]
    end
    if (reset) begin // @[Apb2CSTrgt.scala 471:26]
      pWriteFF <= 1'h0; // @[Apb2CSTrgt.scala 471:26]
    end else if (pWriteFF) begin // @[Apb2CSTrgt.scala 496:19]
      pWriteFF <= 1'h0; // @[Apb2CSTrgt.scala 498:15]
    end else begin
      pWriteFF <= _GEN_1;
    end
    pReadyFF <= reset | _GEN_25; // @[Apb2CSTrgt.scala 472:{26,26}]
    if (reset) begin // @[Apb2CSTrgt.scala 473:26]
      pRDataFF <= 32'h0; // @[Apb2CSTrgt.scala 473:26]
    end else if (~pReadyFF) begin // @[Apb2CSTrgt.scala 584:20]
      pRDataFF <= {{16'd0}, _GEN_23};
    end
    if (reset) begin // @[Apb2CSTrgt.scala 474:26]
      pSlvErrFF <= 1'h0; // @[Apb2CSTrgt.scala 474:26]
    end else if (~pReadyFF) begin // @[Apb2CSTrgt.scala 584:20]
      pSlvErrFF <= regAliasFF;
    end else if (pWriteFF) begin // @[Apb2CSTrgt.scala 496:19]
      pSlvErrFF <= _GEN_14;
    end
    if (reset) begin // @[Apb2CSTrgt.scala 477:27]
      regAliasFF <= 1'h0; // @[Apb2CSTrgt.scala 477:27]
    end else if (io_apb2T_req_pSel & ~io_apb2T_req_pEnable) begin // @[Apb2CSTrgt.scala 480:52]
      regAliasFF <= |io_apb2T_req_pAddr[31:3]; // @[Apb2CSTrgt.scala 488:16]
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
  pAddrFF = _RAND_2[3:0];
  _RAND_3 = {1{`RANDOM}};
  pWriteFF = _RAND_3[0:0];
  _RAND_4 = {1{`RANDOM}};
  pReadyFF = _RAND_4[0:0];
  _RAND_5 = {1{`RANDOM}};
  pRDataFF = _RAND_5[31:0];
  _RAND_6 = {1{`RANDOM}};
  pSlvErrFF = _RAND_6[0:0];
  _RAND_7 = {1{`RANDOM}};
  regAliasFF = _RAND_7[0:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module SimpleApb2T(
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
  wire  t_clock; // @[SimpleApb2T.scala 45:17]
  wire  t_reset; // @[SimpleApb2T.scala 45:17]
  wire [31:0] t_io_apb2T_req_pAddr; // @[SimpleApb2T.scala 45:17]
  wire  t_io_apb2T_req_pSel; // @[SimpleApb2T.scala 45:17]
  wire  t_io_apb2T_req_pEnable; // @[SimpleApb2T.scala 45:17]
  wire  t_io_apb2T_req_pWrite; // @[SimpleApb2T.scala 45:17]
  wire [31:0] t_io_apb2T_req_pWData; // @[SimpleApb2T.scala 45:17]
  wire [3:0] t_io_apb2T_req_pStrb; // @[SimpleApb2T.scala 45:17]
  wire  t_io_apb2T_rsp_pReady; // @[SimpleApb2T.scala 45:17]
  wire [31:0] t_io_apb2T_rsp_pRData; // @[SimpleApb2T.scala 45:17]
  wire  t_io_apb2T_rsp_pSlvErr; // @[SimpleApb2T.scala 45:17]
  wire [7:0] t_io_rwVec_0; // @[SimpleApb2T.scala 45:17]
  wire [7:0] t_io_roVec_0; // @[SimpleApb2T.scala 45:17]
  wire [7:0] t_io_woVec_0; // @[SimpleApb2T.scala 45:17]
  Apb2CSTrgt t ( // @[SimpleApb2T.scala 45:17]
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
  assign io_apb2T_rsp_pReady = t_io_apb2T_rsp_pReady; // @[SimpleApb2T.scala 58:14]
  assign io_apb2T_rsp_pRData = t_io_apb2T_rsp_pRData; // @[SimpleApb2T.scala 58:14]
  assign io_apb2T_rsp_pSlvErr = t_io_apb2T_rsp_pSlvErr; // @[SimpleApb2T.scala 58:14]
  assign io_rw_SimpleRw_RwBits = t_io_rwVec_0; // @[SimpleApb2T.scala 61:25]
  assign io_wo_SimpleRoWo_WoBits = t_io_woVec_0; // @[SimpleApb2T.scala 67:27]
  assign t_clock = clock;
  assign t_reset = reset;
  assign t_io_apb2T_req_pAddr = io_apb2T_req_pAddr; // @[SimpleApb2T.scala 58:14]
  assign t_io_apb2T_req_pSel = io_apb2T_req_pSel; // @[SimpleApb2T.scala 58:14]
  assign t_io_apb2T_req_pEnable = io_apb2T_req_pEnable; // @[SimpleApb2T.scala 58:14]
  assign t_io_apb2T_req_pWrite = io_apb2T_req_pWrite; // @[SimpleApb2T.scala 58:14]
  assign t_io_apb2T_req_pWData = io_apb2T_req_pWData; // @[SimpleApb2T.scala 58:14]
  assign t_io_apb2T_req_pStrb = io_apb2T_req_pStrb; // @[SimpleApb2T.scala 58:14]
  assign t_io_roVec_0 = io_ro_SimpleRoWo_RoBits; // @[SimpleApb2T.scala 64:17]
endmodule
