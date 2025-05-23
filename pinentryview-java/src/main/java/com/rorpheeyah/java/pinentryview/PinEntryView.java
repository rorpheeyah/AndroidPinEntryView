package com.rorpheeyah.java.pinentryview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.MovementMethod;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.res.ResourcesCompat;

import org.jetbrains.annotations.Contract;

import java.lang.reflect.Field;

/**
 * A highly customizable PIN entry view component for Android applications.
 * <p>
 * This component provides a visually separated field for inputting PINs, OTPs,
 * passwords, or any other code requiring individual character display.
 *
 * @author rorpheeyah
 */
public class PinEntryView extends AppCompatEditText {

    //=====================================================================
    // CONSTANTS
    //=====================================================================
    private static final String TAG = PinEntryView.class.getSimpleName();
    private static final boolean DBG = false;
    private static final int BLINK = 500;
    private static final InputFilter[] NO_FILTERS = new InputFilter[0];

    // Gravity constants
    public static final int GRAVITY_START = 0;
    public static final int GRAVITY_CENTER = 1;
    public static final int GRAVITY_END = 2;

    /**
     * The default number of PIN items to display
     */
    public static final int DEFAULT_COUNT = 4;

    /**
     * Rectangle box style with borders on all sides
     */
    public static final int VIEW_TYPE_RECTANGLE = 0;

    /**
     * Line style with a border at the bottom
     */
    public static final int VIEW_TYPE_LINE = 1;

    /**
     * No borders or lines, just spaced text
     */
    public static final int VIEW_TYPE_NONE = 2;

    /**
     * Circle style with round borders
     */
    public static final int VIEW_TYPE_CIRCLE = 3;

    //=====================================================================
    // INTERFACES
    //=====================================================================
    /**
     * Interface for receiving callbacks when the PIN is fully entered.
     */
    public interface OnPinEnteredListener {
        /**
         * Called when the PIN has been fully entered.
         *
         * @param pin The entered PIN as a string
         */
        void onPinEntered(String pin);
    }

    //=====================================================================
    // FIELDS
    //=====================================================================
    // Configuration properties
    private int mViewType;
    private int mPinItemCount;
    private int mPinItemWidth;
    private int mPinItemHeight;
    private int mPinItemRadius;
    private int mPinItemSpacing;
    private boolean mAutoFocus;
    private int mGravity = GRAVITY_CENTER;

    private final TextPaint mAnimatorTextPaint = new TextPaint();
    private final PinViewDrawer mDrawer;

    // Error state properties
    private boolean mError = false;
    private int mErrorColor = -1;
    private boolean mErrorShakeEnabled = false;
    private int mErrorTextColor = -1;

    // Success state properties
    private boolean mSuccess = false;
    private int mSuccessColor = -1;
    private boolean mSuccessEnabled = false;
    private boolean mSuccessAnimationEnabled = false;
    private int mSuccessTextColor = -1;

    // Style properties
    private ColorStateList mLineColor;
    private int mCurLineColor = Color.BLACK;
    private int mLineWidth;
    private Drawable mItemBackground;
    private boolean mHideLineWhenFilled;
    private boolean mCursorColorSet = false;

    // Animation properties
    private android.animation.ValueAnimator mDefaultAddAnimator;
    private boolean mAnimationEnabled = false;

    // Password and text properties
    private boolean mPasswordHidden;
    private String mTransformed;

    // Cursor properties
    private PinViewBlink mBlink;
    private boolean mCursorVisible;
    private boolean mDrawCursor;
    private float mCursorHeight;
    private int mCursorWidth;
    private int mCursorColor;

    // Listener for PIN entered events
    private OnPinEnteredListener mPinEnteredListener;

    //=====================================================================
    // CONSTRUCTORS
    //=====================================================================
    /**
     * Creates a new PinEntryView with default style.
     *
     * @param context The Context the view is running in
     */
    public PinEntryView(Context context) {
        this(context, null);
    }

    /**
     * Creates a new PinEntryView with attributes from the specified style.
     *
     * @param context The Context the view is running in
     * @param attrs The attributes of the XML tag that is inflating the view
     */
    public PinEntryView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Creates a new PinEntryView with attributes from the specified style.
     *
     * @param context The Context the view is running in
     * @param attrs The attributes of the XML tag that is inflating the view
     * @param defStyleAttr An attribute in the current theme
     */
    public PinEntryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Initialize drawing tools
        // Drawing tools
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mAnimatorTextPaint.set(getPaint());
        mDrawer = new PinViewDrawer(this, mPaint, mAnimatorTextPaint);

        // Create attribute parser
        PinViewAttributeParser mAttributeParser = new PinViewAttributeParser(context);

        // Set default values
        initDefaultValues();

        // Parse attributes if provided
        if (attrs != null) {
            mAttributeParser.parseAttributes(this, attrs, defStyleAttr);
        }

        mCurLineColor = mLineColor.getDefaultColor();
        updateCursorHeight();
        checkItemRadius();

        // Apply base EditText settings
        PinViewUtils.applyBaseSettings(this);

        // Setup for PIN entry
        setMaxLength(mPinItemCount);
        mPaint.setStrokeWidth(mLineWidth);
        setupAnimator();

        setTransformationMethod(null);
        disableSelectionMenu();
        setupStyle();

        mPasswordHidden = PinViewUtils.isPasswordInputType(getInputType());

        Log.i(TAG, "🔐 PinEntryView initialized with " + mPinItemCount + " items");
    }

    //=====================================================================
    // INITIALIZATION METHODS
    //=====================================================================
    /**
     * Initialize default values for all properties
     */
    private void initDefaultValues() {
        mViewType = VIEW_TYPE_RECTANGLE;
        mPinItemCount = DEFAULT_COUNT;
        mPinItemHeight = PinViewUtils.dpToPx(getContext(), 48);
        mPinItemWidth = PinViewUtils.dpToPx(getContext(), 48);
        mPinItemSpacing = PinViewUtils.dpToPx(getContext(), 5);
        mPinItemRadius = 0;
        mLineWidth = PinViewUtils.dpToPx(getContext(), 2);
        mLineColor = ColorStateList.valueOf(Color.BLACK);
        mCursorVisible = true;
        mCursorColor = getCurrentTextColor();
        mCursorWidth = PinViewUtils.dpToPx(getContext(), 2);
        mHideLineWhenFilled = false;
        mAutoFocus = false;
        mErrorColor = -1;
        mErrorShakeEnabled = false;
        mSuccessColor = -1;
        mSuccessEnabled = false;
        mSuccessAnimationEnabled = false;
    }

    /**
     * Sets up the view's style and behavior programmatically.
     */
    private void setupStyle() {
        // Basic appearance settings
        setBackground(null);
        setMinHeight(0);
        setMaxLines(1);

        // Critical input settings
        setFocusable(true);
        setFocusableInTouchMode(true);
        setCursorVisible(true);

        // Enable keyboard display
        setShowSoftInputOnFocus(true);

        // Create invisible drawable programmatically
        Drawable invisibleDrawable = PinViewDrawableFactory.createInvisibleDrawable();

        // Apply cursor and handles based on API level
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setTextCursorDrawable(invisibleDrawable);
            setTextSelectHandle(invisibleDrawable);
            setTextSelectHandleLeft(invisibleDrawable);
            setTextSelectHandleRight(invisibleDrawable);
        } else {
            setInvisibleTextHandlesViaReflection(invisibleDrawable);
        }

        // Always set the click listener - showKeyboard() will check autoFocus setting
        setOnClickListener(v -> showKeyboard());

        // Setup keyboard actions
        setupKeyboardActions();

        Log.d(TAG, "📱 View style setup completed");
    }

    /**
     * Uses reflection to set invisible text cursor and handles
     */
    @SuppressLint({"PrivateApi", "DiscouragedPrivateApi"})
    private void setInvisibleTextHandlesViaReflection(Drawable invisibleDrawable) {
        try {
            // Get editor field
            Field editorField = TextView.class.getDeclaredField("mEditor");
            editorField.setAccessible(true);
            Object editor = editorField.get(this);

            if (editor == null) {
                // Force editor creation if needed
                setTextIsSelectable(true);
                setTextIsSelectable(false);
                editor = editorField.get(this);
            }

            if (editor != null) {
                // Set cursor drawable
                Field cursorField = editor.getClass().getDeclaredField("mCursorDrawable");
                cursorField.setAccessible(true);
                Drawable[] cursorDrawables = new Drawable[2];
                cursorDrawables[0] = invisibleDrawable;
                cursorDrawables[1] = invisibleDrawable;
                cursorField.set(editor, cursorDrawables);

                // Set selection handles
                Field handleField = editor.getClass().getDeclaredField("mSelectHandleCenter");
                handleField.setAccessible(true);
                handleField.set(editor, invisibleDrawable);

                Field leftHandleField = editor.getClass().getDeclaredField("mSelectHandleLeft");
                leftHandleField.setAccessible(true);
                leftHandleField.set(editor, invisibleDrawable);

                Field rightHandleField = editor.getClass().getDeclaredField("mSelectHandleRight");
                rightHandleField.setAccessible(true);
                rightHandleField.set(editor, invisibleDrawable);

                Log.d(TAG, "📱 Set invisible handles via reflection successful");
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to set handles: " + e.getMessage());
        }
    }

    /**
     * Sets up the animator for character input animation.
     */
    private void setupAnimator() {
        try {
            mDefaultAddAnimator = android.animation.ValueAnimator.ofFloat(0.5f, 1f);
            mDefaultAddAnimator.setDuration(150);
            mDefaultAddAnimator.setInterpolator(new DecelerateInterpolator());
            mDefaultAddAnimator.addUpdateListener(animation -> {
                try {
                    float scale = (Float) animation.getAnimatedValue();
                    int alpha = (int) (255 * scale);
                    mAnimatorTextPaint.setTextSize(getTextSize() * scale);
                    mAnimatorTextPaint.setAlpha(alpha);
                    postInvalidate();
                } catch (Exception e) {
                    Log.e(TAG, "⚠️ Error in animation update", e);
                }
            });
            Log.d(TAG, "🎬 Animation setup completed");
        } catch (Exception e) {
            Log.e(TAG, "🚫 Error setting up animator", e);
            mAnimationEnabled = false;
        }
    }

    /**
     * Validates and adjusts the item radius to ensure it's compatible with other dimensions.
     */
    private void checkItemRadius() {
        try {
            if (mViewType == VIEW_TYPE_LINE) {
                float halfOfLineWidth = ((float) mLineWidth) / 2;
                if (mPinItemRadius > halfOfLineWidth) {
                    Log.w(TAG, "⚠️ Adjusting itemRadius to match lineWidth constraints");
                    mPinItemRadius = (int) halfOfLineWidth;
                }
            } else if (mViewType == VIEW_TYPE_RECTANGLE) {
                float halfOfItemWidth = ((float) mPinItemWidth) / 2;
                if (mPinItemRadius > halfOfItemWidth) {
                    Log.w(TAG, "⚠️ Adjusting itemRadius to match itemWidth constraints");
                    mPinItemRadius = (int) halfOfItemWidth;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "🚫 Error checking item radius", e);
            // Set to safe defaults
            mPinItemRadius = Math.min(mPinItemRadius, mLineWidth / 2);
        }
    }

    /**
     * Sets the maximum length of input allowed.
     *
     * @param maxLength The maximum number of characters, or -1 for no limit
     */
    private void setMaxLength(int maxLength) {
        if (maxLength >= 0) {
            setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        } else {
            setFilters(NO_FILTERS);
        }
        Log.d(TAG, "📏 Max length set to: " + maxLength);
    }

    /**
     * Disables the text selection menu.
     */
    private void disableSelectionMenu() {
        setCustomSelectionActionModeCallback(new PinViewActionModeCallback());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                setCustomInsertionActionModeCallback(new PinViewActionModeCallback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        if (menu != null) {
                            menu.removeItem(android.R.id.autofill);
                        }
                        return true;
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "⚠️ Error setting custom insertion action mode", e);
            }
        }
    }

    /**
     * Returns a human-readable string for a gravity integer value.
     * Useful for logging or debugging gravity settings.
     *
     * @param gravity The gravity constant (GRAVITY_START, GRAVITY_CENTER, GRAVITY_END)
     * @return The string representation of the gravity
     */
    @NonNull
    @Contract(pure = true)
    private String gravityToString(int gravity) {
        switch (gravity) {
            case GRAVITY_START:
                // Item alignment: start (left in LTR, right in RTL)
                return "START";
            case GRAVITY_END:
                // Item alignment: end (right in LTR, left in RTL)
                return "END";
            default:
                // Default or center-aligned
                return "CENTER";
        }
    }

    /**
     * Sets up keyboard action handling.
     * This handles all possible IME actions like Done, Next, Go, etc.
     */
    private void setupKeyboardActions() {

        // Add editor action listener to handle all action buttons
        setOnEditorActionListener((v, actionId, event) -> {

            switch (actionId) {
                case EditorInfo.IME_ACTION_DONE:
                case EditorInfo.IME_ACTION_GO:
                case EditorInfo.IME_ACTION_SEARCH:
                case EditorInfo.IME_ACTION_SEND:
                    hideKeyboard();
                    break;

                case EditorInfo.IME_NULL:
                    if (event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        hideKeyboard();
                    }
                    break;
            }

            return false;
        });
    }

    //=====================================================================
    // OVERRIDE METHODS
    //=====================================================================
    @Override
    public void setInputType(int type) {
        super.setInputType(type);
        mPasswordHidden = PinViewUtils.isPasswordInputType(getInputType());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        int boxHeight = mPinItemHeight;

        if (widthMode == MeasureSpec.EXACTLY) {
            // Parent has told us how big to be. So be it.
            width = widthSize;
        } else {
            int boxesWidth = (mPinItemCount - 1) * mPinItemSpacing + mPinItemCount * mPinItemWidth;
            width = boxesWidth + getPaddingEnd() + getPaddingStart();
            if (mPinItemSpacing == 0) {
                width -= (mPinItemCount - 1) * mLineWidth;
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            // Parent has told us how big to be. So be it.
            height = heightSize;
        } else {
            height = boxHeight + getPaddingTop() + getPaddingBottom();
        }

        setMeasuredDimension(width, height);
        Log.v(TAG, "📐 Measured size: " + width + "x" + height);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        // Clear error when text changes
        if (mError && (lengthAfter != lengthBefore)) {
            setErrorState(false);
        }

        // Clear success when text changes
        if (mSuccess && (lengthAfter != lengthBefore)) {
            setSuccessState(false);
        }

        if (start != text.length()) {
            moveSelectionToEnd();
        }

        makeBlink();

        if (mAnimationEnabled) {
            final boolean isAdd = lengthAfter - lengthBefore > 0;
            if (isAdd && mDefaultAddAnimator != null) {
                try {
                    mDefaultAddAnimator.end();
                    mDefaultAddAnimator.start();
                } catch (Exception e) {
                    Log.e(TAG, "⚠️ Error starting animation", e);
                }
            }
        }

        try {
            TransformationMethod transformation = getTransformationMethod();
            if (transformation == null) {
                mTransformed = getText() == null ? "" : getText().toString();
            } else {
                CharSequence transformed = transformation.getTransformation(getText(), this);
                mTransformed = transformed != null ? transformed.toString() : "";
            }
        } catch (Exception e) {
            Log.e(TAG, "⚠️ Error applying transformation", e);
            mTransformed = getText() == null ? "" : getText().toString();
        }

        // Notify listener when PIN is complete
        if (mPinEnteredListener != null && getText() != null &&
                getText().length() == mPinItemCount) {
            Log.i(TAG, "✅ PIN entry complete");
            mPinEnteredListener.onPinEntered(getText().toString());
        }
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);

        if (focused) {
            moveSelectionToEnd();
            makeBlink();
            mAutoFocus = true; // Enable autoFocus when view gains focus
            Log.v(TAG, "🔍 Focus gained - autoFocus enabled");
        } else {
            Log.v(TAG, "👋 Focus lost");
        }
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);

        if (selEnd != getLength()) {
            moveSelectionToEnd();
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if (mLineColor != null && mLineColor.isStateful()) {
            updateColors();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();

        mDrawer.updatePaints();
        mDrawer.drawPinView(canvas);

        canvas.restore();
    }

    /**
     * Override touch event to handle autoFocus setting
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);

        if (!mAutoFocus) {
            mAutoFocus = true;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                performClick();
                return true;
            }
            return result;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            showKeyboard();
        }

        return result;
    }

    /**
     * Force focus when visibility changes if auto-focus is enabled
     */
    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (mAutoFocus && visibility == VISIBLE && changedView == this) {
            // Reapply focus settings when becoming visible
            postDelayed(() -> {
                requestFocus();
                if (isFocused()) {
                    showKeyboard();
                }
            }, 200);
        }
    }

    @Override
    protected MovementMethod getDefaultMovementMethod() {
        return DefaultMovementMethod.getInstance();
    }

    @Override
    public void setCursorVisible(boolean visible) {
        if (mCursorVisible != visible) {
            mCursorVisible = visible;
            invalidateCursor(mCursorVisible);
            makeBlink();
            Log.d(TAG, "👁️ Cursor visibility set to: " + visible);
        }
    }

    @Override
    public boolean isCursorVisible() {
        return mCursorVisible;
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        switch (screenState) {
            case View.SCREEN_STATE_ON:
                resumeBlink();
                break;
            case View.SCREEN_STATE_OFF:
                suspendBlink();
                break;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        resumeBlink();

        post(() -> {
            // Create invisible drawable programmatically
            Drawable invisibleDrawable = PinViewDrawableFactory.createInvisibleDrawable();

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                setInvisibleTextHandlesViaReflection(invisibleDrawable);
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        suspendBlink();
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        updateCursorHeight();
    }

    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
        updateCursorHeight();
    }

    @Override
    public void setTypeface(Typeface tf) {
        super.setTypeface(tf);
        if (mAnimatorTextPaint != null) {
            mAnimatorTextPaint.set(getPaint());
        }
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        super.setTypeface(tf, style);
    }

    @Override
    public boolean isSuggestionsEnabled() {
        return false;
    }

    //=====================================================================
    // PUBLIC SETTERS AND GETTERS
    //=====================================================================

    /**
     * Sets the gravity for the PIN items when the view width is larger than needed.
     *
     * @param gravity GRAVITY_START, GRAVITY_CENTER, or GRAVITY_END
     */
    public void setPinItemGravity(int gravity) {
        if (gravity != GRAVITY_START && gravity != GRAVITY_CENTER && gravity != GRAVITY_END) {
            gravity = GRAVITY_CENTER;
        }
        if (mGravity != gravity) {
            mGravity = gravity;
            invalidate();
        }
        Log.d(TAG, "🔄 Gravity set to: " + gravityToString(gravity));
    }

    /**
     * Gets the current gravity setting.
     *
     * @return The current gravity (START, CENTER, or END)
     */
    public int getGravity() {
        return mGravity;
    }

    /**
     * Sets a listener to be notified when the full PIN is entered.
     *
     * @param listener The callback interface
     */
    public void setOnPinEnteredListener(OnPinEnteredListener listener) {
        this.mPinEnteredListener = listener;
        Log.d(TAG, "🎧 PIN entry listener set");
    }

    /**
     * Sets whether the view should automatically request focus and show keyboard.
     *
     * @param autoFocus True to enable auto-focus, false to disable
     */
    public void setAutoFocus(boolean autoFocus) {
        this.mAutoFocus = autoFocus;

        // Update focus-related settings
        setShowSoftInputOnFocus(true); // Always keep this true

        // Always ensure focusable for touch and programmatic focus
        setFocusable(true);
        setFocusableInTouchMode(true);

        Log.d(TAG, "🔍 Auto-focus set to: " + autoFocus);
    }

    /**
     * Gets the current auto-focus setting.
     *
     * @return True if auto-focus is enabled, false otherwise
     */
    public boolean isAutoFocus() {
        return mAutoFocus;
    }

    /**
     * Sets whether to hide entered text with password dots.
     *
     * @param hidden True to hide text as dots, false to show actual characters
     */
    public void setPasswordHidden(boolean hidden) {
        mPasswordHidden = hidden;
        requestLayout();
        Log.d(TAG, "🙈 Password hidden mode set to: " + hidden);
    }

    /**
     * Checks if entered text is hidden with password dots.
     *
     * @return True if text is hidden, false otherwise
     */
    public boolean isPasswordHidden() {
        return mPasswordHidden;
    }

    /**
     * Gets the transformed text (with any transformation method applied).
     *
     * @return The transformed text
     */
    public String getTransformedText() {
        return mTransformed;
    }

    /**
     * Gets whether to draw the cursor.
     *
     * @return True if cursor is drawn, false otherwise
     */
    public boolean drawCursor() {
        return mDrawCursor;
    }

    /**
     * Gets the cursor height.
     *
     * @return The cursor height in pixels
     */
    public float getCursorHeight() {
        return mCursorHeight;
    }

    /**
     * Sets the color of the border/line for each pin item.
     *
     * @param color The color value
     */
    public void setLineColor(@ColorInt int color) {
        mLineColor = ColorStateList.valueOf(color);
        updateColors();
        Log.d(TAG, "🎨 Line color set to: #" + Integer.toHexString(0xFFFFFF & color));
    }

    /**
     * Sets the color state list for the border/line.
     *
     * @param colors The color state list to use
     */
    public void setLineColor(ColorStateList colors) {
        if (colors == null) {
            mLineColor = ColorStateList.valueOf(Color.BLACK);
            Log.w(TAG, "⚠️ Null ColorStateList provided, using default black");
        } else {
            mLineColor = colors;
        }
        updateColors();
    }

    /**
     * Gets the color state list for the border/line.
     *
     * @return The color state list
     */
    public ColorStateList getLineColors() {
        return mLineColor;
    }

    /**
     * Gets the current line color.
     *
     * @return The current line color
     */
    @ColorInt
    public int getCurrentLineColor() {
        if (mSuccess && mSuccessEnabled && mSuccessColor != -1) {
            return mSuccessColor;
        } else if (mError && mErrorColor != -1) {
            return mErrorColor;
        }
        return mCurLineColor;
    }

    /**
     * Gets the current text color based on the view state.
     *
     * @return The current text color
     */
    @ColorInt
    public int getActiveTextColor() {
        if (mSuccess && mSuccessEnabled && mSuccessTextColor != -1) {
            return mSuccessTextColor;
        } else if (mError && mErrorTextColor != -1) {
            return mErrorTextColor;
        }
        return super.getCurrentTextColor();
    }

    /**
     * Sets error state for the PIN entry view
     *
     * @param error True to show error state, false to hide
     */
    public void setErrorState(boolean error) {
        mError = error;
        if (mError) {
            mSuccess = false;  // Clear success state when setting error
            if (mErrorShakeEnabled && mErrorColor != -1) {
                shakeAnimation();
            }
        }
        invalidate();
        Log.d(TAG, "⚠️ Error state set to: " + error);
    }

    /**
     * Sets error state with custom color
     *
     * @param error True to show error state, false to hide
     * @param errorColor The color to use for the error state
     */
    public void setErrorState(boolean error, @ColorInt int errorColor) {
        setErrorColor(errorColor);
        setErrorState(error);
    }

    /**
     * Checks if the view is in error state
     *
     * @return True if in error state, false otherwise
     */
    public boolean isError() {
        return mError;
    }

    /**
     * Sets the error color
     *
     * @param errorColor The color to use for error state
     */
    public void setErrorColor(@ColorInt int errorColor) {
        mErrorColor = errorColor;
        if (mError) {
            invalidate();
        }
        Log.d(TAG, "🎨 Error color set to: #" + Integer.toHexString(0xFFFFFF & errorColor));
    }

    /**
     * Gets the current error color
     *
     * @return The error color
     */
    @ColorInt
    public int getErrorColor() {
        return mErrorColor;
    }

    /**
     * Sets the error text color
     *
     * @param errorTextColor The color to use for text in error state
     */
    public void setErrorTextColor(@ColorInt int errorTextColor) {
        mErrorTextColor = errorTextColor;
        if (mError) {
            invalidate();
        }
        Log.d(TAG, "🎨 Error text color set to: #" + Integer.toHexString(0xFFFFFF & errorTextColor));
    }

    /**
     * Gets the current error text color
     *
     * @return The error text color
     */
    @ColorInt
    public int getErrorTextColor() {
        return mErrorTextColor;
    }

    /**
     * Sets whether shake animation is enabled for error state
     *
     * @param enabled True to enable shake animation, false to disable
     */
    public void setErrorShakeEnabled(boolean enabled) {
        mErrorShakeEnabled = enabled;
        Log.d(TAG, "🔄 Error shake animation " + (enabled ? "enabled" : "disabled"));
    }

    /**
     * Checks if shake animation is enabled for error state
     *
     * @return True if shake animation is enabled, false otherwise
     */
    public boolean isErrorShakeEnabled() {
        return mErrorShakeEnabled;
    }

    /**
     * Sets success state for the PIN entry view
     *
     * @param success True to show success state, false to hide
     */
    public void setSuccessState(boolean success) {
        mSuccess = success;
        if (mSuccess && mSuccessEnabled) {
            mError = false;  // Clear error state when setting success
            if (mSuccessAnimationEnabled && mSuccessColor != -1) {
                successAnimation();
            }
        }
        invalidate();
        Log.d(TAG, "✅ Success state set to: " + success);
    }

    /**
     * Sets success state with custom color
     *
     * @param success True to show success state, false to hide
     * @param successColor The color to use for the success state
     */
    public void setSuccessState(boolean success, @ColorInt int successColor) {
        setSuccessColor(successColor);
        setSuccessState(success);
    }

    /**
     * Checks if the view is in success state
     *
     * @return True if in success state, false otherwise
     */
    public boolean isSuccess() {
        return mSuccess;
    }

    /**
     * Sets the success color
     *
     * @param successColor The color to use for success state
     */
    public void setSuccessColor(@ColorInt int successColor) {
        mSuccessColor = successColor;
        if (mSuccess) {
            invalidate();
        }
        Log.d(TAG, "🎨 Success color set to: #" + Integer.toHexString(0xFFFFFF & successColor));
    }

    /**
     * Gets the current success color
     *
     * @return The success color
     */
    @ColorInt
    public int getSuccessColor() {
        return mSuccessColor;
    }

    /**
     * Sets the success text color
     *
     * @param successTextColor The color to use for text in success state
     */
    public void setSuccessTextColor(@ColorInt int successTextColor) {
        mSuccessTextColor = successTextColor;
        if (mSuccess) {
            invalidate();
        }
        Log.d(TAG, "🎨 Success text color set to: #" + Integer.toHexString(0xFFFFFF & successTextColor));
    }

    /**
     * Gets the current success text color
     *
     * @return The success text color
     */
    @ColorInt
    public int getSuccessTextColor() {
        return mSuccessTextColor;
    }

    /**
     * Sets whether success state is enabled
     *
     * @param enabled True to enable success state, false to disable
     */
    public void setSuccessEnabled(boolean enabled) {
        mSuccessEnabled = enabled;
        Log.d(TAG, "✅ Success state " + (enabled ? "enabled" : "disabled"));
    }

    /**
     * Checks if success state is enabled
     *
     * @return True if success state is enabled, false otherwise
     */
    public boolean isSuccessEnabled() {
        return mSuccessEnabled;
    }

    /**
     * Sets whether success animation is enabled
     *
     * @param enabled True to enable success animation, false to disable
     */
    public void setSuccessAnimationEnabled(boolean enabled) {
        mSuccessAnimationEnabled = enabled;
        Log.d(TAG, "✅ Success animation " + (enabled ? "enabled" : "disabled"));
    }

    /**
     * Checks if success animation is enabled
     *
     * @return True if success animation is enabled, false otherwise
     */
    public boolean isSuccessAnimationEnabled() {
        return mSuccessAnimationEnabled;
    }

    /**
     * Performs a success animation on the view
     */
    private void successAnimation() {
        try {
            // Scale up and down animation for success state
            android.animation.AnimatorSet animatorSet = new android.animation.AnimatorSet();

            android.animation.ObjectAnimator scaleX = android.animation.ObjectAnimator.ofFloat(
                    this, "scaleX", 1f, 1.05f, 1f);
            android.animation.ObjectAnimator scaleY = android.animation.ObjectAnimator.ofFloat(
                    this, "scaleY", 1f, 1.05f, 1f);

            animatorSet.playTogether(scaleX, scaleY);
            animatorSet.setDuration(500);
            animatorSet.start();

            Log.d(TAG, "✅ Success animation started");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error starting success animation", e);
        }
    }

    /**
     * Performs a shake animation on the view
     */
    private void shakeAnimation() {
        try {
            android.animation.ObjectAnimator animator = android.animation.ObjectAnimator.ofFloat(
                    this, "translationX", 0, 15, -15, 15, -15, 8, -8, 0);
            animator.setDuration(700);
            animator.start();
            Log.d(TAG, "🔄 Shake animation started");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error starting shake animation", e);
        }
    }

    /**
     * Gets the line color for the specified state.
     *
     * @param states The view states to match
     * @return The color for the given states
     */
    public int getLineColorForState(int... states) {
        return mLineColor != null ? mLineColor.getColorForState(states, mCurLineColor) : mCurLineColor;
    }

    /**
     * Sets the width of the border/line for each pin item.
     *
     * @param borderWidth The width in pixels
     */
    public void setLineWidth(@Px int borderWidth) {
        mLineWidth = borderWidth;
        checkItemRadius();
        requestLayout();
        Log.d(TAG, "📏 Line width set to: " + borderWidth + "px");
    }

    /**
     * Gets the width of the border/line for each pin item.
     *
     * @return The line width in pixels
     */
    public int getLineWidth() {
        return mLineWidth;
    }

    /**
     * Sets the style of the PIN entry view.
     * <p>
     * Available view types:
     * <ul>
     *     <li>{@link #VIEW_TYPE_RECTANGLE} - Rectangle box style with borders on all sides</li>
     *     <li>{@link #VIEW_TYPE_LINE} - Line style with a border at the bottom</li>
     *     <li>{@link #VIEW_TYPE_NONE} - No borders or lines, just spaced text</li>
     *     <li>{@link #VIEW_TYPE_CIRCLE} - Circle style with round borders</li>
     * </ul>
     *
     * @param viewType The view type constant to use
     */
    public void setViewType(int viewType) {
        if (viewType < 0 || viewType > 3) {
            Log.w(TAG, "⚠️ Invalid view type: " + viewType + ", defaulting to rectangle");
            viewType = VIEW_TYPE_RECTANGLE;
        }
        mViewType = viewType;
        requestLayout();
        Log.d(TAG, "🔄 View type set to: " + viewType);
    }

    /**
     * Gets the current view type.
     *
     * @return The view type
     */
    public int getViewType() {
        return mViewType;
    }

    /**
     * Sets the number of pin items (maximum input length).
     *
     * @param count The number of pin items to display
     */
    public void setItemCount(int count) {
        mPinItemCount = count;
        setMaxLength(count);
        requestLayout();
        Log.d(TAG, "🔢 Item count set to: " + count);
    }

    /**
     * Gets the number of pin items.
     *
     * @return The number of pin items
     */
    public int getItemCount() {
        return mPinItemCount;
    }

    /**
     * Sets the corner radius for rectangular pin items or end radius for line-type items.
     *
     * @param itemRadius The radius in pixels
     */
    public void setItemRadius(@Px int itemRadius) {
        mPinItemRadius = itemRadius;
        checkItemRadius();
        requestLayout();
        Log.d(TAG, "🔘 Item radius set to: " + itemRadius + "px");
    }

    /**
     * Gets the corner radius for pin items.
     *
     * @return The radius in pixels
     */
    public int getItemRadius() {
        return mPinItemRadius;
    }

    /**
     * Sets the spacing between pin items.
     *
     * @param itemSpacing The spacing in pixels
     */
    public void setItemSpacing(@Px int itemSpacing) {
        mPinItemSpacing = itemSpacing;
        requestLayout();
        Log.d(TAG, "↔️ Item spacing set to: " + itemSpacing + "px");
    }

    /**
     * Gets the spacing between pin items.
     *
     * @return The spacing in pixels
     */
    @Px
    public int getItemSpacing() {
        return mPinItemSpacing;
    }

    /**
     * Sets the height of each pin item.
     *
     * @param itemHeight The height in pixels
     */
    public void setItemHeight(@Px int itemHeight) {
        mPinItemHeight = itemHeight;
        updateCursorHeight();
        requestLayout();
        Log.d(TAG, "↕️ Item height set to: " + itemHeight + "px");
    }

    /**
     * Gets the height of each pin item.
     *
     * @return The height in pixels
     */
    public int getItemHeight() {
        return mPinItemHeight;
    }

    /**
     * Sets the width of each pin item.
     *
     * @param itemWidth The width in pixels
     */
    public void setItemWidth(@Px int itemWidth) {
        mPinItemWidth = itemWidth;
        checkItemRadius();
        requestLayout();
        Log.d(TAG, "↔️ Item width set to: " + itemWidth + "px");
    }

    /**
     * Gets the width of each pin item.
     *
     * @return The width in pixels
     */
    public int getItemWidth() {
        return mPinItemWidth;
    }

    /**
     * Enables or disables the character input animation.
     *
     * @param enable True to enable animation, false to disable
     */
    public void setAnimationEnabled(boolean enable) {
        mAnimationEnabled = enable;
        Log.d(TAG, "🎭 Animation " + (enable ? "enabled" : "disabled"));
    }

    /**
     * Checks if animation is enabled.
     *
     * @return True if animation is enabled, false otherwise
     */
    public boolean isAnimationEnabled() {
        return mAnimationEnabled;
    }

    /**
     * Sets whether to hide the item border/line when the item is filled.
     *
     * @param hideLineWhenFilled True to hide borders/lines on filled items, false otherwise
     */
    public void setHideLineWhenFilled(boolean hideLineWhenFilled) {
        this.mHideLineWhenFilled = hideLineWhenFilled;
        Log.d(TAG, "🙈 Hide line when filled: " + hideLineWhenFilled);
    }

    /**
     * Checks if hiding line when filled is enabled.
     *
     * @return True if hide line when filled is enabled, false otherwise
     */
    public boolean isHideLineWhenFilled() {
        return mHideLineWhenFilled;
    }

    /**
     * Sets the background drawable resource for each pin item.
     *
     * @param resId The resource ID of the drawable
     */
    public void setItemBackgroundResources(@DrawableRes int resId) {
        if (resId == 0) {
            mItemBackground = null;
            invalidate();
            return;
        }

        try {
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), resId, getContext().getTheme());
            if (drawable != null) {
                mItemBackground = drawable;
                invalidate();
                Log.d(TAG, "🖼️ Item background resource set: " + resId);
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "🚫 Resource not found: " + resId);
        }
    }

    /**
     * Sets the background color for each pin item.
     *
     * @param color The color to use
     */
    public void setItemBackgroundColor(@ColorInt int color) {
        if (mItemBackground instanceof ColorDrawable) {
            ((ColorDrawable) mItemBackground.mutate()).setColor(color);
        } else {
            setItemBackground(new ColorDrawable(color));
        }
        Log.d(TAG, "🎨 Item background color set to: #" + Integer.toHexString(0xFFFFFF & color));
    }

    /**
     * Sets the background drawable for each pin item.
     *
     * @param background The drawable to use
     */
    public void setItemBackground(Drawable background) {
        mItemBackground = background;
        invalidate();
        Log.d(TAG, "🖼️ Item background set");
    }

    /**
     * Gets the item background drawable.
     *
     * @return The background drawable
     */
    public Drawable getItemBackground() {
        return mItemBackground;
    }

    /**
     * Sets the width of the cursor.
     *
     * @param width The width in pixels
     */
    public void setCursorWidth(@Px int width) {
        mCursorWidth = width;
        if (isCursorVisible()) {
            invalidateCursor(true);
        }
        Log.d(TAG, "📏 Cursor width set to: " + width + "px");
    }

    /**
     * Gets the width of the cursor.
     *
     * @return The width in pixels
     */
    public int getCursorWidth() {
        return mCursorWidth;
    }

    /**
     * Sets the color of the cursor.
     *
     * @param color The color to use
     */
    public void setCursorColor(@ColorInt int color) {
        mCursorColor = color;
        mCursorColorSet = true;
        if (isCursorVisible()) {
            invalidateCursor(true);
        }
        Log.d(TAG, "🎨 Cursor color set to: #" + Integer.toHexString(0xFFFFFF & color));
    }

    /**
     * Gets the color of the cursor.
     *
     * @return The cursor color
     */
    public int getCursorColor() {
        return mCursorColor;
    }

    /**
     * Checks if cursor color has been explicitly set.
     *
     * @return True if cursor color has been set, false otherwise
     */
    public boolean isCursorColorSet() {
        return mCursorColorSet;
    }

    /**
     * Gets the debug flag.
     *
     * @return True if debug mode is on, false otherwise
     */
    public boolean isDebug() {
        return DBG;
    }

    /**
     * Gets the length of the current text.
     *
     * @return The length of the text
     */
    public int getLength() {
        return getText() == null ? 0 : getText().length();
    }

    /**
     * Customize the keyboard action button (Done, Go, Next, etc.)
     *
     * @param imeOptions The desired IME options
     */
    public void setKeyboardActionButton(int imeOptions) {
        setImeOptions(imeOptions);
    }

    /**
     * Sets whether the keyboard should be dismissed when complete
     *
     * @param dismissOnComplete True to automatically dismiss keyboard when PIN is complete
     */
    public void setDismissKeyboardOnComplete(boolean dismissOnComplete) {
        if (dismissOnComplete) {
            setSmartKeyboardBehavior(true);
        } else {
            // Remove text watchers that might auto-dismiss
            TextWatcher[] watchers = getTag() instanceof TextWatcher[] ?
                    (TextWatcher[]) getTag() : null;
            if (watchers != null) {
                for (TextWatcher watcher : watchers) {
                    removeTextChangedListener(watcher);
                }
            }
        }
    }

    /**
     * Alternative method to handle keyboard showing/hiding based on whether the PIN is complete.
     * This gives more control over keyboard behavior.
     */
    public void setSmartKeyboardBehavior(boolean enabled) {
        if (enabled) {
            // Add text watcher to automatically hide keyboard when PIN is complete
            addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Not needed
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Not needed - handled in the main onTextChanged
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == mPinItemCount) {
                        // PIN is complete, hide keyboard after a short delay
                        postDelayed(() -> hideKeyboard(), 200);
                    }
                }
            });
        }
    }

    //=====================================================================
    // INTERNAL UTILITY METHODS
    //=====================================================================
    /**
     * Moves the cursor to the end of the text.
     */
    private void moveSelectionToEnd() {
        setSelection(getLength());
    }

    /**
     * Shows the keyboard if autoFocus is enabled
     */
    private void showKeyboard() {
        // Don't show keyboard if autoFocus is disabled
        if (!mAutoFocus && !isFocused()) {
            Log.d(TAG, "🔍 Ignoring keyboard request - autoFocus disabled and view not focused");
            return;
        }

        // Request focus first
        requestFocus();

        // Show keyboard with a slight delay to ensure focus is established
        postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);
    }

    /**
     * Explicitly hides the keyboard.
     * Can be called from parent activity/fragment when needed.
     */
    public void hideKeyboard() {
        clearFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }

    /**
     * Updates colors based on the current state.
     */
    private void updateColors() {
        boolean inval = false;

        int color;
        if (mLineColor != null) {
            color = mLineColor.getColorForState(getDrawableState(), 0);
        } else {
            color = getCurrentTextColor();
        }

        if (color != mCurLineColor) {
            mCurLineColor = color;
            inval = true;
        }

        if (inval) {
            invalidate();
        }
    }

    /**
     * Checks if the cursor should blink.
     * Now respects autoFocus setting.
     *
     * @return True if the cursor should blink, false otherwise
     */
    private boolean shouldBlink() {
        return isCursorVisible() && isFocused();
    }

    /**
     * Sets up cursor blinking.
     */
    private void makeBlink() {
        if (shouldBlink()) {
            if (mBlink == null) {
                mBlink = new PinViewBlink(this);
            }
            removeCallbacks(mBlink);
            mDrawCursor = false;
            postDelayed(mBlink, BLINK);
        } else {
            if (mBlink != null) {
                removeCallbacks(mBlink);
            }
        }
    }

    /**
     * Suspends cursor blinking.
     */
    private void suspendBlink() {
        if (mBlink != null) {
            mBlink.cancel();
            invalidateCursor(false);
        }
    }

    /**
     * Resumes cursor blinking.
     */
    private void resumeBlink() {
        if (mBlink != null) {
            mBlink.uncancel();
            makeBlink();
        }
    }

    /**
     * Invalidates the cursor display.
     *
     * @param showCursor Whether to show the cursor
     */
    public void invalidateCursor(boolean showCursor) {
        if (mDrawCursor != showCursor) {
            mDrawCursor = showCursor;
            invalidate();
        }
    }

    /**
     * Updates the cursor height based on current text size.
     */
    private void updateCursorHeight() {
        int delta = 2 * PinViewUtils.dpToPx(getContext(), 2);
        mCursorHeight = mPinItemHeight - getTextSize() > delta ? getTextSize() + delta : getTextSize();
    }

}