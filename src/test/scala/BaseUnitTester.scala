// See README.md for license details.
package ambel

import java.io.File
import scala.math._
import scala.util.Random
import org.scalatest._
import chisel3._
import chisel3.util._
import chiseltest._

/** =Unit Tester Base Trait=
  */
trait BaseUnitTester extends FlatSpec
    with ChiselScalatestTester
    with BeforeAndAfterAllConfigMap {

  // The following variables may be overridden by supplying -D<name>=<value>
  // command line argument to test or testOnly. E.g.
  // testOnly <test name> -- -Dseed=12345 -Ddebug=1 -Dverbose=1
  var _seed: Int = 0
  var _debug: Boolean = false
  var _verbose: Boolean = false

  override def beforeAll(configMap: ConfigMap) = {
    _seed = configMap.getWithDefault[String]("seed", "0").toInt
    _debug = if (configMap.getWithDefault[String]("debug", "0").toInt > 0) true else false
    _verbose = if (configMap.getWithDefault[String]("verbose", "0").toInt > 0) true else false
    println(f"    _seed    = ${_seed}")
    println(f"    _debug   = ${_debug}")
    println(f"    _verbose = ${_verbose}")
  }

  val rand = new Random(_seed)
}
