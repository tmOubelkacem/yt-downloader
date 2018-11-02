package otm.mine.converter

import java.io.File

import it.sauronsoftware.jave.{AudioAttributes, Encoder, EncodingAttributes}

object VideoToAudioConverter {
	val medea_repo_path = "/home/groupevsc.com/taoufik_oubelkacem/attached/videos"
	val sampleRate: Int = 44100
	val channels: Int = 1
	
	def main(args: Array[String]): Unit = {
		
		println(s"\n iciiii :  ${System.getProperty("user.dir")} \n")
		
		val source = new File(s"$medea_repo_path/Latinoamérica.mp4")
		val target = new File(s"$medea_repo_path/Latinoamérica.mp3")
		
		val audio = new AudioAttributes()
		audio.setCodec("libmp3lame")
		audio.setBitRate(new Integer(128000))
		audio.setChannels(new Integer(2))
		audio.setSamplingRate(new Integer(44100))
		
		val attrs = new EncodingAttributes()
		attrs.setFormat("mp3")
		attrs.setAudioAttributes(audio)
		
		val encoder = new Encoder()
		encoder.encode(source, target, attrs)
	}
}
