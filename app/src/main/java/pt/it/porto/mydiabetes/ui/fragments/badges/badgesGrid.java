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

public class badgesGrid extends Fragment  {
    private View layout;

    public static pt.it.porto.mydiabetes.ui.fragments.badges.badgesGrid newInstance() {
        pt.it.porto.mydiabetes.ui.fragments.badges.badgesGrid fragment = new pt.it.porto.mydiabetes.ui.fragments.badges.badgesGrid();
        return fragment;
    }

    public badgesGrid() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_badges_grid, container, false);
        return layout;
    }
}
