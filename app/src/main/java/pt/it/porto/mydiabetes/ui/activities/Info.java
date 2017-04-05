package pt.it.porto.mydiabetes.ui.activities;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import pt.it.porto.mydiabetes.R;


public class Info extends BaseActivity {


    /**
     * Used to get the current date. Recipe from SO http://stackoverflow.com/questions/7607165/how-to-write-build-time-stamp-into-apk
     * Need android.keepTimestampsInApk = true see: https://code.google.com/p/android/issues/detail?id=220039
     * @param context the context to extract AI and Package Name
     * @return the time in millisecs
     * @throws NameNotFoundException from getting the Application info
     * @throws IOException if an error getting the zipfile for the apk
     */
    public static long getBuildDate(Context context) throws NameNotFoundException, IOException {
        long time;

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
        setContentView(R.layout.activity_info);

        // Show the Up button in the action bar.
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        TextView versionTextView = (TextView) findViewById(R.id.infoVersionView);
        String version = getString(R.string.information_about_version);
        try {
            version += getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (NameNotFoundException e1) {
            Log.e("GETTING VERSION NAME: ", e1.getMessage());
        }

        try {
            long time;
            time = getBuildDate(this);
            String date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(time);
            version += " (" + date + ")";
        } catch (NameNotFoundException e) {
            Log.d("Info", "NameNotFoundException on getting build date");
        } catch (IOException e) {
            Log.d("Info", "IOException on getting build date");
        }
        versionTextView.setText(version);

        // 	Show the Up button in the action bar.
        getActionBar();
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
