package models

import nlp.NlpProcessor
import play.api.libs.json.{Json, JsObject, JsString}

/**
 * Created by Chris on 12/13/2015.
 */
class SentimentMapper (lst:  Map[String, List[Tweet]]) extends Runnable{
  var sentiment_by_state = Map[String, Float]()
  var color_by_state:JsObject = Json.obj()

  /**
   * Scales values in map to make lowest value 0 and highest value 4
   * @param lst map of state to average score
   * @return map of states to scaled scores
   */
  def scaled(lst:Map[String, Float]) :Map[String, Float] = {
    var max = 0.0f
    var min = 4.0f
    for((state , value) <- lst) {
      if (value > max) {
        max = value
      }
      if (value < min) {
        min = value
      }
    }
    var normList = Map[String, Float]()
    for((state , value) <- lst) {
      normList += (state -> ((value-min)/(max-min)) * 4.0f)
    }
    normList
  }

  /**
   * Runs sentiment analysis for each state and maps the average value of each state to a color.
   */
  def run() {
    for((state , value) <- lst) {
      val len : Float = value.length
      for(tweet <- value){
        var current_avg: Float = 0.0f
        if (sentiment_by_state.contains(state)) {
          current_avg = sentiment_by_state(state)
        }
        val delta_avg = NlpProcessor.getSentiment(tweet.text)/len
        sentiment_by_state += (state -> (current_avg + delta_avg))
      }
      if (! sentiment_by_state.contains(state)) {
        sentiment_by_state += (state -> 2.0f)
      }
    }
//    val normList = scaled(sentiment_by_state)
    val normList = sentiment_by_state
    for((state , value) <- normList) {
      val color = sentimentToColor(value)
      println(state + ": " + value)
      val obj = Json.obj("color" -> JsString(color), "score" -> value)
      color_by_state += (state.replaceAll(" ", "") -> obj)
    }
  }

  /**
   * Converts sentiment score to a color.
   * @param sentiment sentiment score
   * @return hex color as string
   */
  def sentimentToColor( sentiment: Float) : String = {
    val adjustedValue = ((sentiment/4.0)*255.0).asInstanceOf[Int]//.asInstanceOf[Int]
    val r = adjustedValue
    val g = 30
    val b = 255 - adjustedValue
    val hexVal: String = "#%02x%02x%02x".format( r, g, b)
    hexVal
  }
}
