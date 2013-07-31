import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.core.scenario.configuration.ConfiguredScenarioBuilder
import com.excilys.ebi.gatling.http.Predef._
import akka.util.duration._

import bootstrap._

class ITest3 extends Simulation {
  setUp(
    ITest3.runScenario(10, 2),
    ITest3.runScenario(100, 2)
  )
}

object ITest3 {
  val acctFeeder = csv(Config.accts).random
  val body =
    """
      |{
      |	"fields" : ["accountNumber"],
      |    "query" : {
      |    	"filtered" : {
      |    		"filter" : {
      |    			"term" : {
      |    				"accountNumber" : ${accountNumber1},
      |    				"_cache" : false
      |    			}
      |    		}
      |    	}
      |    },
      |    "facets" : {
      |        "type_amount_stats" : {
      |            "terms_stats" : {
      |                "key_field" : "category",
      |                "value_field" : "amount"
      |            }
      |        }
      |    }
      |}
    """

  def runScenario(users: Int, ramp: Int) : ConfiguredScenarioBuilder = {
    return runScenario(users, ramp, 1)
  }

  def runScenario(users: Int, ramp: Int, repeat: Int) : ConfiguredScenarioBuilder = {
    val id = "Test 3 - users:%d, ramp:%d, repeat:%d".format(users, ramp, repeat)
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
