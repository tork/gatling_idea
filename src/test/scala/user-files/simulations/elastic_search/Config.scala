import com.excilys.ebi.gatling.http.Predef._

object Config {
  val host = "http://bigdata05.dev.bekk.no:9200"
  val httpConf = httpConfig
    .baseURL(host)
    .acceptCharsetHeader("ISO-8859-1,utf-8;q=0.7,*;q=0.7")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("fr,fr")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:8.0.1) Gecko/20100101 Firefox/8.0.1")
}
