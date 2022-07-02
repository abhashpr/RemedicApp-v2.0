package com.example.android.remedicappml;


import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.ColorInt;

import java.util.Random;

public class MyGraph extends View implements ValueAnimator.AnimatorUpdateListener {

    private final int GRAPH_ANIMATION_DURATION = 500;
    private final Paint mBarPaint;
    private final Paint mGridPaint;
    private final Paint mGuidelinePaint;
    private final Paint mGraphLinePaint;
    private final Paint mGridBaseAxis;
    private final Paint mNormalRangePaint;
    private final Paint mAxisText;

    private final int barColor = getResources().getColor(R.color.blue);
    private final int gridColor = getResources().getColor(R.color.teal_700);
    private final int guidelineColor = getResources().getColor(R.color.gray);
    private final int graphLineColor = getResources().getColor(R.color.blue);
    private final int graphGridBaseColor = getResources().getColor(R.color.shady);
    private final int graphNormalRange = getResources().getColor(R.color.teal_200);
    private final int textColor = getResources().getColor(R.color.black);

    private final int gridThicknessInPx = 5;
    private final int guidelineThicknessInPx = 2;
    private final int getGraphLineThicknessInPx = 4;
    private final int getGraphBaseThicknessInPx = 3;

    private final float mPadding = 20;
    private float[] data = {10f, 20f, 25f, 30f, 35f, 55f, 70f, 80f, 67f, 55f, 33f, 21f, 5f,
            5f, 10f, 15f, 20f, 35f, 55f, 45f, 43f, 34f};
    private int dataCount = data.length;

    private ValueAnimator mAnimator;
    private float mAnimatingFraction;
    private Random r = new Random();

    public MyGraph(Context context, AttributeSet attrs) {
        super(context, attrs);

        mAxisText = new Paint();
        mAxisText.setStyle(Paint.Style.STROKE);
        mAxisText.setTextSize(20);
        mAxisText.setColor(textColor);

        mBarPaint = new Paint();
        mBarPaint.setStyle(Paint.Style.FILL);
        mBarPaint.setColor(barColor);

        mGridPaint = new Paint();
        mGridPaint.setStyle(Paint.Style.STROKE);
        mGridPaint.setColor(gridColor);
        mGridPaint.setStrokeWidth(gridThicknessInPx);

        mGuidelinePaint = new Paint();
        mGuidelinePaint.setStyle(Paint.Style.STROKE);
        mGuidelinePaint.setColor(guidelineColor);
        mGuidelinePaint.setStrokeWidth(guidelineThicknessInPx);

        mGraphLinePaint = new Paint();
        mGraphLinePaint.setStyle(Paint.Style.STROKE);
        mGraphLinePaint.setColor(graphLineColor);
        mGraphLinePaint.setStrokeWidth(getGraphLineThicknessInPx);

        mGridBaseAxis = new Paint();
        mGridBaseAxis.setStyle(Paint.Style.STROKE);
        mGridBaseAxis.setColor(graphGridBaseColor);
        mGridBaseAxis.setStrokeWidth(getGraphBaseThicknessInPx);

        mNormalRangePaint = new Paint();
        mNormalRangePaint.setStyle(Paint.Style.STROKE);
        mNormalRangePaint.setColor(graphNormalRange);
        mNormalRangePaint.setStrokeWidth(getGraphBaseThicknessInPx);

        mAnimator = new ValueAnimator();
        mAnimator.setDuration(GRAPH_ANIMATION_DURATION);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addUpdateListener(this);

        for (int i = 0; i < data.length; i++) {
            data[i] = 0f + r.nextFloat() * (89f - 0f);
        }

        mAnimator.setFloatValues(0f, 1f);
        mAnimator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        // Get our interpolated float from the animation
        mAnimatingFraction = animation.getAnimatedFraction();
        invalidate();
    }

    public void setParameter() {
        for (int i = 0; i < data.length; i++) {
            data[i] = 0f + r.nextFloat() * (99f - 0f);
        }

        mAnimator.setFloatValues(0f, 1f);
        mAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        final int height = getHeight();
        final int width = getWidth();
        final float gridLeft = 0;
        final float gridBottom = height - mPadding;
        final float gridTop = mPadding;
        final float gridRight = width - mPadding;

        // Draw Gridlines
        canvas.drawLine(gridLeft+3*mPadding, gridBottom, gridRight, gridBottom, mGridBaseAxis);

        // Draw guidelines
        float guideLineSpacing = (gridBottom - gridTop) / 5f;
        float y;
        for (int i = 0; i < 5; i++) {
            y = gridTop + i * guideLineSpacing;
            canvas.drawText(String.valueOf(10F*(i+1)), gridLeft, y, mAxisText);
            canvas.drawLine(gridLeft+3*mPadding, y, gridRight, y, mGuidelinePaint);
            //if (i == 2 || i == 3)
            //   canvas.drawLine(gridLeft, y, gridRight, y, mNormalRangePaint);
            //
            //else
            //    canvas.drawLine(gridLeft, y, gridRight, y, mGuidelinePaint);
            //
        }

        // Draw bars
        float spacing = 2f;
        float totalColumnSpacing = spacing * (dataCount + 1);
        float columnWidth = (gridRight - gridLeft - totalColumnSpacing) / dataCount;
        float columnLeft = gridLeft + spacing;
        float columnRight = columnLeft + columnWidth;

        int counter = 1;
        float prevX = 0;
        float prevY = 0;
        float X = gridLeft + 3 * mPadding + spacing + columnWidth/2;
        float Y;
        // Draw bar peaks
        for (float percentage: data) {
            // Calculate top of each bar
            Y = gridTop + height * (1f - (percentage * mAnimatingFraction) / 100) - 1.5f * mPadding;
            if (counter > 1) {
                X = prevX + columnWidth + spacing;
                canvas.drawLine(prevX, prevY, X, Y, mGraphLinePaint);
            }
            prevX = X;
            prevY = Y;
            counter++;
        }
    }
}

