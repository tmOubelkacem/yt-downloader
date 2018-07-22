package otm.mine.common.sslcertification

import javax.net.ssl.{HostnameVerifier, SSLSession}

// Verifies all host names by simply returning true.
object VerifiesAllHostNames extends HostnameVerifier {
  def verify(s: String, sslSession: SSLSession) = true
}
