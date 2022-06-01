---
layout: default
---
# AMBEL - A Chisel Library for Generating AMBA Components

AMBEL is a [Chisel3](https://www.chisel-lang.org/) library for generating [AMBA](https://www.arm.com/architecture/system-architectures/amba) components featuring an APB Control/Status Register (CSR) target (an APB slave) generator driven by a simple JSON schema for capturing register descriptions and the register address map.
## Project Documentation

[ScalaDocs](latest/api/ambel)

## Coverage
[Verilator Line Coverage](coverage/ambel)

One of the goals for AMBEL as an open source hardware (OSHW) project is transparency of functional verification coverage. There are some challenges with the interpretation of Verilator line coverage of Chisel3 generated Verilog. However, it is good metric for evaluating the quality of the unit tests. The HTML coverage reports are deployed to GitHub pages at the link above when CI unit tests pass.
