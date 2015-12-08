package controllers

import javax.inject.Inject

import models.TwitterAPI
import play.api._
import play.api.libs.ws.WSClient
import play.api.mvc._

class Application @Inject() (ws: WSClient) extends Controller {

  def index = Action {
    val api = new TwitterAPI(ws)
    api.authorize()
//    Ok(api.authorize())
//    Ok(views.html.index("Your new application is ready."))
  }

}
