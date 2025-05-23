package com.rorpheeyah.java.pinentryview;

import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Class that manages cursor blinking for PinEntryView.
 */
public class PinViewBlink implements Runnable {
    private static final String TAG = "PinViewBlink";
    private static final int BLINK_TIMEOUT = 500; // milliseconds

    private boolean mCancelled;
    private final WeakReference<PinEntryView> viewReference;

    /**
     * Creates a new blink manager for the given PinEntryView.
     *
     * @param view The PinEntryView to manage blinking for
     */
    public PinViewBlink(PinEntryView view) {
        this.viewReference = new WeakReference<>(view);
        Log.v(TAG, "⏱️ Blink manager created");
    }

    @Override
    public void run() {
        if (mCancelled) {
            Log.v(TAG, "⏱️ Blink cancelled, skipping");
            return;
        }

        PinEntryView view = viewReference.get();
        if (view == null) {
            // View has been garbage collected, stop blinking
            Log.v(TAG, "⏱️ View reference lost, stopping blink");
            return;
        }

        try {
            view.removeCallbacks(this);

            boolean shouldBlink = view.isCursorVisible() && view.isFocused();
            if (shouldBlink) {
                boolean blinkState = !view.drawCursor();
                view.invalidateCursor(blinkState);
                view.postDelayed(this, BLINK_TIMEOUT);
                Log.v(TAG, "⏱️ Blinking cursor: " + (blinkState ? "visible" : "hidden"));
            }
        } catch (Exception e) {
            // Handle any unexpected exceptions to prevent crashes
            Log.e(TAG, "⚠️ Error in blink animation", e);
        }
    }

    /**
     * Cancels the blink cycle.
     */
    public void cancel() {
        if (!mCancelled) {
            PinEntryView view = viewReference.get();
            if (view != null) {
                view.removeCallbacks(this);
                Log.v(TAG, "⏱️ Blink cycle cancelled");
            }
            mCancelled = true;
        }
    }

    /**
     * Restarts the blink cycle after cancellation.
     */
    public void uncancel() {
        mCancelled = false;
        Log.v(TAG, "⏱️ Blink cycle restarted");
    }
}
