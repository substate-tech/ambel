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
The `Apb2CSTrgt` Module implements a basic APB2 control/status register set with the registers and their address map supplied via a simple JSON description, passed as a parameter to the Module. The JSON register description is parsed using [circe](https://github.com/circe/circe) and the resulting objects are used to generate the registers, read-write access to them via the APB2 interface, and any associated direct IO for the registers using Chisel's [`MixedVec`](https://www.chisel-lang.org/api/latest/chisel3/util/MixedVec.html). Setting the `GEN_BUNDLE` parameter to true it is possible to generate a set of Bundles suitable for ordered connection to the IO externally. There's an auto-generated Bundle for each register bit field type with a member for each bit field named after its register name and bit field name (as specified in the JSON). 

### [Apb2CSTrgt register bit field modes](docs/register_bit_field_modes.md)
### [Register description JSON schema](docs/register_description_JSON_schema.md)
### Simple Example

The following JSON file can be used to parameterize Apb2CSTrgt to implement an extremely simple Module with a single 32-bit xregister, with a single 8-bit read-write bit-field (the rest of the bits are marked as RESERVED, will not be writable and will read zero). 
```JSON
{
 "regMap": [
  {
   "offset": 0,
   "name": "SIMPLE_RW0", "typeRef": "SIMPLE_8B_RW",
   "comment": "Simple read/write register 0"
  }
 ],
 "regTypes": [
  {
   "typeRef": "SIMPLE_8B_RW",
   "width": 32,
   "fields": [
    {"bits": [ 7, 0], "name": "RW_BITS", "mode": "RW", "resetVal": 0, "comment": "Example RW bit-field"},
    {"bits": [31, 8], "name": "RESERVED"}
   ],
   "comment": "Simple read-write register with single 8-bit field"
  }
 ]
}
```


## [Apb2Net](src/main/scala/Apb2Net.scala)
The `Apb2Net` Module implements an APB2 network connecting a parameterizable number of APB2 initiators and targets with further parameters defining address map for the  targets (base addresses and sizes). 

## [Apb2Slice](src/main/scala/Apb2Slice.scala)
The `Apb2Slice` Module is a simple APB2 bus register slice for pipelining/timing closure. The Module has a pair of APB2 interfaces (initiator and target) and works by registering initiator requests, forwarding them to the target interface, and extending the response with wait states until the target responds. The target response itself is registered to return to the initator. 
