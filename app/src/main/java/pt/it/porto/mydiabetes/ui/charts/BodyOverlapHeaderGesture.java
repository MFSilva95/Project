package pt.it.porto.mydiabetes.ui.charts;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.os.Build;
import android.util.Property;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public abstract class BodyOverlapHeaderGesture extends GestureDetector.SimpleOnGestureListener {

	private static final int SWIPE_MIN_DISTANCE = 10;
	//		private static final int SWIPE_MIN_DISTANCE = 100;
	private static final int SWIPE_ERROR_MARGIN = 50;

	private static final Property<View, Integer> VIEW_LAYOUT_PADDING_BOT = new Property<View, Integer>(Integer.class, "viewPaddingBot") {

		public void set(View object, Integer value) {
			object.setPadding(0, 0, 0, value);
		}

		public Integer get(View object) {
			return object.getPaddingBottom();
		}
	};
	private static final Property<View, Integer> VIEW_LAYOUT_HEIGHT = new Property<View, Integer>(Integer.class, "viewLayoutHeight") {

		public void set(View object, Integer value) {
			object.getLayoutParams().height = value.intValue();
			object.requestLayout();
		}

		public Integer get(View object) {
			return object.getLayoutParams().height;
		}
	};

	private MotionEvent mLastOnDownEvent = null;
	private View viewHeader;
	private View viewBody;
	private boolean isExpanded = true;
	private int diffHeight;


	public BodyOverlapHeaderGesture(View viewHeader, View viewBody) {
		this.viewHeader = viewHeader;
		this.viewBody = viewBody;

		diffHeight = viewBody.getPaddingBottom();
	}

	@Override
	public boolean onDown(MotionEvent e) {
		//Android 4.0 bug means e1 in onFling may be NULL due to onLongPress eating it.
		mLastOnDownEvent = e;
		return super.onDown(e);
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		if (e1 == null)
			e1 = mLastOnDownEvent;
		if (e1 == null || e2 == null)
			return false;
		if ((e1.getY() - e2.getY()) > SWIPE_MIN_DISTANCE) {
			expand();
		} else if ((e1.getY() - e2.getY()) < -SWIPE_MIN_DISTANCE) {
//			collapse();
		}

		return super.onScroll(e1, e2, distanceX, distanceY);
	}

	public void expand() {
		if (!isExpanded) {
			isExpanded = true;

			ObjectAnimator heightAnimator = ObjectAnimator.ofFloat(viewBody, "translationY", 0);
			viewBody.setPadding(0, 0, 0, 0);
			heightAnimator.addListener(new Animator.AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {

				}

				@Override
				public void onAnimationEnd(Animator animation) {
					if(isExpanded()) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
							viewHeader.setClipBounds(new Rect(0, 0, viewHeader.getRight(), ((FrameLayout.LayoutParams) viewBody.getLayoutParams()).topMargin));
						}
						viewHeader.setLayerType(View.LAYER_TYPE_HARDWARE, null);
					}
				}

				@Override
				public void onAnimationCancel(Animator animation) {

				}

				@Override
				public void onAnimationRepeat(Animator animation) {

				}
			});
			heightAnimator.start();

			onExpand();
		}
	}

	public abstract void onExpand();

	public void collapse() {
		if (isExpanded) {
			isExpanded = false;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
				viewHeader.setClipBounds(null);
			}
			viewHeader.setLayerType(View.LAYER_TYPE_NONE, null);

			ObjectAnimator heightAnimator = ObjectAnimator.ofFloat(viewBody, "translationY", diffHeight);
			heightAnimator.start();
			viewBody.setPadding(0, 0, 0, diffHeight);

			onCollapse();
		}
	}

	public abstract void onCollapse();

	public boolean isExpanded() {
		return isExpanded;
	}

}
