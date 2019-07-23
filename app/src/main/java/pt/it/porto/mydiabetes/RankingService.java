package pt.it.porto.mydiabetes;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;

import pt.it.porto.mydiabetes.sync.ServerSync;
import pt.it.porto.mydiabetes.ui.fragments.home.homeRightFragment;

public class RankingService extends JobService {

    public RankingService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        //receive from server my position compared with other users
        getRanking();

        //Toast.makeText(this,"boas",Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        return false;
    }

    Context c;
    public void getRanking() {
        try {
            ServerSync ss = ServerSync.getInstance(this);
            c = ss.getContext();
            ss.syncRank(new ServerSync.ServerSyncListener() {
                @Override
                public void onSyncSuccessful() {
                    //homeRightFragment.missingNetwork.setVisibility(View.GONE);
                    homeRightFragment.setRankInfo(c);
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
    }
}