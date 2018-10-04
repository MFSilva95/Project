package pt.it.porto.mydiabetes.utils;

import android.content.Context;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by parra on 17/02/2017.
 */

public class CustomViewPager extends ViewPager {

    private float initialXValue;
    private BottomNavigationView bottomNavigationView;
    private boolean right;
    private boolean left;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.right = false;
        this.left = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.IsSwipeAllowed(event)) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.IsSwipeAllowed(event)) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    private boolean IsSwipeAllowed(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            initialXValue = event.getX();
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            try {
                float diffX = event.getX() - initialXValue;
                if (diffX > 0) {
                    bottomNavigationView.getMenu().getItem(this.getCurrentItem()-1).setChecked(true);
                    // swipe from left to right detected
                    return true;
                } else if (diffX < 0) {
                    bottomNavigationView.getMenu().getItem(this.getCurrentItem()+1).setChecked(true);
                    // swipe from right to left detected
                    return true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        return true;
    }


    public void blockSwipeRight(boolean left) {
        this.left = left;
    }

    public void blockSwipeLeft(boolean right) {
        this.right = right;
    }

    public void setBotNav(BottomNavigationView botNav) {
        this.bottomNavigationView = botNav;
    }
}