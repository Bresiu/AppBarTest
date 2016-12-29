package bresiu.com.appbarlayouttest;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class ExpandableScrollView extends ScrollView {


	public ExpandableScrollView(Context context) {
		super(context);
	}

	public ExpandableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ExpandableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//Log.d("BRES", "getLayoutParams().height: " + getLayoutParams().height);
		//Log.d("BRES", "onMeasure");
	}

	@Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, 1731);
		//Log.d("BRES", "onLayout: l: " + l + " t: " + t + " r: " + r + " b: " + b);
	}

	@Override
	protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
		super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);
		//Log.d("BRES", "measureChild");
	}

	@Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		//Log.d("BRES", "onSizeChanged: w: " + w + " h: " + h + " oldw: " + oldw + " oldh: " + oldh);
	}


}
