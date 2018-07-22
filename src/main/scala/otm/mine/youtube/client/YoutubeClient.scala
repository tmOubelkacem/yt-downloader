package otm.mine.youtube.client

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import otm.mine.common.Video
import otm.mine.youtube.model.Model._
import play.api.libs.json._
import play.api.libs.ws.JsonBodyReadables._
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object YoutubeClient {

  implicit val system = ActorSystem("tracker-downloader")
  implicit val materializer = ActorMaterializer.create(system)

  private val wsClient = StandaloneAhcWSClient()

  def getPlaylistItems(playListId: String):Option[List[Video]] = {

    val futureResponse: Future[Option[List[Item]]] = wsClient.url("https://www.googleapis.com/youtube/v3/playlistItems")
      .withQueryStringParameters(
        ("key" -> "key"),
        ("part"-> "snippet,contentDetails"),
        ("maxResults"-> "50"),
        ("playlistId"-> playListId)
      )
      .get()
      .map(response => {
        val statusCode = response.status
        statusCode match {
          case code if (200 to 299 contains code) => {
            val jsonBody = response.body[JsValue]
            (jsonBody \ "items").validate[List[Item]]
            Some((jsonBody \ "items").as[List[Item]])
          }
          case _ => {
            println(s"YouTube API request failed with status code ${statusCode} - ${response.body}")
            None
          }
        }
      })

    // TODO : (!) changer ce QuickWin par une fonction partielle a passée en paramètre
    val maybeItems = Await.result(futureResponse, 30 seconds)
    extractVideos(maybeItems)
//    futureResponse.onComplete{
//      case Success(optionalItems) => {
//        extractVideos(optionalItems)
//      }
//      case Failure(ex) => {
//        println(s"An error occurs when calling YouTube API with exception :  ${ex}")
//        None
//      }
//    }
  }

  private def extractVideos(maybeItems: Option[List[Item]]): Option[List[Video]] = {
    maybeItems match {
      case Some(items) => {
        val videos: List[Video] = items.map(item => Video(item.contentDetails.videoId, item.snippet.title.replace("/", "-")))
        Some(videos)
      }
      case None => None
    }
  }

}


