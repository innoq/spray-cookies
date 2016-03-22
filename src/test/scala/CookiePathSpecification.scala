import net.spraycookies.CookieJar
import net.spraycookies.tldlist.DefaultEffectiveTldList
import org.scalacheck.Prop.forAll
import org.scalacheck._
import spray.http.{ Uri, HttpCookie }

object CookiePathSpecification extends Properties("CookiePathJuggling") {

  val hostAndSchemeGen = Gen.oneOf("http://www1.example.com", "http://www2.example.com", "http://other.org", "http://www.something.net")

  val someNameGen = Gen.identifier suchThat (s ⇒ (s.length < 500) && (s.length > 1)) // avoid URLs >1024 in total
  val dirDepth = Gen.choose(0, 2)
  val end: Gen[String] = Gen.oneOf("/", "")

  // hints for smarter generation of this kind of data are welcome ;-)
  val cookieGen = for {
    host ← hostAndSchemeGen
    dir ← someNameGen
    subdir ← someNameGen
    depth ← dirDepth
    end ← end
    name ← someNameGen
    value ← someNameGen
  } yield CookieContainer(host, depth, dir, subdir, end, name, value)

  property("cookiesSetAndUsedForPath") = forAll(cookieGen) { url ⇒
    {
      val cookiejar = new CookieJar(DefaultEffectiveTldList)

      cookiejar.setCookie(HttpCookie(url.name, url.value), url.toString)
      val cookiesDirect = cookiejar.cookiesfor(Uri(url.toString))

      val uri = Uri(url.toString)
      val lastSep = uri.path.toString.lastIndexOf('/')
      val shortPath = if (lastSep > 0) {
        uri.path.toString.substring(0, lastSep)
      } else {
        "/"
      }
      val shortUri = uri.withPath(Uri(shortPath).path)
      val cookiesShort = cookiejar.cookiesfor(shortUri)

      if (cookiesDirect.size == 1 && cookiesShort.size == 1) {
        true
      } else {
        println(url + "\t" + url.name + "\t" + url.value)
        println("shortUrl: " + shortUri)
        println("direct: " + cookiesDirect)
        println("short: " + cookiesShort)
        false
      }
    }
  }

}

sealed case class CookieContainer(host: String, depth: Int, dir: String, subdir: String, end: String, name: String, value: String) {

  override def toString = {
    val sb = StringBuilder.newBuilder
    sb.append(host + "/")
    if (depth > 0) {
      sb.append(dir)
      if (depth > 1) {
        sb.append("/" + subdir)
      }
      sb.append(end)
    }
    sb.toString
  }
}
