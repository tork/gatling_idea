import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import akka.util.duration._

import bootstrap._

class OInsertTuples extends Simulation {
   val scn = scenario("ElasticSearch Benchmark 0")
     .repeat(50) {
       var req = "/gatling/bench0/%03d".format(0)
       var arg = """{name: "%03d", age: %d}"""
       exec(
         http("req0")
             .put(req)
                .body(arg).asJSON
           .check(status.is(200)))
   }
  setUp(scn.users(1).protocolConfig(Config.httpConf))
}
