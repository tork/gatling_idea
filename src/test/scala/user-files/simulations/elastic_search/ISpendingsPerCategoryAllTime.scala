import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import akka.util.duration._

import bootstrap._

class ISpendingsPerCategoryAllTime extends Simulation {
  val acctFeeder = csv("accts.csv").queue

  val scn = scenario("ElasticSearch Benchmark 0")
    .feed(acctFeeder)
    .repeat(1) {
    val req = "/sb1/transer/_search"

    exec(
      http("req0")
        .post(req)
        .body(
        """{"size" : 0,
          |"query" : {
          |"filtered" : {
          |"filter" : {
          |"term" : {"accountNumber" : ${accountNumber}}}}},
          |"facets" : {
          |"type_amount_stats" : {
          |"terms_stats" : {
          |"key_field" : "transactionCode",
          |"value_field" : "amount"}}}}""".stripMargin).asJSON
        .check(status.is(200)
      )
    )

    /* I am slow. Uncomment for speed comparison!
      exec(
      http("req1")
        .post(req)
        .body(
        """{"fields": ["accountNumber"],
          |"facets": {
          |"type_amount_stats": {
          |"terms_stats": {
          |"key_field": "transactionCode",
          |"value_field": "amount"
          |},
          |"facet_filter": {
          |"term": {"accountNumber": ${accountNumber}}}}}}""".stripMargin).asJSON
        .check(status.is(200))
    )
    */
  }
  setUp(scn.users(1).ramp(1).protocolConfig(Config.httpConf))
}
