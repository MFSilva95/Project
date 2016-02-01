package pt.it.porto.mydiabetes.sync;

import android.content.Context;
import android.os.Environment;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

public class ServerSync {

	private static ServerSync instance;
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
		AsyncHttpClient client = new AsyncHttpClient();
		File file = new File(Environment.getDataDirectory() + "/data/" + context.getPackageName() + "/databases/DB_Diabetes");
		RequestParams params = new RequestParams();
		try {
			params.put("db", file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		params.put("user", "teste1");
		params.put("password", "teste1");
		params.put("XDEBUG_SESSION", 17338);
		client.post("http://192.168.1.74/diabetes/transfer_db.php", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				// called when response HTTP status is "200 OK"
				if (listener != null) {
					listener.onSyncSuccessful();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				if (listener != null) {
					listener.onSyncUnSuccessful();
				}
			}
		});


	}

	private void setContext(Context context) {
		this.context = context;
	}

	public interface ServerSyncListener {
		void onSyncSuccessful();

		void onSyncUnSuccessful();

	}

}
