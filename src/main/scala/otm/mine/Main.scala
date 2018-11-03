package otm.mine

import com.typesafe.config.ConfigFactory
import otm.mine.common.Settings
import otm.mine.converter.VideoToAudioConverter
import otm.mine.downloader.YTVideoDownloader
import otm.mine.youtube.client.YoutubeClient

object Main {
  def main(args: Array[String]): Unit = {

    val settings: Settings = new Settings(ConfigFactory.load())

    val youtubeClient: YoutubeClient = new YoutubeClient(settings)
    val ytVideoDownloader: YTVideoDownloader = new YTVideoDownloader(settings)
    val videoToAudioConverter: VideoToAudioConverter = new VideoToAudioConverter(settings)

    val maybeVideos = youtubeClient.getPlaylistItems(settings.playListId)
    maybeVideos match {
      case Some(videos) => videos.foreach(
        video => {
          println(s"-> Video ${video.id} -  ${video.title}")
          ytVideoDownloader.download(video)
          println("--------------------------------------------------------------")
        }
    )
        videoToAudioConverter.convert()
        println("All playlist items done")
      case None => println("No video found in the given playlist !")
    }
  }
}
