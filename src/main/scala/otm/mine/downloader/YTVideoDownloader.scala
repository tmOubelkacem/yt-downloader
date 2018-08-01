package otm.mine.downloader

import java.io.File
import java.net.URL

import com.github.axet.vget.VGet
import otm.mine.common.Video

object YTVideoDownloader {
	
	private final val OUTPUT_PATH_REPOSITORY: String = "/home/groupevsc.com/taoufik_oubelkacem/attached/ytVideos"
	
	def download(video: Video): Unit ={
		val vGet: VGet = new VGet(new URL(video.url), new File(s"$OUTPUT_PATH_REPOSITORY/${video.title}.mp4"))
		vGet.download()
		
		println(s"-> ${video.url} ... Downloaded")
	}
	
}
