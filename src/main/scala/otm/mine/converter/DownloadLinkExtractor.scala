package otm.mine.converter

import java.util.regex.Pattern

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object DownloadLinkExtractor {

  def extract(html: String): Option[String] = {
    val document: Document = Jsoup.parse(html)
    Option.apply(
      document.body()
        .getElementsByClass("download-mp3-url btn audio q320")
        .first()
        .attr("href")
    )/*.map(
      btnAction => {
        val btnLink: String = btnAction.stripPrefix("window.open('").stripSuffix("')").trim
        if (btnLink.nonEmpty) btnLink
        else ""
      }
    )*/

  }

  def extract2(link: String): Unit = {

    val document: Document = Jsoup.connect(link).get
    val downlik = document.body().getElementsByClass("type").first()
    val pattern = Pattern.compile("(?is)var sd = \"(.+?)\"")
    val matcher = pattern.matcher(downlik.html())
    matcher.find()

    val baseUrl = matcher.group(1)
    val raw = "-UV0QGLmYys".getBytes("US-ASCII")
  }

  def extract3(link: String): Unit = {
    val document: Document = Jsoup.connect(link).get
    val downloadLinkElement = document.body().getElementsByClass("download-mp3-url btn audio q320").first()

    downloadLinkElement.attr("href")
  }

}


object MayMain4 {
  def main(args: Array[String]): Unit = {

    val link = DownloadLinkExtractor.extract3("https://youtubemp3api.com/@api/button/mp3/FaXZKsHKXKo")
    println(s"Link = $link")
  }
}