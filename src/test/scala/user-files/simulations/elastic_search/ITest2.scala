import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.core.scenario.configuration.ConfiguredScenarioBuilder
import com.excilys.ebi.gatling.http.Predef._
import akka.util.duration._

import bootstrap._

class ITest2 extends Simulation {
  setUp(
    ITest2.runScenario(10, 2),
    ITest2.runScenario(100, 2)
  )
}

object ITest2 {
  val acctFeeder = csv(Config.accts).random
  val body =
    """
      |{
      |	"size" : 0,
      |    "query" : {
      |        "filtered" : {
      |        	"filter" : {
      |				"term" : {
      |    				"accountNumber" : ${accountNumber1},
      |    				"_cache" : false
      |    			}
      |			}
      |        }
      |    },
      |    "facets" : {
      |        "spending/month" : {
      |            "date_histogram" : {
      |                "key_field" : "date",
      |                "value_field" : "amount",
      |                "interval" : "month",
      |                "time_zone" : 2
      |            }
      |        }
      |    }
      |}
    """

  def runScenario(users: Int, ramp: Int) : ConfiguredScenarioBuilder = {
    return runScenario(users, ramp, 1)
  }

  def runScenario(users: Int, ramp: Int, repeat: Int) : ConfiguredScenarioBuilder = {
    val id = "Test 2 - users:%d, ramp:%d, repeat:%d".format(users, ramp, repeat)
    val scn = scenario(id)
      .feed(acctFeeder)
      .repeat(repeat) {
      val req = "/_search?routing=${accountNumber1}"

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
