package bresiu.com.appbarlayouttest;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import bresiu.com.appbarlayouttest.topmenu.TopMenu;

public class MainActivity extends AppCompatActivity {

	private static final int ANIMATION_DURATION = 500;
	private View redView;
	private View blueView;
	private View redTextView;
	private View titleTextView;
	private View locationView;
	private View navigationBar;
	private ConstraintLayout root;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override protected void onResume() {
		super.onResume();
		setContentView(R.layout.activity_main);

		root = (ConstraintLayout) findViewById(R.id.activity_main);

		RedView redeView = new RedView(this);
		root.addView(redeView);

		redeView.addSearchView(root);
		redeView.addFilterView(root);

		redView = findViewById(R.id.red_view);
		locationView = findViewById(R.id.location);
		blueView = findViewById(R.id.blue_view);
		redTextView = findViewById(R.id.edit_text);
		titleTextView = findViewById(R.id.title);
		navigationBar = findViewById(R.id.bottom_navigation);
		redTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override public void onFocusChange(final View view, boolean isFocused) {
				if (isFocused) {
					final int startHeight = redView.getLayoutParams().height;
					redTextView.setOnFocusChangeListener(null);
					titleTextView.animate()
							.alpha(0)
							.setDuration(ANIMATION_DURATION)
							.translationY(blueView.getLayoutParams().height / 2)
							.scaleY(0.2f)
							.scaleX(0.2f)
							.setInterpolator(new FastOutSlowInInterpolator())
							.start();
					blueView.animate()
							.translationY(-blueView.getLayoutParams().height)
							.setDuration(ANIMATION_DURATION)
							.setInterpolator(new FastOutSlowInInterpolator())
							.start();
					redView.animate()
							.translationY(-blueView.getLayoutParams().height)
							.setDuration(ANIMATION_DURATION)
							.setInterpolator(new FastOutSlowInInterpolator())
							.setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
								@Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
									float value = (float) valueAnimator.getAnimatedValue();
									locationView.setVisibility(View.VISIBLE);
									if (value == 1) {
										redView.getLayoutParams().height = startHeight * 2;
										locationView.setAlpha(1);
									} else {
										redView.getLayoutParams().height = (int) (startHeight + (startHeight * value));
										locationView.setAlpha(value);
									}
									redView.requestLayout();
								}
							})
							.withEndAction(new Runnable() {
								@Override public void run() {
									int freeSpace =
											root.getHeight() - redView.getHeight() - navigationBar.getHeight();
									Log.d("BRES", "freeSpace: " + freeSpace);
									Log.d("BRES", "root.getHeight(): " + root.getHeight());
									Log.d("BRES", "redView.getHeight(): " + redView.getHeight());
									Log.d("BRES", "navigationBar.getHeight(): " + navigationBar.getHeight());
									TopMenu.make(root).showView();
								}
							})
							.start();
					int freeSpace = root.getHeight() - redView.getHeight() - navigationBar.getHeight();
					Log.d("BRES", "freeSpace: " + freeSpace);
					Log.d("BRES", "root.getHeight(): " + root.getHeight());
					Log.d("BRES", "redView.getHeight(): " + redView.getHeight());
					Log.d("BRES", "navigationBar.getHeight(): " + navigationBar.getHeight());
				}
			}
		});
	}
}