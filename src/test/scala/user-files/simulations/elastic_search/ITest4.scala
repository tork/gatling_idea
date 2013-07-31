import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.core.scenario.configuration.ConfiguredScenarioBuilder
import com.excilys.ebi.gatling.http.Predef._
import akka.util.duration._

import bootstrap._

class ITest4 extends Simulation {
  setUp(
    ITest4.runScenario(10, 2),
    ITest4.runScenario(100, 2)
  )
}

object ITest4 {
  val acctFeeder = csv(Config.acctsMulti).random
  val body =
    """
      |{
      |  "fields": [
      |    "accountNumber"
      |  ],
      |  "query": {
      |    "filtered": {
      |      "filter": {
      |        "terms": {
      |          "accountNumber": [
      |            ${accountNumber1},
      |            ${accountNumber2},
      |            ${accountNumber3}
      |          ],
      |          "execution": "bool_nocache",
      |          "_cache": false
      |        }
      |      }
      |    }
      |  },
      |  "facets": {
      |    "type_amount_stats": {
      |      "terms_stats": {
      |        "key_field": "category",
      |        "value_field": "amount"
      |      }
      |    }
      |  }
      |}
    """

  def runScenario(users: Int, ramp: Int) : ConfiguredScenarioBuilder = {
    return runScenario(users, ramp, 1)
  }

  def runScenario(users: Int, ramp: Int, repeat: Int) : ConfiguredScenarioBuilder = {
    val id = "Test 4 - users:%d, ramp:%d, repeat:%d".format(users, ramp, repeat)
    val scn = scenario(id)
      .feed(acctFeeder)
      .repeat(repeat) {
      val req = "/_search?routing=${accountNumber1},${accountNumber2},${accountNumber3}"

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
