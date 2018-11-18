package otm.mine.youtube.client

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import otm.mine.common.{Settings, Video}
import otm.mine.youtube.model.Model._
import play.api.libs.json._
import play.api.libs.ws.JsonBodyReadables._
import play.api.libs.ws.StandaloneWSRequest
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class YoutubeClient(settings: Settings) {

  implicit val system = ActorSystem("tracker-downloader")
  implicit val materializer = ActorMaterializer.create(system)

  private val wsClient = StandaloneAhcWSClient()

  def get(playListId: String, pageToken: Option[String]) ={
    var hasNextPageToken = true
    var optPageToken = pageToken
    var finalResult :Option[List[Video]] = Some(List[Video]())

    while(hasNextPageToken){
      val (optPageToken2, optPartialResult) = process(playListId, optPageToken)
      optPageToken = optPageToken2
      finalResult = Option((finalResult ++ optPartialResult).flatten.toList).filter(_.nonEmpty)

      hasNextPageToken = optPageToken.isDefined
    }
    finalResult
  }

  private def process(playListId: String, pageToken: Option[String]): (Option[String], Option[List[Video]]) = {
    val firstFutureResponse = callYouTube(playListId, pageToken)
    val futureResponse = handleResponse(firstFutureResponse)
    val (maybeNextPageToken, maybeItems) = Await.result(futureResponse, 30 seconds)

    (maybeNextPageToken, extractVideos(maybeItems))
  }

  private def callYouTube(playListId: String, pageToken: Option[String]): Future[StandaloneWSRequest#Self#Response] = {
    wsClient.url(settings.youtubeApiUrl)
      .withQueryStringParameters(
        ("key" -> settings.youtubeApiKey),
        ("part"-> "snippet,contentDetails"),
        ("maxResults"-> "50"),
        ("playlistId"-> playListId),
        ("pageToken"-> pageToken.getOrElse(""))
      )
      .get()
  }

  private def handleResponse(futureResponse: Future[StandaloneWSRequest#Self#Response])={
    futureResponse.map(response => {
      val statusCode = response.status
      statusCode match {
        case code if (200 to 299 contains code) => {
          val jsonBody = response.body[JsValue]
          val jsOptNextPageToken = (jsonBody \ "nextPageToken").validateOpt[String]
          val jsOptItems = (jsonBody \ "items").validateOpt[List[Item]]

          (
            jsOptNextPageToken.get,
            jsOptItems.get
          )
        }
        case _ => {
          println(s"YouTube API request failed with status code ${statusCode} - ${response.body}")
          (None, None)
        }
      }
    })
  }

  def getPlaylistItems(playListId: String): Option[List[Video]] = {
    val futureResponse: Future[Option[List[Item]]] = wsClient.url(settings.youtubeApiUrl)
      .withQueryStringParameters(
        ("key" -> settings.youtubeApiKey),
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


