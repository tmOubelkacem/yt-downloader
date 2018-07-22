package otm.mine.common

case class Video(id : String, title: String) {
  def url: String = s"https://www.youtube.com/watch?v=$id"
}