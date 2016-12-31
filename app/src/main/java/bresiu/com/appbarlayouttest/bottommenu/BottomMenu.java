package bresiu.com.appbarlayouttest.bottommenu;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import bresiu.com.appbarlayouttest.R;

/**
 * BottomMenu show a custom views on bottom of a screen with sliding-from-bottom animation. BottomMenu appear above all other
 * elements on screen and only one can be displayed at a time.
 * <p>
 * They automatically disappear after a new one is created or after user interaction elsewhere on the screen,
 * particularly after interactions that summon a new surface or activity.
 * <p>
 * To be notified when a BottomMenu has been shown or dismissed, you can provide a {@link Callback}
 * via {@link #setCallback(Callback)}.</p>
 */
public final class BottomMenu {
    private static final int MSG_SHOW = 0;
    private static final int MSG_DISMISS = 1;
    private static final Handler sHandler;

    static {
        sHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case MSG_SHOW:
                        ((BottomMenu) message.obj).showView();
                        return true;
                    case MSG_DISMISS:
                        ((BottomMenu) message.obj).hideView();
                        return true;
                }
                return false;
            }
        });
    }

    private final BottomMenu.BottomMenuViewContainer mView;
    private final BottomMenuManager.Callback mManagerCallback = new BottomMenuManager.Callback() {
        @Override
        public void show() {
            sHandler.sendMessage(sHandler.obtainMessage(MSG_SHOW, BottomMenu.this));
        }

        @Override
        public void dismiss() {
            sHandler.sendMessage(sHandler.obtainMessage(MSG_DISMISS, BottomMenu.this));
        }
    };
    private final ViewGroup mTargetParent;
    private int mAnimationDuration;
    private boolean hasEvents;
    private BottomMenu.Callback mCallback;

    private BottomMenu(ViewGroup parent) {
        mTargetParent = parent;
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        mView = (BottomMenu.BottomMenuViewContainer) inflater.inflate(R.layout.bottom_menu_layout,
                mTargetParent, false);
    }

    /**
     * Make a BottomMenu to display a list of custom views vertically
     * <p>
     * <p>BottomMenu will try and find a parent view to hold BottomMenu's view from the value given
     * to {@code view}. BottomMenu will walk up the view tree trying to find a suitable parent,
     * which is defined as a {@link CoordinatorLayout} or the window decor's content view,
     * whichever comes first.
     * <p>
     * <p>Having a {@link CoordinatorLayout} in your view hierarchy allows BottomMenu to enable
     * certain features, such as moving of widgets like
     * {@link android.support.design.widget.FloatingActionButton}.
     *
     * @param view The view to find a parent from.
     */
    @NonNull
    public static BottomMenu make(@NonNull View view) {
        return new BottomMenu(findSuitableParent(view));
    }

    private static ViewGroup findSuitableParent(View view) {
        ViewGroup fallback = null;
        do {
            if (view instanceof CoordinatorLayout) {
                return (ViewGroup) view;
            } else if (view instanceof FrameLayout) {
                if (view.getId() == android.R.id.content) {
                    return (ViewGroup) view;
                } else {
                    fallback = (ViewGroup) view;
                }
            }
            if (view != null) {
                final ViewParent parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
            }
        } while (view != null);
        return fallback;
    }

    /**
     * Show the {@link BottomMenu}.
     */
    @SuppressWarnings("unused")
    public void show() {
        BottomMenuManager.getInstance().show(mManagerCallback);
    }

    /**
     * Dismiss the {@link BottomMenu}.
     */
    @SuppressWarnings("all")
    public void dismiss() {
        BottomMenuManager.getInstance().dismiss(mManagerCallback);
    }

    /**
     * Set elevation to the {@link BottomMenu} - this will add shadow to the view.
     */
    public BottomMenu setElevation(int elevation) {
        mView.setElevation(elevation);
        return this;
    }

    /**
     * Set a callback to be called when this the visibility of this {@link BottomMenu} changes.
     */
    @NonNull
    @SuppressWarnings("all")
    public BottomMenu setCallback(@NonNull BottomMenu.Callback callback) {
        mCallback = callback;
        return this;
    }

    /**
     * Add view to {@link BottomMenu} below any other if already present. This will also add
     * {@link DividerView} between to separate views.
     */
    public BottomMenu addViewAtBottom(@NonNull View view) {
        if (hasEvents) {
            mView.addView(new DividerView(mTargetParent.getContext()));
        }
        mView.addView(view);
        hasEvents = true;
        return this;
    }

    /**
     * Return whether this {@link BottomMenu} is currently being shown.
     */
    @SuppressWarnings("unused")
    public boolean isShown() {
        return BottomMenuManager.getInstance().isCurrent(mManagerCallback);
    }

    /**
     * Returns whether this {@link BottomMenu} is currently being shown, or is queued to be
     * shown next.
     */
    @SuppressWarnings("all")
    public boolean isShownOrQueued() {
        return BottomMenuManager.getInstance().isCurrentOrNext(mManagerCallback);
    }

    private void showView() {
        if (mView.getParent() == null) {
            mTargetParent.addView(mView, 0);
        }

        mView.setOnAttachStateChangeListener(
                new BottomMenu.BottomMenuViewContainer.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {
                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {
                        if (isShownOrQueued()) {
                            sHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onViewHidden();
                                }
                            });
                        }
                    }
                });

        if (ViewCompat.isLaidOut(mView)) {
            animateViewIn();
        } else {
            mView.setOnLayoutChangeListener(
                    new BottomMenu.BottomMenuViewContainer.OnLayoutChangeListener() {
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
                .setDuration(getAnimationDuration())
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(View view) {
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        onViewShown();
                    }
                })
                .start();
    }

    private int getAnimationDuration() {
        if (mAnimationDuration > 0) {
            return mAnimationDuration;
        } else {
            return mView.getContext().getResources().getInteger(R.integer.animation_duration);
        }
    }

    /**
     * Set animation duration to the {@link BottomMenu}. This applies to showing and hiding animation.
     */
    @SuppressWarnings("unused")
    public BottomMenu animationDuration(int animationDuration) {
        mAnimationDuration = animationDuration;
        return this;
    }

    private void animateViewOut() {
        ViewCompat.animate(mView)
                .translationY(mView.getHeight())
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(getAnimationDuration())
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(View view) {
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        onViewHidden();
                    }
                })
                .start();
    }

    private void hideView() {
        if (mView.getVisibility() == View.VISIBLE) {
            animateViewOut();
        } else {
            onViewHidden();
        }
    }

    private void onViewShown() {
        if (mCallback != null) {
            mCallback.onShown(this);
        }
    }

    private void onViewHidden() {
        BottomMenuManager.getInstance().onDismissed(mManagerCallback);
        if (mCallback != null) {
            mCallback.onDismissed(this);
        }
        final ViewParent parent = mView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(mView);
        }
    }

    /**
     * Callback class for {@link BottomMenu} instances.
     *
     * @see BottomMenu#setCallback(Callback)
     */
    @SuppressWarnings("all")
    public interface Callback {
        /**
         * Called when the given {@link BottomMenu} has been dismissed, either through a user interaction,
         * or new one is being scheduled.
         *
         * @param bottomMenu The bottomMenu which has been dismissed.
         * @see BottomMenu#dismiss()
         */
        void onDismissed(BottomMenu bottomMenu);

        /**
         * Called when the given {@link BottomMenu} is visible.
         *
         * @param bottomMenu The bottomMenu which is now visible.
         * @see BottomMenu#show()
         */
        void onShown(BottomMenu bottomMenu);
    }

    /**
     * Callback class for user interaction outside of {@link BottomMenu}.
     *
     * @see BottomMenu#setCallback(Callback)
     */
    @SuppressWarnings("all")
    public interface OnUserInteractionCallback {
        void onDismissed();
    }

    /**
     * Views container class
     */
    private static class BottomMenuViewContainer extends CardView {
        private BottomMenu.BottomMenuViewContainer.OnLayoutChangeListener mOnLayoutChangeListener;
        private BottomMenu.BottomMenuViewContainer.OnAttachStateChangeListener mOnAttachStateChangeListener;
        private LinearLayout mContainer;
        private int mElevation;

        public BottomMenuViewContainer(Context context) {
            this(context, null);
        }

        public BottomMenuViewContainer(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            initCard();
            initInnerContainer();
            ViewCompat.setFitsSystemWindows(this, true);
            ViewCompat.setOnApplyWindowInsetsListener(this,
                    new android.support.v4.view.OnApplyWindowInsetsListener() {
                        @Override
                        public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat insets) {
                            view.setPadding(view.getPaddingLeft(), insets.getSystemWindowInsetTop(), view.getPaddingRight(), view.getPaddingBottom());
                            return insets;
                        }
                    });
        }

        private void initCard() {
            if (mElevation == 0) {
                mElevation = getResources().getInteger(R.integer.card_elevation);
            }
            setMaxCardElevation(mElevation);
            setCardElevation(mElevation);
            setRadius(0);
            setClickable(true);
            setPreventCornerOverlap(false);
        }

        private void setElevation(int elevation) {
            mElevation = elevation;
        }

      private void initInnerContainer() {
            mContainer = new LinearLayout(getContext());
            mContainer.setOrientation(LinearLayout.VERTICAL);
            setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.background));
            FrameLayout.LayoutParams containerLayout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mContainer.setLayoutParams(containerLayout);
            super.addView(mContainer);
        }

        @Override
        public void addView(View child) {
            mContainer.addView(child);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            if (mOnLayoutChangeListener != null) {
                mOnLayoutChangeListener.onLayoutChange(this, left, top, right, bottom);
            }
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (mOnAttachStateChangeListener != null) {
                mOnAttachStateChangeListener.onViewAttachedToWindow(this);
            }
            ViewCompat.requestApplyInsets(this);
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (mOnAttachStateChangeListener != null) {
                mOnAttachStateChangeListener.onViewDetachedFromWindow(this);
            }
        }

        void setOnLayoutChangeListener(
                BottomMenu.BottomMenuViewContainer.OnLayoutChangeListener onLayoutChangeListener) {
            mOnLayoutChangeListener = onLayoutChangeListener;
        }

        void setOnAttachStateChangeListener(
                BottomMenu.BottomMenuViewContainer.OnAttachStateChangeListener listener) {
            mOnAttachStateChangeListener = listener;
        }

        interface OnLayoutChangeListener {
            void onLayoutChange(View view, int left, int top, int right, int bottom);
        }

        interface OnAttachStateChangeListener {
            void onViewAttachedToWindow(View view);

            void onViewDetachedFromWindow(View view);
        }
    }
}