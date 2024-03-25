package ir.saltech.answersheet.`object`.network.handler

import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient.Builder
import okhttp3.OkHttpClient
import org.conscrypt.Conscrypt
import java.security.Security
import java.util.concurrent.TimeUnit

object SSLHandler {
    fun enableTLS13(): OkHttpClient {
        Security.insertProviderAt(Conscrypt.newProvider(), 1)
        val okHttpBuilder: Builder = OkHttpClient()
            .newBuilder()
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .cache(null)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS))
        try {
            okHttpBuilder.sslSocketFactory(InternalSSLSocketFactory(), InternalX509TrustManager())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return okHttpBuilder.build()
    }

    fun enableTLS12(): OkHttpClient {
        val client: Builder = Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .cache(null)
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
        return TLS12SocketFactory.enableTls12OnPreLollipop(client).build()
    }
}
