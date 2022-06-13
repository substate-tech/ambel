# Register description JSON schema
JSON was chosen over other formats (e.g. XML or RDL) for the AMBEL register descriptions for human readability/maintainability and for the availability of powerful JSON parsers for Scala, such as [circe](https://github.com/circe/circe).

The JSON schema consists of two top level objects: the register map `regMap` and the register types `regTypes`. The register map is a list of registers with each register defined by its address `offset`, its `name`, its `typeRef` and an optional `comment` string which can be used to describe the register. The `typeRef` object is a reference to one of the objects in the `regTypes` list. Each object in the `regTypes` list describes the attributes of a particular register type. Every register in the `regMap` must reference one of the register types in the `regTypes` list via its `typeRef`. In this way, if the register map contains more than one instance of a particular type of register, we only need to describe that register type once.

Each register type object in `regTypes` consists of a `typeRef` (just a label via which it may be referenced in the `regMap`), its `width`, a list of its bit-`fields` and an optional `comment` string which can be used to describe the register type.

Each object in the `fields` list describes a bit-field, detailing the location of its `bits` in the register, its `name`, its `mode` and, optionally, its reset value `resetVal` and a `comment` string which can be used to describe the bit-field's functionality.
