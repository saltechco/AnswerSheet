package ir.saltech.answersheet.object.network.handler;

import org.conscrypt.Conscrypt;

import java.security.Security;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;

public final class SSLHandler {
	public static OkHttpClient enableTLS13() {
		Security.insertProviderAt(Conscrypt.newProvider(), 1);
		OkHttpClient.Builder okHttpBuilder = new OkHttpClient()
				                                     .newBuilder()
				                                     .followRedirects(true)
				                                     .followSslRedirects(true)
				                                     .retryOnConnectionFailure(true)
				                                     .cache(null)
				                                     .connectTimeout(10, TimeUnit.SECONDS)
				                                     .readTimeout(10, TimeUnit.SECONDS)
				                                     .writeTimeout(10, TimeUnit.SECONDS)
				                                     .connectionSpecs(Collections.singletonList(ConnectionSpec.MODERN_TLS));
		try {
			okHttpBuilder.sslSocketFactory(new InternalSSLSocketFactory(), new InternalX509TrustManager());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return okHttpBuilder.build();
	}
	
	public static OkHttpClient enableTLS12() {
		OkHttpClient.Builder client = new OkHttpClient.Builder()
				                              .followRedirects(true)
				                              .followSslRedirects(true)
				                              .retryOnConnectionFailure(true)
				                              .cache(null)
				                              .connectTimeout(5, TimeUnit.SECONDS)
				                              .readTimeout(5, TimeUnit.SECONDS)
				                              .writeTimeout(5, TimeUnit.SECONDS);
		return TLS12SocketFactory.enableTls12OnPreLollipop(client).build();
	}
}
