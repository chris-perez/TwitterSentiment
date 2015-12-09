package controllers

import javax.inject.Inject

import models.TwitterAPI
import play.api._
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.duration._


class Application @Inject() (ws: WSClient) extends Controller {

  def index = Action.async {
//    Ok(request.body.toString)
    val api = new TwitterAPI(ws)
    /*val url = "https://api.twitter.com/oauth2/token"
    val futureResult = ws.url(url).withHeaders("Authorization" -> api.encodedCredentials).get()
    futureResult.map {
      response => Ok("Response: " + response + "\nBody: " + response.body.toString)
    }*/
    api.authorize().map(result => Ok(result))

    /*futureResult.map {
      response => Ok(response)
    }*/


//    Ok(api.authorize())
//    Ok(views.html.index("Your new application is ready."))
  }

}
