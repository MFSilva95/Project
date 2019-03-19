package pt.it.porto.mydiabetes.sync;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.database.PhotoSyncDb;
import pt.it.porto.mydiabetes.database.Preferences;
import pt.it.porto.mydiabetes.utils.DbUtils;

public class ServerSync {

	public static final MediaType MEDIA_TYPE_BINARY = MediaType.parse("application/octet-stream");
	private static final String BASE_URL = "https://mydiabetes.dcc.fc.up.pt/newsite/";
	private static ServerSync instance;
	private String username;
	private String password;
	private PhotoSyncDb photoSyncDb;
	private OkHttpClient client;

	private Context context;
	private ServerSyncListener listener;
	private Handler mainHandler;

	private ServerSync() {
	}

	public static ServerSync getInstance(Context context) {
		if (instance == null) {
			synchronized (ServerSync.class) {
				if (instance == null) {
					instance = new ServerSync();
				}
			}
		}
		instance.setContext(context);
		return instance;
	}

	public void send(final ServerSyncListener listener) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		this.listener = listener;



		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init((KeyStore) null);
		TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
		if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
			throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
		}
		X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
		SSLContext sslContext = SSLContext.getInstance("SSL");
		sslContext.init(null, new TrustManager[] { trustManager }, null);
		SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

		try {
			client = new OkHttpClient.Builder()
					.sslSocketFactory(getCert(),trustManager)
					.build();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		}

		mainHandler = new Handler(context.getMainLooper());

		username = Preferences.getUsername(context);
		password = Preferences.getPassword(context);

		sendDb(new Callback() {

			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
				ServerSync.this.onFailure();
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
                //Log.i("RAWR", "onResponse: "+response);
				// now sends the images
				photoSyncDb = new PhotoSyncDb(MyDiabetesStorage.getInstance(context));
				processNextPhoto();
			}
		});

	}
	private SSLSocketFactory getCert() throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException {

		// Load CAs from an InputStream
		// (could be from a resource or ByteArrayInputStream or ...)
		CertificateFactory cf = CertificateFactory.getInstance("X.509");

		InputStream caInput = this.context.getAssets().open("certMyDiabetes.crt");//new BufferedInputStream(new FileInputStream());
		Certificate ca;
		try {
			ca = cf.generateCertificate(caInput);
			//System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
		} finally {
			caInput.close();
		}

// Create a KeyStore containing our trusted CAs
		String keyStoreType = KeyStore.getDefaultType();
		KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		keyStore.load(null, null);
		keyStore.setCertificateEntry("ca", ca);

// Create a TrustManager that trusts the CAs in our KeyStore
		String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
		tmf.init(keyStore);

// Create an SSLContext that uses our TrustManager
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(null, tmf.getTrustManagers(), null);

// Tell the URLConnection to use a SocketFactory from our SSLContext
		URL url = new URL(BASE_URL + "transfer_db.php");
		HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();

		return context.getSocketFactory();
		//urlConnection.setSSLSocketFactory(context.getSocketFactory());
		//InputStream in = urlConnection.getInputStream();
		//copyInputStreamToOutputStream(in, System.out);

	}

	private void sendDb(Callback callback) {
		File file = DbUtils.exportDb(context);
		RequestBody formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
														  .addFormDataPart("user", username)
														  .addFormDataPart("password", password)
														  .addFormDataPart("db", "db", RequestBody.create(MEDIA_TYPE_BINARY, file))
														  .build();


		Request request = new Request.Builder() .url(BASE_URL + "transfer_db.php").post(formBody).build();

		client.newCall(request).enqueue(callback);
	}

	//
	private void sendPhoto(final String photo) {
		File file = new File(photo);
		RequestBody formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
														  .addFormDataPart("user", username)
														  .addFormDataPart("password", password)
														  .addFormDataPart("img", file.getName(), RequestBody.create(MEDIA_TYPE_BINARY, file))
														  .build();


		Request request = new Request.Builder().url(BASE_URL + "transfer_img.php").post(formBody).build();

		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				ServerSync.this.onFailure();
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				photoSyncDb.removePhoto(photo);
				processNextPhoto();
			}
		});
	}

	private void onFailure() {
		if (listener != null) {
			mainHandler.post(new Runnable() {
				@Override
				public void run() {
					listener.onSyncUnSuccessful();
				}
			});
		}
	}

	private void processNextPhoto() {
		Cursor listPhotos = photoSyncDb.getListPhotos();
		if (listPhotos.getCount() > 0) {
			listPhotos.moveToFirst();
			String photo = listPhotos.getString(0);
			if (!new File(photo).exists()) {
				photoSyncDb.removePhoto(photo);
				processNextPhoto();
			} else {
				sendPhoto(listPhotos.getString(0));
			}
		} else {
			if (listener != null) {
				mainHandler.post(new Runnable() {
					@Override
					public void run() {
						listener.onSyncSuccessful();
					}
				});
			}
		}
	}

	public void testCredentials(ServerSyncListener listener) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

		this.listener = listener;
		//client = new OkHttpClient();

		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init((KeyStore) null);
		TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
		if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
			throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
		}
		X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
		SSLContext sslContext = SSLContext.getInstance("SSL");
		sslContext.init(null, new TrustManager[] { trustManager }, null);
		SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        try {
            client = new OkHttpClient.Builder()
                    .sslSocketFactory(getCert(),trustManager)
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }

		mainHandler = new Handler(context.getMainLooper());

		username = Preferences.getUsername(context);
		password = Preferences.getPassword(context);

		sendDb(new Callback() {

			@Override
			public void onFailure(Call call, IOException e) {
                e.printStackTrace();
				if(e!=null && e instanceof UnknownHostException){
					ServerSync.this.onNoNetworkAvailable();
				} else {
					ServerSync.this.onFailure();
				}
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
//				response.body().string() to get the server's response
				String responseTxt = response.body().string();
				//Log.i("RAWR", "onResponse: IMA HERE LOL-> "+responseTxt);
				if (ServerSync.this.listener != null) {
					if (responseTxt.contains("invalid user or password")) {
						ServerSync.this.listener.onSyncUnSuccessful();
					} else {
						ServerSync.this.listener.onSyncSuccessful();
					}
				}
			}
		});
	}

	private void onNoNetworkAvailable() {
		if (listener != null) {
			mainHandler.post(new Runnable() {
				@Override
				public void run() {
					listener.noNetworkAvailable();
				}
			});
		}
	}

	private void setContext(Context context) {
		this.context = context;
	}

	public interface ServerSyncListener {
		void onSyncSuccessful();

		void onSyncUnSuccessful();

		void noNetworkAvailable();

	}

}
