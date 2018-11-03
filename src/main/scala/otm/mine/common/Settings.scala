package otm.mine.common

import com.typesafe.config.Config

class Settings(config: Config) {
  val youtubeApiUrl = config.getString("youtube.api.url")
  val youtubeApiKey = config.getString("youtube.api.key")
  val playListId = config.getString("youtube.playList.id")

  val videoOutRepo = config.getString("repository.out.video")
  val audioOutRepo = config.getString("repository.out.audio")
}
