package pt.it.porto.mydiabetes.ui.listAdapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import pt.it.porto.mydiabetes.ui.fragments.home.homeMiddleFragment;

public class homePageAdapter extends FragmentPagerAdapter {
    private static final int FRAGMENT_ONE_POSITION = 0;
    private static final int FRAGMENT_TWO_POSITION = 1;
    private static final int FRAGMENT_THREE_POSITION = 2;
    private static final int COUNT = 3;

    public homePageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case FRAGMENT_TWO_POSITION:
                return new homeMiddleFragment();
        }
        return null;
    }

    @Override
    public int getItemPosition(Object object) {
        //Log.i("rawr", "getItemPosition: GET POS");
        return -2;
    }

    @Override
    public int getCount() {
        return COUNT;
    }

}