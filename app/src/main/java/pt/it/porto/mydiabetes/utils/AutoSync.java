package pt.it.porto.mydiabetes.utils;

import android.app.Activity;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.widget.Toast;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.sync.ServerSync;


public class AutoSync extends JobService {

    public static int MYJOBID = 150;
    Context context;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        context = this;
        String username = pt.it.porto.mydiabetes.database.Preferences.getUsername(this);
        if(username==null){
            return false;
        }
        try {
            ServerSync.getInstance(this).send(new ServerSync.ServerSyncListener() {
                @Override
                public void onSyncSuccessful() {
                }

                @Override
                public void onSyncUnSuccessful() {
                }

                @Override
                public void noNetworkAvailable() {
                    onSyncUnSuccessful();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
         * True - if your service needs to process
         * the work (on a separate thread).
         * False - if there's no more work to be done for this job.
         */
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
