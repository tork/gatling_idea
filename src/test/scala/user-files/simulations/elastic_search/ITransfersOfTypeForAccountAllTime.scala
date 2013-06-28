import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import akka.util.duration._

import bootstrap._

class ITransfersOfTypeForAccountAllTime extends Simulation {
  val acctFeeder = csv("accts.csv").queue

  val scn = scenario("ElasticSearch Benchmark 0")
    .feed(acctFeeder)
    .repeat(1) {
    val req = "/sb1/transer/_search"

    exec(
      http("req0")
        .post(req)
        .body(
        """
          |{"fields" : ["id"],
          |"query" : {
          |"filtered" : {
          |"filter" : {
          |"and" : [{
          |"term" : {"transactionCode" : "_205"}},{
          |"term" : {"accountNumber" : ${accountNumber}}}]}}}}
        """.stripMargin).asJSON


        //.body("""{query: {term: {"accountNumber": ${accountNumber}}}}}""").asJSON
        .check(status.is(200))
    )
  }
  setUp(scn.users(1).ramp(3).protocolConfig(Config.httpConf))
}
