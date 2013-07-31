import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import akka.util.duration._

import bootstrap._

class ITestMulti extends Simulation {
  val NUM_TESTS = 4
  val DEFAULT_REPEAT = 4

  var repeat, users, ramp = 0

  setFrequency(70, 10*60)

  def setFrequency(testsPerSec : Int, runtime : Int) {
    val rept = DEFAULT_REPEAT
    val usrs = ((testsPerSec * runtime) / NUM_TESTS) / rept
    val ramp = runtime

    setParameters(usrs, ramp, rept)
  }

  def setParameters(users : Int, ramp : Int, repeat : Int) {
    this.users = users
    this.ramp = ramp
    this.repeat = repeat
  }

  setUp(
    ITest1.runScenario(users, ramp, repeat),
    ITest2.runScenario(users, ramp, repeat),
    ITest3.runScenario(users, ramp, repeat),
    ITest4.runScenario(users, ramp, repeat)
  )
}
