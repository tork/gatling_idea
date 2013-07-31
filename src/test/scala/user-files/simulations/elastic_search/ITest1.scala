import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.core.scenario.configuration.ConfiguredScenarioBuilder
import com.excilys.ebi.gatling.http.Predef._
import akka.util.duration._

import bootstrap._
import java.text.{ParsePosition, SimpleDateFormat}
import java.util.Date

class ITest1 extends Simulation {
  setUp(
    ITest1.runScenario(10, 2),
    ITest1.runScenario(100, 2)
  )
}

object ITest1 {
  val acctFeeder = csv(Config.accts).random
  val sdf = new SimpleDateFormat("yyyy-MM-dd")
  val startDate = sdf.parse("2010-12-01", new ParsePosition(0)).getTime()
  val body =
    """
      |{
      |	"fields" : ["id","date", "accountNumber", "description", "transactionCode", "amount", "remoteAccountNumber"],
      | 	"size" : "50",
      |	"query" : {
      |		"filtered" : {
      |			"filter" : {
      |				"and" : [
      |					{
      |						"range" : {
      |							"date" : {
      |								"from" : %d
      |							},
      |							"_cache" : false
      |						}
      |					},
      |					{
      |						"term" : {
      |    						"accountNumber" : ${accountNumber1},
      |    						"_cache" : false
      |    					}
      |					}
      |				]
      |			}
      |		}
      |	}
      |}
    """

  def runScenario(users: Int, ramp: Int) : ConfiguredScenarioBuilder = {
    return runScenario(users, ramp, 1)
  }

  def runScenario(users: Int, ramp: Int, repeat: Int) : ConfiguredScenarioBuilder = {
    val id = "Test 1 - users:%d, ramp:%d, repeat:%d".format(users, ramp, repeat)
    val scn = scenario(id)
      .feed(acctFeeder)
      .repeat(repeat) {
      val req = "/_search?routing=${accountNumber1}"

      exec(
        http(id)
          .post(req)
          .body(body.format(startDate).stripMargin).asJSON
          .check(status.is(200)
        )
      )
    }

    return scn.users(users).ramp(ramp).protocolConfig(Config.httpConf)
  }
}
