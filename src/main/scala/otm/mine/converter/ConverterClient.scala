package otm.mine.converter

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import otm.mine.common.Video
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object ConverterClient {

  implicit val system = ActorSystem("tracker-downloader")
  implicit val materializer = ActorMaterializer.create(system)

  private val YOUTUBE_URL_FORMAT = "https://www.youtube.com/watch?v="
  private val RESPONSE_PATTERN = """.*?(\"linkconverted\":\")(.*?)(\",)(.*?)""".r

  val wsClient = StandaloneAhcWSClient()

  def convertVideo(video: Video)(handleResponse: (String, String) => Unit) = {
    println(s"Start converting $video ...")
    generateRemoteMp3Resource(video.id) onComplete {
      case Success(optionalResponse) => {
        optionalResponse match {
          case Some(_) => {
            getConvertedMp3ResourceLink(video.id) onComplete {
              case Success(optionalDownloadLink) => {
                optionalDownloadLink match {
                  case Some(downloadLink) =>
                    handleResponse(downloadLink, video.title)
                    println(s"End converting $video")
                  case None => println(s"No download Link is generated for video : $video")
                }
              }
              case Failure(ex) => println(s"Error : $ex")
            }
          }
          case None => None
        }
      }
      case Failure(ex) => println(s"Error : $ex")
    }
  }

  def generateRemoteMp3Resource(videoId: String): Future[Option[String]] = {
    val futureResponse = wsClient.url("https://ytbapi.com/dl.php")
      .withQueryStringParameters(
        ("link" -> s"$YOUTUBE_URL_FORMAT$videoId"),
        ("format" -> "mp3")
      )
      .get()
      .map(response => {
        val statusCode = response.status
        statusCode match {
          case code if (200 to 299 contains code) => {
            Some("OK Download button generated")
          }
          case _ => {
            println(s"YTBAPI generate request failed with status code ${statusCode} - ${response.body}")
            None
          }
        }
      })
    futureResponse
  }

  def getConvertedMp3ResourceLink(videoId: String): Future[Option[String]] ={
    val futureResponse = wsClient.url("https://d.h2download.org/json.php")
      .withQueryStringParameters(
        ("callback" -> "jQuery321024674132705459262_1526214154610"), //quick-win : pour l'instant, une valeur constante qui marche !!
        ("step" -> "choose"),
        ("link" -> s"$YOUTUBE_URL_FORMAT$videoId"),
        ("v" -> s"$videoId"),
        ("to" -> "mp3"),
        ("ql" -> "192"),
        ("st" -> "-1"),
        ("en" -> "-1")
      )
      .get()
      .map(response => {
        val statusCode = response.status
        statusCode match {
          case code if (200 to 299 contains code) => {
            RESPONSE_PATTERN.findFirstMatchIn(response.body) match {
              case Some(downloadLink) => Some(s"${downloadLink.group(2).replace("\\", "")}")
              case _ => None
            }
          }
          case _ => {
            println(s"YTBAPI generate request failed with status code ${statusCode} - ${response.body}")
            None
          }
        }
      })
    futureResponse
  }
}
