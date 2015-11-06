package com.jadg.mydiabetes.ui.charts;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;

public abstract class BodyOverlapHeaderGesture {

	private View viewHeader;
	private View viewBody;
	private boolean isExpanded = true;
	private int diffHeight;


	public BodyOverlapHeaderGesture(View viewHeader, View viewBody) {
		this.viewHeader = viewHeader;
		this.viewBody = viewBody;

		diffHeight = viewBody.getPaddingBottom();
	}

	public void expand() {
		if (!isExpanded) {
			isExpanded = true;

			ObjectAnimator heightAnimator = ObjectAnimator.ofFloat(viewBody, "translationY", 0);
			viewBody.setPadding(0, 0, 0, 0);
			AnimatorSet set = new AnimatorSet();
			set.play(heightAnimator);
			set.start();

			onExpand();
		}
	}

	public abstract void onExpand();

	public void collapse() {
		if (isExpanded) {
			isExpanded = false;

			ObjectAnimator heightAnimator = ObjectAnimator.ofFloat(viewBody, "translationY", diffHeight);
			AnimatorSet set = new AnimatorSet();
			set.play(heightAnimator);
			set.start();
			viewBody.setPadding(0, 0, 0, diffHeight);

			onCollapse();
		}
	}

	public abstract void onCollapse();

	public boolean isExpanded() {
		return isExpanded;
	}

	Property<View, Integer> VIEW_LAYOUT_PADDING_BOT = new Property<View, Integer>(Integer.class, "viewPaddingBot") {

		public void set(View object, Integer value) {
			object.setPadding(0, 0, 0, value);
//			object.setPadding(object.getPaddingLeft(), object.getPaddingTop(), object.getPaddingRight(), value);
		}

		public Integer get(View object) {
			return object.getPaddingBottom();
		}
	};

	Property<View, Integer> VIEW_LAYOUT_HEIGHT = new Property<View, Integer>(Integer.class, "viewLayoutHeight") {

		public void set(View object, Integer value) {
			object.getLayoutParams().height = value.intValue();
			object.requestLayout();
		}

		public Integer get(View object) {
			return object.getLayoutParams().height;
		}
	};

}
