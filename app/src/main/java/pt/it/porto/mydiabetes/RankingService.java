package pt.it.porto.mydiabetes;

import android.app.Activity;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import pt.it.porto.mydiabetes.sync.ServerSync;
import pt.it.porto.mydiabetes.ui.activities.Home;
import pt.it.porto.mydiabetes.ui.dialogs.FeatureWebSyncDialog;
import pt.it.porto.mydiabetes.ui.dialogs.RankWebSyncDialog;
import pt.it.porto.mydiabetes.ui.fragments.home.homeRightFragment;
import pt.it.porto.mydiabetes.utils.DateUtils;

public class RankingService extends JobService {

    public RankingService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        //receive from server my position compared with other users
        getRanking();

        Toast.makeText(this,"boas",Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        return false;
    }


    public void getRanking() {
        try {
            ServerSync.getInstance(this).syncRank(new ServerSync.ServerSyncListener() {
                @Override
                public void onSyncSuccessful() {
                }

                @Override
                public void onSyncUnSuccessful() {
                }

                @Override
                public void noNetworkAvailable() {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        homeRightFragment.missingNetwork.setVisibility(View.GONE);
        homeRightFragment.setRankInfo(this);
    }
}