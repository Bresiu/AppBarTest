package bresiu.com.appbarlayouttest.bottommenu;

import android.os.Handler;
import android.os.Looper;

import java.lang.ref.WeakReference;

/**
 * Manages {@link BottomMenu}s.
 */
class BottomMenuManager {
    private final Object mLock;
    private final Handler mHandler;
    private BottomMenuRecord mCurrentBottomMenu;
    private BottomMenuRecord mNextBottomMenu;

    private BottomMenuManager() {
        mLock = new Object();
        mHandler = new Handler(Looper.getMainLooper());
    }

    static BottomMenuManager getInstance() {
        return InstanceHolder.instance;
    }

    void show(BottomMenuManager.Callback callback) {
        synchronized (mLock) {
            if (isCurrentBottomMenuLocked(callback)) {
                mHandler.removeCallbacksAndMessages(mCurrentBottomMenu);
                return;
            } else {
                mNextBottomMenu = new BottomMenuRecord(callback);
            }

            if (mCurrentBottomMenu == null || !cancelBottomMenuLocked(mCurrentBottomMenu)) {
                mCurrentBottomMenu = null;
                showNextBottomMenuLocked();
            }
        }
    }

    void dismiss(BottomMenuManager.Callback callback) {
        synchronized (mLock) {
            if (isCurrentBottomMenuLocked(callback)) {
                cancelBottomMenuLocked(mCurrentBottomMenu);
            } else if (isNextBottomMenuLocked(callback)) {
                cancelBottomMenuLocked(mNextBottomMenu);
            }
        }
    }

    void onDismissed(BottomMenuManager.Callback callback) {
        synchronized (mLock) {
            if (isCurrentBottomMenuLocked(callback)) {
                mCurrentBottomMenu = null;
                if (mNextBottomMenu != null) {
                    showNextBottomMenuLocked();
                }
            }
        }
    }

    boolean isCurrent(BottomMenuManager.Callback callback) {
        synchronized (mLock) {
            return isCurrentBottomMenuLocked(callback);
        }
    }

    boolean isCurrentOrNext(BottomMenuManager.Callback callback) {
        synchronized (mLock) {
            return isCurrentBottomMenuLocked(callback) || isNextBottomMenuLocked(callback);
        }
    }

    private void showNextBottomMenuLocked() {
        if (mNextBottomMenu != null) {
            mCurrentBottomMenu = mNextBottomMenu;
            mNextBottomMenu = null;

            final BottomMenuManager.Callback callback = mCurrentBottomMenu.callback.get();
            if (callback != null) {
                callback.show();
            } else {
                mCurrentBottomMenu = null;
            }
        }
    }

    private boolean cancelBottomMenuLocked(BottomMenuRecord record) {
        final BottomMenuManager.Callback callback = record.callback.get();
        if (callback != null) {
            mHandler.removeCallbacksAndMessages(record);
            callback.dismiss();
            return true;
        }
        return false;
    }

    private boolean isCurrentBottomMenuLocked(BottomMenuManager.Callback callback) {
        return mCurrentBottomMenu != null && mCurrentBottomMenu.isBottomMenu(callback);
    }

    private boolean isNextBottomMenuLocked(BottomMenuManager.Callback callback) {
        return mNextBottomMenu != null && mNextBottomMenu.isBottomMenu(callback);
    }

    interface Callback {
        void show();

        void dismiss();
    }

    private static class InstanceHolder {
        static BottomMenuManager instance = new BottomMenuManager();
    }

    private static class BottomMenuRecord {
        final WeakReference<BottomMenuManager.Callback> callback;

        BottomMenuRecord(BottomMenuManager.Callback callback) {
            this.callback = new WeakReference<>(callback);
        }

        boolean isBottomMenu(BottomMenuManager.Callback callback) {
            return callback != null && this.callback.get() == callback;
        }
    }
}
