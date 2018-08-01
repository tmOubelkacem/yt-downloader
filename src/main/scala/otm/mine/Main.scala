package otm.mine

import otm.mine.downloader.YTVideoDownloader
import otm.mine.youtube.client.YoutubeClient

object Main {
  def main(args: Array[String]): Unit = {

    val maybeVideos = YoutubeClient.getPlaylistItems("PLSnW5nGQic2Wro40yrDOvnZYobzg1Vy92")
    maybeVideos match {
      case Some(videos) => videos.foreach(
        video => {
          println(s"-> Video ${video.id} -  ${video.title}")
	        YTVideoDownloader.download(video)
          println("--------------------------------------------------------------")
        }
      )
        println("All playlist items done")
      case None => println("No video found in the given playlist !")
    }
  }
}
