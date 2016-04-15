package pt.it.porto.mydiabetes.ui.views;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * This is an extended EditText with a Prefix and Suffix.
 *
 */
public class ExtendedEditText extends EditText {
	private static final String TAG = "ExtendedEditText";
	private int extraPadding;
	// Stuff to do with our rendering
	TextPaint mTextPaint = new TextPaint();
	float mFontHeight;

	// The actual suffix
	String mSuffix = "";

	// These are used to store details obtained from the EditText's rendering process
	Rect line0bounds = new Rect();
	int mLine0Baseline;

	public ExtendedEditText(Context context, AttributeSet attrs) {
		super(context, attrs);

		mFontHeight = getTextSize();

//		mTextPaint.setColor(getCurrentTextColor());
//		mTextPaint.setTypeface(this.getTypeface());
		mTextPaint = getPaint();
//		mTextPaint.setTextSize(mFontHeight);
//		mTextPaint.setTextAlign(Paint.Align.LEFT);
		extraPadding = (int) (mTextPaint.measureText(" ") / 2);
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
		if (mSuffix != null) {
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