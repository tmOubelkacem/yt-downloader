package otm.mine

import java.io._
import java.net.{HttpURLConnection, URL}

import javax.net.ssl._
import otm.mine.common.sslcertification.{TrustAll, VerifiesAllHostNames}

object DownloadProcessor {

  val sslContext = SSLContext.getInstance("SSL")
  sslContext.init(null, Array(TrustAll), new java.security.SecureRandom())
  HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory)
  HttpsURLConnection.setDefaultHostnameVerifier(VerifiesAllHostNames)

  def download(mp3Url: String, videoTitle: String) = {
    var out: OutputStream = null
    var in: InputStream = null
    try {
      val url = new URL(mp3Url)

      val connection = url.openConnection().asInstanceOf[HttpURLConnection]
      in = connection.getInputStream
      val localFile = s"/home/otm/Musique/sport_05-2018/$videoTitle.mp3"

      out = new BufferedOutputStream(new FileOutputStream(localFile))
      val byteArray = Stream.continually(in.read).takeWhile(-1 !=).map(_.toByte).toArray
      out.write(byteArray)


      println(s"$videoTitle ... downloaded")
    }catch {
      case e: Exception => println(e.printStackTrace())
    } finally {
      out.close
      in.close
    }
  }

}

//object MayMain3 {
//  def main(args: Array[String]): Unit = {
//    DownloadProcessor.download("https://cd7.ytbapi.com/download.php?q=8132a313bc047f373e0f622da39af008.mp3","chanson_ici2")
//  }
//}




