package controllers

import javax.inject.Inject

import models.TwitterAPI
import models.Tweet
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
  val api = new TwitterAPI(ws)

  def index = Action {
    Ok(views.html.map())
  }

  def getPoly() = Action {

    //Json response example -> https://www.playframework.com/documentation/2.2.x/ScalaJson

    val json: JsValue = JsObject(Seq(
    "Colorado" -> JsString("#ff0000"),
    "Idaho" -> JsString("#7f00ff")
    ))
    Ok(json)
  }

  def getSentiment(text : String) = Action {

    val score: Int = NlpProcessor.getSentiment(text)
    val json: JsValue = JsObject(Seq(
      "Score" -> JsNumber(score)
    ))
    Ok(json)
  }
  //given list of tweet objects,
  def sentimentToColor( sentiment: Float) : String = {
   val adjustedValue = ((sentiment/4.0)*255.0).asInstanceOf[Int]//.asInstanceOf[Int]
   val r = adjustedValue
   val g = 30
   val b = 255 - adjustedValue
   val hexVal: String = "#%02x%02x%02x".format( r, g, b)
    hexVal
  }

  def sentimentMap(lst: Map[String, List[Tweet]]) : JsObject = {
    var sentiment_by_state = Map[String, Float]()
    var color_by_state:JsObject = Json.obj()
    for((state , value) <- lst) {
      println("State: " + state)
        val len : Float = value.length
        for(tweet <- value){
          val current_avg = 0
          if (sentiment_by_state.contains(state)) {
            val current_avg: Float = sentiment_by_state(state)
          }
          val delta_avg = NlpProcessor.getSentiment(tweet.text)/len
          sentiment_by_state += (state -> (current_avg + delta_avg))
        }
      if (! sentiment_by_state.contains(state)) {
        sentiment_by_state += (state -> 2.0f)
      }
     //val temp: String = state
     val color =sentimentToColor(sentiment_by_state(state))
      color_by_state += (state -> JsString(color))
//     color_by_state.append(Json.obj(state -> color))
    }

    color_by_state
  }
  def getAvgSentiment( lst: List[Tweet]) :Double = {
    var sum = 0.0
    var count = 0.0
    //val analyzer: nlpProcesser = new nlpProcessor
    lst foreach { item =>
      sum += NlpProcessor.getSentiment(item.text)
      count += 1.0
    }
    sum/count
  }
  def getTweets() = Action {
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

  def query(query : String) = Action {
    val json = sentimentMap(api.getStateTweets(query))
    /*val json: JsValue = JsObject(Seq(
      "Colorado" -> JsString("#ff0000"),
      "Idaho" -> JsString("#7f00ff")
    ))*/
    Ok(json)
  }

}
