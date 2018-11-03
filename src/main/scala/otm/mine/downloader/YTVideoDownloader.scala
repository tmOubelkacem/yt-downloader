package otm.mine.downloader		// call Python script


import otm.mine.common.{Settings, Video}

import sys.process._

class YTVideoDownloader(settings: Settings) {

	def download(video: Video): Unit ={
		s"python ${System.getProperty("user.dir")}/src/main/scripts/youtubeDownloader.py ${video.url} ${settings.videoOutRepo}" ! ProcessLogger(stdout append _, stderr append _)
		println("stdout: " + stdout)
		println("stderr: " + stderr)
	}
	
}
