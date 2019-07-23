package pt.it.porto.mydiabetes.ui.fragments.register;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.activities.WelcomeActivity;


public class welcomeFragment extends Fragment implements WelcomeActivity.RegistryFragmentPage {

    public static welcomeFragment newInstance() {
        welcomeFragment fragment = new welcomeFragment();
        return fragment;
    }

    public welcomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_welcome, container, false);
        return layout;
    }

    @Override
    public boolean allFieldsAreValid() {
        return true;
    }

    @Override
    public void saveData(Bundle container) {

    }

    @Override
    public int getSubtitle() {
        return R.string.welcome;
    }

}
