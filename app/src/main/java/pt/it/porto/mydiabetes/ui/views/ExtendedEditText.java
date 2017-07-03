package pt.it.porto.mydiabetes.ui.views;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.AttributeSet;

import pt.it.porto.mydiabetes.R;

/**
 * This is an extended EditText with a Suffix.
 */
public class ExtendedEditText extends TextInputEditText {
	private static final String TAG = "ExtendedEditText";
	// Stuff to do with our rendering
	TextPaint mTextPaint;
	float mFontHeight;
	// The actual suffix
	String mSuffix = "";
	// These are used to store details obtained from the EditText's rendering process
	Rect line0bounds = new Rect();
	int mLine0Baseline;
	TextWatcher textWatcher;
	private int extraPadding;
	private boolean showSuffix = false;

	public ExtendedEditText(Context context, AttributeSet attrs) {
		super(context, attrs);

		mFontHeight = getTextSize();

//		mTextPaint.setColor(getCurrentTextColor());
//		mTextPaint.setTypeface(this.getTypeface());
		mTextPaint = new TextPaint(getPaint());
//		mTextPaint.setTextSize(mFontHeight);
//		mTextPaint.setTextAlign(Paint.Align.LEFT);
		extraPadding = (int) (mTextPaint.measureText(" ") / 2);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ExtendedEditText, 0, 0);
		try {
			mSuffix = a.getString(R.styleable.ExtendedEditText_suffix);
			//noinspection ResourceType
			mTextPaint.setColor( a.getColor(R.styleable.ExtendedEditText_suffix_color, ResourcesCompat.getColor(getResources(), R.color.text_units, null)));
		} finally {
			a.recycle();
		}
		setupSuffix();
	}

	private void setupSuffix() {
		super.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				if (textWatcher != null) {
					textWatcher.beforeTextChanged(s, start, count, after);
				}

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (textWatcher != null) {
					textWatcher.onTextChanged(s, start, before, count);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s != null && !s.toString().isEmpty() && mSuffix != null) {
					showSuffix = true;
				} else {
					showSuffix = false;
				}
				if (textWatcher != null) {
					textWatcher.afterTextChanged(s);
				}
			}
		});
	}

	@Override
	public void setTypeface(Typeface typeface) {
		super.setTypeface(typeface);
		if (mTextPaint != null) {
			// Sometimes TextView itself calls me when i'm naked
			mTextPaint.setTypeface(typeface);
		}

		postInvalidate();
	}

	public void setSuffix(String s) {
		mSuffix = s;
	}

	@Override
	public void onDraw(Canvas c) {
		mLine0Baseline = getLineBounds(0, line0bounds);
		super.onDraw(c);
		if (mSuffix != null && showSuffix) {
			// Now we can calculate what we need!
			int xSuffix = (int) mTextPaint.measureText(getText().toString()) + extraPadding + getPaddingLeft();

			// We need to draw this like this because
			// setting a right drawable doesn't work properly and we want this
			// just after the text we are editing (but untouchable)
//			c.drawText(mSuffix, xSuffix, line0bounds.bottom, mTextPaint);
			c.drawText(mSuffix, xSuffix, getBaseline(), mTextPaint);
		}
	}
}