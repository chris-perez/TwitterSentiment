package controllers

import play.api.mvc._
import play.api.libs.json._

class Application extends Controller {

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

}
