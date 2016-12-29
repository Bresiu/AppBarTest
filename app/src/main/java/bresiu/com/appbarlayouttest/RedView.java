package bresiu.com.appbarlayouttest;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class RedView extends FrameLayout {
	// TODO add first presentation mode (slowly settling in)
	public static final int MODE_WELCOME_SCREEN = 0;
	public static final int MODE_SEARCH = 1;
	public static final int MODE_STANDBY = 2;

	View filterView;

	public RedView(Context context) {
		this(context, null);
	}

	public RedView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RedView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setBackgroundColor(ContextCompat.getColor(getContext(), R.color.JELLY_BEAN));
		FrameLayout.LayoutParams layoutParams =
				new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT);
		setLayoutParams(layoutParams);
	}

	public void addSearchView(ConstraintLayout root) {
		View view = new View(getContext());
		ConstraintSet constraintSet = new ConstraintSet();
		constraintSet.clone(root);
		constraintSet.connect(view.getId(), ConstraintSet.START, root.getId(), ConstraintSet.START, 0);
		constraintSet.connect(view.getId(), ConstraintSet.END, root.getId(), ConstraintSet.END, 0);
		constraintSet.connect(view.getId(), ConstraintSet.TOP, root.getId(), ConstraintSet.TOP, 0);
		constraintSet.connect(view.getId(), ConstraintSet.BOTTOM, root.getId(), ConstraintSet.BOTTOM, 0);
		constraintSet.applyTo(root);

		view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.OCEAN_GREEN));
		FrameLayout.LayoutParams layoutParams =
				new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300);
		view.setLayoutParams(layoutParams);
		view.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View view) {
				animateFilterView();
			}
		});
		addView(view);
	}

	public void addFilterView(ConstraintLayout root) {
		filterView = new View(getContext());
		filterView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.ORANGE_YELLOW));
		FrameLayout.LayoutParams layoutParams =
				new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500);
		filterView.setLayoutParams(layoutParams);

		ConstraintSet constraintSet = new ConstraintSet();
		constraintSet.clone(root);
		constraintSet.connect(filterView.getId(), ConstraintSet.START, root.getId(), ConstraintSet.START, 0);
		constraintSet.connect(filterView.getId(), ConstraintSet.END, root.getId(), ConstraintSet.END, 0);
		constraintSet.connect(filterView.getId(), ConstraintSet.TOP, root.getId(), ConstraintSet.TOP, 0);
		constraintSet.applyTo(root);

		filterView.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View view) {
				filterView.animate().translationYBy(300).start();
			}
		});
		addView(filterView, 0);
	}

	public void animateFilterView() {
		filterView.animate().translationYBy(-300).start();
	}

}
