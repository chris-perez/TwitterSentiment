package models

import javax.inject.Inject
import play.api.libs.ws._
import scala.concurrent.Future
import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax

/**
 * Created by chris_000 on 12/5/2015.
 */
class TwitterAPI @Inject() (ws: WSClient) {
  var endpoint: String = "https://api.twitter.com/1.1/search/tweets.json"

  def getTweets(q: String, result_type: String, lat: Int, long: Int, radius: Int): String = {
    val url = endpoint + "?q=" + q + "&result_type=" + result_type + "&geocode=" + lat + "," + long + "," + radius + "mi"
    val futureResult: Future[String] = ws.url(url).get().map {
      response =>
        (response.json \ "person" \ "name").as[String]
    }
    futureResult[String]
  }

  def getTweets(q: String, result_type: String): String = {
    val url = endpoint + "?q=" + q + "&result_type=" + result_type

    val futureResult: Future[String] = ws.url(url).get().map {
      response => response.json
        (response.json \ "person" \ "name").as[String]
    }
    futureResult[String]
  }

  def getTweetsByHashTag(hashTag: String): String = {
    getTweets("%23" + hashTag, "recent")
  }
}
