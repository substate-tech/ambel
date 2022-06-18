<!--- 
This is the README.md for the Chisel Lang Chisel3 RTL generator project called Ambel which is an AMBA APB APB2 AXI compnent module
generator with an APB slave "APB slave" APB Control/Status APB Registers Register Map Verilog generator and other AMBA AP2 components 
like an APB bridge network "network on chip" NoC bit field configurable register pipeline slice "register file" open source hardware
VHDL CSR
--->
![AMBEL Logo](docs/AMBEL.png)

A [Chisel3](https://github.com/chipsalliance/chisel3) library for generating [AMBA](https://developer.arm.com/architectures/system-architectures/amba) components featuring a Verilog APB Control/Status Register (CSR) target (an APB slave) generator

[AMBEL on GitHub Pages](https://richmorj.github.io/ambel/)

# Overview
AMBEL aims to provide a collection of parameterizable [Chisel3](https://github.com/chipsalliance/chisel3) Modules compatible with ARM's widely adopted [AMBA (Advanced Microcontroller Bus Architecture)](https://developer.arm.com/architectures/system-architectures/amba) protocols. The initial collection keeps it simple, focusing solely on [APB2](https://developer.arm.com/documentation/ihi0011/a/AMBA-APB) protocol and offering Modules that may be connected to implement APB2 networks connecting APB2 initators and targets. The key Module in this small collection is [`Apb2CSTrgt`](src/main/scala/Apb2CSTrgt.scala) which is an APB2 target (an APB slave) implementing control/status registers, defined using a simple JSON schema.

The Modules are primarily intended for integration into other Chisel designs, but they could also be used to generate Verilog for integration into Verilog designs.

![ci push status](https://github.com/richmorj/ambel/actions/workflows/ci.yaml/badge.svg?event=push)

![coverage status](https://richmorj.github.io/ambel/badges/coverage.svg) **([Latest coverage report](https://richmorj.github.io/ambel/coverage/ambel/))**

# Contributing
Contributions and collaborators welcome! Please see the guide to [contributing](CONTRIBUTING.md) if you want to join in.

# Modules

## [Apb2CSTrgt](src/main/scala/Apb2CSTrgt.scala)
The `Apb2CSTrgt` Module implements a basic APB2 control/status register set with the registers and their address map supplied via a simple JSON description, passed as a parameter to the Module. The JSON register description is parsed using [circe](https://github.com/circe/circe) and the resulting objects are used to generate the registers, read-write access to them via the APB2 interface, and any associated direct IO for the registers using Chisel's [`MixedVec`](https://www.chisel-lang.org/api/latest/chisel3/util/MixedVec.html). Setting the parameter `GEN_BUNDLE = true` it is possible to generate a set of Bundles suitable for ordered connection to the IO externally. There's an auto-generated Bundle for each register bit field type with a member for each bit field named after its register name and bit field name (as specified in the JSON). 

### [Apb2CSTrgt register bit field modes](docs/register_bit_field_modes.md)
### [Register description JSON schema](docs/register_description_JSON_schema.md)
### Simple Example

The following JSON file [versioned here](src/main/json/Simple.json) can be used to parameterize Apb2CSTrgt to implement an extremely simple Module with a single 32-bit register which has a single 8-bit read-write bit-field (the rest of the bits are marked as RESERVED, will not be writable and will read zero). 
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

Generating the Verilog for this parameterization will produce a module ([versioned here](src/main/verilog/examples/SimpleApb2CSTrgt.v))with the following interface.
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
Running generation with the parameter `GEN_BUNDLE = true` will also generate a Chisel Bundle [versioned here](src/main/scala/examples/Simple.scala).
```scala
/** =Bundles for Connection to Apb2CSTrgt(REG_DESC_JSON="src/main/json/Simple.json")
  *
  * THIS IS AUTO-GENERATED CODE - DO NOT MODIFY BY HAND!
  */
class _SimpleRwVec_ extends Bundle {
  val SimpleRw_RwBits = UInt(8.W)
}
class _SimpleRoVec_ extends Bundle {
  val SimpleRoWo_RoBits = UInt(8.W)
}
class _SimpleWoVec_ extends Bundle {
  val SimpleRoWo_WoBits = UInt(8.W)
}
```
The Bundle can then be used to wrap the paramterized Apb2CSTrgt Module and connect its MixedVec output to a named member of the generated Bundle. This is shown in [SimpleApb2CSTrgt.scala](src/main/scala/examples/SimpleApb2CSTrgt.scala).
```scala
class SimpleApb2CSTrgt() extends Module {
  val ADDR_W = 32
  val DATA_W = 32

  val t = Module(new Apb2CSTrgt(
    DATA_W = DATA_W,
    REG_DESC_JSON = "src/main/json/Simple.json",
    VERBOSE = true,
    GEN_BUNDLE = false))

  val io = IO(new Bundle {
    val apb2T = new Apb2IO(ADDR_W, DATA_W)
    val rw = Output(new _SimpleRwVec_)
    val ro = Input(new _SimpleRoVec_)
    val wo = Output(new _SimpleWoVec_)
  })

  t.io.apb2T <> io.apb2T

  // Connect RW bit-field outputs
  io.rw.SimpleRw_RwBits := t.io.rwVec(0)

  // Connect RO bit-field outputs
  t.io.roVec(0) := io.ro.SimpleRoWo_RoBits

  // Connect WO bit-field outputs
  io.wo.SimpleRoWo_WoBits := t.io.woVec(0)
}
```

## [Apb2Net](src/main/scala/Apb2Net.scala)
The `Apb2Net` Module implements an APB2 network connecting a parameterizable number of APB2 initiators and targets with further parameters defining address map for the  targets (base addresses and sizes). 

## [Apb2Slice](src/main/scala/Apb2Slice.scala)
The `Apb2Slice` Module is a simple APB2 bus register slice for pipelining/timing closure. The Module has a pair of APB2 interfaces (initiator and target) and works by registering initiator requests, forwarding them to the target interface, and extending the response with wait states until the target responds. The target response itself is registered to return to the initator. 
