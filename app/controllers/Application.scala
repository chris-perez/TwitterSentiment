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

  def index = Action {
    val api = new TwitterAPI(ws)
    val tweets = api.getStateTweets("donaldtrump")
    var map = Map()

    Ok(api.authorize())
//    Ok(api.getTweets("donaldtrump", "recent"))
//    Ok(api.formattedTweets("donaldtrump"))
  }


}
