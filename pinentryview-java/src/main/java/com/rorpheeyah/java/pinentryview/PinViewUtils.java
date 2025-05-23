package com.rorpheeyah.java.pinentryview;

import android.content.Context;
import android.os.Build;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

/**
 * Utility class providing helper methods for PinEntryView components.
 * <p>
 * This class includes methods for converting dp to pixels, checking input types,
 * and applying base settings to EditText fields used in PIN entry scenarios.
 * </p>
 */
public class PinViewUtils {
    private static final String TAG = "PinViewUtils";

    /**
     * Converts dp to pixels.
     *
     * @param context The context to get the resources from
     * @param dp The value in dp
     * @return The value in pixels
     */
    public static int dpToPx(Context context, float dp) {
        try {
            return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
        } catch (Exception e) {
            Log.e(TAG, "⚠️ Error converting dp to px", e);
            return (int) dp;
        }
    }

    /**
     * Checks if the input type is a password type.
     *
     * @param inputType The input type to check
     * @return True if the input type is a password type, false otherwise
     */
    public static boolean isPasswordInputType(int inputType) {
        final int variation = inputType & (EditorInfo.TYPE_MASK_CLASS | EditorInfo.TYPE_MASK_VARIATION);
        return variation == (EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD)
                || variation == (EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD)
                || variation == (EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD);
    }

    /**
     * Applies base EditText settings that would normally come from styles.xml
     *
     * @param editText The EditText to apply settings to
     */
    public static void applyBaseSettings(EditText editText) {
        try {
            // Remove background
            editText.setBackground(null);

            // Set minimum height to 0
            editText.setMinimumHeight(0);

            // Set max lines to 1
            editText.setMaxLines(1);

            // Hide text cursor and selection handles programmatically
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                editText.setTextCursorDrawable(null);
            }

            // Input type settings
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);

            // Center text
            editText.setGravity(Gravity.CENTER);

            Log.d(TAG, "🛠️ Base settings applied to EditText");
        } catch (Exception e) {
            Log.e(TAG, "⚠️ Error applying base settings", e);
        }
    }
}
