package ir.saltech.answersheet.object.network.handler;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;

/**
 *  * Enables TLS v1.2 when creating SSLSockets.
 *  *</pre>
 * &nbsp;
 * <pre class="brush: java"> * For some reason, android supports TLS v1.2 from API 16, but enables it by
 *  * default only from API 20.
 *  * @link https://developer.android.com/reference/javax/net/ssl/SSLSocket.html
 *  * @see SSLSocketFactory
 *  
 */
public class TLS12SocketFactory extends SSLSocketFactory {
	private static final String[] TLS_V12_ONLY = {"TLSv1.2"};

	final SSLSocketFactory delegate;

	public TLS12SocketFactory(SSLSocketFactory base) {
		this.delegate = base;
	}

	public static OkHttpClient.Builder enableTls12OnPreLollipop(@NonNull OkHttpClient.Builder client) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
			try {
				SSLContext sc = SSLContext.getInstance("TLSv1.2");
				sc.init(null, null, null);
				client.sslSocketFactory(new TLS12SocketFactory(sc.getSocketFactory()));

				ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
						.tlsVersions(TlsVersion.TLS_1_2)
						.build();

				List<ConnectionSpec> specs = new ArrayList<>();
				specs.add(cs);
				specs.add(ConnectionSpec.COMPATIBLE_TLS);
				specs.add(ConnectionSpec.CLEARTEXT);

				client.connectionSpecs(specs);
			} catch (Exception exc) {
				Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", exc);
			}
		}

		return client;
	}

	@Override
	public String[] getDefaultCipherSuites() {
		return delegate.getDefaultCipherSuites();
	}

	@Override
	public String[] getSupportedCipherSuites() {
		return delegate.getSupportedCipherSuites();
	}

	@Override
	public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
		return patch(delegate.createSocket(s, host, port, autoClose));
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException {
		return patch(delegate.createSocket(host, port));
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
		return patch(delegate.createSocket(host, port, localHost, localPort));
	}

	@Override
	public Socket createSocket(InetAddress host, int port) throws IOException {
		return patch(delegate.createSocket(host, port));
	}

	@Override
	public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
		return patch(delegate.createSocket(address, port, localAddress, localPort));
	}

	private Socket patch(Socket s) {
		if (s instanceof SSLSocket) {
			((SSLSocket) s).setEnabledProtocols(TLS_V12_ONLY);
		}
		return s;
	}
}
