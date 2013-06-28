import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import akka.util.duration._

import bootstrap._

class ITransfersForAccountAllTime extends Simulation {
  val acctFeeder = csv("accts.csv").queue

  val scn = scenario("ElasticSearch Benchmark 0")
    .feed(acctFeeder)
    .repeat(1) {
    val req = "/sb1/transer/_search"

    exec(
      http("req0")
        .post(req)
//        .body("""{query: {term: {"transactionCodeText": "GIRO"}}}""").asJSON
        .body("""{query: {term: {"accountNumber": ${accountNumber}}}}}""").asJSON
        .check(status.is(200))
    )
  }
  setUp(scn.users(1000).ramp(3).protocolConfig(Config.httpConf))
}
