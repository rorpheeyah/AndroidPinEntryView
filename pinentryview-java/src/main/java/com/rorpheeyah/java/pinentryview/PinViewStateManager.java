package com.rorpheeyah.java.pinentryview;

import android.graphics.Color;
import android.util.Log;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages different visual states for the PinEntryView.
 * This centralizes state management and reduces code duplication.
 */
public class PinViewStateManager {

    private static final String TAG = "PinViewStateManager";

    // State storage
    private final Map<PinViewState.Type, PinViewState> mStates = new HashMap<>();
    private PinViewState mCurrentState;
    private final PinEntryView mView;

    // Default colors for fallback
    private int mDefaultLineColor;
    private int mDefaultTextColor;
    private int mDefaultBackgroundColor;

    /**
     * Creates a new state manager for the given view
     */
    public PinViewStateManager(@NonNull PinEntryView view) {
        mView = view;

        // Initialize with default normal state
        mCurrentState = PinViewState.createNormalState();
        mStates.put(PinViewState.Type.NORMAL, mCurrentState);

        // Store default colors with null safety
        try {
            mDefaultLineColor = view.getCurrentLineColor();
        } catch (Exception e) {
            mDefaultLineColor = Color.BLACK;
        }

        try {
            mDefaultTextColor = view.getCurrentTextColor();
        } catch (Exception e) {
            mDefaultTextColor = Color.BLACK;
        }

        try {
            mDefaultBackgroundColor = view.getItemBackgroundColor();
        } catch (Exception e) {
            mDefaultBackgroundColor = Color.TRANSPARENT;
        }

        Log.d(TAG, "🎛️ StateManager initialized");
    }

    /**
     * Sets the state configuration for a specific state type
     */
    public void configureState(@NonNull PinViewState.Type type, @NonNull PinViewState state) {
        if (state.getType() != type) {
            throw new IllegalArgumentException("State type mismatch: expected " + type + ", got " + state.getType());
        }

        mStates.put(type, state);
        Log.d(TAG, "🎨 Configured state: " + state);

        // If this is the current state, apply it immediately  
        if (mCurrentState.getType() == type) {
            applyCurrentState();
        }
    }

    /**
     * Transitions to the specified state
     */
    public void setState(@NonNull PinViewState.Type type) {
        PinViewState newState = mStates.get(type);
        if (newState == null) {
            // Create default state if not configured
            newState = createDefaultState(type);
            mStates.put(type, newState);
        }

        if (mCurrentState.getType() != type) {
            PinViewState.Type previousType = mCurrentState.getType();
            mCurrentState = newState;

            applyCurrentState();
            handleStateTransition(previousType, type);

            Log.d(TAG, "🔄 State transition: " + previousType + " -> " + type);
        }
    }

    /**
     * Gets the current state
     */
    @NonNull
    public PinViewState getCurrentState() {
        return mCurrentState;
    }

    /**
     * Gets the current state type
     */
    @NonNull
    public PinViewState.Type getCurrentStateType() {
        return mCurrentState.getType();
    }

    /**
     * Checks if currently in the specified state
     */
    public boolean isInState(@NonNull PinViewState.Type type) {
        return mCurrentState.getType() == type;
    }

    /**
     * Gets the configured state for a type, or null if not configured
     */
    @Nullable
    public PinViewState getState(@NonNull PinViewState.Type type) {
        return mStates.get(type);
    }

    /**
     * Gets the active line color based on current state
     */
    @ColorInt
    public int getActiveLineColor() {
        if (mCurrentState.hasLineColor()) {
            return mCurrentState.getLineColor();
        }
        return mDefaultLineColor;
    }

    /**
     * Gets the active text color based on current state
     */
    @ColorInt
    public int getActiveTextColor() {
        if (mCurrentState.hasTextColor()) {
            return mCurrentState.getTextColor();
        }
        return mDefaultTextColor;
    }

    /**
     * Gets the active background color based on current state
     */
    @ColorInt
    public int getActiveBackgroundColor() {
        if (mCurrentState.hasBackgroundColor()) {
            return mCurrentState.getBackgroundColor();
        }
        return mDefaultBackgroundColor;
    }

    /**
     * Updates default colors (called when view properties change)
     */
    public void updateDefaults(int lineColor, int textColor, int backgroundColor) {
        mDefaultLineColor = lineColor;
        mDefaultTextColor = textColor;
        mDefaultBackgroundColor = backgroundColor;
    }

    /**
     * Applies the current state to the view
     */
    private void applyCurrentState() {
        // Update view appearance
        mView.invalidate();
    }

    /**
     * Handles state transition animations and effects
     */
    private void handleStateTransition(@NonNull PinViewState.Type from, @NonNull PinViewState.Type to) {
        PinViewState newState = mStates.get(to);
        if (newState == null) return;

        // Handle animations based on the new state
        if (to == PinViewState.Type.ERROR && newState.isShakeEnabled()) {
            mView.shakeAnimation();
        } else if (to == PinViewState.Type.SUCCESS && newState.isAnimationEnabled()) {
            mView.successAnimation();
        }

        // Clear other states when transitioning
        if (to == PinViewState.Type.ERROR) {
            // Clear success when setting error
        } else if (to == PinViewState.Type.SUCCESS) {
            // Clear error when setting success
        }
    }

    /**
     * Creates a default state for the given type
     */
    @NonNull
    private PinViewState createDefaultState(@NonNull PinViewState.Type type) {
        switch (type) {
            case ERROR:
                return PinViewState.createErrorState();
            case SUCCESS:
                return PinViewState.createSuccessState();
            case NORMAL:
            default:
                return PinViewState.createNormalState();
        }
    }

    /**
     * Convenience method to set error state
     */
    public void setError(boolean error) {
        if (error) {
            setState(PinViewState.Type.ERROR);
        } else if (isInState(PinViewState.Type.ERROR)) {
            setState(PinViewState.Type.NORMAL);
        }
    }

    /**
     * Convenience method to set success state
     */
    public void setSuccess(boolean success) {
        if (success) {
            setState(PinViewState.Type.SUCCESS);
        } else if (isInState(PinViewState.Type.SUCCESS)) {
            setState(PinViewState.Type.NORMAL);
        }
    }

    /**
     * Checks if currently in error state
     */
    public boolean isError() {
        return isInState(PinViewState.Type.ERROR);
    }

    /**
     * Checks if currently in success state
     */
    public boolean isSuccess() {
        return isInState(PinViewState.Type.SUCCESS);
    }

    // Convenience methods for backward compatibility

    /**
     * Gets the error color from the error state configuration
     */
    @ColorInt
    public int getErrorColor() {
        PinViewState errorState = getState(PinViewState.Type.ERROR);
        return errorState != null && errorState.hasLineColor() ? errorState.getLineColor() : -1;
    }

    /**
     * Gets the success color from the success state configuration
     */
    @ColorInt
    public int getSuccessColor() {
        PinViewState successState = getState(PinViewState.Type.SUCCESS);
        return successState != null && successState.hasLineColor() ? successState.getLineColor() : -1;
    }

    /**
     * Gets the error text color from the error state configuration
     */
    @ColorInt
    public int getErrorTextColor() {
        PinViewState errorState = getState(PinViewState.Type.ERROR);
        return errorState != null && errorState.hasTextColor() ? errorState.getTextColor() : -1;
    }

    /**
     * Gets the success text color from the success state configuration
     */
    @ColorInt
    public int getSuccessTextColor() {
        PinViewState successState = getState(PinViewState.Type.SUCCESS);
        return successState != null && successState.hasTextColor() ? successState.getTextColor() : -1;
    }

    /**
     * Gets the error background color from the error state configuration
     */
    @ColorInt
    public int getErrorBackgroundColor() {
        PinViewState errorState = getState(PinViewState.Type.ERROR);
        return errorState != null && errorState.hasBackgroundColor() ? errorState.getBackgroundColor() : -1;
    }

    /**
     * Gets the success background color from the success state configuration
     */
    @ColorInt
    public int getSuccessBackgroundColor() {
        PinViewState successState = getState(PinViewState.Type.SUCCESS);
        return successState != null && successState.hasBackgroundColor() ? successState.getBackgroundColor() : -1;
    }

    /**
     * Checks if success state is enabled (for backward compatibility)
     */
    public boolean isSuccessEnabled() {
        return getState(PinViewState.Type.SUCCESS) != null;
    }

    /**
     * Sets error color in the error state configuration
     */
    public void setErrorColor(@ColorInt int color) {
        PinViewState errorState = getState(PinViewState.Type.ERROR);
        if (errorState == null) {
            errorState = PinViewState.createErrorState();
            configureState(PinViewState.Type.ERROR, errorState);
        }
        errorState.setLineColor(color);
    }

    /**
     * Sets success color in the success state configuration
     */
    public void setSuccessColor(@ColorInt int color) {
        PinViewState successState = getState(PinViewState.Type.SUCCESS);
        if (successState == null) {
            successState = PinViewState.createSuccessState();
            configureState(PinViewState.Type.SUCCESS, successState);
        }
        successState.setLineColor(color);
    }

    /**
     * Sets error text color in the error state configuration
     */
    public void setErrorTextColor(@ColorInt int color) {
        PinViewState errorState = getState(PinViewState.Type.ERROR);
        if (errorState == null) {
            errorState = PinViewState.createErrorState();
            configureState(PinViewState.Type.ERROR, errorState);
        }
        errorState.setTextColor(color);
    }

    /**
     * Sets success text color in the success state configuration
     */
    public void setSuccessTextColor(@ColorInt int color) {
        PinViewState successState = getState(PinViewState.Type.SUCCESS);
        if (successState == null) {
            successState = PinViewState.createSuccessState();
            configureState(PinViewState.Type.SUCCESS, successState);
        }
        successState.setTextColor(color);
    }

    /**
     * Sets error background color in the error state configuration
     */
    public void setErrorBackgroundColor(@ColorInt int color) {
        PinViewState errorState = getState(PinViewState.Type.ERROR);
        if (errorState == null) {
            errorState = PinViewState.createErrorState();
            configureState(PinViewState.Type.ERROR, errorState);
        }
        errorState.setBackgroundColor(color);
    }

    /**
     * Sets success background color in the success state configuration
     */
    public void setSuccessBackgroundColor(@ColorInt int color) {
        PinViewState successState = getState(PinViewState.Type.SUCCESS);
        if (successState == null) {
            successState = PinViewState.createSuccessState();
            configureState(PinViewState.Type.SUCCESS, successState);
        }
        successState.setBackgroundColor(color);
    }

    /**
     * Sets shake animation enabled for error state
     */
    public void setErrorShakeEnabled(boolean enabled) {
        PinViewState errorState = getState(PinViewState.Type.ERROR);
        if (errorState == null) {
            errorState = PinViewState.createErrorState();
            configureState(PinViewState.Type.ERROR, errorState);
        }
        errorState.setShakeEnabled(enabled);
    }

    /**
     * Checks if shake animation is enabled for error state
     */
    public boolean isErrorShakeEnabled() {
        PinViewState errorState = getState(PinViewState.Type.ERROR);
        return errorState != null && errorState.isShakeEnabled();
    }

    /**
     * Sets success animation enabled
     */
    public void setSuccessAnimationEnabled(boolean enabled) {
        PinViewState successState = getState(PinViewState.Type.SUCCESS);
        if (successState == null) {
            successState = PinViewState.createSuccessState();
            configureState(PinViewState.Type.SUCCESS, successState);
        }
        successState.setAnimationEnabled(enabled);
    }

    /**
     * Checks if success animation is enabled
     */
    public boolean isSuccessAnimationEnabled() {
        PinViewState successState = getState(PinViewState.Type.SUCCESS);
        return successState != null && successState.isAnimationEnabled();
    }
}
