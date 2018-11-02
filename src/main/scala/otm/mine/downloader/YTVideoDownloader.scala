package otm.mine.downloader

import otm.mine.common.Video
import sys.process._

object YTVideoDownloader {
	val youtubeDownloaderScript = s"${System.getProperty("user.dir")}/src/main/scripts/youtubeDownloader.py"
	val OUT_REPO = "/home/groupevsc.com/taoufik_oubelkacem/attached/videos"
	
	def download(video: Video): Unit ={
		// call Python script
		s"python ${System.getProperty("user.dir")}/src/main/scripts/youtubeDownloader.py ${video.url} $OUT_REPO" ! ProcessLogger(stdout append _, stderr append _)
		println("stdout: " + stdout)
		println("stderr: " + stderr)
	}
	
}
