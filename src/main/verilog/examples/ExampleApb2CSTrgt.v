module Apb2CSTrgt(
  input         clock,
  input         reset,
  input  [5:0]  io_apb2T_req_pAddr,
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
`endif // RANDOMIZE_REG_INIT
  reg  AmbelCtrl_CoreReset; // @[Apb2CSTrgt.scala 322:53]
  reg  AmbelDebugCtrl_Halt; // @[Apb2CSTrgt.scala 322:53]
  reg  AmbelDebugCtrl_Step; // @[Apb2CSTrgt.scala 341:49]
  reg [15:0] AmbelFooBar_Foo; // @[Apb2CSTrgt.scala 322:53]
  reg [15:0] AmbelFooBar_Bar; // @[Apb2CSTrgt.scala 322:53]
  reg [31:0] AmbelBaz0_BazBits; // @[Apb2CSTrgt.scala 322:53]
  reg [31:0] AmbelBaz1_BazBits; // @[Apb2CSTrgt.scala 322:53]
  reg [7:0] AmbelWoGobits_GoBits; // @[Apb2CSTrgt.scala 341:49]
  reg [15:0] AmbelW1cStatus_StausBits; // @[Apb2CSTrgt.scala 348:49]
  reg [63:0] AmbelBigRegExample_BigBits; // @[Apb2CSTrgt.scala 325:53]
  reg [5:0] pAddrFF; // @[Apb2CSTrgt.scala 430:26]
  reg  pWriteFF; // @[Apb2CSTrgt.scala 431:26]
  reg  pReadyFF; // @[Apb2CSTrgt.scala 432:26]
  reg [31:0] pRDataFF; // @[Apb2CSTrgt.scala 433:26]
  reg  pSlvErrFF; // @[Apb2CSTrgt.scala 434:26]
  wire  _GEN_1 = io_apb2T_req_pSel & ~io_apb2T_req_pEnable & io_apb2T_req_pWrite; // @[Apb2CSTrgt.scala 439:52 441:14 448:14]
  wire  _GEN_2 = io_apb2T_req_pSel & ~io_apb2T_req_pEnable ? io_apb2T_req_pWrite : pReadyFF; // @[Apb2CSTrgt.scala 439:52 442:14 432:26]
  wire [3:0] regIndex = pAddrFF[5:2]; // @[Apb2CSTrgt.scala 452:23]
  wire  _T_2 = regIndex == 4'h0; // @[Apb2CSTrgt.scala 460:22]
  wire  fieldPStrbBits_0 = io_apb2T_req_pStrb[0]; // @[Apb2CSTrgt.scala 482:39]
  wire [31:0] _GEN_4 = fieldPStrbBits_0 ? io_apb2T_req_pWData : {{31'd0}, AmbelCtrl_CoreReset}; // @[Apb2CSTrgt.scala 484:53 500:23 322:53]
  wire  _GEN_5 = fieldPStrbBits_0 ? 1'h0 : fieldPStrbBits_0; // @[Apb2CSTrgt.scala 457:15 484:53]
  wire [31:0] _GEN_6 = regIndex == 4'h0 ? _GEN_4 : {{31'd0}, AmbelCtrl_CoreReset}; // @[Apb2CSTrgt.scala 460:31 322:53]
  wire  _GEN_7 = regIndex == 4'h0 & _GEN_5; // @[Apb2CSTrgt.scala 457:15 460:31]
  wire  _T_3 = regIndex == 4'h1; // @[Apb2CSTrgt.scala 460:22]
  wire  _GEN_8 = fieldPStrbBits_0 | _GEN_7; // @[Apb2CSTrgt.scala 502:59 504:25]
  wire [31:0] _GEN_9 = fieldPStrbBits_0 ? io_apb2T_req_pWData : {{31'd0}, AmbelDebugCtrl_Halt}; // @[Apb2CSTrgt.scala 484:53 500:23 322:53]
  wire  _GEN_10 = fieldPStrbBits_0 ? _GEN_7 : _GEN_8; // @[Apb2CSTrgt.scala 484:53]
  wire  _GEN_11 = fieldPStrbBits_0 | _GEN_10; // @[Apb2CSTrgt.scala 502:59 504:25]
  wire [30:0] _GEN_12 = fieldPStrbBits_0 ? io_apb2T_req_pWData[31:1] : {{30'd0}, AmbelDebugCtrl_Step}; // @[Apb2CSTrgt.scala 484:53 500:23 341:49]
  wire  _GEN_13 = fieldPStrbBits_0 ? _GEN_10 : _GEN_11; // @[Apb2CSTrgt.scala 484:53]
  wire [31:0] _GEN_14 = regIndex == 4'h1 ? _GEN_9 : {{31'd0}, AmbelDebugCtrl_Halt}; // @[Apb2CSTrgt.scala 460:31 322:53]
  wire  _GEN_15 = regIndex == 4'h1 ? _GEN_13 : _GEN_7; // @[Apb2CSTrgt.scala 460:31]
  wire [30:0] _GEN_16 = regIndex == 4'h1 ? _GEN_12 : {{30'd0}, AmbelDebugCtrl_Step}; // @[Apb2CSTrgt.scala 460:31 341:49]
  wire  _T_4 = regIndex == 4'h2; // @[Apb2CSTrgt.scala 460:22]
  wire  fieldPStrbBits_1 = io_apb2T_req_pStrb[1]; // @[Apb2CSTrgt.scala 482:39]
  wire  _GEN_17 = fieldPStrbBits_0 | fieldPStrbBits_1 | _GEN_15; // @[Apb2CSTrgt.scala 502:59 504:25]
  wire [31:0] _GEN_18 = fieldPStrbBits_0 & fieldPStrbBits_1 ? io_apb2T_req_pWData : {{16'd0}, AmbelFooBar_Foo}; // @[Apb2CSTrgt.scala 484:53 500:23 322:53]
  wire  _GEN_19 = fieldPStrbBits_0 & fieldPStrbBits_1 ? _GEN_15 : _GEN_17; // @[Apb2CSTrgt.scala 484:53]
  wire  fieldPStrbBits_0_4 = io_apb2T_req_pStrb[2]; // @[Apb2CSTrgt.scala 482:39]
  wire  fieldPStrbBits_1_1 = io_apb2T_req_pStrb[3]; // @[Apb2CSTrgt.scala 482:39]
  wire  _GEN_20 = fieldPStrbBits_0_4 | fieldPStrbBits_1_1 | _GEN_19; // @[Apb2CSTrgt.scala 502:59 504:25]
  wire  _GEN_22 = fieldPStrbBits_0_4 & fieldPStrbBits_1_1 ? _GEN_19 : _GEN_20; // @[Apb2CSTrgt.scala 484:53]
  wire [31:0] _GEN_23 = regIndex == 4'h2 ? _GEN_18 : {{16'd0}, AmbelFooBar_Foo}; // @[Apb2CSTrgt.scala 460:31 322:53]
  wire  _GEN_24 = regIndex == 4'h2 ? _GEN_22 : _GEN_15; // @[Apb2CSTrgt.scala 460:31]
  wire  _T_9 = regIndex == 4'h3; // @[Apb2CSTrgt.scala 460:22]
  wire  _GEN_26 = fieldPStrbBits_0 | fieldPStrbBits_1 | fieldPStrbBits_0_4 | fieldPStrbBits_1_1 | _GEN_24; // @[Apb2CSTrgt.scala 502:59 504:25]
  wire  _GEN_28 = fieldPStrbBits_0 & fieldPStrbBits_1 & fieldPStrbBits_0_4 & fieldPStrbBits_1_1 ? _GEN_24 : _GEN_26; // @[Apb2CSTrgt.scala 484:53]
  wire  _GEN_30 = regIndex == 4'h3 ? _GEN_28 : _GEN_24; // @[Apb2CSTrgt.scala 460:31]
  wire  _T_16 = regIndex == 4'h4; // @[Apb2CSTrgt.scala 460:22]
  wire  _GEN_31 = fieldPStrbBits_0 | fieldPStrbBits_1 | fieldPStrbBits_0_4 | fieldPStrbBits_1_1 | _GEN_30; // @[Apb2CSTrgt.scala 502:59 504:25]
  wire  _GEN_33 = fieldPStrbBits_0 & fieldPStrbBits_1 & fieldPStrbBits_0_4 & fieldPStrbBits_1_1 ? _GEN_30 : _GEN_31; // @[Apb2CSTrgt.scala 484:53]
  wire  _GEN_35 = regIndex == 4'h4 ? _GEN_33 : _GEN_30; // @[Apb2CSTrgt.scala 460:31]
  wire  _T_23 = regIndex == 4'h5; // @[Apb2CSTrgt.scala 460:22]
  wire  _GEN_36 = regIndex == 4'h5 | _GEN_35; // @[Apb2CSTrgt.scala 460:31 507:23]
  wire  _T_24 = regIndex == 4'h6; // @[Apb2CSTrgt.scala 460:22]
  wire  _T_25 = regIndex == 4'h7; // @[Apb2CSTrgt.scala 460:22]
  wire  _GEN_37 = fieldPStrbBits_0 | _GEN_36; // @[Apb2CSTrgt.scala 502:59 504:25]
  wire [31:0] _GEN_38 = fieldPStrbBits_0 ? io_apb2T_req_pWData : {{24'd0}, AmbelWoGobits_GoBits}; // @[Apb2CSTrgt.scala 484:53 500:23 341:49]
  wire  _GEN_39 = fieldPStrbBits_0 ? _GEN_36 : _GEN_37; // @[Apb2CSTrgt.scala 484:53]
  wire [31:0] _GEN_40 = regIndex == 4'h7 ? _GEN_38 : {{24'd0}, AmbelWoGobits_GoBits}; // @[Apb2CSTrgt.scala 460:31 341:49]
  wire  _GEN_41 = regIndex == 4'h7 ? _GEN_39 : _GEN_36; // @[Apb2CSTrgt.scala 460:31]
  wire  _T_26 = regIndex == 4'h8; // @[Apb2CSTrgt.scala 460:22]
  wire  _GEN_42 = regIndex == 4'h8 | _GEN_41; // @[Apb2CSTrgt.scala 460:31 507:23]
  wire  _T_27 = regIndex == 4'h9; // @[Apb2CSTrgt.scala 460:22]
  wire  clrBits_0 = io_apb2T_req_pWData[0]; // @[Apb2CSTrgt.scala 491:70]
  wire  clrBits_1 = io_apb2T_req_pWData[1]; // @[Apb2CSTrgt.scala 491:70]
  wire  clrBits_2 = io_apb2T_req_pWData[2]; // @[Apb2CSTrgt.scala 491:70]
  wire  clrBits_3 = io_apb2T_req_pWData[3]; // @[Apb2CSTrgt.scala 491:70]
  wire  clrBits_4 = io_apb2T_req_pWData[4]; // @[Apb2CSTrgt.scala 491:70]
  wire  clrBits_5 = io_apb2T_req_pWData[5]; // @[Apb2CSTrgt.scala 491:70]
  wire  clrBits_6 = io_apb2T_req_pWData[6]; // @[Apb2CSTrgt.scala 491:70]
  wire  clrBits_7 = io_apb2T_req_pWData[7]; // @[Apb2CSTrgt.scala 491:70]
  wire  clrBits_8 = io_apb2T_req_pWData[8]; // @[Apb2CSTrgt.scala 491:70]
  wire  clrBits_9 = io_apb2T_req_pWData[9]; // @[Apb2CSTrgt.scala 491:70]
  wire  clrBits_10 = io_apb2T_req_pWData[10]; // @[Apb2CSTrgt.scala 491:70]
  wire  clrBits_11 = io_apb2T_req_pWData[11]; // @[Apb2CSTrgt.scala 491:70]
  wire  clrBits_12 = io_apb2T_req_pWData[12]; // @[Apb2CSTrgt.scala 491:70]
  wire  clrBits_13 = io_apb2T_req_pWData[13]; // @[Apb2CSTrgt.scala 491:70]
  wire  clrBits_14 = io_apb2T_req_pWData[14]; // @[Apb2CSTrgt.scala 491:70]
  wire  clrBits_15 = io_apb2T_req_pWData[15]; // @[Apb2CSTrgt.scala 491:70]
  wire  nxtBits__2 = clrBits_2 ? 1'h0 : AmbelW1cStatus_StausBits[2]; // @[Apb2CSTrgt.scala 493:30 494:25 490:38]
  wire  nxtBits__5 = clrBits_5 ? 1'h0 : AmbelW1cStatus_StausBits[5]; // @[Apb2CSTrgt.scala 493:30 494:25 490:38]
  wire  nxtBits__13 = clrBits_13 ? 1'h0 : AmbelW1cStatus_StausBits[13]; // @[Apb2CSTrgt.scala 493:30 494:25 490:38]
  wire  nxtBits__4 = clrBits_4 ? 1'h0 : AmbelW1cStatus_StausBits[4]; // @[Apb2CSTrgt.scala 493:30 494:25 490:38]
  wire  nxtBits__9 = clrBits_9 ? 1'h0 : AmbelW1cStatus_StausBits[9]; // @[Apb2CSTrgt.scala 493:30 494:25 490:38]
  wire  nxtBits__10 = clrBits_10 ? 1'h0 : AmbelW1cStatus_StausBits[10]; // @[Apb2CSTrgt.scala 493:30 494:25 490:38]
  wire  nxtBits__15 = clrBits_15 ? 1'h0 : AmbelW1cStatus_StausBits[15]; // @[Apb2CSTrgt.scala 493:30 494:25 490:38]
  wire  nxtBits__12 = clrBits_12 ? 1'h0 : AmbelW1cStatus_StausBits[12]; // @[Apb2CSTrgt.scala 493:30 494:25 490:38]
  wire  nxtBits__7 = clrBits_7 ? 1'h0 : AmbelW1cStatus_StausBits[7]; // @[Apb2CSTrgt.scala 493:30 494:25 490:38]
  wire  nxtBits__0 = clrBits_0 ? 1'h0 : AmbelW1cStatus_StausBits[0]; // @[Apb2CSTrgt.scala 493:30 494:25 490:38]
  wire  nxtBits__1 = clrBits_1 ? 1'h0 : AmbelW1cStatus_StausBits[1]; // @[Apb2CSTrgt.scala 493:30 494:25 490:38]
  wire  nxtBits__14 = clrBits_14 ? 1'h0 : AmbelW1cStatus_StausBits[14]; // @[Apb2CSTrgt.scala 493:30 494:25 490:38]
  wire  nxtBits__6 = clrBits_6 ? 1'h0 : AmbelW1cStatus_StausBits[6]; // @[Apb2CSTrgt.scala 493:30 494:25 490:38]
  wire  nxtBits__3 = clrBits_3 ? 1'h0 : AmbelW1cStatus_StausBits[3]; // @[Apb2CSTrgt.scala 493:30 494:25 490:38]
  wire  nxtBits__11 = clrBits_11 ? 1'h0 : AmbelW1cStatus_StausBits[11]; // @[Apb2CSTrgt.scala 493:30 494:25 490:38]
  wire  nxtBits__8 = clrBits_8 ? 1'h0 : AmbelW1cStatus_StausBits[8]; // @[Apb2CSTrgt.scala 493:30 494:25 490:38]
  wire [7:0] AmbelW1cStatus_StausBits_lo = {nxtBits__7,nxtBits__6,nxtBits__5,nxtBits__4,nxtBits__3,nxtBits__2,nxtBits__1
    ,nxtBits__0}; // @[Apb2CSTrgt.scala 497:34]
  wire [15:0] _AmbelW1cStatus_StausBits_T = {nxtBits__15,nxtBits__14,nxtBits__13,nxtBits__12,nxtBits__11,nxtBits__10,
    nxtBits__9,nxtBits__8,AmbelW1cStatus_StausBits_lo}; // @[Apb2CSTrgt.scala 497:34]
  wire  _GEN_59 = fieldPStrbBits_0 | fieldPStrbBits_1 | _GEN_42; // @[Apb2CSTrgt.scala 502:59 504:25]
  wire  _GEN_61 = fieldPStrbBits_0 & fieldPStrbBits_1 ? _GEN_42 : _GEN_59; // @[Apb2CSTrgt.scala 484:53]
  wire  _GEN_63 = regIndex == 4'h9 ? _GEN_61 : _GEN_42; // @[Apb2CSTrgt.scala 460:31]
  wire  _T_30 = regIndex == 4'ha; // @[Apb2CSTrgt.scala 460:22]
  wire  _GEN_64 = fieldPStrbBits_0 | fieldPStrbBits_1 | fieldPStrbBits_0_4 | fieldPStrbBits_1_1 | _GEN_63; // @[Apb2CSTrgt.scala 502:59 504:25]
  wire  _GEN_66 = fieldPStrbBits_0 & fieldPStrbBits_1 & fieldPStrbBits_0_4 & fieldPStrbBits_1_1 ? _GEN_63 : _GEN_64; // @[Apb2CSTrgt.scala 484:53]
  wire  _GEN_68 = regIndex == 4'ha ? _GEN_66 : _GEN_63; // @[Apb2CSTrgt.scala 460:31]
  wire  _T_37 = regIndex == 4'hb; // @[Apb2CSTrgt.scala 460:22]
  wire  _T_38 = regIndex == 4'hc; // @[Apb2CSTrgt.scala 460:22]
  wire  _T_39 = regIndex == 4'hd; // @[Apb2CSTrgt.scala 460:22]
  wire  _T_40 = regIndex == 4'he; // @[Apb2CSTrgt.scala 460:22]
  wire  _T_41 = regIndex == 4'hf; // @[Apb2CSTrgt.scala 460:22]
  wire  _GEN_73 = regIndex == 4'hf | (regIndex == 4'he | (regIndex == 4'hd | (regIndex == 4'hc | (regIndex == 4'hb |
    _GEN_68)))); // @[Apb2CSTrgt.scala 460:31 507:23]
  wire  setBits_0 = io_wcVec_0[0]; // @[Apb2CSTrgt.scala 526:43]
  wire  setBits_1 = io_wcVec_0[1]; // @[Apb2CSTrgt.scala 526:43]
  wire  setBits_2 = io_wcVec_0[2]; // @[Apb2CSTrgt.scala 526:43]
  wire  setBits_3 = io_wcVec_0[3]; // @[Apb2CSTrgt.scala 526:43]
  wire  setBits_4 = io_wcVec_0[4]; // @[Apb2CSTrgt.scala 526:43]
  wire  setBits_5 = io_wcVec_0[5]; // @[Apb2CSTrgt.scala 526:43]
  wire  setBits_6 = io_wcVec_0[6]; // @[Apb2CSTrgt.scala 526:43]
  wire  setBits_7 = io_wcVec_0[7]; // @[Apb2CSTrgt.scala 526:43]
  wire  setBits_8 = io_wcVec_0[8]; // @[Apb2CSTrgt.scala 526:43]
  wire  setBits_9 = io_wcVec_0[9]; // @[Apb2CSTrgt.scala 526:43]
  wire  setBits_10 = io_wcVec_0[10]; // @[Apb2CSTrgt.scala 526:43]
  wire  setBits_11 = io_wcVec_0[11]; // @[Apb2CSTrgt.scala 526:43]
  wire  setBits_12 = io_wcVec_0[12]; // @[Apb2CSTrgt.scala 526:43]
  wire  setBits_13 = io_wcVec_0[13]; // @[Apb2CSTrgt.scala 526:43]
  wire  setBits_14 = io_wcVec_0[14]; // @[Apb2CSTrgt.scala 526:43]
  wire  setBits_15 = io_wcVec_0[15]; // @[Apb2CSTrgt.scala 526:43]
  wire  nxtBits_1_1 = setBits_1 | AmbelW1cStatus_StausBits[1]; // @[Apb2CSTrgt.scala 528:24 529:19 525:32]
  wire  nxtBits_1_2 = setBits_2 | AmbelW1cStatus_StausBits[2]; // @[Apb2CSTrgt.scala 528:24 529:19 525:32]
  wire  nxtBits_1_7 = setBits_7 | AmbelW1cStatus_StausBits[7]; // @[Apb2CSTrgt.scala 528:24 529:19 525:32]
  wire  nxtBits_1_8 = setBits_8 | AmbelW1cStatus_StausBits[8]; // @[Apb2CSTrgt.scala 528:24 529:19 525:32]
  wire  nxtBits_1_5 = setBits_5 | AmbelW1cStatus_StausBits[5]; // @[Apb2CSTrgt.scala 528:24 529:19 525:32]
  wire  nxtBits_1_9 = setBits_9 | AmbelW1cStatus_StausBits[9]; // @[Apb2CSTrgt.scala 528:24 529:19 525:32]
  wire  nxtBits_1_3 = setBits_3 | AmbelW1cStatus_StausBits[3]; // @[Apb2CSTrgt.scala 528:24 529:19 525:32]
  wire  nxtBits_1_15 = setBits_15 | AmbelW1cStatus_StausBits[15]; // @[Apb2CSTrgt.scala 528:24 529:19 525:32]
  wire  nxtBits_1_4 = setBits_4 | AmbelW1cStatus_StausBits[4]; // @[Apb2CSTrgt.scala 528:24 529:19 525:32]
  wire  nxtBits_1_10 = setBits_10 | AmbelW1cStatus_StausBits[10]; // @[Apb2CSTrgt.scala 528:24 529:19 525:32]
  wire  nxtBits_1_11 = setBits_11 | AmbelW1cStatus_StausBits[11]; // @[Apb2CSTrgt.scala 528:24 529:19 525:32]
  wire  nxtBits_1_0 = setBits_0 | AmbelW1cStatus_StausBits[0]; // @[Apb2CSTrgt.scala 528:24 529:19 525:32]
  wire  nxtBits_1_6 = setBits_6 | AmbelW1cStatus_StausBits[6]; // @[Apb2CSTrgt.scala 528:24 529:19 525:32]
  wire  nxtBits_1_12 = setBits_12 | AmbelW1cStatus_StausBits[12]; // @[Apb2CSTrgt.scala 528:24 529:19 525:32]
  wire  nxtBits_1_14 = setBits_14 | AmbelW1cStatus_StausBits[14]; // @[Apb2CSTrgt.scala 528:24 529:19 525:32]
  wire  nxtBits_1_13 = setBits_13 | AmbelW1cStatus_StausBits[13]; // @[Apb2CSTrgt.scala 528:24 529:19 525:32]
  wire [7:0] AmbelW1cStatus_StausBits_lo_1 = {nxtBits_1_7,nxtBits_1_6,nxtBits_1_5,nxtBits_1_4,nxtBits_1_3,nxtBits_1_2,
    nxtBits_1_1,nxtBits_1_0}; // @[Apb2CSTrgt.scala 532:28]
  wire [15:0] _AmbelW1cStatus_StausBits_T_1 = {nxtBits_1_15,nxtBits_1_14,nxtBits_1_13,nxtBits_1_12,nxtBits_1_11,
    nxtBits_1_10,nxtBits_1_9,nxtBits_1_8,AmbelW1cStatus_StausBits_lo_1}; // @[Apb2CSTrgt.scala 532:28]
  wire [31:0] _GEN_92 = pWriteFF ? _GEN_6 : {{31'd0}, AmbelCtrl_CoreReset}; // @[Apb2CSTrgt.scala 454:19 322:53]
  wire [31:0] _GEN_93 = pWriteFF ? _GEN_14 : {{31'd0}, AmbelDebugCtrl_Halt}; // @[Apb2CSTrgt.scala 454:19 322:53]
  wire [30:0] _GEN_94 = pWriteFF ? _GEN_16 : 31'h0; // @[Apb2CSTrgt.scala 454:19 514:70]
  wire [31:0] _GEN_95 = pWriteFF ? _GEN_23 : {{16'd0}, AmbelFooBar_Foo}; // @[Apb2CSTrgt.scala 454:19 322:53]
  wire [31:0] _GEN_99 = pWriteFF ? _GEN_40 : 32'h0; // @[Apb2CSTrgt.scala 454:19 514:70]
  wire  _GEN_102 = _T_2 & AmbelCtrl_CoreReset; // @[Apb2CSTrgt.scala 540:15 545:31 549:18]
  wire [1:0] shiftedBits_1 = {AmbelDebugCtrl_Step, 1'h0}; // @[Apb2CSTrgt.scala 548:80]
  wire [1:0] _GEN_128 = {{1'd0}, AmbelDebugCtrl_Halt}; // @[Apb2CSTrgt.scala 549:46]
  wire [1:0] _pRDataFF_T = _GEN_128 | shiftedBits_1; // @[Apb2CSTrgt.scala 549:46]
  wire [1:0] _GEN_103 = _T_3 ? _pRDataFF_T : {{1'd0}, _GEN_102}; // @[Apb2CSTrgt.scala 545:31 549:18]
  wire [31:0] shiftedBits_1_1 = {AmbelFooBar_Bar, 16'h0}; // @[Apb2CSTrgt.scala 548:80]
  wire [31:0] _GEN_129 = {{16'd0}, AmbelFooBar_Foo}; // @[Apb2CSTrgt.scala 549:46]
  wire [31:0] _pRDataFF_T_1 = _GEN_129 | shiftedBits_1_1; // @[Apb2CSTrgt.scala 549:46]
  wire [31:0] _GEN_104 = _T_4 ? _pRDataFF_T_1 : {{30'd0}, _GEN_103}; // @[Apb2CSTrgt.scala 545:31 549:18]
  wire [31:0] _GEN_105 = _T_9 ? AmbelBaz0_BazBits : _GEN_104; // @[Apb2CSTrgt.scala 545:31 549:18]
  wire [31:0] _GEN_106 = _T_16 ? AmbelBaz1_BazBits : _GEN_105; // @[Apb2CSTrgt.scala 545:31 549:18]
  wire [31:0] _GEN_107 = _T_23 ? 32'h0 : _GEN_106; // @[Apb2CSTrgt.scala 545:31 549:18]
  wire [31:0] _GEN_109 = _T_24 ? {{24'd0}, io_roVec_0} : _GEN_107; // @[Apb2CSTrgt.scala 545:31 549:18]
  wire [31:0] _GEN_110 = _T_25 ? {{24'd0}, AmbelWoGobits_GoBits} : _GEN_109; // @[Apb2CSTrgt.scala 545:31 549:18]
  wire [31:0] _GEN_111 = _T_26 ? 32'h0 : _GEN_110; // @[Apb2CSTrgt.scala 545:31 549:18]
  wire [31:0] _GEN_113 = _T_27 ? {{16'd0}, AmbelW1cStatus_StausBits} : _GEN_111; // @[Apb2CSTrgt.scala 545:31 549:18]
  wire [63:0] _GEN_114 = _T_30 ? AmbelBigRegExample_BigBits : {{32'd0}, _GEN_113}; // @[Apb2CSTrgt.scala 545:31 549:18]
  wire [63:0] _GEN_115 = _T_37 ? 64'h0 : _GEN_114; // @[Apb2CSTrgt.scala 545:31 549:18]
  wire [63:0] _GEN_117 = _T_38 ? 64'h0 : _GEN_115; // @[Apb2CSTrgt.scala 545:31 549:18]
  wire [63:0] _GEN_119 = _T_39 ? 64'h0 : _GEN_117; // @[Apb2CSTrgt.scala 545:31 549:18]
  wire [63:0] _GEN_121 = _T_40 ? 64'h0 : _GEN_119; // @[Apb2CSTrgt.scala 545:31 549:18]
  wire [63:0] _GEN_123 = _T_41 ? 64'h0 : _GEN_121; // @[Apb2CSTrgt.scala 545:31 549:18]
  wire  _GEN_124 = _T_41 | (_T_40 | (_T_39 | (_T_38 | (_T_37 | (_T_26 | _T_23))))); // @[Apb2CSTrgt.scala 545:31 551:42]
  wire [63:0] _GEN_125 = ~pReadyFF ? _GEN_123 : {{32'd0}, pRDataFF}; // @[Apb2CSTrgt.scala 538:20 433:26]
  wire  _GEN_126 = ~pReadyFF | _GEN_2; // @[Apb2CSTrgt.scala 538:20 541:15]
  wire [31:0] _GEN_130 = reset ? 32'h0 : _GEN_92; // @[Apb2CSTrgt.scala 322:{53,53}]
  wire [31:0] _GEN_131 = reset ? 32'h0 : _GEN_93; // @[Apb2CSTrgt.scala 322:{53,53}]
  wire [30:0] _GEN_132 = reset ? 31'h0 : _GEN_94; // @[Apb2CSTrgt.scala 341:{49,49}]
  wire [31:0] _GEN_133 = reset ? 32'h7b : _GEN_95; // @[Apb2CSTrgt.scala 322:{53,53}]
  wire [31:0] _GEN_134 = reset ? 32'h0 : _GEN_99; // @[Apb2CSTrgt.scala 341:{49,49}]
  wire [63:0] _GEN_135 = reset ? 64'h0 : _GEN_125; // @[Apb2CSTrgt.scala 433:{26,26}]
  assign io_apb2T_rsp_pReady = pReadyFF; // @[Apb2CSTrgt.scala 557:24]
  assign io_apb2T_rsp_pRData = pRDataFF; // @[Apb2CSTrgt.scala 558:24]
  assign io_apb2T_rsp_pSlvErr = pSlvErrFF; // @[Apb2CSTrgt.scala 559:24]
  assign io_rwVec_6 = AmbelBigRegExample_BigBits; // @[Apb2CSTrgt.scala 411:19]
  assign io_rwVec_5 = AmbelBaz1_BazBits; // @[Apb2CSTrgt.scala 411:19]
  assign io_rwVec_4 = AmbelDebugCtrl_Halt; // @[Apb2CSTrgt.scala 411:19]
  assign io_rwVec_3 = AmbelBaz0_BazBits; // @[Apb2CSTrgt.scala 411:19]
  assign io_rwVec_2 = AmbelFooBar_Bar; // @[Apb2CSTrgt.scala 411:19]
  assign io_rwVec_1 = AmbelFooBar_Foo; // @[Apb2CSTrgt.scala 411:19]
  assign io_rwVec_0 = AmbelCtrl_CoreReset; // @[Apb2CSTrgt.scala 411:19]
  assign io_woVec_1 = AmbelWoGobits_GoBits; // @[Apb2CSTrgt.scala 425:19]
  assign io_woVec_0 = AmbelDebugCtrl_Step; // @[Apb2CSTrgt.scala 425:19]
  always @(posedge clock) begin
    AmbelCtrl_CoreReset <= _GEN_130[0]; // @[Apb2CSTrgt.scala 322:{53,53}]
    AmbelDebugCtrl_Halt <= _GEN_131[0]; // @[Apb2CSTrgt.scala 322:{53,53}]
    AmbelDebugCtrl_Step <= _GEN_132[0]; // @[Apb2CSTrgt.scala 341:{49,49}]
    AmbelFooBar_Foo <= _GEN_133[15:0]; // @[Apb2CSTrgt.scala 322:{53,53}]
    if (reset) begin // @[Apb2CSTrgt.scala 322:53]
      AmbelFooBar_Bar <= 16'h1c8; // @[Apb2CSTrgt.scala 322:53]
    end else if (pWriteFF) begin // @[Apb2CSTrgt.scala 454:19]
      if (regIndex == 4'h2) begin // @[Apb2CSTrgt.scala 460:31]
        if (fieldPStrbBits_0_4 & fieldPStrbBits_1_1) begin // @[Apb2CSTrgt.scala 484:53]
          AmbelFooBar_Bar <= io_apb2T_req_pWData[31:16]; // @[Apb2CSTrgt.scala 500:23]
        end
      end
    end
    if (reset) begin // @[Apb2CSTrgt.scala 322:53]
      AmbelBaz0_BazBits <= 32'h1e240; // @[Apb2CSTrgt.scala 322:53]
    end else if (pWriteFF) begin // @[Apb2CSTrgt.scala 454:19]
      if (regIndex == 4'h3) begin // @[Apb2CSTrgt.scala 460:31]
        if (fieldPStrbBits_0 & fieldPStrbBits_1 & fieldPStrbBits_0_4 & fieldPStrbBits_1_1) begin // @[Apb2CSTrgt.scala 484:53]
          AmbelBaz0_BazBits <= io_apb2T_req_pWData; // @[Apb2CSTrgt.scala 500:23]
        end
      end
    end
    if (reset) begin // @[Apb2CSTrgt.scala 322:53]
      AmbelBaz1_BazBits <= 32'h1e240; // @[Apb2CSTrgt.scala 322:53]
    end else if (pWriteFF) begin // @[Apb2CSTrgt.scala 454:19]
      if (regIndex == 4'h4) begin // @[Apb2CSTrgt.scala 460:31]
        if (fieldPStrbBits_0 & fieldPStrbBits_1 & fieldPStrbBits_0_4 & fieldPStrbBits_1_1) begin // @[Apb2CSTrgt.scala 484:53]
          AmbelBaz1_BazBits <= io_apb2T_req_pWData; // @[Apb2CSTrgt.scala 500:23]
        end
      end
    end
    AmbelWoGobits_GoBits <= _GEN_134[7:0]; // @[Apb2CSTrgt.scala 341:{49,49}]
    if (reset) begin // @[Apb2CSTrgt.scala 348:49]
      AmbelW1cStatus_StausBits <= 16'h0; // @[Apb2CSTrgt.scala 348:49]
    end else if (pWriteFF) begin // @[Apb2CSTrgt.scala 454:19]
      if (regIndex == 4'h9) begin // @[Apb2CSTrgt.scala 460:31]
        if (fieldPStrbBits_0 & fieldPStrbBits_1) begin // @[Apb2CSTrgt.scala 484:53]
          AmbelW1cStatus_StausBits <= _AmbelW1cStatus_StausBits_T; // @[Apb2CSTrgt.scala 497:23]
        end
      end
    end else begin
      AmbelW1cStatus_StausBits <= _AmbelW1cStatus_StausBits_T_1; // @[Apb2CSTrgt.scala 532:17]
    end
    if (reset) begin // @[Apb2CSTrgt.scala 325:53]
      AmbelBigRegExample_BigBits <= 64'h0; // @[Apb2CSTrgt.scala 325:53]
    end else if (pWriteFF) begin // @[Apb2CSTrgt.scala 454:19]
      if (regIndex == 4'ha) begin // @[Apb2CSTrgt.scala 460:31]
        if (fieldPStrbBits_0 & fieldPStrbBits_1 & fieldPStrbBits_0_4 & fieldPStrbBits_1_1) begin // @[Apb2CSTrgt.scala 484:53]
          AmbelBigRegExample_BigBits <= {{32'd0}, io_apb2T_req_pWData}; // @[Apb2CSTrgt.scala 500:23]
        end
      end
    end
    if (reset) begin // @[Apb2CSTrgt.scala 430:26]
      pAddrFF <= 6'h0; // @[Apb2CSTrgt.scala 430:26]
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
    pReadyFF <= reset | _GEN_126; // @[Apb2CSTrgt.scala 432:{26,26}]
    pRDataFF <= _GEN_135[31:0]; // @[Apb2CSTrgt.scala 433:{26,26}]
    if (reset) begin // @[Apb2CSTrgt.scala 434:26]
      pSlvErrFF <= 1'h0; // @[Apb2CSTrgt.scala 434:26]
    end else if (~pReadyFF) begin // @[Apb2CSTrgt.scala 538:20]
      pSlvErrFF <= _GEN_124;
    end else if (pWriteFF) begin // @[Apb2CSTrgt.scala 454:19]
      pSlvErrFF <= _GEN_73;
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
  pAddrFF = _RAND_10[5:0];
  _RAND_11 = {1{`RANDOM}};
  pWriteFF = _RAND_11[0:0];
  _RAND_12 = {1{`RANDOM}};
  pReadyFF = _RAND_12[0:0];
  _RAND_13 = {1{`RANDOM}};
  pRDataFF = _RAND_13[31:0];
  _RAND_14 = {1{`RANDOM}};
  pSlvErrFF = _RAND_14[0:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module ExampleApb2CSTrgt(
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
  wire  t_clock; // @[ExampleApb2CSTrgt.scala 30:17]
  wire  t_reset; // @[ExampleApb2CSTrgt.scala 30:17]
  wire [5:0] t_io_apb2T_req_pAddr; // @[ExampleApb2CSTrgt.scala 30:17]
  wire  t_io_apb2T_req_pSel; // @[ExampleApb2CSTrgt.scala 30:17]
  wire  t_io_apb2T_req_pEnable; // @[ExampleApb2CSTrgt.scala 30:17]
  wire  t_io_apb2T_req_pWrite; // @[ExampleApb2CSTrgt.scala 30:17]
  wire [31:0] t_io_apb2T_req_pWData; // @[ExampleApb2CSTrgt.scala 30:17]
  wire [3:0] t_io_apb2T_req_pStrb; // @[ExampleApb2CSTrgt.scala 30:17]
  wire  t_io_apb2T_rsp_pReady; // @[ExampleApb2CSTrgt.scala 30:17]
  wire [31:0] t_io_apb2T_rsp_pRData; // @[ExampleApb2CSTrgt.scala 30:17]
  wire  t_io_apb2T_rsp_pSlvErr; // @[ExampleApb2CSTrgt.scala 30:17]
  wire [63:0] t_io_rwVec_6; // @[ExampleApb2CSTrgt.scala 30:17]
  wire [31:0] t_io_rwVec_5; // @[ExampleApb2CSTrgt.scala 30:17]
  wire  t_io_rwVec_4; // @[ExampleApb2CSTrgt.scala 30:17]
  wire [31:0] t_io_rwVec_3; // @[ExampleApb2CSTrgt.scala 30:17]
  wire [15:0] t_io_rwVec_2; // @[ExampleApb2CSTrgt.scala 30:17]
  wire [15:0] t_io_rwVec_1; // @[ExampleApb2CSTrgt.scala 30:17]
  wire  t_io_rwVec_0; // @[ExampleApb2CSTrgt.scala 30:17]
  wire [7:0] t_io_roVec_0; // @[ExampleApb2CSTrgt.scala 30:17]
  wire [7:0] t_io_woVec_1; // @[ExampleApb2CSTrgt.scala 30:17]
  wire  t_io_woVec_0; // @[ExampleApb2CSTrgt.scala 30:17]
  wire [15:0] t_io_wcVec_0; // @[ExampleApb2CSTrgt.scala 30:17]
  Apb2CSTrgt t ( // @[ExampleApb2CSTrgt.scala 30:17]
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
  assign io_apb2T_rsp_pReady = t_io_apb2T_rsp_pReady; // @[ExampleApb2CSTrgt.scala 44:14]
  assign io_apb2T_rsp_pRData = t_io_apb2T_rsp_pRData; // @[ExampleApb2CSTrgt.scala 44:14]
  assign io_apb2T_rsp_pSlvErr = t_io_apb2T_rsp_pSlvErr; // @[ExampleApb2CSTrgt.scala 44:14]
  assign io_rw_AmbelCtrl_CoreReset = t_io_rwVec_0; // @[ExampleApb2CSTrgt.scala 47:36]
  assign io_rw_AmbelFooBar_Foo = t_io_rwVec_1; // @[ExampleApb2CSTrgt.scala 48:36]
  assign io_rw_AmbelFooBar_Bar = t_io_rwVec_2; // @[ExampleApb2CSTrgt.scala 49:36]
  assign io_rw_AmbelBaz0_BazBits = t_io_rwVec_3; // @[ExampleApb2CSTrgt.scala 50:36]
  assign io_rw_AmbelDebugCtrl_Halt = t_io_rwVec_4; // @[ExampleApb2CSTrgt.scala 51:36]
  assign io_rw_AmbelBaz1_BazBits = t_io_rwVec_5; // @[ExampleApb2CSTrgt.scala 52:36]
  assign io_rw_AmbelBigRegExample_BigBits = t_io_rwVec_6; // @[ExampleApb2CSTrgt.scala 53:36]
  assign io_wo_AmbelDebugCtrl_Step = t_io_woVec_0; // @[ExampleApb2CSTrgt.scala 59:30]
  assign io_wo_AmbelWoGobits_GoBits = t_io_woVec_1; // @[ExampleApb2CSTrgt.scala 60:30]
  assign t_clock = clock;
  assign t_reset = reset;
  assign t_io_apb2T_req_pAddr = io_apb2T_req_pAddr[5:0]; // @[ExampleApb2CSTrgt.scala 44:14]
  assign t_io_apb2T_req_pSel = io_apb2T_req_pSel; // @[ExampleApb2CSTrgt.scala 44:14]
  assign t_io_apb2T_req_pEnable = io_apb2T_req_pEnable; // @[ExampleApb2CSTrgt.scala 44:14]
  assign t_io_apb2T_req_pWrite = io_apb2T_req_pWrite; // @[ExampleApb2CSTrgt.scala 44:14]
  assign t_io_apb2T_req_pWData = io_apb2T_req_pWData; // @[ExampleApb2CSTrgt.scala 44:14]
  assign t_io_apb2T_req_pStrb = io_apb2T_req_pStrb; // @[ExampleApb2CSTrgt.scala 44:14]
  assign t_io_roVec_0 = io_ro_AmbelRoExample_StatusBits; // @[ExampleApb2CSTrgt.scala 56:17]
  assign t_io_wcVec_0 = io_wc_AmbelW1cStatus_StausBits; // @[ExampleApb2CSTrgt.scala 63:17]
endmodule
