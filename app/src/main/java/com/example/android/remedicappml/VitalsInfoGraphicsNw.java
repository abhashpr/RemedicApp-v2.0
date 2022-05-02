package com.example.android.remedicappml;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class VitalsInfoGraphicsNw extends GraphicOverlay.Graphic
        implements ValueAnimator.AnimatorUpdateListener {

    private int shapeColor;
    private boolean displayShapeName;

    private Paint paintShape;
    private Paint textPaint, subTextPaint, miniTextPaint, dividerPaint, needle, p;

    private int xTextPos = 0;
    private int yTextPos = 0;

    public HashMap<String, Float> vitals;

    private ValueAnimator mAnimator;
    private float mAnimatingFraction;

    public VitalsInfoGraphicsNw (
            GraphicOverlay overlay,
            float beatsPerMinute,
            float spo2Index,
            @Nullable Integer framesPerSecond) {
        super(overlay);

        paintShape = new Paint();
        paintShape.setAntiAlias(true);
        paintShape.setColor(getApplicationContext().getResources().getColor(R.color.bgw));
        paintShape.setTextSize(60f);
        paintShape.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(getApplicationContext().getResources().getColor(R.color.shady));
        textPaint.setTextSize(60f);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        subTextPaint = new Paint();
        subTextPaint.setAntiAlias(true);
        subTextPaint.setColor(getApplicationContext().getResources().getColor(R.color.shady));
        subTextPaint.setTextSize(25f);

        miniTextPaint = new Paint();
        miniTextPaint.setAntiAlias(true);
        miniTextPaint.setColor(getApplicationContext().getResources().getColor(R.color.black));
        miniTextPaint.setTextSize(20f);
        miniTextPaint.setStrokeWidth(5f);

        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setColor(getApplicationContext().getResources().getColor(R.color.gray));
        miniTextPaint.setStrokeWidth(1f);

        needle = new Paint();
        needle.setAntiAlias(true);
        needle.setColor(getApplicationContext().getResources().getColor(R.color.gray));

        p = new Paint();
        p.setAntiAlias(true);
        p.setColor(getApplicationContext().getResources().getColor(R.color.bgw));

        mAnimator = new ValueAnimator();
        mAnimator.setDuration(2000);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addUpdateListener(this);

        mAnimator.setFloatValues(0f, 1f);
        mAnimator.start();

        setupVitals(beatsPerMinute, spo2Index);

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        // Get our interpolated float from the animation
        mAnimatingFraction = animation.getAnimatedFraction();
        postInvalidate();
    }

    private void setupVitals(float bpm, float spo2) {
        vitals = new HashMap<String, Float>();
        vitals.put("bpm", bpm);
        vitals.put("spo2", spo2);
        vitals.put("si", 0f);
    }

    @Override
    public void draw(Canvas canvas) {
        drawRoundReadingShape(canvas);
    }

    private void drawRoundReadingShape(Canvas canvas) {

        float screenWidth = canvas.getWidth();
        float screenHeight = canvas.getHeight();
        float screenWidthFrac = 0.9f;
        float topMarginFrac = 0.74F;

        String s;
        float strPos = 0f;
        float linePos = 0f;
        int bw, bh;
        float bp = 0f;

        float rectLeft = screenWidth * (1 - screenWidthFrac)/2;
        float rectTop = screenHeight * topMarginFrac;
        float rectWidth = screenWidth * screenWidthFrac;
        float rectHeight = screenHeight * 0.12f;
        float rectRight = rectLeft + rectWidth;
        float rectBottom = rectTop + rectHeight;
        float cy = rectTop + rectHeight/2;
        float dividerTopY = cy - 0.3f * rectHeight;
        float dividerBottomY = cy + 0.3f * rectHeight;

        canvas.drawRoundRect(rectLeft, rectTop, rectRight, rectBottom, 20f, 20f, paintShape);

        float leftMargin = (screenWidth * screenWidthFrac) * 0.15f;
        float rightMargin = leftMargin;

        float bpm = vitals.get("bpm");
        strPos = rectLeft + leftMargin;

        drawReadings(canvas, strPos, String.valueOf(bpm), "HEART RATE", cy, bp);

        linePos = screenWidth * (1 - screenWidthFrac)/2 + rectWidth/3;
        canvas.drawLine(linePos, dividerTopY, linePos, dividerBottomY, dividerPaint);

        strPos = screenWidth * (1 - screenWidthFrac)/2 + rectWidth/2;
        drawReadings(canvas, strPos, "99", "SPO2 (%)", cy, bp);

        linePos = screenWidth * (1 - screenWidthFrac)/2 + 2 * (rectWidth/3);
        canvas.drawLine(linePos, dividerTopY, linePos, dividerBottomY, dividerPaint);

        strPos = rectLeft + rectWidth - rightMargin;
        drawReadings(canvas, strPos, "3", "SI(/10)", cy, bp);

    }

    private void drawReadings(Canvas canvas, float strPos, String valReading,
                              String descReading, float verticalPos,
                              float textMargin) {
        Rect bounds = new Rect();
        textPaint.getTextBounds(valReading, 0, valReading.length(), bounds);
        float boundsHeight = bounds.height();
        float boundsWidth = bounds.width();
        float xPos = strPos - boundsWidth/2;
        float yPos = verticalPos;
        canvas.drawText(valReading, xPos, yPos, textPaint);

        subTextPaint.getTextBounds(descReading, 0, descReading.length(), bounds);
        boundsWidth = bounds.width();
        xPos = strPos - boundsWidth/2;
        yPos = verticalPos + boundsHeight + textMargin;
        canvas.drawText(descReading, xPos, yPos, subTextPaint);
    }

    private void setupPaint() {
        paintShape = new Paint();
        paintShape.setStyle(Paint.Style.FILL);
        paintShape.setColor(shapeColor);
        paintShape.setTextSize(30);
    }

}
