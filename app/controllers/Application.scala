package controllers

import nlp.NlpProcessor
import play.api.mvc._
import play.api.libs.json._
import edu.stanford.nlp._

class Application extends Controller {

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

    val score : Int =  NlpProcessor.getSentiment(text)
    val json: JsValue = JsObject(Seq(
      "Score" -> JsNumber(score)
    ))
    Ok(json)
  }

  def query(query : String) = Action {
    val json: JsValue = JsObject(Seq(
      "Colorado" -> JsString("#ff0000"),
      "Idaho" -> JsString("#7f00ff")
    ))
    Ok(json)
  }

}
