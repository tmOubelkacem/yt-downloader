package otm.mine.converter

import java.io.File

import it.sauronsoftware.jave.{AudioAttributes, Encoder, EncodingAttributes}
import otm.mine.common.Settings

class VideoToAudioConverter(settings: Settings) {
	val sampleRate: Int = 44100
	val channels: Int = 1

	private def getMp4FilesToConvert(): List[File] ={
		val videoDirectory = new File(settings.videoOutRepo)

		if (videoDirectory.exists && videoDirectory.isDirectory) {
			videoDirectory.listFiles().filter(_.isFile).toList.filter { file =>
				file.getName.endsWith("mp4")
			}
		} else {
			List[File]()
		}
	}

	def convert()={
		getMp4FilesToConvert().foreach( mp4File => {
			val mp3File = new File(s"${settings.audioOutRepo}/${mp4File.getName.replace("mp4", "mp3")}")

			val audio = new AudioAttributes()
			audio.setCodec("libmp3lame")
			audio.setBitRate(new Integer(128000))
			audio.setChannels(new Integer(2))
			audio.setSamplingRate(new Integer(44100))

			val attrs = new EncodingAttributes()
			attrs.setFormat("mp3")
			attrs.setAudioAttributes(audio)

			val encoder = new Encoder()
			encoder.encode(mp4File, mp3File, attrs)
		})
	}
}
