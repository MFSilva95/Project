package pt.it.porto.mydiabetes.sync;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
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
import java.util.concurrent.CountDownLatch;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pt.it.porto.mydiabetes.BuildConfig;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.RankingService;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.database.PhotoSyncDb;
import pt.it.porto.mydiabetes.database.Preferences;
import pt.it.porto.mydiabetes.ui.activities.Home;
import pt.it.porto.mydiabetes.ui.dialogs.RankWebSyncDialog;
import pt.it.porto.mydiabetes.ui.fragments.home.homeRightFragment;
import pt.it.porto.mydiabetes.utils.DbUtils;

import static pt.it.porto.mydiabetes.ui.activities.SettingsImportExport.backup;

public class ServerSync {

	public static final MediaType MEDIA_TYPE_BINARY = MediaType.parse("application/octet-stream");
	private static final String BASE_URL = BuildConfig.SERVER_URL;
	private static ServerSync instance;
	private String username;
	private String password;
	private PhotoSyncDb photoSyncDb;
	private OkHttpClient client;
	private static String[] ranks = new String[8];

	private Context context;
	private ServerSyncListener listener;
	private Handler mainHandler;

	private ServerSync() {
	}

	public Context getContext() {
		return context;
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

	public void send(final ServerSyncListener listener) throws Exception {
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


		client = new OkHttpClient.Builder()
                .sslSocketFactory(getCert(),trustManager)
                .build();


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


//			@Override
//			public void onFailure(Call call, IOException e) {
//				e.printStackTrace();
//				ServerSync.this.onFailure();
//			}
//
//			@Override
//			public void onResponse(Call call, Response response) throws IOException {
//                //Log.i("RAWR", "onResponse: "+response);
//				// now sends the images
//				photoSyncDb = new PhotoSyncDb(MyDiabetesStorage.getInstance(context));
//				processNextPhoto();
//			}
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
		//URL url = new URL(BASE_URL + "transfer_db.php");
		//HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();

		return context.getSocketFactory();
		//urlConnection.setSSLSocketFactory(context.getSocketFactory());
		//InputStream in = urlConnection.getInputStream();
		//copyInputStreamToOutputStream(in, System.out);

	}

	private void sendDb(Callback callback) throws Exception {
		File file = backup(context,true);//DbUtils.get_database_file(context, true);

		if(file!=null){
            RequestBody formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("user", username)
                    .addFormDataPart("password", password)
                    .addFormDataPart("db", "db", RequestBody.create(MEDIA_TYPE_BINARY, file))
                    .build();


            Request request = new Request.Builder().url(BASE_URL + "transfer_db.php").post(formBody).build();

            client.newCall(request).enqueue(callback);
        }else{throw new Exception("could not export DB");}

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

	public void testCredentials(ServerSyncListener listener) throws Exception {

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


			client = new OkHttpClient.Builder()
                .sslSocketFactory(getCert(),trustManager)
                .build();


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

	public void syncRank(ServerSyncListener listener) throws Exception {
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

		client = new OkHttpClient.Builder()
				.sslSocketFactory(getCert(),trustManager)
				.build();

		username = Preferences.getUsername(context);
		password = Preferences.getPassword(context);

		if(username == null || password == null){
			throw new Exception("NO LOGIN AVAILABLE");
		}

		RequestBody requestBody = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("username", username)
				.addFormDataPart("password", password)
				.build();

		Request request = new Request.Builder()
				.url(BASE_URL + "get_ranking_pos.php")
				.post(requestBody)
				.build();

		final CountDownLatch countDownLatch = new CountDownLatch(1);
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {

				ServerSync.this.onFailure();
				countDownLatch.countDown();
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				try {
					System.out.println("Entra aqui");
					String responseData = response.body().string();
					System.out.println("ranks: "+responseData);
					JSONObject rank = new JSONObject(responseData);
					ranks[0] = rank.getString("mypoints");
					ranks[1] = rank.getString("mystreak");
					ranks[2] = rank.getString("points");
					ranks[3] = rank.getString("streak");
					ranks[4] = rank.getString("points_w");
					ranks[5] = rank.getString("streak_w");
					ranks[6] = rank.getString("glic");
					ranks[7] = rank.getString("h");

					Preferences.saveRankInfo(context,ranks[0],ranks[1],ranks[2],ranks[3],ranks[4],ranks[5],ranks[6],ranks[7]);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				countDownLatch.countDown();
			}
		});
		countDownLatch.await();
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
