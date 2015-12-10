package controllers

//<<<<<<< HEAD
import _root_.models.TwitterAPI
import nlp.NlpProcessor
import play.api.mvc._
import play.api.libs.json._
import edu.stanford.nlp._
//=======
//>>>>>>> twitter-requests
import javax.inject.Inject

import models.TwitterAPI
import play.api._
import play.api.libs.ws.WSClient
import nlp.NlpProcessor
import play.api.mvc._
import play.api.libs.json._
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

    val score: Int = NlpProcessor.getSentiment(text)
    val json: JsValue = JsObject(Seq(
      "Score" -> JsNumber(score)
    ))
    Ok(json)
  }
  //given list of tweet objects,
  def sentimentToColor( sentiment: float) : String = {
   val adjustedValue = Int((sentiment/4.0)*255.0)
   val r = adjustedValue
   val g = 30
   val b = 255 - adjustedValue
   val String hexVal = String.format("#%02x%02x%02x", r, g, b)
    hexVal
  }
  def sentimentMap(lst: List[List[Tweet]]) = {
    var sentiment_by_state = Map(string, float)
    var color_by_state:JsArray = Json.arr()
    lst.foreach {
      item =>
        val len = float(item.length)
        item.foreach{
          tweet =>
            val current_avg = sentiment_by_state(tweet.state)
            val delta_avg = NlpProcessor.getSentiment(tweet.text)/len
            sentiment_by_state + (tweet.state -> current_avg + delta_avg)

        }
     val temp: string =item.head.state
     val color =sentimentToColor(sentiment_by_state(temp))
     color_by_state.append(Json.obj("state"-> temp, "color"-> color))
    }
    color_by_state
  }
  def getAvgSentiment( lst: List[Tweet]) = {
    var sum = 0.0
    var count = 0.0
    //val analyzer: nlpProcesser = new nlpProcessor
    lst foreach { item =>
      sum += NlpProcessor.getSentiment(item.text)
      count += 1.0
    }
    return sum/count
  }
  def make
  def getTweets() = Action {
    val api = new TwitterAPI(ws)
    val tweets = api.getStateTweets("donaldtrump")
    val tweetList:JsArray = Json.arr()

    for (t <- tweets) {
      tweetList.append(Json.obj(
        "text" -> t.text,
        "state" -> t.state.name
      ))
    }

    Ok(tweetList)
    //    Ok(api.authorize())
    //    Ok(api.getTweets("donaldtrump", "recent"))
    //    Ok(api.formattedTweets("donaldtrump"))
  }

}
