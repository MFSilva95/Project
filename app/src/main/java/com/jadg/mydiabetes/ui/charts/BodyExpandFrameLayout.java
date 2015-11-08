package com.jadg.mydiabetes.ui.charts;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.jadg.mydiabetes.R;

public class BodyExpandFrameLayout extends FrameLayout {

	private View head;
	private View body;

	private BodyOverlapHeaderGesture bodyOverlapHeaderGesture;
	private GestureDetector gestureDetector;


	public BodyExpandFrameLayout(Context context) {
		super(context);
	}

	public BodyExpandFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BodyExpandFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public BodyExpandFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}


	private void init() {
		head = findViewById(R.id.chart);
		body = findViewById(R.id.list_vals);

		head.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (bodyOverlapHeaderGesture.isExpanded()) {
					bodyOverlapHeaderGesture.collapse();
					return true;
				} else {
					return false;
				}

			}
		});
		bodyOverlapHeaderGesture.collapse();
		gestureDetector = new GestureDetector(getContext(), bodyOverlapHeaderGesture);
	}


	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!bodyOverlapHeaderGesture.isExpanded() && ev.getY() >= head.getBottom()) {
			return gestureDetector.onTouchEvent(ev);
		}
		return super.onInterceptTouchEvent(ev);
	}

	public BodyOverlapHeaderGesture getBodyOverlapHeaderGesture() {
		return bodyOverlapHeaderGesture;
	}

	public void setBodyOverlapHeaderGesture(BodyOverlapHeaderGesture bodyOverlapHeaderGesture) {
		this.bodyOverlapHeaderGesture = bodyOverlapHeaderGesture;
		init();
	}
}
