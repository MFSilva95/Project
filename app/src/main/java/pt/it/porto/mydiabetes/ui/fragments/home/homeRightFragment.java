package pt.it.porto.mydiabetes.ui.fragments.home;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.activities.AddEvent;
import pt.it.porto.mydiabetes.ui.activities.WelcomeActivity;

/**
 * Created by parra on 21/02/2017.
 */

public class homeRightFragment extends Fragment  {

    FloatingActionButton fab;

    public static pt.it.porto.mydiabetes.ui.fragments.home.homeRightFragment newInstance() {
        pt.it.porto.mydiabetes.ui.fragments.home.homeRightFragment fragment = new pt.it.porto.mydiabetes.ui.fragments.home.homeRightFragment();
        return fragment;
    }

    public homeRightFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_home_right, container, false);

        fab = (FloatingActionButton) layout.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddEvent.class);
                startActivity(intent,
                        ActivityOptions.makeSceneTransitionAnimation(getActivity(), fab, "fab").toBundle());
            }
        });

        return layout;
    }


}
