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
The `Apb2CSTrgt` Module implements a basic APB2 control/status register set with the registers and their address map supplied via a simple JSON description, passed as a parameter to the Module. The JSON register description is parsed using [circe](https://github.com/circe/circe) and the resulting objects are used to generate the registers, read-write access to them via the APB2 interface, and also *any associated direct IO for the registers* using Chisel's [`MixedVec`](https://www.chisel-lang.org/api/latest/chisel3/util/MixedVec.html).

Setting the parameter `GEN_BUNDLE = true` it is possible to generate a set of Bundles suitable for ordered connection to the generated, numbered IO externally. There's an auto-generated Bundle for each register bit field type with a member for each bit field named after its register name and bit field name (as specified in the JSON). See the [Simple Example](docs/simple_example.md) to quickly get the gist of this.

The APB2 target (slave) interface on the Apb2CSTrgt Module has support for the specification of the address width and data width via the `ADDR_W` and `DATA_W` parameters. Any number and variety of bit-field modes may be specified for a given register and `PSTRB` is supported such that writes may target only certain bit-fields (with some restrictions described in [the documentation](https://richmorj.github.io/ambel/latest/api/ambel/Apb2CSTrgt.html)). Support for `PPROT` is on the backlog, see [issue #10](https://github.com/richmorj/ambel/issues/10).

The screenshot below shows a few APB write-read-back cycles for the first register, which has a single 8-bit RW bit-field on its first byte, of the [Simple Example](src/main/scala/examples/SimpleApb2CSTrgt.scala) as well as the RW Output updating after each write.

![Simple RW APB access](docs/Simple_RW_APB_access.png)

### [Apb2CSTrgt register bit field modes](docs/register_bit_field_modes.md)
### [Register description JSON schema](docs/register_description_JSON_schema.md)
### [Simple Example](docs/simple_example.md)

## [Apb2Net](src/main/scala/Apb2Net.scala)
The `Apb2Net` Module implements an APB2 network connecting a parameterizable number of APB2 initiators and targets with further parameters defining address map for the  targets (base addresses and sizes). 

## [Apb2Slice](src/main/scala/Apb2Slice.scala)
The `Apb2Slice` Module is a simple APB2 bus register slice for pipelining/timing closure. The Module has a pair of APB2 interfaces (initiator and target) and works by registering initiator requests, forwarding them to the target interface, and extending the response with wait states until the target responds. The target response itself is registered to return to the initator. 
