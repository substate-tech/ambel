<img src="https://github.com/richmorj/ambel/blob/main/docs/AMBEL.png" width="400" />

A ![Chisel](https://github.com/chipsalliance/chisel3) library for generating ![AMBA](https://developer.arm.com/architectures/system-architectures/amba) components

# Overview
AMBEL aims to provide a collection of parameterizable ![Chisel](https://github.com/chipsalliance/chisel3) Modules compatible with ARM's widely adopted ![AMBA (Advanced Microcontroller Bus Architecture)](https://developer.arm.com/architectures/system-architectures/amba) protocols. The initial collection keeps it simple, focusing solely on ![APB2](https://developer.arm.com/documentation/ihi0011/a/AMBA-APB) protocol and offering Modules that may be connected to implement APB2 networks connecting APB2 initators and targets. The key Module in this small collection is [Apb2CSTrgt](src/main/scala/Apb2CSTrgt.scala) which is an APB2 target implementing control/status registers, defined using a simple JSON schema.

# Modules

## Apb2CSTrgt
The Apb2CSTrgt Module implements a basic APB2 control/status register set with the registers and their address map supplied via a simple JSON description, passed as a parameter to the Module.

### Register bit field types

#### RW register bits
Read-write. Generally these are static configuration bit fields and are connected to Outputs. The register bits can only be set or cleared by writing to the register.

#### RO register bits
Read-only. These are connected to Inputs which should be driven by registered external status signals from the design instantiating Apb2CSTrgt. Writing to these registers has no effect.

#### WO register bits
Write-only. These are connected to Outputs but after being written to '1' they are always set back to to '0' on the following clock cycle, so writing a '1' to a WO register bit will create a single-cycle pulse on the corresponding Output. This bit field mode can be used to implement 'go bits' which trigger some event elsewhere in the design instantiating Apb2CSTrgt.  Writing a '0' has no effect, reads are always '0'.

#### W1C register bits
Write-1-to-clear. These are connected to Inputs on which a single cycle pulse will set the corresponding register bit to '1'. Writing a '1' to the same bit will clear it. Writing a '0' has no effect, regardless of the current value of the bit. This bit field mode is the one to use for interrupt status registers. Interrupt enable/mask registers should be implemented using RW bit fields with the enable/mask logic implemented externally in the design instantiating Apb2CSTrgt.

### Register description JSON schema
TODO

## Apb2Net
The Apb2Net Module implements an APB2 network connecting a parameterizable number of APB2 initiators and targets with further parameters defining address map for the  targets (base addresses and sizes). 

## Apb2Slice
The Apb2Slice Module is a simple APB2 bus register slice for pipelining/timing closure. The Module has a pair of APB2 interfaces (initiator and target) and works by registering initiator requests, forwarding them to the target interface, and extending the response with wait states until the target responds. The target response itself is registered to return to the initator. 
