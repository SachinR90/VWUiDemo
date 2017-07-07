package com.winjit.vwdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import static com.winjit.vwdemo.Utils.getPixelFromDp;


@SuppressWarnings("UnusedParameters")
public class CurveView extends View {
    
    private Paint dashPaint;
    private Paint straightPaint;
    private Paint arrowPaint;
    private Paint gradientPaint;
    private Path  arrowPath;
    private Path  gradientPath;
    private RectF computRect;
    
    public CurveView(Context context) {
        this(context, null, 0);
    }
    
    public CurveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public CurveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    
    private void init(Context context, AttributeSet attrs) {
        computRect = new RectF();
        dashPaint = new Paint();
        dashPaint.setStyle(Paint.Style.STROKE);
        dashPaint.setPathEffect(new DashPathEffect(new float[]{getPixelFromDp(4f), getPixelFromDp(4f)}, 0));
        dashPaint.setColor(Color.WHITE);
        dashPaint.setStrokeWidth(getPixelFromDp(2f));
        dashPaint.setStrokeJoin(Paint.Join.ROUND);
        dashPaint.setStrokeCap(Paint.Cap.ROUND);
        dashPaint.setAntiAlias(true);
        dashPaint.setDither(true);
        dashPaint.setAlpha(128);
        
        straightPaint = new Paint();
        straightPaint.setStyle(Paint.Style.STROKE);
        straightPaint.setColor(Color.WHITE);
        straightPaint.setStrokeWidth(getPixelFromDp(2f));
        straightPaint.setAntiAlias(true);
        straightPaint.setDither(true);
        straightPaint.setStrokeJoin(Paint.Join.ROUND);
        straightPaint.setStrokeCap(Paint.Cap.ROUND);
        straightPaint.setAlpha(128);
        
        arrowPaint = new Paint();
        arrowPaint.setStyle(Paint.Style.STROKE);
        arrowPaint.setColor(Color.WHITE);
        arrowPaint.setStrokeWidth(getPixelFromDp(3f));
        arrowPaint.setStrokeCap(Paint.Cap.SQUARE);
        arrowPath = new Path();
        
        gradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        gradientPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        gradientPaint.setStrokeWidth(getPixelFromDp(3f));
        gradientPaint.setStrokeCap(Paint.Cap.SQUARE);
        gradientPaint.setAlpha(220);
        
        gradientPath = new Path();
        
    }
    
    @Override
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        if (width <= 0 || height <= 0) {
            super.onDraw(canvas);
            return;
        }
        float centerX = -width / 8;
        float centerY = height / 2;
        
        canvas.drawCircle(centerX, centerY, (float) (height / 2.550), dashPaint);
        
        canvas.drawCircle(centerX, centerY, (float) (height / 2.4), straightPaint);
        
        centerX = width / 2;
        arrowPath.reset();
        arrowPath.moveTo(centerX - getPixelFromDp(5f), centerY - getPixelFromDp(5f));
        arrowPath.lineTo(centerX, centerY);
        arrowPath.lineTo(centerX - getPixelFromDp(5f), centerY + getPixelFromDp(5f));
        canvas.drawPath(arrowPath, arrowPaint);
        
        //draw the gradient path
        
        gradientPath.reset();
        float startX = 0;
        float startY = centerY - getPixelFromDp(30);
        float endY = centerY + getPixelFromDp(30);
        
        gradientPath.moveTo(startX, startY);
        //draw horizontal vertical line
        gradientPath.lineTo(getPixelFromDp(48f), startY);
        gradientPath.lineTo(getPixelFromDp(54f), startY - getPixelFromDp(6f));
        gradientPath.lineTo(getPixelFromDp(60f), startY);
        gradientPath.lineTo(width, startY);
        
        //draw vertical end line
        gradientPath.lineTo(width, endY);
        
        //draw horizontal bottom line
        gradientPath.lineTo(getPixelFromDp(60f), endY);
        gradientPath.lineTo(getPixelFromDp(54f), endY + getPixelFromDp(6f));
        gradientPath.lineTo(getPixelFromDp(48f), endY);
        gradientPath.lineTo(startX, endY);
        gradientPath.close();
        computRect.set(startX, startY, width, endY);
        gradientPaint.setShader(new RadialGradient(computRect.centerX(), computRect.centerY(), (width / 2 + width / 3), Color.TRANSPARENT, Color.WHITE, Shader.TileMode.MIRROR));
        canvas.drawPath(gradientPath, gradientPaint);
    }
}
