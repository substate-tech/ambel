<!--- 
This is the README.md for the Chisel Lang Chisel3 RTL generator project called Ambel which is an AMBA APB APB2 AXI compnent module
generator with an APB slave "APB slave" APB Control/Status APB Registers Register Map Verilog generator and other AMBA AP2 components 
like an APB bridge network "network on chip" NoC bit field configurable register pipeline slice "register file" open source hardware
VHDL CSR
--->
![AMBEL Logo](docs/AMBEL.png)

A [Chisel3](https://github.com/chipsalliance/chisel3) library for generating [AMBA](https://developer.arm.com/architectures/system-architectures/amba) components featuring a Verilog APB Control/Status Register (CSR) target (an APB slave) generator

[AMBEL on GitHub Pages](https://substate-tech.github.io/ambel/)

![ci push status](https://github.com/substate-tech/ambel/actions/workflows/ci.yaml/badge.svg?event=push)

![coverage status](https://substate-tech.github.io/ambel/badges/coverage.svg) **([Latest coverage report](https://substate-tech.github.io/ambel/coverage/ambel/))**

# Contents
- [Overview](#overview)
- [Installation](#overview)
- [Contributing](#installation)
- [Modules](#modules)
- [Running Tests](#running-tests)
- [Generating Verilog](#generating-verilog)

# Overview
AMBEL aims to provide a collection of parameterizable [Chisel3](https://github.com/chipsalliance/chisel3) Modules compatible with ARM's widely adopted [AMBA (Advanced Microcontroller Bus Architecture)](https://developer.arm.com/architectures/system-architectures/amba) protocols. The initial collection keeps it simple, focusing solely on [APB2](https://developer.arm.com/documentation/ihi0011/a/AMBA-APB) protocol and offering Modules that may be connected to implement APB2 networks connecting APB2 initators and targets. The key Module in this small collection is [`Apb2CSTrgt`](src/main/scala/Apb2CSTrgt.scala) which is an APB2 target (an APB slave) implementing control/status registers, defined using a simple JSON schema.

The Modules are primarily intended for integration into other Chisel designs, but they could also be used to generate Verilog for integration into Verilog designs.

# Installation
Please follow the general instructions for getting setup to run Chisel locally found on in [the Chisel3 repo](https://github.com/chipsalliance/chisel3/blob/master/SETUP.md).

## Dependencies
AMBEL has the following dependencies
- [Chisel3 v3.5.4](https://github.com/chipsalliance/chisel3/releases/tag/v3.5.4)
- [circe v0.7.0](https://github.com/circe/circe/releases/tag/v0.7.0)
- [ChiselTest v0.5.1](https://github.com/ucb-bar/chiseltest/releases/tag/v0.5.1)

Tests can be run with either Treadle or Verilator (Verilator is used to generate line coverage in Actions). Currently Verilator 4.218 2022-01-17 rev v4.218 is being used. See [Running Tests](#Running-Tests) for further details.

# Contributing
Contributions and collaborators welcome! Please see the guide to [contributing](CONTRIBUTING.md) if you want to join in.

# Modules

## [Apb2CSTrgt](src/main/scala/Apb2CSTrgt.scala)
The `Apb2CSTrgt` Module implements a basic APB2 control/status register set with the registers and their address map supplied via a simple JSON description, passed as a parameter to the Module. The JSON register description is parsed using [circe](https://github.com/circe/circe) and the resulting objects are used to generate the registers, read-write access to them via the APB2 interface, and also *any associated direct IO for the registers* using Chisel's [`MixedVec`](https://www.chisel-lang.org/api/latest/chisel3/util/MixedVec.html).

Setting the parameter `GEN_BUNDLE = true` it is possible to generate a set of Bundles suitable for ordered connection to the generated, numbered IO externally. There's an auto-generated Bundle for each register bit field type with a member for each bit field named after its register name and bit field name (as specified in the JSON). See the [Simple Example](docs/simple_example.md) to quickly get the gist of this.

The APB2 target (slave) interface on the Apb2CSTrgt Module has support for the specification of the address width and data width via the `ADDR_W` and `DATA_W` parameters. Any number and variety of bit-field modes may be specified for a given register and `PSTRB` is supported such that writes may target only certain bit-fields (with some restrictions described in [the documentation](https://substate-tech.github.io/ambel/latest/api/ambel/Apb2CSTrgt.html)). Support for `PPROT` is on the backlog, see [issue #10](https://github.com/substate-tech/ambel/issues/10).

The screenshot below shows a few APB write-read-back cycles for the first register, which has a single 8-bit RW bit-field on its first byte, of the [Simple Example](src/main/scala/examples/SimpleApb2CSTrgt.scala) as well as the RW Output updating after each write.

![Simple RW APB access](docs/Simple_RW_APB_access.png)

### [Apb2CSTrgt register bit field modes](docs/register_bit_field_modes.md)
### [Register description JSON schema](docs/register_description_JSON_schema.md)
### [Simple Example](docs/simple_example.md)

## [Apb2Net](src/main/scala/Apb2Net.scala)
The `Apb2Net` Module implements an APB2 network connecting a parameterizable number of APB2 initiators and targets with further parameters defining address map for the  targets (base addresses and sizes). 

## [Apb2Slice](src/main/scala/Apb2Slice.scala)
The `Apb2Slice` Module is a simple APB2 bus register slice for pipelining/timing closure. The Module has a pair of APB2 interfaces (initiator and target) and works by registering initiator requests, forwarding them to the target interface, and extending the response with wait states until the target responds. The target response itself is registered to return to the initator. 

# Running Tests
You can run all [the tests](src/test/scala) from the command line in sbt with
```sbt
test
```
If you want to run one particular test suite you can do so as follows
```sbt
testOnly ambel.SimpleApb2CSTrgtUnitTester
```
To dump waves (VCD) you can add `-- -DwriteVcd=1`. Some other useful command line options have also been implemented:
- `-Dbackend=verilator` : select Verilator as the simulator instead of Treadle (the default)
- `-Dseed=123` : specify 123 as the randomization seed (some tests generate and use random data)
- `-Ddebug=1` : pass the DEBUG flag into the unit tester (e.g. to enable very verbose debug messages)
- `-Dverbose=1` : pass the VERBOSE flag into the unit tester to enable verbose messages, by default the tests run quite quietly

E.g. to use all of the above options for the Simple Example tests and dump a VCD file
```sbt
testOnly ambel.SimpleApb2CSTrgtUnitTester -- -Dbackend=verilator -Dseed=123 -Ddebug=1 -Dverbose=1 -DwriteVcd=1
```
# Generating Verilog
If you would like to get a feel for the Verilog emitted by the AMBEL Apb2CSTrgt Module at the moment the most direct way is to manualy edit the `Apb2CSTrgtDriver()` source code to point to your own JSON register description, then run
```sbt
runMain ambel.Apb2CSTrgtDriver --target-dir src/main/verilog --log-level info --log-file Apb2CSTrgtDriver.log
```
