package models

import javax.inject.Inject
import com.google.common.base.Charsets
import com.google.common.io.BaseEncoding
import play.api.libs.ws._
import scala.concurrent.{Await, Future}
import play.api.libs.json._
import play.api._
import play.api.Play.current

import scala.concurrent.duration.Duration

// JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax
import  scala.concurrent.ExecutionContext.Implicits.global


/**
 * Created by chris_000 on 12/5/2015.
 */
class TwitterAPI @Inject() (ws: WSClient) {
  val TWITTER_API_KEY = "euoX2XoXH9Q5WY4AzVwitVdZf"
  val TWITTER_API_SECRET = "1bxT0CSIcWdNOg9pUhf7wPtsyCzLK99kd7lXRHEdZyK2HgaSs1"
  val TWITTER_API_CREDENTIALS = TWITTER_API_KEY + ":" + TWITTER_API_SECRET
  val encodedCredentials:String = BaseEncoding.base64().encode(TWITTER_API_CREDENTIALS.getBytes(Charsets.UTF_8))

  var endpoint: String = "https://api.twitter.com/1.1/search/tweets.json"   // Twitter endpoint
  // Authorize Twitter credentials on start
  var bearerToken = authorize()
  var states: List[State] = List()

  // get states from states.json file and create list of State objects
  var statesJson = Json.parse(Play.classloader.getResourceAsStream("public/json/states.json")).as[JsArray].value
  for (s <- statesJson) {
    val state = s.as[JsObject]
    val name = (state\"name").as[String]
    val id = (state\"id").as[String]
    states = new State(name, id)::states
  }

  /**
   * Scrapes tweets from twitter matching a query for every state.
   * @param q query to search for
   * @return map of state names to list of Tweet objects
   */
  def getStateTweets(q: String): Map[String, List[Tweet]] = {
    println("Scraping tweets...")
    var tweetMap:Map[String, List[Tweet]] = Map()
    for (s <- states) {
      var tweets:List[Tweet] = List()
      try {
        if (q.startsWith("#")) {
          q.replace("#", "%23")
        }
        val json = getTweets(q, "recent", s.id)
        val statuses: Seq[JsValue] = (json \ "statuses").as[JsArray].value
        for (t <- statuses) {
          tweets = new Tweet((t \ "text").as[String], s)::tweets
        }
        tweetMap += (s.name -> tweets)
      } catch  {
        case e:Exception => Console.err.println("Reached Twtter rate limit")
      }
    }
    println("Done scraping tweets... ")
    tweetMap
  }

  /**
   * Searches for tweets by query in a given radius
   * @param q query
   * @param result_type should always be 'recent'
   * @param lat latitude of coordinates
   * @param long longitude of coordinates
   * @param radius radius of circle to search in
   * @return array of tweets as JsValue
   */
  def getTweets(q: String, result_type: String, lat: Double, long: Double, radius: Double): JsValue = {
    val url = endpoint + "?q=" + q + "&result_type=" + result_type + "&geocode=" + lat + "," + long + "," + radius + "mi"  + "&count=100"
    val futureResult: Future[JsValue] = ws.url(url).withHeaders("Authorization" -> ("Bearer " + bearerToken)).get().map {
      response => response.json
    }
    Await.result(futureResult, Duration.Inf)
  }

  /**
   *
   * @param q Gets tweets that match a query and a place
   * @param result_type should always be 'recent'
   * @param place id of twitter place to search within
   * @return array of tweets as JsValue
   */
  def getTweets(q: String, result_type: String, place: String): JsValue = {
    val url = endpoint + "?q=place%3A" + place + " " + q + "&result_type=" + result_type + "&count=100"
    val futureResult: Future[JsValue] = ws.url(url).withHeaders("Authorization" -> ("Bearer " + bearerToken)).get().map {
      response => response.json
    }
    Await.result(futureResult, Duration.Inf)
  }

  /**
   * Gets tweets that match a query
   * @param q query
   * @param result_type should always be 'recent'
   * @return array of tweets as JsValue
   */
  def getTweets(q: String, result_type: String): JsValue = {
    val url = endpoint + "?q=" + q + "&result_type=" + result_type + "&count=100"
    val futureResult: Future[JsValue] = ws.url(url).withHeaders("Authorization" -> ("Bearer " + bearerToken)).get().map {
      response => response.json
    }
    Await.result(futureResult, Duration.Inf)
  }

  /**
   * Authorizes twitter credentials.
   * @return Bearer token to be used in header of each request to Twitter
   */
  def authorize(): String = {
    val url = "https://api.twitter.com/oauth2/token"
    val futureResult: Future[String] = ws.url(url)
      .withHeaders("Authorization" -> ("Basic " + encodedCredentials)).post(Map("grant_type"-> Seq("client_credentials"))).map(
          response => (response.json \ "access_token").as[String]
      )
    Await.result(futureResult, Duration.Inf)
  }
}
