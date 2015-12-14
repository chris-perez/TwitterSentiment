package models

import nlp.NlpProcessor
import play.api.libs.json.{Json, JsObject, JsString}

/**
 * Created by Chris on 12/13/2015.
 */
class SentimentMapper (lst:  Map[String, List[Tweet]]) extends Runnable{
  var sentiment_by_state = Map[String, Float]()
  var color_by_state:JsObject = Json.obj()

  def run() {
    for((state , value) <- lst) {
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
      val color = sentimentToColor(sentiment_by_state(state))
      println(state + ": " + sentiment_by_state(state))
      color_by_state += (state.replaceAll(" ", "") -> JsString(color))
    }
  }

  def sentimentToColor( sentiment: Float) : String = {
    val adjustedValue = ((sentiment/4.0)*255.0).asInstanceOf[Int]//.asInstanceOf[Int]
    val r = adjustedValue
    val g = 30
    val b = 255 - adjustedValue
    val hexVal: String = "#%02x%02x%02x".format( r, g, b)
    hexVal
  }
}
