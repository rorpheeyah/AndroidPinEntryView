package com.rorpheeyah.java.pinentryview;

import androidx.annotation.ColorInt;

/**
 * Represents different visual states for the PinEntryView.
 * This consolidates error, success, and normal states into a unified system.
 */
public class PinViewState {

    /**
     * State types for the PIN entry view
     */
    public enum Type {
        NORMAL,
        ERROR,
        SUCCESS
    }

    // State properties
    private final Type mType;
    private int mLineColor = -1;
    private int mTextColor = -1;
    private int mBackgroundColor = -1;
    private boolean mAnimationEnabled = false;
    private boolean mShakeEnabled = false;

    /**
     * Creates a new PinViewState
     *
     * @param type The state type
     */
    public PinViewState(Type type) {
        this.mType = type;
    }

    /**
     * Gets the state type
     */
    public Type getType() {
        return mType;
    }

    /**
     * Sets the line/border color for this state
     */
    public PinViewState setLineColor(@ColorInt int color) {
        mLineColor = color;
        return this;
    }

    /**
     * Gets the line/border color for this state
     */
    @ColorInt
    public int getLineColor() {
        return mLineColor;
    }

    /**
     * Checks if line color is set
     */
    public boolean hasLineColor() {
        return mLineColor != -1;
    }

    /**
     * Sets the text color for this state
     */
    public PinViewState setTextColor(@ColorInt int color) {
        mTextColor = color;
        return this;
    }

    /**
     * Gets the text color for this state
     */
    @ColorInt
    public int getTextColor() {
        return mTextColor;
    }

    /**
     * Checks if text color is set
     */
    public boolean hasTextColor() {
        return mTextColor != -1;
    }

    /**
     * Sets the background color for this state
     */
    public PinViewState setBackgroundColor(@ColorInt int color) {
        mBackgroundColor = color;
        return this;
    }

    /**
     * Gets the background color for this state
     */
    @ColorInt
    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    /**
     * Checks if background color is set
     */
    public boolean hasBackgroundColor() {
        return mBackgroundColor != -1;
    }

    /**
     * Sets whether animation is enabled for this state
     */
    public PinViewState setAnimationEnabled(boolean enabled) {
        mAnimationEnabled = enabled;
        return this;
    }

    /**
     * Gets whether animation is enabled for this state
     */
    public boolean isAnimationEnabled() {
        return mAnimationEnabled;
    }

    /**
     * Sets whether shake animation is enabled (typically for error state)
     */
    public PinViewState setShakeEnabled(boolean enabled) {
        mShakeEnabled = enabled;
        return this;
    }

    /**
     * Gets whether shake animation is enabled
     */
    public boolean isShakeEnabled() {
        return mShakeEnabled;
    }

    /**
     * Creates a default error state
     */
    public static PinViewState createErrorState() {
        return new PinViewState(Type.ERROR)
                .setShakeEnabled(true);
    }

    /**
     * Creates a default success state
     */
    public static PinViewState createSuccessState() {
        return new PinViewState(Type.SUCCESS)
                .setAnimationEnabled(true);
    }

    /**
     * Creates a normal state
     */
    public static PinViewState createNormalState() {
        return new PinViewState(Type.NORMAL);
    }

    @Override
    public String toString() {
        return "PinViewState{" +
                "type=" + mType +
                ", lineColor=" + (mLineColor != -1 ? "#" + Integer.toHexString(0xFFFFFF & mLineColor) : "not set") +
                ", textColor=" + (mTextColor != -1 ? "#" + Integer.toHexString(0xFFFFFF & mTextColor) : "not set") +
                ", backgroundColor=" + (mBackgroundColor != -1 ? "#" + Integer.toHexString(0xFFFFFF & mBackgroundColor) : "not set") +
                ", animationEnabled=" + mAnimationEnabled +
                ", shakeEnabled=" + mShakeEnabled +
                '}';
    }
}