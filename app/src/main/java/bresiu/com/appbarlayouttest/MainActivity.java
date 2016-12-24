package bresiu.com.appbarlayouttest;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final View redView = findViewById(R.id.red_view);
		final View blueView = findViewById(R.id.blue_view);
		final View redTextView = findViewById(R.id.edit_text);
		redTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override public void onFocusChange(View view, boolean b) {
				if (b) {
					redTextView.setOnFocusChangeListener(null);
					AnimationUtils.collapse(blueView, findViewById(R.id.title), 400,
							new AnimationUtils.AnimationEndListener() {
								@Override public void onAnimationEnd(View view) {
									int newHeight = redView.getLayoutParams().height * 2;
									AnimationUtils.expand(redView, newHeight, 200,
											new AnimationUtils.AnimationEndListener() {
												@Override public void onAnimationEnd(final View view) {
													ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(),
															((ColorDrawable) view.getBackground()).getColor(),
															ContextCompat.getColor(MainActivity.this, R.color.SAFFRON));
													colorAnimation.setDuration(100);
													colorAnimation.addUpdateListener(
															new ValueAnimator.AnimatorUpdateListener() {
																@Override
																public void onAnimationUpdate(ValueAnimator valueAnimator) {
																	view.setBackgroundColor((int) valueAnimator.getAnimatedValue());
																}
															});
													colorAnimation.start();
												}
											});
								}
							});
				}
			}
		});
	}
}