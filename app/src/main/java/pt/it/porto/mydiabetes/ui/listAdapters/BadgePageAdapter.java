package pt.it.porto.mydiabetes.ui.listAdapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import pt.it.porto.mydiabetes.ui.fragments.badges.badgesGrid;
import pt.it.porto.mydiabetes.ui.fragments.badges.badgesList;
import pt.it.porto.mydiabetes.ui.fragments.home.homeLeftFragment;
import pt.it.porto.mydiabetes.ui.fragments.home.homeMiddleFragment;
import pt.it.porto.mydiabetes.ui.fragments.home.homeRightFragment;
import pt.it.porto.mydiabetes.ui.fragments.register.welcomeFragment;

public class BadgePageAdapter extends FragmentPagerAdapter {
    private static final int FRAGMENT_ONE_POSITION = 0;
    private static final int FRAGMENT_TWO_POSITION = 1;
    private static final int COUNT = 2;

    public BadgePageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case FRAGMENT_ONE_POSITION:
                return new badgesGrid();
            case FRAGMENT_TWO_POSITION:
                return new badgesList();
        }
        return null;
    }

    @Override
    public int getCount() {
        return COUNT;
    }

}