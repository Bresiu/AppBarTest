package bresiu.com.appbarlayouttest.bottommenu;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import bresiu.com.appbarlayouttest.R;

/**
 * Custom divider class created for dynamically including dividers between views in {@link
 * BottomMenu}
 */
public class DividerView extends View {
	protected DividerView(Context context) {
		super(context);
		setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				(int) context.getResources().getDimension(R.dimen.divider_height)));
		setBackgroundColor(ContextCompat.getColor(context, R.color.divider));
	}
}
