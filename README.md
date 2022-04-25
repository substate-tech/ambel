<!--- 
This is the README.md for the Chisel Lang Chisel3 RTL generator project called Ambel which is an AMBA APB APB2 AXI compnent module
generator with an APB slave "APB slave" APB Control/Status APB Registers Register Map Verilog generator and other AMBA AP2 components 
like an APB bridge network "network on chip" NoC bit field configurable register pipeline slice "register file" open source hardware
--->
![AMBEL Logo](docs/AMBEL.png)

A [Chisel3](https://github.com/chipsalliance/chisel3) library for generating [AMBA](https://developer.arm.com/architectures/system-architectures/amba) components

# Overview
AMBEL aims to provide a collection of parameterizable [Chisel3](https://github.com/chipsalliance/chisel3) Modules compatible with ARM's widely adopted [AMBA (Advanced Microcontroller Bus Architecture)](https://developer.arm.com/architectures/system-architectures/amba) protocols. The initial collection keeps it simple, focusing solely on [APB2](https://developer.arm.com/documentation/ihi0011/a/AMBA-APB) protocol and offering Modules that may be connected to implement APB2 networks connecting APB2 initators and targets. The key Module in this small collection is [Apb2CSTrgt](src/main/scala/Apb2CSTrgt.scala) which is an APB2 target implementing control/status registers, defined using a simple JSON schema.

The Modules are primarily intended for integration into other Chisel designs, but they could also be used to generate Verilog for integration into Verilog designs.

![ci push status](https://github.com/richmorj/ambel/actions/workflows/ci.yaml/badge.svg?event=push)

# Modules

## [Apb2CSTrgt](src/main/scala/Apb2CSTrgt.scala)
The Apb2CSTrgt Module implements a basic APB2 control/status register set with the registers and their address map supplied via a simple JSON description, passed as a parameter to the Module. The JSON register description is parsed using [circe](https://github.com/circe/circe) and the resulting objects are used to generate the registers, read-write access to them via the APB2 interface, and any associated direct IO for the registers using Chisel's [MixedVec](https://www.chisel-lang.org/api/latest/chisel3/util/MixedVec.html). Setting the `GEN_BUNDLE` parameter to true it is possible to generate a set of Bundles suitable for ordered connection to the MixedVec IO externally. There's an auto-generated Bundle for each register bit field type with a member for each bit field named after its register name and bit field name (as specified in the JSON). An example of usage is given in [ExampleApb2CSTrgt.scala](src/main/scala/examples/ExampleApb2CSTrgt.scala). The generated Verilog for this example design is also versioned in [ExampleApb2CSTrgt.v](src/main/verilog/examples/ExampleApb2CSTrgt.v)

### Register bit field modes

#### RW register bits
Read-write. Generally these are static configuration bit fields and are connected to Outputs. The register bits can only be set or cleared by writing to the register.

#### RO register bits
Read-only. These are connected to Inputs which should be driven by registered external status signals from the design instantiating Apb2CSTrgt. Writing to these registers has no effect.

#### WO register bits
Write-only. These are connected to Outputs but after being written to '1' they are always set back to to '0' on the following clock cycle, so writing a '1' to a WO register bit will create a single-cycle pulse on the corresponding Output. This bit field mode can be used to implement 'go bits' which trigger some event elsewhere in the design instantiating Apb2CSTrgt.  Writing a '0' has no effect, reads are always '0'.

#### W1C register bits
Write-1-to-clear. These are connected to Inputs on which a single cycle pulse will set the corresponding register bit to '1'. Writing a '1' to the same bit will clear it. Writing a '0' has no effect, regardless of the current value of the bit. This bit field mode is the one to use for interrupt status registers. Interrupt enable/mask registers should be implemented using RW bit fields with the enable/mask logic implemented externally in the design instantiating Apb2CSTrgt.

### Register description JSON schema
JSON was chosen over other formats (e.g. XML or RDL) for the AMBEL register descriptions for human readability/maintainability and for the availability of powerful JSON parsers for Scala, such as [circe](https://github.com/circe/circe).

The JSON schema consists of two top level objects: the register map `regMap` and the register types `regTypes`. The register map is a list of registers with each register defined by its address `offset`, its `name`, its `typeRef` and an optional `comment` string which can be used to describe the register. The `typeRef` object is a reference to one of the objects in the `regTypes` list. Each object in the `regTypes` list describes the attributes of a particular register type. Every register in the `regMap` must reference one of the register types in the `regTypes` list via its `typeRef`. In this way, if the register map contains more than one instance of a particular type of register, we only need to describe that register type once.

Each register type object in `regTypes` consists of a `typeRef` (just a lable via which it may be referenced in the `regMap`), its `width`, a list of its bit-`fields` and an optional `comment` string which can be used to describe the register type.

Each object in the `fields` list describes a bit-field, detailing the location of its `bits` in the register, its `name`, its `mode` and, optionally, its reset value `resetVal` and a `comment` string which can be used to describe the bit-field's functionality.

## [Apb2Net](src/main/scala/Apb2Net.scala)
The Apb2Net Module implements an APB2 network connecting a parameterizable number of APB2 initiators and targets with further parameters defining address map for the  targets (base addresses and sizes). 

## [Apb2Slice](src/main/scala/Apb2Slice.scala)
The Apb2Slice Module is a simple APB2 bus register slice for pipelining/timing closure. The Module has a pair of APB2 interfaces (initiator and target) and works by registering initiator requests, forwarding them to the target interface, and extending the response with wait states until the target responds. The target response itself is registered to return to the initator. 
