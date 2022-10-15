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
  output [63:0] io_rwVec_6,
  output [31:0] io_rwVec_5,
  output        io_rwVec_4,
  output [31:0] io_rwVec_3,
  output [15:0] io_rwVec_2,
  output [15:0] io_rwVec_1,
  output        io_rwVec_0,
  input  [7:0]  io_roVec_0,
  output [7:0]  io_woVec_1,
  output        io_woVec_0,
  input  [15:0] io_wcVec_0
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
  reg [31:0] _RAND_8;
  reg [63:0] _RAND_9;
  reg [31:0] _RAND_10;
  reg [31:0] _RAND_11;
  reg [31:0] _RAND_12;
  reg [31:0] _RAND_13;
  reg [31:0] _RAND_14;
  reg [31:0] _RAND_15;
`endif // RANDOMIZE_REG_INIT
  reg  AmbelCtrl_CoreReset; // @[Apb2CSTrgt.scala 335:53]
  reg  AmbelDebugCtrl_Halt; // @[Apb2CSTrgt.scala 335:53]
  reg  AmbelDebugCtrl_Step; // @[Apb2CSTrgt.scala 354:49]
  reg [15:0] AmbelFooBar_Foo; // @[Apb2CSTrgt.scala 335:53]
  reg [15:0] AmbelFooBar_Bar; // @[Apb2CSTrgt.scala 335:53]
  reg [31:0] AmbelBaz0_BazBits; // @[Apb2CSTrgt.scala 335:53]
  reg [31:0] AmbelBaz1_BazBits; // @[Apb2CSTrgt.scala 335:53]
  reg [7:0] AmbelWoGobits_GoBits; // @[Apb2CSTrgt.scala 354:49]
  reg [15:0] AmbelW1cStatus_StausBits; // @[Apb2CSTrgt.scala 361:49]
  reg [63:0] AmbelBigRegExample_BigBits; // @[Apb2CSTrgt.scala 338:53]
  reg [6:0] pAddrFF; // @[Apb2CSTrgt.scala 470:26]
  reg  pWriteFF; // @[Apb2CSTrgt.scala 471:26]
  reg  pReadyFF; // @[Apb2CSTrgt.scala 472:26]
  reg [31:0] pRDataFF; // @[Apb2CSTrgt.scala 473:26]
  reg  pSlvErrFF; // @[Apb2CSTrgt.scala 474:26]
  reg  regAliasFF; // @[Apb2CSTrgt.scala 477:27]
  wire  _GEN_1 = io_apb2T_req_pSel & ~io_apb2T_req_pEnable & io_apb2T_req_pWrite; // @[Apb2CSTrgt.scala 480:52 483:14 490:14]
  wire  _GEN_2 = io_apb2T_req_pSel & ~io_apb2T_req_pEnable ? io_apb2T_req_pWrite : pReadyFF; // @[Apb2CSTrgt.scala 480:52 484:14 472:26]
  wire [3:0] regIndex = pAddrFF[5:2]; // @[Apb2CSTrgt.scala 476:24 494:12]
  wire  _T_2 = regIndex == 4'h0; // @[Apb2CSTrgt.scala 506:24]
  wire  fieldPStrbBits_0 = io_apb2T_req_pStrb[0]; // @[Apb2CSTrgt.scala 528:41]
  wire [31:0] _GEN_5 = fieldPStrbBits_0 ? io_apb2T_req_pWData : {{31'd0}, AmbelCtrl_CoreReset}; // @[Apb2CSTrgt.scala 530:55 546:25 335:53]
  wire  _GEN_6 = fieldPStrbBits_0 ? 1'h0 : fieldPStrbBits_0; // @[Apb2CSTrgt.scala 499:15 530:55]
  wire [31:0] _GEN_7 = regIndex == 4'h0 ? _GEN_5 : {{31'd0}, AmbelCtrl_CoreReset}; // @[Apb2CSTrgt.scala 506:33 335:53]
  wire  _GEN_8 = regIndex == 4'h0 & _GEN_6; // @[Apb2CSTrgt.scala 499:15 506:33]
  wire  _T_3 = regIndex == 4'h1; // @[Apb2CSTrgt.scala 506:24]
  wire  _GEN_9 = fieldPStrbBits_0 | _GEN_8; // @[Apb2CSTrgt.scala 548:61 550:27]
  wire [31:0] _GEN_10 = fieldPStrbBits_0 ? io_apb2T_req_pWData : {{31'd0}, AmbelDebugCtrl_Halt}; // @[Apb2CSTrgt.scala 530:55 546:25 335:53]
  wire  _GEN_11 = fieldPStrbBits_0 ? _GEN_8 : _GEN_9; // @[Apb2CSTrgt.scala 530:55]
  wire  _GEN_12 = fieldPStrbBits_0 | _GEN_11; // @[Apb2CSTrgt.scala 548:61 550:27]
  wire [30:0] _GEN_13 = fieldPStrbBits_0 ? io_apb2T_req_pWData[31:1] : {{30'd0}, AmbelDebugCtrl_Step}; // @[Apb2CSTrgt.scala 530:55 546:25 354:49]
  wire  _GEN_14 = fieldPStrbBits_0 ? _GEN_11 : _GEN_12; // @[Apb2CSTrgt.scala 530:55]
  wire [31:0] _GEN_15 = regIndex == 4'h1 ? _GEN_10 : {{31'd0}, AmbelDebugCtrl_Halt}; // @[Apb2CSTrgt.scala 506:33 335:53]
  wire  _GEN_16 = regIndex == 4'h1 ? _GEN_14 : _GEN_8; // @[Apb2CSTrgt.scala 506:33]
  wire [30:0] _GEN_17 = regIndex == 4'h1 ? _GEN_13 : {{30'd0}, AmbelDebugCtrl_Step}; // @[Apb2CSTrgt.scala 506:33 354:49]
  wire  _T_4 = regIndex == 4'h2; // @[Apb2CSTrgt.scala 506:24]
  wire  fieldPStrbBits_1 = io_apb2T_req_pStrb[1]; // @[Apb2CSTrgt.scala 528:41]
  wire  _GEN_18 = fieldPStrbBits_0 | fieldPStrbBits_1 | _GEN_16; // @[Apb2CSTrgt.scala 548:61 550:27]
  wire [31:0] _GEN_19 = fieldPStrbBits_0 & fieldPStrbBits_1 ? io_apb2T_req_pWData : {{16'd0}, AmbelFooBar_Foo}; // @[Apb2CSTrgt.scala 530:55 546:25 335:53]
  wire  _GEN_20 = fieldPStrbBits_0 & fieldPStrbBits_1 ? _GEN_16 : _GEN_18; // @[Apb2CSTrgt.scala 530:55]
  wire  fieldPStrbBits_0_4 = io_apb2T_req_pStrb[2]; // @[Apb2CSTrgt.scala 528:41]
  wire  fieldPStrbBits_1_1 = io_apb2T_req_pStrb[3]; // @[Apb2CSTrgt.scala 528:41]
  wire  _GEN_21 = fieldPStrbBits_0_4 | fieldPStrbBits_1_1 | _GEN_20; // @[Apb2CSTrgt.scala 548:61 550:27]
  wire [15:0] _GEN_22 = fieldPStrbBits_0_4 & fieldPStrbBits_1_1 ? io_apb2T_req_pWData[31:16] : AmbelFooBar_Bar; // @[Apb2CSTrgt.scala 530:55 546:25 335:53]
  wire  _GEN_23 = fieldPStrbBits_0_4 & fieldPStrbBits_1_1 ? _GEN_20 : _GEN_21; // @[Apb2CSTrgt.scala 530:55]
  wire [31:0] _GEN_24 = regIndex == 4'h2 ? _GEN_19 : {{16'd0}, AmbelFooBar_Foo}; // @[Apb2CSTrgt.scala 506:33 335:53]
  wire  _GEN_25 = regIndex == 4'h2 ? _GEN_23 : _GEN_16; // @[Apb2CSTrgt.scala 506:33]
  wire  _T_9 = regIndex == 4'h3; // @[Apb2CSTrgt.scala 506:24]
  wire  _GEN_27 = fieldPStrbBits_0 | fieldPStrbBits_1 | fieldPStrbBits_0_4 | fieldPStrbBits_1_1 | _GEN_25; // @[Apb2CSTrgt.scala 548:61 550:27]
  wire [31:0] _GEN_28 = fieldPStrbBits_0 & fieldPStrbBits_1 & fieldPStrbBits_0_4 & fieldPStrbBits_1_1 ?
    io_apb2T_req_pWData : AmbelBaz0_BazBits; // @[Apb2CSTrgt.scala 530:55 546:25 335:53]
  wire  _GEN_29 = fieldPStrbBits_0 & fieldPStrbBits_1 & fieldPStrbBits_0_4 & fieldPStrbBits_1_1 ? _GEN_25 : _GEN_27; // @[Apb2CSTrgt.scala 530:55]
  wire  _GEN_31 = regIndex == 4'h3 ? _GEN_29 : _GEN_25; // @[Apb2CSTrgt.scala 506:33]
  wire  _T_16 = regIndex == 4'h4; // @[Apb2CSTrgt.scala 506:24]
  wire  _GEN_32 = fieldPStrbBits_0 | fieldPStrbBits_1 | fieldPStrbBits_0_4 | fieldPStrbBits_1_1 | _GEN_31; // @[Apb2CSTrgt.scala 548:61 550:27]
  wire [31:0] _GEN_33 = fieldPStrbBits_0 & fieldPStrbBits_1 & fieldPStrbBits_0_4 & fieldPStrbBits_1_1 ?
    io_apb2T_req_pWData : AmbelBaz1_BazBits; // @[Apb2CSTrgt.scala 530:55 546:25 335:53]
  wire  _GEN_34 = fieldPStrbBits_0 & fieldPStrbBits_1 & fieldPStrbBits_0_4 & fieldPStrbBits_1_1 ? _GEN_31 : _GEN_32; // @[Apb2CSTrgt.scala 530:55]
  wire  _GEN_36 = regIndex == 4'h4 ? _GEN_34 : _GEN_31; // @[Apb2CSTrgt.scala 506:33]
  wire  _T_23 = regIndex == 4'h5; // @[Apb2CSTrgt.scala 506:24]
  wire  _GEN_37 = regIndex == 4'h5 | _GEN_36; // @[Apb2CSTrgt.scala 506:33 553:25]
  wire  _T_24 = regIndex == 4'h6; // @[Apb2CSTrgt.scala 506:24]
  wire  _T_25 = regIndex == 4'h7; // @[Apb2CSTrgt.scala 506:24]
  wire  _GEN_38 = fieldPStrbBits_0 | _GEN_37; // @[Apb2CSTrgt.scala 548:61 550:27]
  wire [31:0] _GEN_39 = fieldPStrbBits_0 ? io_apb2T_req_pWData : {{24'd0}, AmbelWoGobits_GoBits}; // @[Apb2CSTrgt.scala 530:55 546:25 354:49]
  wire  _GEN_40 = fieldPStrbBits_0 ? _GEN_37 : _GEN_38; // @[Apb2CSTrgt.scala 530:55]
  wire [31:0] _GEN_41 = regIndex == 4'h7 ? _GEN_39 : {{24'd0}, AmbelWoGobits_GoBits}; // @[Apb2CSTrgt.scala 506:33 354:49]
  wire  _GEN_42 = regIndex == 4'h7 ? _GEN_40 : _GEN_37; // @[Apb2CSTrgt.scala 506:33]
  wire  _T_26 = regIndex == 4'h8; // @[Apb2CSTrgt.scala 506:24]
  wire  _GEN_43 = regIndex == 4'h8 | _GEN_42; // @[Apb2CSTrgt.scala 506:33 553:25]
  wire  _T_27 = regIndex == 4'h9; // @[Apb2CSTrgt.scala 506:24]
  wire  clrBits_0 = io_apb2T_req_pWData[0]; // @[Apb2CSTrgt.scala 537:72]
  wire  clrBits_1 = io_apb2T_req_pWData[1]; // @[Apb2CSTrgt.scala 537:72]
  wire  clrBits_2 = io_apb2T_req_pWData[2]; // @[Apb2CSTrgt.scala 537:72]
  wire  clrBits_3 = io_apb2T_req_pWData[3]; // @[Apb2CSTrgt.scala 537:72]
  wire  clrBits_4 = io_apb2T_req_pWData[4]; // @[Apb2CSTrgt.scala 537:72]
  wire  clrBits_5 = io_apb2T_req_pWData[5]; // @[Apb2CSTrgt.scala 537:72]
  wire  clrBits_6 = io_apb2T_req_pWData[6]; // @[Apb2CSTrgt.scala 537:72]
  wire  clrBits_7 = io_apb2T_req_pWData[7]; // @[Apb2CSTrgt.scala 537:72]
  wire  clrBits_8 = io_apb2T_req_pWData[8]; // @[Apb2CSTrgt.scala 537:72]
  wire  clrBits_9 = io_apb2T_req_pWData[9]; // @[Apb2CSTrgt.scala 537:72]
  wire  clrBits_10 = io_apb2T_req_pWData[10]; // @[Apb2CSTrgt.scala 537:72]
  wire  clrBits_11 = io_apb2T_req_pWData[11]; // @[Apb2CSTrgt.scala 537:72]
  wire  clrBits_12 = io_apb2T_req_pWData[12]; // @[Apb2CSTrgt.scala 537:72]
  wire  clrBits_13 = io_apb2T_req_pWData[13]; // @[Apb2CSTrgt.scala 537:72]
  wire  clrBits_14 = io_apb2T_req_pWData[14]; // @[Apb2CSTrgt.scala 537:72]
  wire  clrBits_15 = io_apb2T_req_pWData[15]; // @[Apb2CSTrgt.scala 537:72]
  wire  nxtBits__9 = clrBits_9 ? 1'h0 : AmbelW1cStatus_StausBits[9]; // @[Apb2CSTrgt.scala 539:32 540:27 536:40]
  wire  nxtBits__0 = clrBits_0 ? 1'h0 : AmbelW1cStatus_StausBits[0]; // @[Apb2CSTrgt.scala 539:32 540:27 536:40]
  wire  nxtBits__3 = clrBits_3 ? 1'h0 : AmbelW1cStatus_StausBits[3]; // @[Apb2CSTrgt.scala 539:32 540:27 536:40]
  wire  nxtBits__6 = clrBits_6 ? 1'h0 : AmbelW1cStatus_StausBits[6]; // @[Apb2CSTrgt.scala 539:32 540:27 536:40]
  wire  nxtBits__14 = clrBits_14 ? 1'h0 : AmbelW1cStatus_StausBits[14]; // @[Apb2CSTrgt.scala 539:32 540:27 536:40]
  wire  nxtBits__10 = clrBits_10 ? 1'h0 : AmbelW1cStatus_StausBits[10]; // @[Apb2CSTrgt.scala 539:32 540:27 536:40]
  wire  nxtBits__15 = clrBits_15 ? 1'h0 : AmbelW1cStatus_StausBits[15]; // @[Apb2CSTrgt.scala 539:32 540:27 536:40]
  wire  nxtBits__8 = clrBits_8 ? 1'h0 : AmbelW1cStatus_StausBits[8]; // @[Apb2CSTrgt.scala 539:32 540:27 536:40]
  wire  nxtBits__1 = clrBits_1 ? 1'h0 : AmbelW1cStatus_StausBits[1]; // @[Apb2CSTrgt.scala 539:32 540:27 536:40]
  wire  nxtBits__2 = clrBits_2 ? 1'h0 : AmbelW1cStatus_StausBits[2]; // @[Apb2CSTrgt.scala 539:32 540:27 536:40]
  wire  nxtBits__7 = clrBits_7 ? 1'h0 : AmbelW1cStatus_StausBits[7]; // @[Apb2CSTrgt.scala 539:32 540:27 536:40]
  wire  nxtBits__12 = clrBits_12 ? 1'h0 : AmbelW1cStatus_StausBits[12]; // @[Apb2CSTrgt.scala 539:32 540:27 536:40]
  wire  nxtBits__13 = clrBits_13 ? 1'h0 : AmbelW1cStatus_StausBits[13]; // @[Apb2CSTrgt.scala 539:32 540:27 536:40]
  wire  nxtBits__4 = clrBits_4 ? 1'h0 : AmbelW1cStatus_StausBits[4]; // @[Apb2CSTrgt.scala 539:32 540:27 536:40]
  wire  nxtBits__5 = clrBits_5 ? 1'h0 : AmbelW1cStatus_StausBits[5]; // @[Apb2CSTrgt.scala 539:32 540:27 536:40]
  wire  nxtBits__11 = clrBits_11 ? 1'h0 : AmbelW1cStatus_StausBits[11]; // @[Apb2CSTrgt.scala 539:32 540:27 536:40]
  wire [7:0] AmbelW1cStatus_StausBits_lo = {nxtBits__7,nxtBits__6,nxtBits__5,nxtBits__4,nxtBits__3,nxtBits__2,nxtBits__1
    ,nxtBits__0}; // @[Apb2CSTrgt.scala 543:36]
  wire [15:0] _AmbelW1cStatus_StausBits_T = {nxtBits__15,nxtBits__14,nxtBits__13,nxtBits__12,nxtBits__11,nxtBits__10,
    nxtBits__9,nxtBits__8,AmbelW1cStatus_StausBits_lo}; // @[Apb2CSTrgt.scala 543:36]
  wire  _GEN_60 = fieldPStrbBits_0 | fieldPStrbBits_1 | _GEN_43; // @[Apb2CSTrgt.scala 548:61 550:27]
  wire [15:0] _GEN_61 = fieldPStrbBits_0 & fieldPStrbBits_1 ? _AmbelW1cStatus_StausBits_T : AmbelW1cStatus_StausBits; // @[Apb2CSTrgt.scala 530:55 543:25 361:49]
  wire  _GEN_62 = fieldPStrbBits_0 & fieldPStrbBits_1 ? _GEN_43 : _GEN_60; // @[Apb2CSTrgt.scala 530:55]
  wire  _GEN_64 = regIndex == 4'h9 ? _GEN_62 : _GEN_43; // @[Apb2CSTrgt.scala 506:33]
  wire  _T_30 = regIndex == 4'ha; // @[Apb2CSTrgt.scala 506:24]
  wire  _GEN_65 = fieldPStrbBits_0 | fieldPStrbBits_1 | fieldPStrbBits_0_4 | fieldPStrbBits_1_1 | _GEN_64; // @[Apb2CSTrgt.scala 548:61 550:27]
  wire [63:0] _GEN_66 = fieldPStrbBits_0 & fieldPStrbBits_1 & fieldPStrbBits_0_4 & fieldPStrbBits_1_1 ? {{32'd0},
    io_apb2T_req_pWData} : AmbelBigRegExample_BigBits; // @[Apb2CSTrgt.scala 530:55 546:25 338:53]
  wire  _GEN_67 = fieldPStrbBits_0 & fieldPStrbBits_1 & fieldPStrbBits_0_4 & fieldPStrbBits_1_1 ? _GEN_64 : _GEN_65; // @[Apb2CSTrgt.scala 530:55]
  wire  _GEN_69 = regIndex == 4'ha ? _GEN_67 : _GEN_64; // @[Apb2CSTrgt.scala 506:33]
  wire  _T_37 = regIndex == 4'hb; // @[Apb2CSTrgt.scala 506:24]
  wire  _T_38 = regIndex == 4'hc; // @[Apb2CSTrgt.scala 506:24]
  wire  _T_39 = regIndex == 4'hd; // @[Apb2CSTrgt.scala 506:24]
  wire  _T_40 = regIndex == 4'he; // @[Apb2CSTrgt.scala 506:24]
  wire  _T_41 = regIndex == 4'hf; // @[Apb2CSTrgt.scala 506:24]
  wire  _GEN_74 = regIndex == 4'hf | (regIndex == 4'he | (regIndex == 4'hd | (regIndex == 4'hc | (regIndex == 4'hb |
    _GEN_69)))); // @[Apb2CSTrgt.scala 506:33 553:25]
  wire  _GEN_75 = regAliasFF | _GEN_74; // @[Apb2CSTrgt.scala 501:23 502:17]
  wire [31:0] _GEN_76 = regAliasFF ? {{31'd0}, AmbelCtrl_CoreReset} : _GEN_7; // @[Apb2CSTrgt.scala 501:23 335:53]
  wire [31:0] _GEN_77 = regAliasFF ? {{31'd0}, AmbelDebugCtrl_Halt} : _GEN_15; // @[Apb2CSTrgt.scala 501:23 335:53]
  wire [30:0] _GEN_78 = regAliasFF ? {{30'd0}, AmbelDebugCtrl_Step} : _GEN_17; // @[Apb2CSTrgt.scala 501:23 354:49]
  wire [31:0] _GEN_79 = regAliasFF ? {{16'd0}, AmbelFooBar_Foo} : _GEN_24; // @[Apb2CSTrgt.scala 501:23 335:53]
  wire [31:0] _GEN_83 = regAliasFF ? {{24'd0}, AmbelWoGobits_GoBits} : _GEN_41; // @[Apb2CSTrgt.scala 501:23 354:49]
  wire  setBits_0 = io_wcVec_0[0]; // @[Apb2CSTrgt.scala 572:43]
  wire  setBits_1 = io_wcVec_0[1]; // @[Apb2CSTrgt.scala 572:43]
  wire  setBits_2 = io_wcVec_0[2]; // @[Apb2CSTrgt.scala 572:43]
  wire  setBits_3 = io_wcVec_0[3]; // @[Apb2CSTrgt.scala 572:43]
  wire  setBits_4 = io_wcVec_0[4]; // @[Apb2CSTrgt.scala 572:43]
  wire  setBits_5 = io_wcVec_0[5]; // @[Apb2CSTrgt.scala 572:43]
  wire  setBits_6 = io_wcVec_0[6]; // @[Apb2CSTrgt.scala 572:43]
  wire  setBits_7 = io_wcVec_0[7]; // @[Apb2CSTrgt.scala 572:43]
  wire  setBits_8 = io_wcVec_0[8]; // @[Apb2CSTrgt.scala 572:43]
  wire  setBits_9 = io_wcVec_0[9]; // @[Apb2CSTrgt.scala 572:43]
  wire  setBits_10 = io_wcVec_0[10]; // @[Apb2CSTrgt.scala 572:43]
  wire  setBits_11 = io_wcVec_0[11]; // @[Apb2CSTrgt.scala 572:43]
  wire  setBits_12 = io_wcVec_0[12]; // @[Apb2CSTrgt.scala 572:43]
  wire  setBits_13 = io_wcVec_0[13]; // @[Apb2CSTrgt.scala 572:43]
  wire  setBits_14 = io_wcVec_0[14]; // @[Apb2CSTrgt.scala 572:43]
  wire  setBits_15 = io_wcVec_0[15]; // @[Apb2CSTrgt.scala 572:43]
  wire  nxtBits_1_2 = setBits_2 | AmbelW1cStatus_StausBits[2]; // @[Apb2CSTrgt.scala 574:24 575:19 571:32]
  wire  nxtBits_1_6 = setBits_6 | AmbelW1cStatus_StausBits[6]; // @[Apb2CSTrgt.scala 574:24 575:19 571:32]
  wire  nxtBits_1_10 = setBits_10 | AmbelW1cStatus_StausBits[10]; // @[Apb2CSTrgt.scala 574:24 575:19 571:32]
  wire  nxtBits_1_4 = setBits_4 | AmbelW1cStatus_StausBits[4]; // @[Apb2CSTrgt.scala 574:24 575:19 571:32]
  wire  nxtBits_1_0 = setBits_0 | AmbelW1cStatus_StausBits[0]; // @[Apb2CSTrgt.scala 574:24 575:19 571:32]
  wire  nxtBits_1_11 = setBits_11 | AmbelW1cStatus_StausBits[11]; // @[Apb2CSTrgt.scala 574:24 575:19 571:32]
  wire  nxtBits_1_15 = setBits_15 | AmbelW1cStatus_StausBits[15]; // @[Apb2CSTrgt.scala 574:24 575:19 571:32]
  wire  nxtBits_1_12 = setBits_12 | AmbelW1cStatus_StausBits[12]; // @[Apb2CSTrgt.scala 574:24 575:19 571:32]
  wire  nxtBits_1_5 = setBits_5 | AmbelW1cStatus_StausBits[5]; // @[Apb2CSTrgt.scala 574:24 575:19 571:32]
  wire  nxtBits_1_13 = setBits_13 | AmbelW1cStatus_StausBits[13]; // @[Apb2CSTrgt.scala 574:24 575:19 571:32]
  wire  nxtBits_1_14 = setBits_14 | AmbelW1cStatus_StausBits[14]; // @[Apb2CSTrgt.scala 574:24 575:19 571:32]
  wire  nxtBits_1_9 = setBits_9 | AmbelW1cStatus_StausBits[9]; // @[Apb2CSTrgt.scala 574:24 575:19 571:32]
  wire  nxtBits_1_7 = setBits_7 | AmbelW1cStatus_StausBits[7]; // @[Apb2CSTrgt.scala 574:24 575:19 571:32]
  wire  nxtBits_1_3 = setBits_3 | AmbelW1cStatus_StausBits[3]; // @[Apb2CSTrgt.scala 574:24 575:19 571:32]
  wire  nxtBits_1_1 = setBits_1 | AmbelW1cStatus_StausBits[1]; // @[Apb2CSTrgt.scala 574:24 575:19 571:32]
  wire  nxtBits_1_8 = setBits_8 | AmbelW1cStatus_StausBits[8]; // @[Apb2CSTrgt.scala 574:24 575:19 571:32]
  wire [7:0] AmbelW1cStatus_StausBits_lo_1 = {nxtBits_1_7,nxtBits_1_6,nxtBits_1_5,nxtBits_1_4,nxtBits_1_3,nxtBits_1_2,
    nxtBits_1_1,nxtBits_1_0}; // @[Apb2CSTrgt.scala 578:28]
  wire [15:0] _AmbelW1cStatus_StausBits_T_1 = {nxtBits_1_15,nxtBits_1_14,nxtBits_1_13,nxtBits_1_12,nxtBits_1_11,
    nxtBits_1_10,nxtBits_1_9,nxtBits_1_8,AmbelW1cStatus_StausBits_lo_1}; // @[Apb2CSTrgt.scala 578:28]
  wire [31:0] _GEN_104 = pWriteFF ? _GEN_76 : {{31'd0}, AmbelCtrl_CoreReset}; // @[Apb2CSTrgt.scala 496:19 335:53]
  wire [31:0] _GEN_105 = pWriteFF ? _GEN_77 : {{31'd0}, AmbelDebugCtrl_Halt}; // @[Apb2CSTrgt.scala 496:19 335:53]
  wire [30:0] _GEN_106 = pWriteFF ? _GEN_78 : 31'h0; // @[Apb2CSTrgt.scala 496:19 561:70]
  wire [31:0] _GEN_107 = pWriteFF ? _GEN_79 : {{16'd0}, AmbelFooBar_Foo}; // @[Apb2CSTrgt.scala 496:19 335:53]
  wire [31:0] _GEN_111 = pWriteFF ? _GEN_83 : 32'h0; // @[Apb2CSTrgt.scala 496:19 561:70]
  wire  _GEN_115 = _T_2 & AmbelCtrl_CoreReset; // @[Apb2CSTrgt.scala 586:15 596:31 600:18]
  wire [1:0] shiftedBits_1 = {AmbelDebugCtrl_Step, 1'h0}; // @[Apb2CSTrgt.scala 599:80]
  wire [1:0] _GEN_141 = {{1'd0}, AmbelDebugCtrl_Halt}; // @[Apb2CSTrgt.scala 600:46]
  wire [1:0] _pRDataFF_T = _GEN_141 | shiftedBits_1; // @[Apb2CSTrgt.scala 600:46]
  wire [1:0] _GEN_116 = _T_3 ? _pRDataFF_T : {{1'd0}, _GEN_115}; // @[Apb2CSTrgt.scala 596:31 600:18]
  wire [31:0] shiftedBits_1_1 = {AmbelFooBar_Bar, 16'h0}; // @[Apb2CSTrgt.scala 599:80]
  wire [31:0] _GEN_142 = {{16'd0}, AmbelFooBar_Foo}; // @[Apb2CSTrgt.scala 600:46]
  wire [31:0] _pRDataFF_T_1 = _GEN_142 | shiftedBits_1_1; // @[Apb2CSTrgt.scala 600:46]
  wire [31:0] _GEN_117 = _T_4 ? _pRDataFF_T_1 : {{30'd0}, _GEN_116}; // @[Apb2CSTrgt.scala 596:31 600:18]
  wire [31:0] _GEN_118 = _T_9 ? AmbelBaz0_BazBits : _GEN_117; // @[Apb2CSTrgt.scala 596:31 600:18]
  wire [31:0] _GEN_119 = _T_16 ? AmbelBaz1_BazBits : _GEN_118; // @[Apb2CSTrgt.scala 596:31 600:18]
  wire [31:0] _GEN_120 = _T_23 ? 32'h0 : _GEN_119; // @[Apb2CSTrgt.scala 596:31 600:18]
  wire [31:0] _GEN_122 = _T_24 ? {{24'd0}, io_roVec_0} : _GEN_120; // @[Apb2CSTrgt.scala 596:31 600:18]
  wire [31:0] _GEN_123 = _T_25 ? {{24'd0}, AmbelWoGobits_GoBits} : _GEN_122; // @[Apb2CSTrgt.scala 596:31 600:18]
  wire [31:0] _GEN_124 = _T_26 ? 32'h0 : _GEN_123; // @[Apb2CSTrgt.scala 596:31 600:18]
  wire [31:0] _GEN_126 = _T_27 ? {{16'd0}, AmbelW1cStatus_StausBits} : _GEN_124; // @[Apb2CSTrgt.scala 596:31 600:18]
  wire [63:0] _GEN_127 = _T_30 ? AmbelBigRegExample_BigBits : {{32'd0}, _GEN_126}; // @[Apb2CSTrgt.scala 596:31 600:18]
  wire [63:0] _GEN_128 = _T_37 ? 64'h0 : _GEN_127; // @[Apb2CSTrgt.scala 596:31 600:18]
  wire [63:0] _GEN_130 = _T_38 ? 64'h0 : _GEN_128; // @[Apb2CSTrgt.scala 596:31 600:18]
  wire [63:0] _GEN_132 = _T_39 ? 64'h0 : _GEN_130; // @[Apb2CSTrgt.scala 596:31 600:18]
  wire [63:0] _GEN_134 = _T_40 ? 64'h0 : _GEN_132; // @[Apb2CSTrgt.scala 596:31 600:18]
  wire [63:0] _GEN_136 = _T_41 ? 64'h0 : _GEN_134; // @[Apb2CSTrgt.scala 596:31 600:18]
  wire  _GEN_137 = _T_41 | (_T_40 | (_T_39 | (_T_38 | (_T_37 | (_T_26 | (_T_23 | regAliasFF)))))); // @[Apb2CSTrgt.scala 596:31 602:42]
  wire [63:0] _GEN_138 = ~pReadyFF ? _GEN_136 : {{32'd0}, pRDataFF}; // @[Apb2CSTrgt.scala 584:20 473:26]
  wire  _GEN_139 = ~pReadyFF | _GEN_2; // @[Apb2CSTrgt.scala 584:20 587:15]
  wire [31:0] _GEN_143 = reset ? 32'h0 : _GEN_104; // @[Apb2CSTrgt.scala 335:{53,53}]
  wire [31:0] _GEN_144 = reset ? 32'h0 : _GEN_105; // @[Apb2CSTrgt.scala 335:{53,53}]
  wire [30:0] _GEN_145 = reset ? 31'h0 : _GEN_106; // @[Apb2CSTrgt.scala 354:{49,49}]
  wire [31:0] _GEN_146 = reset ? 32'h7b : _GEN_107; // @[Apb2CSTrgt.scala 335:{53,53}]
  wire [31:0] _GEN_147 = reset ? 32'h0 : _GEN_111; // @[Apb2CSTrgt.scala 354:{49,49}]
  wire [63:0] _GEN_148 = reset ? 64'h0 : _GEN_138; // @[Apb2CSTrgt.scala 473:{26,26}]
  assign io_apb2T_rsp_pReady = pReadyFF; // @[Apb2CSTrgt.scala 608:24]
  assign io_apb2T_rsp_pRData = pRDataFF; // @[Apb2CSTrgt.scala 609:24]
  assign io_apb2T_rsp_pSlvErr = pSlvErrFF; // @[Apb2CSTrgt.scala 610:24]
  assign io_rwVec_6 = AmbelBigRegExample_BigBits; // @[Apb2CSTrgt.scala 453:19]
  assign io_rwVec_5 = AmbelBaz1_BazBits; // @[Apb2CSTrgt.scala 453:19]
  assign io_rwVec_4 = AmbelDebugCtrl_Halt; // @[Apb2CSTrgt.scala 453:19]
  assign io_rwVec_3 = AmbelBaz0_BazBits; // @[Apb2CSTrgt.scala 453:19]
  assign io_rwVec_2 = AmbelFooBar_Bar; // @[Apb2CSTrgt.scala 453:19]
  assign io_rwVec_1 = AmbelFooBar_Foo; // @[Apb2CSTrgt.scala 453:19]
  assign io_rwVec_0 = AmbelCtrl_CoreReset; // @[Apb2CSTrgt.scala 453:19]
  assign io_woVec_1 = AmbelWoGobits_GoBits; // @[Apb2CSTrgt.scala 465:19]
  assign io_woVec_0 = AmbelDebugCtrl_Step; // @[Apb2CSTrgt.scala 465:19]
  always @(posedge clock) begin
    AmbelCtrl_CoreReset <= _GEN_143[0]; // @[Apb2CSTrgt.scala 335:{53,53}]
    AmbelDebugCtrl_Halt <= _GEN_144[0]; // @[Apb2CSTrgt.scala 335:{53,53}]
    AmbelDebugCtrl_Step <= _GEN_145[0]; // @[Apb2CSTrgt.scala 354:{49,49}]
    AmbelFooBar_Foo <= _GEN_146[15:0]; // @[Apb2CSTrgt.scala 335:{53,53}]
    if (reset) begin // @[Apb2CSTrgt.scala 335:53]
      AmbelFooBar_Bar <= 16'h1c8; // @[Apb2CSTrgt.scala 335:53]
    end else if (pWriteFF) begin // @[Apb2CSTrgt.scala 496:19]
      if (!(regAliasFF)) begin // @[Apb2CSTrgt.scala 501:23]
        if (regIndex == 4'h2) begin // @[Apb2CSTrgt.scala 506:33]
          AmbelFooBar_Bar <= _GEN_22;
        end
      end
    end
    if (reset) begin // @[Apb2CSTrgt.scala 335:53]
      AmbelBaz0_BazBits <= 32'h1e240; // @[Apb2CSTrgt.scala 335:53]
    end else if (pWriteFF) begin // @[Apb2CSTrgt.scala 496:19]
      if (!(regAliasFF)) begin // @[Apb2CSTrgt.scala 501:23]
        if (regIndex == 4'h3) begin // @[Apb2CSTrgt.scala 506:33]
          AmbelBaz0_BazBits <= _GEN_28;
        end
      end
    end
    if (reset) begin // @[Apb2CSTrgt.scala 335:53]
      AmbelBaz1_BazBits <= 32'h1e240; // @[Apb2CSTrgt.scala 335:53]
    end else if (pWriteFF) begin // @[Apb2CSTrgt.scala 496:19]
      if (!(regAliasFF)) begin // @[Apb2CSTrgt.scala 501:23]
        if (regIndex == 4'h4) begin // @[Apb2CSTrgt.scala 506:33]
          AmbelBaz1_BazBits <= _GEN_33;
        end
      end
    end
    AmbelWoGobits_GoBits <= _GEN_147[7:0]; // @[Apb2CSTrgt.scala 354:{49,49}]
    if (reset) begin // @[Apb2CSTrgt.scala 361:49]
      AmbelW1cStatus_StausBits <= 16'h0; // @[Apb2CSTrgt.scala 361:49]
    end else if (pWriteFF) begin // @[Apb2CSTrgt.scala 496:19]
      if (!(regAliasFF)) begin // @[Apb2CSTrgt.scala 501:23]
        if (regIndex == 4'h9) begin // @[Apb2CSTrgt.scala 506:33]
          AmbelW1cStatus_StausBits <= _GEN_61;
        end
      end
    end else begin
      AmbelW1cStatus_StausBits <= _AmbelW1cStatus_StausBits_T_1; // @[Apb2CSTrgt.scala 578:17]
    end
    if (reset) begin // @[Apb2CSTrgt.scala 338:53]
      AmbelBigRegExample_BigBits <= 64'h0; // @[Apb2CSTrgt.scala 338:53]
    end else if (pWriteFF) begin // @[Apb2CSTrgt.scala 496:19]
      if (!(regAliasFF)) begin // @[Apb2CSTrgt.scala 501:23]
        if (regIndex == 4'ha) begin // @[Apb2CSTrgt.scala 506:33]
          AmbelBigRegExample_BigBits <= _GEN_66;
        end
      end
    end
    if (reset) begin // @[Apb2CSTrgt.scala 470:26]
      pAddrFF <= 7'h0; // @[Apb2CSTrgt.scala 470:26]
    end else if (io_apb2T_req_pSel & ~io_apb2T_req_pEnable) begin // @[Apb2CSTrgt.scala 480:52]
      pAddrFF <= io_apb2T_req_pAddr[6:0]; // @[Apb2CSTrgt.scala 482:14]
    end
    if (reset) begin // @[Apb2CSTrgt.scala 471:26]
      pWriteFF <= 1'h0; // @[Apb2CSTrgt.scala 471:26]
    end else if (pWriteFF) begin // @[Apb2CSTrgt.scala 496:19]
      pWriteFF <= 1'h0; // @[Apb2CSTrgt.scala 498:15]
    end else begin
      pWriteFF <= _GEN_1;
    end
    pReadyFF <= reset | _GEN_139; // @[Apb2CSTrgt.scala 472:{26,26}]
    pRDataFF <= _GEN_148[31:0]; // @[Apb2CSTrgt.scala 473:{26,26}]
    if (reset) begin // @[Apb2CSTrgt.scala 474:26]
      pSlvErrFF <= 1'h0; // @[Apb2CSTrgt.scala 474:26]
    end else if (~pReadyFF) begin // @[Apb2CSTrgt.scala 584:20]
      pSlvErrFF <= _GEN_137;
    end else if (pWriteFF) begin // @[Apb2CSTrgt.scala 496:19]
      pSlvErrFF <= _GEN_75;
    end
    if (reset) begin // @[Apb2CSTrgt.scala 477:27]
      regAliasFF <= 1'h0; // @[Apb2CSTrgt.scala 477:27]
    end else if (io_apb2T_req_pSel & ~io_apb2T_req_pEnable) begin // @[Apb2CSTrgt.scala 480:52]
      regAliasFF <= |io_apb2T_req_pAddr[31:6]; // @[Apb2CSTrgt.scala 488:16]
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
  AmbelCtrl_CoreReset = _RAND_0[0:0];
  _RAND_1 = {1{`RANDOM}};
  AmbelDebugCtrl_Halt = _RAND_1[0:0];
  _RAND_2 = {1{`RANDOM}};
  AmbelDebugCtrl_Step = _RAND_2[0:0];
  _RAND_3 = {1{`RANDOM}};
  AmbelFooBar_Foo = _RAND_3[15:0];
  _RAND_4 = {1{`RANDOM}};
  AmbelFooBar_Bar = _RAND_4[15:0];
  _RAND_5 = {1{`RANDOM}};
  AmbelBaz0_BazBits = _RAND_5[31:0];
  _RAND_6 = {1{`RANDOM}};
  AmbelBaz1_BazBits = _RAND_6[31:0];
  _RAND_7 = {1{`RANDOM}};
  AmbelWoGobits_GoBits = _RAND_7[7:0];
  _RAND_8 = {1{`RANDOM}};
  AmbelW1cStatus_StausBits = _RAND_8[15:0];
  _RAND_9 = {2{`RANDOM}};
  AmbelBigRegExample_BigBits = _RAND_9[63:0];
  _RAND_10 = {1{`RANDOM}};
  pAddrFF = _RAND_10[6:0];
  _RAND_11 = {1{`RANDOM}};
  pWriteFF = _RAND_11[0:0];
  _RAND_12 = {1{`RANDOM}};
  pReadyFF = _RAND_12[0:0];
  _RAND_13 = {1{`RANDOM}};
  pRDataFF = _RAND_13[31:0];
  _RAND_14 = {1{`RANDOM}};
  pSlvErrFF = _RAND_14[0:0];
  _RAND_15 = {1{`RANDOM}};
  regAliasFF = _RAND_15[0:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module ExampleApb2T(
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
  output        io_rw_AmbelCtrl_CoreReset,
  output [15:0] io_rw_AmbelFooBar_Foo,
  output [15:0] io_rw_AmbelFooBar_Bar,
  output [31:0] io_rw_AmbelBaz0_BazBits,
  output        io_rw_AmbelDebugCtrl_Halt,
  output [31:0] io_rw_AmbelBaz1_BazBits,
  output [63:0] io_rw_AmbelBigRegExample_BigBits,
  input  [7:0]  io_ro_AmbelRoExample_StatusBits,
  output        io_wo_AmbelDebugCtrl_Step,
  output [7:0]  io_wo_AmbelWoGobits_GoBits,
  input  [15:0] io_wc_AmbelW1cStatus_StausBits
);
  wire  t_clock; // @[ExampleApb2T.scala 55:17]
  wire  t_reset; // @[ExampleApb2T.scala 55:17]
  wire [31:0] t_io_apb2T_req_pAddr; // @[ExampleApb2T.scala 55:17]
  wire  t_io_apb2T_req_pSel; // @[ExampleApb2T.scala 55:17]
  wire  t_io_apb2T_req_pEnable; // @[ExampleApb2T.scala 55:17]
  wire  t_io_apb2T_req_pWrite; // @[ExampleApb2T.scala 55:17]
  wire [31:0] t_io_apb2T_req_pWData; // @[ExampleApb2T.scala 55:17]
  wire [3:0] t_io_apb2T_req_pStrb; // @[ExampleApb2T.scala 55:17]
  wire  t_io_apb2T_rsp_pReady; // @[ExampleApb2T.scala 55:17]
  wire [31:0] t_io_apb2T_rsp_pRData; // @[ExampleApb2T.scala 55:17]
  wire  t_io_apb2T_rsp_pSlvErr; // @[ExampleApb2T.scala 55:17]
  wire [63:0] t_io_rwVec_6; // @[ExampleApb2T.scala 55:17]
  wire [31:0] t_io_rwVec_5; // @[ExampleApb2T.scala 55:17]
  wire  t_io_rwVec_4; // @[ExampleApb2T.scala 55:17]
  wire [31:0] t_io_rwVec_3; // @[ExampleApb2T.scala 55:17]
  wire [15:0] t_io_rwVec_2; // @[ExampleApb2T.scala 55:17]
  wire [15:0] t_io_rwVec_1; // @[ExampleApb2T.scala 55:17]
  wire  t_io_rwVec_0; // @[ExampleApb2T.scala 55:17]
  wire [7:0] t_io_roVec_0; // @[ExampleApb2T.scala 55:17]
  wire [7:0] t_io_woVec_1; // @[ExampleApb2T.scala 55:17]
  wire  t_io_woVec_0; // @[ExampleApb2T.scala 55:17]
  wire [15:0] t_io_wcVec_0; // @[ExampleApb2T.scala 55:17]
  Apb2CSTrgt t ( // @[ExampleApb2T.scala 55:17]
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
    .io_rwVec_6(t_io_rwVec_6),
    .io_rwVec_5(t_io_rwVec_5),
    .io_rwVec_4(t_io_rwVec_4),
    .io_rwVec_3(t_io_rwVec_3),
    .io_rwVec_2(t_io_rwVec_2),
    .io_rwVec_1(t_io_rwVec_1),
    .io_rwVec_0(t_io_rwVec_0),
    .io_roVec_0(t_io_roVec_0),
    .io_woVec_1(t_io_woVec_1),
    .io_woVec_0(t_io_woVec_0),
    .io_wcVec_0(t_io_wcVec_0)
  );
  assign io_apb2T_rsp_pReady = t_io_apb2T_rsp_pReady; // @[ExampleApb2T.scala 69:14]
  assign io_apb2T_rsp_pRData = t_io_apb2T_rsp_pRData; // @[ExampleApb2T.scala 69:14]
  assign io_apb2T_rsp_pSlvErr = t_io_apb2T_rsp_pSlvErr; // @[ExampleApb2T.scala 69:14]
  assign io_rw_AmbelCtrl_CoreReset = t_io_rwVec_0; // @[ExampleApb2T.scala 72:29]
  assign io_rw_AmbelFooBar_Foo = t_io_rwVec_1; // @[ExampleApb2T.scala 73:25]
  assign io_rw_AmbelFooBar_Bar = t_io_rwVec_2; // @[ExampleApb2T.scala 74:25]
  assign io_rw_AmbelBaz0_BazBits = t_io_rwVec_3; // @[ExampleApb2T.scala 75:27]
  assign io_rw_AmbelDebugCtrl_Halt = t_io_rwVec_4; // @[ExampleApb2T.scala 76:29]
  assign io_rw_AmbelBaz1_BazBits = t_io_rwVec_5; // @[ExampleApb2T.scala 77:27]
  assign io_rw_AmbelBigRegExample_BigBits = t_io_rwVec_6; // @[ExampleApb2T.scala 78:36]
  assign io_wo_AmbelDebugCtrl_Step = t_io_woVec_0; // @[ExampleApb2T.scala 84:29]
  assign io_wo_AmbelWoGobits_GoBits = t_io_woVec_1; // @[ExampleApb2T.scala 85:30]
  assign t_clock = clock;
  assign t_reset = reset;
  assign t_io_apb2T_req_pAddr = io_apb2T_req_pAddr; // @[ExampleApb2T.scala 69:14]
  assign t_io_apb2T_req_pSel = io_apb2T_req_pSel; // @[ExampleApb2T.scala 69:14]
  assign t_io_apb2T_req_pEnable = io_apb2T_req_pEnable; // @[ExampleApb2T.scala 69:14]
  assign t_io_apb2T_req_pWrite = io_apb2T_req_pWrite; // @[ExampleApb2T.scala 69:14]
  assign t_io_apb2T_req_pWData = io_apb2T_req_pWData; // @[ExampleApb2T.scala 69:14]
  assign t_io_apb2T_req_pStrb = io_apb2T_req_pStrb; // @[ExampleApb2T.scala 69:14]
  assign t_io_roVec_0 = io_ro_AmbelRoExample_StatusBits; // @[ExampleApb2T.scala 81:17]
  assign t_io_wcVec_0 = io_wc_AmbelW1cStatus_StausBits; // @[ExampleApb2T.scala 88:17]
endmodule
