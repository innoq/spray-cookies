package net.spraycookies

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll
import org.scalacheck.Gen

object CookiejarSpecification extends Properties("CookieHandling") {
  import spray.http.HttpRequest
  import spray.http.HttpResponse
  import spray.http.HttpCookie
  import spray.http.HttpHeaders.`Set-Cookie`
  import spray.http.HttpHeaders.Cookie
  import net.spraycookies.tldlist.EffectiveTldList
  import scala.concurrent._
  import scala.concurrent.duration._
  import ExecutionContext.Implicits.global

  def emptyRequest = HttpRequest()
  def emptyTldlist = new EffectiveTldList {
    def contains(domain: String) = false
  }

  val genToken = {
    val separators = List('(', ')', '<', '>', '@', ',', ';', ':', '\\', '"', '/', '[', ']', '?', '=', '{', '}', ' ', '\t')
    val allowedChars = Range(32, 126).map(_.toChar).toSet -- separators
    val genTokenChar = Gen.oneOf(allowedChars.toSeq)
    Gen.containerOf[List, Char](genTokenChar).suchThat(_ != Nil).map(_.mkString)
  }

  val genCookieValue = {
    val genCookieValueChar = Gen.oneOf(Gen.choose(0x21, 0x21), Gen.choose(0x23, 0x2B), Gen.choose(0x2D, 0x3A), Gen.choose(0x3C, 0x5B), Gen.choose(0x5D, 0x7E)).map(i ⇒ i.toChar)
    Gen.containerOf[List, Char](genCookieValueChar).suchThat(_ != Nil).map(_.mkString)
  }

  val genBareCookie = for {
    name ← genToken
    value ← genCookieValue
  } yield HttpCookie(name, value)

  val genBareCookieList = Gen.containerOf[List, HttpCookie](genBareCookie)

  property("withCookies") = forAll(genBareCookieList) { cookies ⇒
    {
      val jar = new CookieJar(emptyTldlist)

      val addingPipeline = (req: HttpRequest) ⇒ {
        val resp = HttpResponse()
        Future {
          val setCookieHeaders = cookies.map(`Set-Cookie`(_))
          val cookiedresp = resp.withHeaders(setCookieHeaders)
          cookiedresp
        }
      }

      val testingPipeline = (req: HttpRequest) ⇒ {
        Future {
          val httpCookies = req.headers.collect({ case Cookie(httpCookies) ⇒ httpCookies }).flatten
          if (httpCookies.length > cookies.length) throw new Exception("received more cookies than expected")
          else if (!cookies.forall(expected ⇒ httpCookies.exists(received ⇒ received.name == expected.name))) throw new Exception("reponse didn't contain cookies for all names")
          else if (!httpCookies.forall(received ⇒ cookies.exists(testcookie ⇒ testcookie.name == received.name))) throw new Exception("reponse contained a cookie with a name that is not expected")
          else HttpResponse()
        }
      }

      val cookiedPipeline = Cookiehandling.withCookies(None, Some(jar))(addingPipeline)
      Await.result(cookiedPipeline(emptyRequest), 10.seconds)
      val cookiedTestPipeline = Cookiehandling.withCookies(Some(jar), None)(testingPipeline)
      val ftestresult = cookiedTestPipeline(HttpRequest())
      val testresult = try {
        Await.result(ftestresult, 10.seconds)
        true
      } catch {
        case t: Exception ⇒ {
          println(s"test failed $t")
          false
        }
      }

      testresult
    }
  }
}
