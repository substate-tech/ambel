// See README.md for license details.
package ambel

import java.io.File
import scala.math._
import scala.util.Random
import org.scalatest._
import org.scalatest.flatspec.AnyFlatSpec
import chisel3._
import chisel3.util._
import chiseltest._
import chiseltest.simulator.VerilatorFlags
import firrtl.AnnotationSeq

/** =Unit Tester Base Trait=
  */
trait BaseUnitTester extends AnyFlatSpec
    with ChiselScalatestTester
    with BeforeAndAfterAllConfigMap {

  // The following variables may be overridden by supplying -D<name>=<value>
  // command line argument to test or testOnly. E.g.
  // testOnly <test name> -- -Dseed=12345 -Ddebug=1 -Dverbose=1
  // Default simulator backend is Treadle but Verilator is also supported
  // with -- -Dbackend=verilator. If Verilator is selected then coverage
  // is enabled by setting the appropriate annotations.
  var _seed: Int = 0
  var _debug: Boolean = false
  var _verbose: Boolean = false
  var _backend: String = "treadle"

  var rand: Random = new Random(0)
  var annos: AnnotationSeq = Seq()

  override def beforeAll(configMap: ConfigMap) = {
    _seed = configMap.getWithDefault[String]("seed", "0").toInt
    _debug = if (configMap.getWithDefault[String]("debug", "0").toInt > 0) true else false
    _verbose = if (configMap.getWithDefault[String]("verbose", "0").toInt > 0) true else false
    _backend = configMap.getWithDefault[String]("backend", "treadle").toString

    println(f"    _seed    = ${_seed}")
    println(f"    _debug   = ${_debug}")
    println(f"    _verbose = ${_verbose}")
    println(f"    _backend = ${_backend}")

    rand = new Random(_seed)

    if (_backend == "verilator") {
      annos = annos :+ VerilatorBackendAnnotation
      annos = annos :+ VerilatorFlags(Seq("--coverage-line"))
    }
  }


  override def afterAll(configMap: ConfigMap) = {
    if (_backend == "verilator") {
      // Line coverage post-processing

    }
  }
}
