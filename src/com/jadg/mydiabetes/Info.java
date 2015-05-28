package com.jadg.mydiabetes;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;






public class Info extends Activity {


	/**
	 * If set to true will build the webview. Default is false as it takes longer to load the activity. 
	 * TODO: check about the possibility of using the UPorto logo
	 */
	private boolean useWebView = false;
	private WebView mWebView;
	
	public static long getInstallDate(Context context) throws NameNotFoundException {
		long time = 0;
		PackageManager pm = context.getPackageManager();
		PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
		time = packageInfo.lastUpdateTime;

		return time;
	}

	/**
	 * Used to get the current date. Recipe from SO http://stackoverflow.com/questions/7607165/how-to-write-build-time-stamp-into-apk
	 * @param context the context to extract AI and Package Name
	 * @return the time in millisecs
	 * @throws NameNotFoundException 
	 * @throws IOException 
	 */
	public static long getBuildDate(Context context) throws NameNotFoundException, IOException {
		long time = 0;

			ApplicationInfo ai = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(), 0);
			ZipFile zf = new ZipFile(ai.sourceDir);
			ZipEntry ze = zf.getEntry("classes.dex");
			time = ze.getTime();
			zf.close();

		return time;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(useWebView) {
			setContentView(R.layout.activity_info2);
			useWebView();
		}
		else{
			setContentView(R.layout.activity_info);
			TextView versionTextView = (TextView) findViewById(R.id.infoVersionView);
			String version = "Versão ";
			try {
				version += getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			} catch (NameNotFoundException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
				Log.e("ERROR GETTING VERSION NAME: ", e1.getMessage());
			}
			
            try {
            	long time;
            	time = getBuildDate(this);
				String installDate = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(time);
				version += " (" + installDate +")";
			} catch (NameNotFoundException e) {
				Log.d("Info","NameNotFoundException on getting build date");
            } catch (IOException e) {
            	Log.d("Info","IOException on getting build date");
			}
            versionTextView.setText(version);
		}
		// 	Show the Up button in the action bar.
		getActionBar();
	}
	
	private String auxAddParagrah(String st) {
		return "<p>"+st+"</p>";
	}
	/**
	 * Create a WebView for the About dialog to enable a better look
	 */
	private void useWebView(){
		/* Testing web view for justification */
		mWebView = (WebView) findViewById(R.id.webviewInfo); 
		
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		
		String contentInfo = auxAddParagrah(getString(R.string.information_about)) +
				auxAddParagrah("<strong>"+getString(R.string.information_about_using)+"</strong>") +
				auxAddParagrah(getString(R.string.information_about_developers));
				
		String backgroundColor = String.format("#%06X", (0xFFFFFF &  getResources().getColor(R.color.background_holo_dark))),
				textColor = String.format("#%06X", (0xFFFFFF & getResources().getColor(R.color.primary_text_holo_dark)));
		
		Spannable sp = new SpannableString(getString(R.string.information_about_contact));
		Linkify.addLinks(sp, Linkify.ALL);
		contentInfo+= "<div style='text-align:center'>"+Html.toHtml(sp)+"</div>";
		
		mWebView.loadData(				
				"<head> <style>a:link { color:#00FFFF;} a:visited {color:#BCA9F5;} </style></head>"
				+ "<body align='justify' style='color:"+textColor + ";background-color:"+backgroundColor+"'>"
				+contentInfo+"</body>", "text/html; charset=UTF-8", null);
				//use null as per http://stackoverflow.com/questions/6152789/character-set-in-webview-android
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.info, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
