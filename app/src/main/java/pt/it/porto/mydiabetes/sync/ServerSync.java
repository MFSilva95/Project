package pt.it.porto.mydiabetes.sync;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.database.PhotoSyncDb;
import pt.it.porto.mydiabetes.database.Preferences;

public class ServerSync {

	private static final String BASE_URL = "http://127.0.0.1:8080/diabetes/";
	private static ServerSync instance;
	private RequestParams params;
	private AsyncHttpClient client;
	private PhotoSyncDb photoSyncDb;

	private Context context;
	private ServerSyncListener listener;

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

	public void send(final ServerSyncListener listener) {
		this.listener = listener;
		client = new AsyncHttpClient();
		params = new RequestParams();
		File file = new File(Environment.getDataDirectory() + "/data/" + context.getPackageName() + "/databases/DB_Diabetes");
		try {
			params.put("db", file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		params.put("user", Preferences.getUsername(context));
		params.put("password", Preferences.getPassword(context));
		client.post(BASE_URL + "transfer_db.php", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				// called when response HTTP status is "200 OK"
				// now sends the images
				photoSyncDb = new PhotoSyncDb(MyDiabetesStorage.getInstance(context));
				processNextPhoto();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				if (listener != null) {
					listener.onSyncUnSuccessful();
				}
				client.cancelAllRequests(true);
			}
		});


	}

	private void sendPhoto(final String photo) {
		params.remove("db");
		try {
			params.put("img", new File(photo));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		client.post(BASE_URL + "transfer_img.php", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				photoSyncDb.removePhoto(photo);
				processNextPhoto();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

			}
		});
	}

	private void processNextPhoto() {
		Cursor listPhotos = photoSyncDb.getListPhotos();
		if (listPhotos.getCount() > 0) {
			listPhotos.moveToFirst();
			sendPhoto(listPhotos.getString(0));
		} else {
			if (listener != null) {
				listener.onSyncSuccessful();
			}
		}
	}

	private void setContext(Context context) {
		this.context = context;
	}

	public interface ServerSyncListener {
		void onSyncSuccessful();

		void onSyncUnSuccessful();

	}

}
