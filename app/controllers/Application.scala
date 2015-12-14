package controllers

import javax.inject.Inject

import models.{SentimentMapper, TwitterAPI, Tweet}
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
    println("Running sentiment analysis . . .")

    var color_by_state:JsObject = Json.obj()

    val sentimentMapper1 = new SentimentMapper(lst.slice(0, lst.size/4))
    val thread1 = new Thread(sentimentMapper1)
    thread1.start()
    val sentimentMapper2 = new SentimentMapper(lst.slice(lst.size/4, lst.size/2))
    val thread2 = new Thread(sentimentMapper2)
    thread2.start()
    val sentimentMapper3 = new SentimentMapper(lst.slice(lst.size/2, 3*(lst.size/4)))
    val thread3 = new Thread(sentimentMapper3)
    thread3.start()
    val sentimentMapper4 = new SentimentMapper(lst.slice(3*(lst.size/4), lst.size+1))
    val thread4 = new Thread(sentimentMapper4)
    thread4.start()

    thread1.join()
    color_by_state = color_by_state.deepMerge(sentimentMapper1.color_by_state)
    thread2.join()
    color_by_state = color_by_state.deepMerge(sentimentMapper2.color_by_state)
    thread3.join()
    color_by_state = color_by_state.deepMerge(sentimentMapper3.color_by_state)
    thread4.join()
    color_by_state = color_by_state.deepMerge(sentimentMapper4.color_by_state)

    println("Finished sentiment analysis.")

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
