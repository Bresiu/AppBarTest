package bresiu.com.appbarlayouttest;

import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class AnimationUtils {

	public static void collapse(final View collapseView, final View alphaView,
			final int animationDuration, final AnimationEndListener animationEndListener) {
		final int initialHeight = collapseView.getMeasuredHeight();
		final Animation animation = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation transformation) {
				if (interpolatedTime == 1) {
					collapseView.setVisibility(View.GONE);
					animationEndListener.onAnimationEnd(collapseView);
					cancel();
				} else {
					collapseView.getLayoutParams().height =
							initialHeight - (int) (initialHeight * interpolatedTime);
					alphaView.setAlpha(1 - interpolatedTime);
					collapseView.requestLayout();
				}
			}

			@Override public boolean willChangeBounds() {
				return true;
			}
		};
		animation.setInterpolator(new FastOutSlowInInterpolator());
		animation.setDuration(animationDuration);
		collapseView.startAnimation(animation);
	}

	//TODO: add start height
	public static void expand(final View expandView, final int expandValue, int animationDuration,
			final AnimationEndListener animationEndListener) {
		Animation animation = new Animation() {
			@Override protected void applyTransformation(float interpolatedTime, Transformation t) {
				int startHeight = expandView.getLayoutParams().height;
				if (interpolatedTime == 1) {
					expandView.getLayoutParams().height = expandValue;
					animationEndListener.onAnimationEnd(expandView);
					cancel();
				} else {
					expandView.getLayoutParams().height =
							(int) (startHeight + ((expandValue - startHeight) * interpolatedTime));
				}
				expandView.requestLayout();
			}

			@Override public boolean willChangeBounds() {
				return true;
			}
		};
		animation.setInterpolator(new FastOutSlowInInterpolator());
		animation.setDuration(animationDuration);
		expandView.startAnimation(animation);
	}

	public interface AnimationEndListener {
		void onAnimationEnd(View view);
	}
}