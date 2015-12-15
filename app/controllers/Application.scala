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

  /**
   * @return map view
   */
  def index = Action {
    Ok(views.html.map())
  }

  /**
   * Gets the sentiment score of a string
   * @param text string to perform sentiment analysis on
   * @return sentiment score as json
   */
  def getSentiment(text : String) = Action {
    val score: Int = NlpProcessor.getSentiment(text)
    val json: JsValue = JsObject(Seq(
      "Score" -> JsNumber(score)
    ))
    Ok(json)
  }

  /**
   * Runs sentiment analysis for each state.
   * @param lst map of state to list of Tweets
   * @return JsObject of state to color representing average sentiment
   */
  def sentimentMap(lst: Map[String, List[Tweet]]) : JsObject = {
    println("Running sentiment analysis . . .")

    var color_by_state:JsObject = Json.obj()

    // runs 4 threads, each of which works on 1/4 of the states
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

    // get results from each thread and merge it into the final map
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

  /**
   * Scraps tweets for each state by given query and finds average sentiment for each state represented by color
   * @param query search query
   * @return Json map of state to color and average score
   */
  def query(query : String) = Action {
    val json = sentimentMap(api.getStateTweets(query))
    Ok(json)
  }

}
