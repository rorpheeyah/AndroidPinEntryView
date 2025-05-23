package com.rorpheeyah.java.pinentryview;

import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Default implementation of the ActionMode.Callback interface to disable text selection.
 * This class prevents the text selection menu from appearing when long-pressing the PinEntryView.
 */
public class PinViewActionModeCallback implements ActionMode.Callback {
    private static final String TAG = PinViewActionModeCallback.class.getSimpleName();

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        Log.v(TAG, "🚫 Action mode creation blocked");
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        Log.v(TAG, "🚫 Action mode preparation blocked");
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        Log.v(TAG, "🚫 Action item click blocked");
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        // No action needed
        Log.v(TAG, "🚫 Action mode destruction ignored");
    }
}