package ru.art2000.extensions.platform

import java.security.KeyStore
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

// Copied from OkHttp
fun platformTrustManager(): X509TrustManager {
    val trustManagerFactory = TrustManagerFactory.getInstance(
        TrustManagerFactory.getDefaultAlgorithm()
    )
    trustManagerFactory.init(null as KeyStore?)
    val trustManagers = trustManagerFactory.trustManagers
    check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
        "Unexpected default trust managers:" + trustManagers.joinToString()
    }

    return trustManagers[0] as X509TrustManager
}