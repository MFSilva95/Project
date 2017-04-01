package pt.it.porto.mydiabetes.ui.fragments.badges;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pt.it.porto.mydiabetes.R;

/**
 * Created by parra on 21/02/2017.
 */

public class badgesList extends Fragment  {
    private View layout;

    public static pt.it.porto.mydiabetes.ui.fragments.badges.badgesList newInstance() {
        pt.it.porto.mydiabetes.ui.fragments.badges.badgesList fragment = new pt.it.porto.mydiabetes.ui.fragments.badges.badgesList();
        return fragment;
    }

    public badgesList() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_badges_list, container, false);
        return layout;
    }
}
