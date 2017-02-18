package bresiu.com.appbarlayouttest.topmenu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import bresiu.com.appbarlayouttest.R;

public final class TopMenu {
	private static final int ANIMATION_DURATION = 700;
	private final TopMenu.TopMenuViewContainer mView;
	private final ViewGroup mTargetParent;

	private TopMenu(ViewGroup parent) {
		mTargetParent = parent;
		Context context = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);
		mView = (TopMenu.TopMenuViewContainer) inflater.inflate(R.layout.top_menu_layout,
				mTargetParent, false);
	}

	@NonNull public static TopMenu make(@NonNull ViewGroup viewGroup) {
		return new TopMenu(viewGroup);
	}

	public void showView() {
		if (mView.getParent() == null) {
			mTargetParent.addView(mView);
		}
		if (ViewCompat.isLaidOut(mView)) {
			animateViewIn();
		} else {
			mView.setOnLayoutChangeListener(
					new TopMenu.TopMenuViewContainer.OnLayoutChangeListener() {
						@Override
						public void onLayoutChange(View view, int left, int top, int right, int bottom) {
							mView.setOnLayoutChangeListener(null);
							animateViewIn();
						}
					});
		}
	}

	private void animateViewIn() {
		ViewCompat.setTranslationY(mView, -mView.getHeight());
		ViewCompat.animate(mView)
				// setting this parameter prevents from showing padding with shadow on API < 21
				.translationY(-mView.getPaddingBottom())
				.setInterpolator(new FastOutSlowInInterpolator())
				.setDuration(ANIMATION_DURATION)
				.start();
	}

	private void animateViewOut() {
		ViewCompat.animate(mView)
				.translationY(-mView.getHeight())
				.setInterpolator(new FastOutSlowInInterpolator())
				.setDuration(ANIMATION_DURATION)
				.start();
	}

	public void hideView() {
		animateViewOut();
	}

	private static class TopMenuViewContainer extends CardView {
		private TopMenu.TopMenuViewContainer.OnLayoutChangeListener mOnLayoutChangeListener;

		public TopMenuViewContainer(Context context) {
			this(context, null);
		}

		public TopMenuViewContainer(Context context, AttributeSet attributeSet) {
			super(context, attributeSet);
			initCard();
			ViewCompat.setFitsSystemWindows(this, true);
			ViewCompat.setOnApplyWindowInsetsListener(this,
					new android.support.v4.view.OnApplyWindowInsetsListener() {
						@Override
						public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat insets) {
							view.setPadding(view.getPaddingLeft(), insets.getSystemWindowInsetTop(),
									view.getPaddingRight(), view.getPaddingBottom());
							return insets;
						}
					});
		}

		private void initCard() {
			setCardElevation(3);
			setRadius(0);
			setClickable(true);
			setPreventCornerOverlap(false);
		}

		@Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
			super.onLayout(changed, left, top, right, bottom);
			if (mOnLayoutChangeListener != null) {
				mOnLayoutChangeListener.onLayoutChange(this, left, top, right, bottom);
			}
		}

		@Override protected void onAttachedToWindow() {
			super.onAttachedToWindow();
			ViewCompat.requestApplyInsets(this);
		}

		@Override protected void onDetachedFromWindow() {
			super.onDetachedFromWindow();
		}

		void setOnLayoutChangeListener(
				TopMenu.TopMenuViewContainer.OnLayoutChangeListener onLayoutChangeListener) {
			mOnLayoutChangeListener = onLayoutChangeListener;
		}

		interface OnLayoutChangeListener {
			void onLayoutChange(View view, int left, int top, int right, int bottom);
		}
	}
}