package pt.it.porto.mydiabetes.ui.listAdapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import pt.it.porto.mydiabetes.ui.fragments.register.AddGlycemiaObjectivesFragment;
import pt.it.porto.mydiabetes.ui.fragments.register.AddInsulinsFragment;
import pt.it.porto.mydiabetes.ui.fragments.register.FactorsFragment;
import pt.it.porto.mydiabetes.ui.fragments.register.PersonalDataFragment;
import pt.it.porto.mydiabetes.ui.fragments.register.welcomeFragment;

public class welcomePageAdapter extends FragmentPagerAdapter {
    private static final int FRAGMENT_ONE_POSITION = 0;
    private static final int FRAGMENT_TWO_POSITION = 1;
    private static final int FRAGMENT_THREE_POSITION = 2;
    private static final int FRAGMENT_FOUR_POSITION = 3;
    private static final int FRAGMENT_FIVE_POSITION = 4;
    private static final int COUNT = 5;

    public welcomePageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case FRAGMENT_ONE_POSITION:
                return new welcomeFragment();
            case FRAGMENT_TWO_POSITION:
                return new PersonalDataFragment();
            case FRAGMENT_THREE_POSITION:
                return new FactorsFragment();
            case FRAGMENT_FOUR_POSITION:
                return new AddInsulinsFragment();
            case FRAGMENT_FIVE_POSITION:
                return new AddGlycemiaObjectivesFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return COUNT;
    }

}