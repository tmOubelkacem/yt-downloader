package otm.mine.common.sslcertification

import java.security.cert.X509Certificate

import javax.net.ssl.X509TrustManager

object TrustAll extends X509TrustManager {
  val getAcceptedIssuers = null

  def checkClientTrusted(x509Certificates: Array[X509Certificate], s: String) = {}

  def checkServerTrusted(x509Certificates: Array[X509Certificate], s: String) = {}
}
