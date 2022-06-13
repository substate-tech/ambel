# Register bit field modes

## RW register bits
Read-write. Generally these are static configuration bit fields and are connected to Outputs. The register bits can only be set or cleared by writing to the register.

## RO register bits
Read-only. These are connected to Inputs which should be driven by registered external status signals from the design instantiating `Apb2CSTrgt`. Writing to these registers has no effect.

## WO register bits
Write-only. These are connected to Outputs but after being written to '1' they are always set back to to '0' on the following clock cycle, so writing a '1' to a WO register bit will create a single-cycle pulse on the corresponding Output. This bit field mode can be used to implement 'go bits' which trigger some event elsewhere in the design instantiating `Apb2CSTrgt`.  Writing a '0' has no effect, reads are always '0'.

## W1C register bits
Write-1-to-clear. These are connected to Inputs on which a single cycle pulse will set the corresponding register bit to '1'. Writing a '1' to the same bit will clear it. Writing a '0' has no effect, regardless of the current value of the bit. This bit field mode is the one to use for interrupt status registers. Interrupt enable/mask registers should be implemented using RW bit fields with the enable/mask logic implemented externally in the design instantiating `Apb2CSTrgt`.
