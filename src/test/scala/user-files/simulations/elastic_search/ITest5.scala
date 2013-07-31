import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.core.scenario.configuration.ConfiguredScenarioBuilder
import com.excilys.ebi.gatling.http.Predef._
import akka.util.duration._

import bootstrap._

class ITest5 extends Simulation {
  val sec = 60
  setUp(
    ITest4.runScenario(10, 2),
    ITest4.runScenario(100, 2),
    ITest4.runScenario(40*sec, 1*sec)
  )
}

object ITest5 {
  val acctFeeder = csv(Config.acctsMulti).random
  val toknFeeder = csv(Config.tokens).random
  val body =
    """
      |{
      |  "query": {
      |    "bool": {
      |      "must": [
      |        {
      |          "term": {
      |            "description": "${token1} ${token2} ${token3}"
      |          }
      |        },
      |        {
      |          "term": {
      |            "accountNumber": "${accountNumber3} ${accountNumber2} ${accountNumber1}"
      |          }
      |        }
      |      ]
      |    }
      |  },
      |  "from": 0,
      |  "size": 0
      |}
    """

  def runScenario(users: Int, ramp: Int) : ConfiguredScenarioBuilder = {
    return runScenario(users, ramp, 1)
  }

  def runScenario(users: Int, ramp: Int, repeat: Int) : ConfiguredScenarioBuilder = {
    val id = "Test 5 - users:%d, ramp:%d, repeat:%d".format(users, ramp, repeat)
    val scn = scenario(id)
      .feed(acctFeeder)
      .repeat(repeat) {
      val req = "/_search?routing=${accountNumber3},${accountNumber2},${accountNumber1}"

      exec(
        http(id)
          .post(req)
          .body(body.stripMargin).asJSON
          .check(status.is(200)
        )
      )
    }

    return scn.users(users).ramp(ramp).protocolConfig(Config.httpConf)
  }
}
