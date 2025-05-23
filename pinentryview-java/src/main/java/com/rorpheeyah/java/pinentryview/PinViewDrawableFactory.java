package com.rorpheeyah.java.pinentryview;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.util.Log;

import androidx.annotation.ColorInt;

/**
 * Factory for creating commonly used drawables in PinEntryView.
 */
public class PinViewDrawableFactory {
    private static final String TAG = PinViewDrawableFactory.class.getSimpleName();

    /**
     * Creates an invisible drawable with zero dimensions and alpha.
     * Used for hiding text cursor and selection handles.
     *
     * @return An invisible drawable
     */
    public static Drawable createInvisibleDrawable() {
        ShapeDrawable drawable = new ShapeDrawable(new RectShape());
        drawable.setIntrinsicWidth(0);
        drawable.setIntrinsicHeight(0);
        drawable.setAlpha(0);
        Log.v(TAG, "🎨 Created invisible drawable");
        return drawable;
    }

    /**
     * Creates a circle drawable for password masking.
     *
     * @param color The color of the circle
     * @param size The size (diameter) of the circle
     * @return A circular drawable
     */
    public static Drawable createCircleDrawable(@ColorInt int color, float size) {
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        drawable.getPaint().setColor(color);
        drawable.setIntrinsicWidth((int) size);
        drawable.setIntrinsicHeight((int) size);
        Log.v(TAG, "🎨 Created circle drawable");
        return drawable;
    }

    /**
     * Creates a rectangular drawable with specified color.
     *
     * @param color The color of the rectangle
     * @return A rectangular drawable
     */
    public static Drawable createRectangleDrawable(@ColorInt int color) {
        return new ColorDrawable(color);
    }
}