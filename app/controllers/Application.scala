package controllers

import javax.inject.Inject

import models.TwitterAPI
import play.api._
import play.api.libs.ws.WSClient
import nlp.NlpProcessor
import play.api.mvc._
import play.api.libs.json._
import play.api.Play.current

import edu.stanford.nlp._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.duration._


class Application @Inject() (ws: WSClient) extends Controller {

  def index = Action {
    Ok(views.html.map())
  }

  def getPoly() = Action {

    //Json response example -> https://www.playframework.com/documentation/2.2.x/ScalaJson
    val json: JsValue = JsObject(Seq(
    "color" -> JsString("#ff0000")
    ))
    Ok(json)
  }

  def getSentiment(text : String) = Action {

    val score : Int =  NlpProcessor.getSentiment(text)
    val json: JsValue = JsObject(Seq(
      "Score" -> JsNumber(score)
    ))
    Ok(json)

  }

  def getTweets() = Action {
    val api = new TwitterAPI(ws)
    val tweetMap = api.getStateTweets("donaldtrump")
    var map:Map[String, JsArray] = Map()

    for (t <- tweetMap) {
      var tweetList:JsArray = Json.arr()
      for (tweet <- t._2) {
        tweetList = tweetList.append(Json.obj(
          "text" -> tweet.text
        ))
      }
      map += (t._1 -> tweetList)
    }
    Ok(Json.toJson(map))
  }

}
