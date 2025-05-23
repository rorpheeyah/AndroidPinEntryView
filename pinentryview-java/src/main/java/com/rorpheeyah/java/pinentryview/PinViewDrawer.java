package com.rorpheeyah.java.pinentryview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;

/**
 * Handles drawing operations for the PinEntryView.
 * Separates drawing logic from main view class.
 */
public class PinViewDrawer {
    private static final String TAG = PinViewDrawer.class.getSimpleName();
    private static final int[] HIGHLIGHT_STATES = new int[]{ android.R.attr.state_selected };

    private final PinEntryView mView;
    private final Paint mPaint;
    private final TextPaint mAnimatorTextPaint;
    private final Rect mTextRect;
    private final RectF mItemBorderRect;
    private final RectF mItemLineRect;
    private final Path mPath;
    private final PointF mItemCenterPoint;

    /**
     * Creates a new PinViewDrawer.
     *
     * @param view The PinEntryView this drawer will draw for
     * @param paint The paint for drawing
     * @param animatorTextPaint The text paint for animation
     */
    public PinViewDrawer(PinEntryView view, Paint paint, TextPaint animatorTextPaint) {
        mView = view;
        mPaint = paint;
        mAnimatorTextPaint = animatorTextPaint;
        mTextRect = new Rect();
        mItemBorderRect = new RectF();
        mItemLineRect = new RectF();
        mPath = new Path();
        mItemCenterPoint = new PointF();
        Log.v(TAG, "🎨 PinViewDrawer initialized");
    }

    /**
     * Main drawing method for the PIN view.
     *
     * @param canvas The canvas to draw on
     */
    public void drawPinView(Canvas canvas) {
        try {
            int highlightIdx = mView.getLength();
            for (int i = 0; i < mView.getItemCount(); i++) {
                boolean highlight = mView.isFocused() && highlightIdx == i;
                mPaint.setColor(highlight ?
                        mView.getLineColorForState(HIGHLIGHT_STATES) :
                        mView.getCurrentLineColor());

                updateItemRectF(i);
                updateCenterPoint();

                int saveCount = canvas.save();
                try {
                    if (mView.getViewType() == PinEntryView.VIEW_TYPE_RECTANGLE) {
                        updatePinBoxPath(i);
                        canvas.clipPath(mPath);
                    } else if (mView.getViewType() == PinEntryView.VIEW_TYPE_CIRCLE) {
                        Path circlePath = new Path();
                        float cx = mItemCenterPoint.x;
                        float cy = mItemCenterPoint.y;
                        float radius = Math.min(mItemBorderRect.width() / 2, mItemBorderRect.height() / 2);
                        circlePath.addCircle(cx, cy, radius, Path.Direction.CW);
                        canvas.clipPath(circlePath);
                    }
                    drawItemBackground(canvas, highlight);
                } catch (Exception e) {
                    Log.e(TAG, "⚠️ Error clipping path or drawing background", e);
                } finally {
                    canvas.restoreToCount(saveCount);
                }

                if (highlight) {
                    drawCursor(canvas);
                }

                if (mView.getViewType() == PinEntryView.VIEW_TYPE_RECTANGLE) {
                    drawPinBox(canvas, i);
                } else if (mView.getViewType() == PinEntryView.VIEW_TYPE_LINE) {
                    drawPinLine(canvas, i);
                } else if (mView.getViewType() == PinEntryView.VIEW_TYPE_CIRCLE) {
                    drawPinCircle(canvas, i);
                }

                if (mView.isDebug()) {
                    drawAnchorLine(canvas);
                }

                String transformed = mView.getTransformedText();
                if (transformed != null && transformed.length() > i) {
                    if (mView.getTransformationMethod() == null && mView.isPasswordHidden()) {
                        drawCircle(canvas, i);
                    } else {
                        drawText(canvas, i);
                    }
                } else if (mView.getHint() != null &&
                        !TextUtils.isEmpty(mView.getHint())) {
                    // Draw hint regardless of viewType and without strict length matching
                    drawHint(canvas, i);
                }
            }

            // Highlight the next item
            if (mView.isFocused() &&
                    mView.getLength() != mView.getItemCount() &&
                    mView.getViewType() == PinEntryView.VIEW_TYPE_RECTANGLE) {
                try {
                    int index = mView.getLength();
                    updateItemRectF(index);
                    updateCenterPoint();
                    updatePinBoxPath(index);
                    mPaint.setColor(mView.getLineColorForState(HIGHLIGHT_STATES));
                    drawPinBox(canvas, index);
                } catch (Exception e) {
                    Log.e(TAG, "⚠️ Error highlighting next item", e);
                }
            } else if (mView.isFocused() &&
                    mView.getLength() != mView.getItemCount() &&
                    mView.getViewType() == PinEntryView.VIEW_TYPE_CIRCLE) {
                try {
                    int index = mView.getLength();
                    updateItemRectF(index);
                    updateCenterPoint();
                    mPaint.setColor(mView.getLineColorForState(HIGHLIGHT_STATES));
                    drawPinCircle(canvas, index);
                } catch (Exception e) {
                    Log.e(TAG, "⚠️ Error highlighting next circle item", e);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "🚫 Error drawing pin view", e);
        }
    }

    /**
     * Updates the paint colors and styles before drawing.
     */
    public void updatePaints() {
        mPaint.setColor(mView.getCurrentLineColor());
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mView.getLineWidth());
        mView.getPaint().setColor(mView.getActiveTextColor());
    }

    /**
     * Draws the background for a PIN item.
     *
     * @param canvas The canvas to draw on
     * @param highlight Whether the item should be highlighted
     */
    private void drawItemBackground(Canvas canvas, boolean highlight) {
        Drawable itemBackground = mView.getItemBackground();
        if (itemBackground == null) {
            return;
        }
        float delta = (float) mView.getLineWidth() / 2;
        int left = Math.round(mItemBorderRect.left - delta);
        int top = Math.round(mItemBorderRect.top - delta);
        int right = Math.round(mItemBorderRect.right + delta);
        int bottom = Math.round(mItemBorderRect.bottom + delta);

        itemBackground.setBounds(left, top, right, bottom);
        itemBackground.setState(highlight ? HIGHLIGHT_STATES : mView.getDrawableState());
        itemBackground.draw(canvas);
    }

    /**
     * Updates the path for drawing a PIN box.
     *
     * @param i The index of the PIN item
     */
    private void updatePinBoxPath(int i) {
        boolean drawRightCorner = false;
        boolean drawLeftCorner = false;
        if (mView.getItemSpacing() != 0) {
            drawLeftCorner = drawRightCorner = true;
        } else {
            if (i == 0 && i != mView.getItemCount() - 1) {
                drawLeftCorner = true;
            }
            if (i == mView.getItemCount() - 1 && i != 0) {
                drawRightCorner = true;
            }
        }
        updateRoundRectPath(mItemBorderRect, mView.getItemRadius(),
                mView.getItemRadius(), drawLeftCorner, drawRightCorner);
    }

    /**
     * Draws the rectangular box for a PIN item.
     *
     * @param canvas The canvas to draw on
     * @param i The index of the PIN item
     */
    private void drawPinBox(Canvas canvas, int i) {
        if (mView.isHideLineWhenFilled() && i < mView.getLength()) {
            return;
        }
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * Draws the line for a PIN item.
     *
     * @param canvas The canvas to draw on
     * @param i The index of the PIN item
     */
    private void drawPinLine(Canvas canvas, int i) {
        if (mView.isHideLineWhenFilled() && i < mView.getLength()) {
            return;
        }
        boolean l, r;
        l = r = true;
        if (mView.getItemSpacing() == 0 && mView.getItemCount() > 1) {
            if (i == 0) {
                // draw only left round
                r = false;
            } else if (i == mView.getItemCount() - 1) {
                // draw only right round
                l = false;
            } else {
                // draw rect
                l = r = false;
            }
        }
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(((float) mView.getLineWidth()) / 10);
        float halfLineWidth = ((float) mView.getLineWidth()) / 2;
        mItemLineRect.set(
                mItemBorderRect.left - halfLineWidth,
                mItemBorderRect.bottom - halfLineWidth,
                mItemBorderRect.right + halfLineWidth,
                mItemBorderRect.bottom + halfLineWidth);

        updateRoundRectPath(mItemLineRect, mView.getItemRadius(), mView.getItemRadius(), l, r);
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * Draws the circle for a PIN item.
     *
     * @param canvas The canvas to draw on
     * @param i The index of the PIN item
     */
    private void drawPinCircle(Canvas canvas, int i) {
        if (mView.isHideLineWhenFilled() && i < mView.getLength()) {
            return;
        }

        float cx = mItemCenterPoint.x;
        float cy = mItemCenterPoint.y;

        // Use the minimum of width/height to ensure a perfect circle
        float radius = Math.min(
                mItemBorderRect.width() / 2,
                mItemBorderRect.height() / 2
        );

        // Account for line width
        radius -= mView.getLineWidth() / 2;

        // Draw the circle
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(cx, cy, radius, mPaint);
    }

    /**
     * Draws the cursor at the current position.
     *
     * @param canvas The canvas to draw on
     */
    private void drawCursor(Canvas canvas) {
        if (mView.drawCursor()) {
            float cx = mItemCenterPoint.x;
            float cy = mItemCenterPoint.y;
            float x = cx;
            float y = cy - mView.getCursorHeight() / 2;

            int color = mPaint.getColor();
            float width = mPaint.getStrokeWidth();
            mPaint.setColor(mView.getCursorColor());
            mPaint.setStrokeWidth(mView.getCursorWidth());

            canvas.drawLine(x, y, x, y + mView.getCursorHeight(), mPaint);

            mPaint.setColor(color);
            mPaint.setStrokeWidth(width);
        }
    }

    /**
     * Updates the rounded rectangle path.
     *
     * @param rectF The rectangle to draw
     * @param rx The x-radius of the rounded corners
     * @param ry The y-radius of the rounded corners
     * @param l Whether to round the left corners
     * @param r Whether to round the right corners
     */
    private void updateRoundRectPath(RectF rectF, float rx, float ry, boolean l, boolean r) {
        updateRoundRectPath(rectF, rx, ry, l, r, r, l);
    }

    /**
     * Updates the rounded rectangle path with specific corner options.
     *
     * @param rectF The rectangle to draw
     * @param rx The x-radius of the rounded corners
     * @param ry The y-radius of the rounded corners
     * @param tl Whether to round the top-left corner
     * @param tr Whether to round the top-right corner
     * @param br Whether to round the bottom-right corner
     * @param bl Whether to round the bottom-left corner
     */
    private void updateRoundRectPath(RectF rectF, float rx, float ry,
                                     boolean tl, boolean tr, boolean br, boolean bl) {
        mPath.reset();

        if (rectF == null) {
            Log.e(TAG, "🚫 Invalid rectF");
            return;
        }

        float l = rectF.left;
        float t = rectF.top;
        float r = rectF.right;
        float b = rectF.bottom;

        float w = r - l;
        float h = b - t;

        float lw = w - 2 * rx;// line width
        float lh = h - 2 * ry;// line height

        mPath.moveTo(l, t + ry);

        if (tl) {
            mPath.rQuadTo(0, -ry, rx, -ry);// top-left corner
        } else {
            mPath.rLineTo(0, -ry);
            mPath.rLineTo(rx, 0);
        }

        mPath.rLineTo(lw, 0);

        if (tr) {
            mPath.rQuadTo(rx, 0, rx, ry);// top-right corner
        } else {
            mPath.rLineTo(rx, 0);
            mPath.rLineTo(0, ry);
        }

        mPath.rLineTo(0, lh);

        if (br) {
            mPath.rQuadTo(0, ry, -rx, ry);// bottom-right corner
        } else {
            mPath.rLineTo(0, ry);
            mPath.rLineTo(-rx, 0);
        }

        mPath.rLineTo(-lw, 0);

        if (bl) {
            mPath.rQuadTo(-rx, 0, -rx, -ry);// bottom-left corner
        } else {
            mPath.rLineTo(-rx, 0);
            mPath.rLineTo(0, -ry);
        }

        mPath.rLineTo(0, -lh);

        mPath.close();
    }

    /**
     * Updates the rectangle for a PIN item.
     *
     * @param i The index of the PIN item
     */
    private void updateItemRectF(int i) {
        float halfLineWidth = ((float) mView.getLineWidth()) / 2;
        
        // Calculate total width needed for all items
        float itemTotalWidth = mView.getItemWidth() * mView.getItemCount() + 
                mView.getItemSpacing() * (mView.getItemCount() - 1);
        if (mView.getItemSpacing() == 0) {
            itemTotalWidth -= mView.getLineWidth() * (mView.getItemCount() - 1);
        }
        
        // Calculate starting X based on gravity
        float startX;
        int viewWidth = mView.getWidth() - mView.getPaddingStart() - mView.getPaddingEnd();
        
        switch (mView.getGravity()) {
            case PinEntryView.GRAVITY_START:
                startX = mView.getScrollX() + mView.getPaddingStart();
                break;
            case PinEntryView.GRAVITY_END:
                startX = mView.getScrollX() + mView.getWidth() - mView.getPaddingEnd() - itemTotalWidth;
                break;
            case PinEntryView.GRAVITY_CENTER:
            default:
                startX = mView.getScrollX() + mView.getPaddingStart() + (viewWidth - itemTotalWidth) / 2;
                break;
        }
        
        // Calculate item position
        float left = startX + i * (mView.getItemSpacing() + mView.getItemWidth()) + halfLineWidth;
        if (mView.getItemSpacing() == 0 && i > 0) {
            left = left - (mView.getLineWidth()) * i;
        }
        float right = left + mView.getItemWidth() - mView.getLineWidth();
        float top = mView.getScrollY() + mView.getPaddingTop() + halfLineWidth;
        float bottom = top + mView.getItemHeight() - mView.getLineWidth();

        mItemBorderRect.set(left, top, right, bottom);
    }

    /**
     * Draws text for a PIN item.
     *
     * @param canvas The canvas to draw on
     * @param i The index of the PIN item
     */
    private void drawText(Canvas canvas, int i) {
        Paint paint = getPaintByIndex(i);
        drawTextAtBox(canvas, paint, mView.getTransformedText(), i);
    }

    /**
     * Draws hint text for a PIN item.
     *
     * @param canvas The canvas to draw on
     * @param i The index of the PIN item
     */
    private void drawHint(Canvas canvas, int i) {
        if (mView.getHint() == null) {
            return;
        }

        // Make sure we don't try to draw beyond the hint length
        if (i >= mView.getHint().length()) {
            return;
        }

        Paint paint = getPaintByIndex(i);
        paint.setColor(mView.getCurrentHintTextColor());
        drawTextAtBox(canvas, paint, mView.getHint(), i);
    }

    /**
     * Draws text at a specific PIN item position.
     *
     * @param canvas The canvas to draw on
     * @param paint The paint to use for drawing
     * @param text The text to draw
     * @param charAt The index of the character to draw
     */
    private void drawTextAtBox(Canvas canvas, Paint paint, CharSequence text, int charAt) {
        if (text == null || charAt >= text.length()) {
            return;
        }
        paint.getTextBounds(text.toString(), charAt, charAt + 1, mTextRect);
        float cx = mItemCenterPoint.x;
        float cy = mItemCenterPoint.y;
        float x = cx - Math.abs((float) mTextRect.width()) / 2 - mTextRect.left;
        float y = cy + Math.abs((float) mTextRect.height()) / 2 - mTextRect.bottom;// always center vertical
        canvas.drawText(text, charAt, charAt + 1, x, y, paint);
    }

    /**
     * Draws a circle for password masking.
     *
     * @param canvas The canvas to draw on
     * @param i The index of the PIN item
     */
    private void drawCircle(Canvas canvas, int i) {
        if (canvas == null) return;

        Paint paint = getPaintByIndex(i);
        float cx = mItemCenterPoint.x;
        float cy = mItemCenterPoint.y;
        canvas.drawCircle(cx, cy, paint.getTextSize() / 2, paint);
    }

    /**
     * Gets the appropriate paint for a PIN item based on its index.
     *
     * @param i The index of the PIN item
     * @return The paint to use
     */
    private Paint getPaintByIndex(int i) {
        if (mView.isAnimationEnabled() && i == mView.getLength() - 1) {
            mAnimatorTextPaint.setColor(mView.getPaint().getColor());
            return mAnimatorTextPaint;
        } else {
            return mView.getPaint();
        }
    }

    /**
     * Draws debug anchor lines.
     *
     * @param canvas The canvas to draw on
     */
    private void drawAnchorLine(Canvas canvas) {
        if (canvas == null) return;

        float cx = mItemCenterPoint.x;
        float cy = mItemCenterPoint.y;
        mPaint.setStrokeWidth(1);
        cx -= mPaint.getStrokeWidth() / 2;
        cy -= mPaint.getStrokeWidth() / 2;

        mPath.reset();
        mPath.moveTo(cx, mItemBorderRect.top);
        mPath.lineTo(cx, mItemBorderRect.top + Math.abs(mItemBorderRect.height()));
        canvas.drawPath(mPath, mPaint);

        mPath.reset();
        mPath.moveTo(mItemBorderRect.left, cy);
        mPath.lineTo(mItemBorderRect.left + Math.abs(mItemBorderRect.width()), cy);
        canvas.drawPath(mPath, mPaint);

        mPath.reset();

        mPaint.setStrokeWidth(mView.getLineWidth());
    }

    /**
     * Updates the center point of the current PIN item.
     */
    private void updateCenterPoint() {
        float cx = mItemBorderRect.left + Math.abs(mItemBorderRect.width()) / 2;
        float cy = mItemBorderRect.top + Math.abs(mItemBorderRect.height()) / 2;
        mItemCenterPoint.set(cx, cy);
    }

    /**
     * Gets the current item border rect for testing/debugging.
     */
    public RectF getItemBorderRect() {
        return mItemBorderRect;
    }

    /**
     * Gets the current center point for testing/debugging.
     */
    public PointF getItemCenterPoint() {
        return mItemCenterPoint;
    }
}