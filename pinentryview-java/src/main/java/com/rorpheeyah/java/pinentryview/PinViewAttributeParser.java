package com.rorpheeyah.java.pinentryview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import org.jetbrains.annotations.Contract;

/**
 * Parser for PinEntryView attributes defined in attrs.xml.
 */
public class PinViewAttributeParser {
    private static final String TAG = "PinViewAttributeParser";
    private final Context mContext;

    /**
     * Creates a new attribute parser.
     *
     * @param context The context to use for resource resolution
     */
    public PinViewAttributeParser(Context context) {
        this.mContext = context;
    }

    /**
     * Parses attributes from XML and applies them to the PinEntryView.
     *
     * @param view The PinEntryView to apply attributes to
     * @param attrs The AttributeSet from XML
     * @param defStyleAttr Default style attribute
     */
    public void parseAttributes(PinEntryView view, AttributeSet attrs, int defStyleAttr) {
        if (attrs == null) return;

        // Get TypedArray of PinEntryView attributes
        TypedArray a = mContext.obtainStyledAttributes(
                attrs, R.styleable.PinEntryView, defStyleAttr, 0);

        try {
            // View type (rectangle, line, or none)
            if (a.hasValue(R.styleable.PinEntryView_viewType)) {
                int viewType = a.getInt(R.styleable.PinEntryView_viewType, PinEntryView.VIEW_TYPE_RECTANGLE);
                view.setViewType(viewType);
                Log.d(TAG, "🔄 Set viewType: " + getViewTypeName(viewType));
            }

            if (a.hasValue(R.styleable.PinEntryView_pinGravity)) {
                int gravity = a.getInt(R.styleable.PinEntryView_pinGravity, PinEntryView.GRAVITY_CENTER);
                view.setPinItemGravity(gravity);
                Log.d(TAG, "🔄 Set pinGravity: " + gravity);
            }

            // Item count
            if (a.hasValue(R.styleable.PinEntryView_itemCount)) {
                int count = a.getInt(R.styleable.PinEntryView_itemCount, PinEntryView.DEFAULT_COUNT);
                view.setItemCount(count);
                Log.d(TAG, "🔢 Set itemCount: " + count);
            }

            // Item width
            if (a.hasValue(R.styleable.PinEntryView_itemWidth)) {
                int width = a.getDimensionPixelSize(R.styleable.PinEntryView_itemWidth,
                        PinViewUtils.dpToPx(mContext, 48));
                view.setItemWidth(width);
                Log.d(TAG, "↔️ Set itemWidth: " + width + "px");
            }

            // Item height
            if (a.hasValue(R.styleable.PinEntryView_itemHeight)) {
                int height = a.getDimensionPixelSize(R.styleable.PinEntryView_itemHeight,
                        PinViewUtils.dpToPx(mContext, 48));
                view.setItemHeight(height);
                Log.d(TAG, "↕️ Set itemHeight: " + height + "px");
            }

            // Item radius (for rounded corners)
            if (a.hasValue(R.styleable.PinEntryView_itemRadius)) {
                int radius = a.getDimensionPixelSize(R.styleable.PinEntryView_itemRadius, 0);
                view.setItemRadius(radius);
                Log.d(TAG, "🔘 Set itemRadius: " + radius + "px");
            }

            // Item spacing
            if (a.hasValue(R.styleable.PinEntryView_itemSpacing)) {
                int spacing = a.getDimensionPixelSize(R.styleable.PinEntryView_itemSpacing,
                        PinViewUtils.dpToPx(mContext, 5));
                view.setItemSpacing(spacing);
                Log.d(TAG, "↔️ Set itemSpacing: " + spacing + "px");
            }

            // Line width
            if (a.hasValue(R.styleable.PinEntryView_lineWidth)) {
                int lineWidth = a.getDimensionPixelSize(R.styleable.PinEntryView_lineWidth,
                        PinViewUtils.dpToPx(mContext, 2));
                view.setLineWidth(lineWidth);
                Log.d(TAG, "📏 Set lineWidth: " + lineWidth + "px");
            }

            // Line color
            if (a.hasValue(R.styleable.PinEntryView_lineColor)) {
                if (isColorStateList(a, R.styleable.PinEntryView_lineColor)) {
                    ColorStateList colorStateList = a.getColorStateList(R.styleable.PinEntryView_lineColor);
                    view.setLineColor(colorStateList);
                    Log.d(TAG, "🎨 Set lineColor state list");
                } else {
                    int color = a.getColor(R.styleable.PinEntryView_lineColor, Color.BLACK);
                    view.setLineColor(color);
                    Log.d(TAG, "🎨 Set lineColor: #" + Integer.toHexString(0xFFFFFF & color));
                }
            }

            // Cursor color
            if (a.hasValue(R.styleable.PinEntryView_cursorColor)) {
                int color = a.getColor(R.styleable.PinEntryView_cursorColor,
                        view.getCurrentTextColor());
                view.setCursorColor(color);
                Log.d(TAG, "🎨 Set cursorColor: #" + Integer.toHexString(0xFFFFFF & color));
            }

            // Cursor width
            if (a.hasValue(R.styleable.PinEntryView_cursorWidth)) {
                int width = a.getDimensionPixelSize(R.styleable.PinEntryView_cursorWidth,
                        PinViewUtils.dpToPx(mContext, 2));
                view.setCursorWidth(width);
                Log.d(TAG, "📏 Set cursorWidth: " + width + "px");
            }

            // Hide line when filled
            if (a.hasValue(R.styleable.PinEntryView_hideLineWhenFilled)) {
                boolean hide = a.getBoolean(R.styleable.PinEntryView_hideLineWhenFilled, false);
                view.setHideLineWhenFilled(hide);
                Log.d(TAG, "🙈 Set hideLineWhenFilled: " + hide);
            }

            // Auto focus
            if (a.hasValue(R.styleable.PinEntryView_autoFocus)) {
                boolean autoFocus = a.getBoolean(R.styleable.PinEntryView_autoFocus, true);
                view.setAutoFocus(autoFocus);
                Log.d(TAG, "🔍 Set autoFocus: " + autoFocus);
            }

            // Animation enabled
            if (a.hasValue(R.styleable.PinEntryView_animationEnabled)) {
                boolean enabled = a.getBoolean(R.styleable.PinEntryView_animationEnabled, false);
                view.setAnimationEnabled(enabled);
                Log.d(TAG, "🎭 Set animationEnabled: " + enabled);
            }

            // Password hidden
            if (a.hasValue(R.styleable.PinEntryView_passwordHidden)) {
                boolean hidden = a.getBoolean(R.styleable.PinEntryView_passwordHidden,
                        PinViewUtils.isPasswordInputType(view.getInputType()));
                view.setPasswordHidden(hidden);
                Log.d(TAG, "🙈 Set passwordHidden: " + hidden);
            }

            // Error color
            if (a.hasValue(R.styleable.PinEntryView_errorColor)) {
                int errorColor = a.getColor(R.styleable.PinEntryView_errorColor, Color.RED);
                view.setErrorColor(errorColor);
                Log.d(TAG, "🎨 Set errorColor: #" + Integer.toHexString(0xFFFFFF & errorColor));
            }

            // Error text color
            if (a.hasValue(R.styleable.PinEntryView_errorTextColor)) {
                int errorTextColor = a.getColor(R.styleable.PinEntryView_errorTextColor, view.getCurrentTextColor());
                view.setErrorTextColor(errorTextColor);
                Log.d(TAG, "🎨 Set errorTextColor: #" + Integer.toHexString(0xFFFFFF & errorTextColor));
            }

            // Error shake enabled
            if (a.hasValue(R.styleable.PinEntryView_errorShakeEnabled)) {
                boolean shakeEnabled = a.getBoolean(R.styleable.PinEntryView_errorShakeEnabled, false);
                view.setErrorShakeEnabled(shakeEnabled);
                Log.d(TAG, "🔄 Set errorShakeEnabled: " + shakeEnabled);
            }

            // Success color
            if (a.hasValue(R.styleable.PinEntryView_successColor)) {
                int successColor = a.getColor(R.styleable.PinEntryView_successColor, Color.GREEN);
                view.setSuccessColor(successColor);
                Log.d(TAG, "🎨 Set successColor: #" + Integer.toHexString(0xFFFFFF & successColor));
            }

            // Success text color
            if (a.hasValue(R.styleable.PinEntryView_successTextColor)) {
                int successTextColor = a.getColor(R.styleable.PinEntryView_successTextColor, view.getCurrentTextColor());
                view.setSuccessTextColor(successTextColor);
                Log.d(TAG, "🎨 Set successTextColor: #" + Integer.toHexString(0xFFFFFF & successTextColor));
            }

            // Success enabled
            if (a.hasValue(R.styleable.PinEntryView_successEnabled)) {
                boolean successEnabled = a.getBoolean(R.styleable.PinEntryView_successEnabled, false);
                view.setSuccessEnabled(successEnabled);
                Log.d(TAG, "✅ Set successEnabled: " + successEnabled);
            }

            // Success animation enabled
            if (a.hasValue(R.styleable.PinEntryView_successAnimationEnabled)) {
                boolean successAnimEnabled = a.getBoolean(R.styleable.PinEntryView_successAnimationEnabled, false);
                view.setSuccessAnimationEnabled(successAnimEnabled);
                Log.d(TAG, "✅ Set successAnimationEnabled: " + successAnimEnabled);
            }

            // Item background color
            if (a.hasValue(R.styleable.PinEntryView_itemBackgroundColor)) {
                int backgroundColor = a.getColor(R.styleable.PinEntryView_itemBackgroundColor, Color.TRANSPARENT);
                view.setItemBackgroundColor(backgroundColor);
                Log.d(TAG, "🎨 Set itemBackgroundColor: #" + Integer.toHexString(0xFFFFFF & backgroundColor));
            }

            // Error background color
            if (a.hasValue(R.styleable.PinEntryView_errorBackgroundColor)) {
                int errorBackgroundColor = a.getColor(R.styleable.PinEntryView_errorBackgroundColor, Color.RED);
                view.setErrorBackgroundColor(errorBackgroundColor);
                Log.d(TAG, "🎨 Set errorBackgroundColor: #" + Integer.toHexString(0xFFFFFF & errorBackgroundColor));
            }

            // Success background color
            if (a.hasValue(R.styleable.PinEntryView_successBackgroundColor)) {
                int successBackgroundColor = a.getColor(R.styleable.PinEntryView_successBackgroundColor, Color.GREEN);
                view.setSuccessBackgroundColor(successBackgroundColor);
                Log.d(TAG, "🎨 Set successBackgroundColor: #" + Integer.toHexString(0xFFFFFF & successBackgroundColor));
            }

        } catch (Exception e) {
            Log.e(TAG, "⚠️ Error parsing attributes", e);
        } finally {
            a.recycle();
        }

        // Also process standard Android attributes (textColor, hint, etc.)
        parseStandardAttributes(view, attrs);

        Log.d(TAG, "🔍 Attributes parsing completed");
    }

    /**
     * Checks if an attribute value is a ColorStateList rather than a simple color.
     */
    private boolean isColorStateList(TypedArray a, int index) {
        try {
            // If getColorStateList doesn't throw an exception and returns non-null,
            // it's a ColorStateList
            return a.getColorStateList(index) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets a human-readable name for a view type value.
     */
    @NonNull
    @Contract(pure = true)
    private String getViewTypeName(int viewType) {
        switch (viewType) {
            case PinEntryView.VIEW_TYPE_RECTANGLE:
                return "rectangle";
            case PinEntryView.VIEW_TYPE_LINE:
                return "line";
            case PinEntryView.VIEW_TYPE_CIRCLE:
                    return "circle";
            case PinEntryView.VIEW_TYPE_NONE:
                return "none";
            default:
                return "unknown (" + viewType + ")";
        }
    }

    /**
     * Parses standard Android attributes
     */
    private void parseStandardAttributes(PinEntryView view, AttributeSet attrs) {
        // Android namespace prefix for qualified names
        final String ANDROID_NS_PREFIX = "android:";

        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            String qualifiedName = attrs.getAttributeName(i);
            String value = attrs.getAttributeValue(i);

            // For Android namespace attributes, the qualified name will start with "android:"
            if (qualifiedName.startsWith(ANDROID_NS_PREFIX)) {
                // Extract the name without the namespace prefix
                String name = qualifiedName.substring(ANDROID_NS_PREFIX.length());

                switch (name) {
                    case "inputType":
                        // Input type is handled by parent EditText
                        break;
                    case "textColor":
                        // Cursor color often inherits text color if not explicitly set
                        if (!view.isCursorColorSet()) {
                            try {
                                if (value.startsWith("#")) {
                                    int color = Color.parseColor(value);
                                    view.setCursorColor(color);
                                    Log.d(TAG, "🎨 Cursor color set from textColor attribute");
                                } else if (value.startsWith("@")) {
                                    int resId = parseResourceId(value);
                                    if (resId != 0) {
                                        int color = ResourcesCompat.getColor(
                                                mContext.getResources(), resId, mContext.getTheme());
                                        view.setCursorColor(color);
                                        Log.d(TAG, "🎨 Cursor color set from textColor resource");
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "⚠️ Error parsing textColor", e);
                            }
                        }
                        break;
                    case "cursorVisible":
                        view.setCursorVisible("true".equals(value));
                        Log.d(TAG, "👁️ Cursor visibility set from attribute");
                        break;
                }
            }
        }
    }

    /**
     * Parses a resource ID from a string resource reference.
     */
    private int parseResourceId(String value) {
        try {
            if (value.startsWith("@")) {
                return Integer.parseInt(value.substring(1));
            }
            return 0;
        } catch (NumberFormatException e) {
            Log.e(TAG, "⚠️ Error parsing resource ID: " + value, e);
            return 0;
        }
    }

    /**
     * Add this helper method to PinEntryView to check if cursor color has been set
     */
    private boolean isCursorColorSet(PinEntryView view) {
        return view.isCursorColorSet();
    }
}