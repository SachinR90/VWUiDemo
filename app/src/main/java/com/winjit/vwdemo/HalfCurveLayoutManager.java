/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.winjit.vwdemo;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

@TargetApi(23)
public class HalfCurveLayoutManager extends CenterEdgeItemsRecyclerView.ChildLayoutManager {
    
    private final Path        mCurvePath      = new Path();
    private final PathMeasure mPathMeasure    = new PathMeasure();
    private final float[]     mPathPoints     = new float[2];
    private final float[]     mPathTangent    = new float[2];
    private final float[]     mAnchorOffsetXY = new float[2];
    private int          mCurvePathHeight;
    private float        mPathLength;
    private float        mCurveBottom;
    private float        mCurveTop;
    private float        mLineGradient;
    private RecyclerView mParentView;
    private int          mLayoutWidth;
    private int          mLayoutHeight;
    
    public HalfCurveLayoutManager(Context context, float factor) {
        super(context, factor);
    }
    
    @Override
    public void updateChild(View child, CenterEdgeItemsRecyclerView parent) {
        if (this.mParentView != parent) {
            this.mParentView = parent;
            this.mLayoutWidth = this.mParentView.getWidth();
            this.mLayoutHeight = this.mParentView.getHeight();
        }
        int childHeight = child.getHeight();
        int mParentViewHeight = this.mParentView.getHeight();
        
        float mXCurveOffset = (this.mLayoutWidth / 2) + (this.mLayoutWidth / 4);
        this.setUpCircularInitialLayout((this.mLayoutWidth * 2) - (this.mLayoutWidth / 4), this.mLayoutHeight);
        this.mAnchorOffsetXY[0] = mXCurveOffset;
        this.mAnchorOffsetXY[1] = (float) childHeight / 2.0F;
        float minCenter = -((float) childHeight) / 2.0F;
        float maxCenter = (float) this.mLayoutHeight + (float) childHeight / 2.0F;
        float range = maxCenter - minCenter;
        float verticalAnchor = (float) child.getTop() + this.mAnchorOffsetXY[1];
        float mYScrollProgress = (verticalAnchor + Math.abs(minCenter)) / range;
        this.mPathMeasure.getPosTan(mYScrollProgress * this.mPathLength, this.mPathPoints, this.mPathTangent);
        boolean topClusterRisk = Math.abs(this.mPathPoints[1] - this.mCurveBottom) < 0.001F && minCenter < this.mPathPoints[1];
        boolean bottomClusterRisk = Math.abs(this.mPathPoints[1] - this.mCurveTop) < 0.001F && maxCenter > this.mPathPoints[1];
        if (topClusterRisk || bottomClusterRisk) {
            this.mPathPoints[1] = verticalAnchor;
            this.mPathPoints[0] = Math.abs(verticalAnchor) * this.mLineGradient;
        }
        int newLeft = (int) (this.mPathPoints[0] - this.mAnchorOffsetXY[0]);
        //offset the horizontal position by x
        child.offsetLeftAndRight(newLeft - child.getLeft());
        float verticalTranslation = this.mPathPoints[1] - verticalAnchor;
        child.setTranslationY(verticalTranslation);
        
        float centerOffset = ((float) childHeight / 2.0f) / (float) mParentViewHeight;
        float yRelativeToCenterOffset = (child.getY() / mParentViewHeight) + centerOffset;
        if (yRelativeToCenterOffset > 0.49 && yRelativeToCenterOffset < 0.51) {
            child.animate().scaleX(1.125f).scaleY(1.125f).translationX(-70f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(250).setStartDelay(50).start();
        } else {
            child.animate().scaleX(1f).scaleY(1f).translationX(1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(250).setStartDelay(50).start();
        }
    }
    
    private void setUpCircularInitialLayout(int width, int height) {
        if (this.mCurvePathHeight != height) {
            this.mCurvePathHeight = height;
            this.mCurveBottom = -0.048F * (float) height;
            this.mCurveTop = 1.048F * (float) height;
            this.mLineGradient = 10.416667F;
            this.mCurvePath.reset();
            this.mCurvePath.moveTo(0.5F * (float) width, this.mCurveBottom);
            
            this.mCurvePath.lineTo(0.34F * (float) width, 0.075F * (float) height);
            
            this.mCurvePath.cubicTo(0.22F * (float) width, 0.17F * (float) height,
                                    0.13F * (float) width, 0.32F * (float) height,
                                    0.13F * (float) width, 0.5F * (float) height);
            
            this.mCurvePath.cubicTo(0.13F * (float) width, 0.68F * (float) height,
                                    0.22F * (float) width, 0.83F * (float) height,
                                    0.34F * (float) width, 0.925F * (float) height);
            
            this.mCurvePath.lineTo(0.5F * (float) width, this.mCurveTop);
            
            this.mPathMeasure.setPath(this.mCurvePath, false);
            this.mPathLength = this.mPathMeasure.getLength();
        }
    }
}