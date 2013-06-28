import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import akka.util.duration._

import bootstrap._

class ITransfersForAccountLastMonth extends Simulation {
  val acctFeeder = csv("accts.csv").queue
  val startDate = "1354320000000"

  val scn = scenario("ElasticSearch Benchmark 0")
    .feed(acctFeeder)
    .repeat(1) {
    val req = "/sb1/transer/_search"

    exec(
      http("req0")
        .post(req)
        .body(
        """
          |{
          |"fields" : ["id","date"],
          |"query" : {
          |"filtered" : {
          |"filter" : {
          |"and" : [{
          |"range" : {
          |"date" : {
          |"from" : %s}}}, {
          |"term" : {"accountNumber" : ${accountNumber}}}]}}}}
        """.format(startDate).stripMargin).asJSON
        .check(status.is(200)
      )
    )
  }
  setUp(scn.users(1).ramp(2).protocolConfig(Config.httpConf))
}
