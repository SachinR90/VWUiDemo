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
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;

@TargetApi(23)
public class CenterEdgeItemsRecyclerView extends RecyclerView {
    private static final String TAG      = CenterEdgeItemsRecyclerView.class.getSimpleName();
    private static final int    NO_VALUE = -2147483648;
    private final OnPreDrawListener mPaddingPreDrawListener;
    private       boolean           mCenterEdgeItems;
    private       boolean           mCenterEdgeItemsWhenThereAreChildren;
    private       int               mOriginalPaddingTop;
    private       int               mOriginalPaddingBottom;
    
    public CenterEdgeItemsRecyclerView(Context context) {
        this(context, (AttributeSet) null);
    }
    
    public CenterEdgeItemsRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public CenterEdgeItemsRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mOriginalPaddingTop = NO_VALUE;
        this.mOriginalPaddingBottom = NO_VALUE;
        this.mPaddingPreDrawListener = new OnPreDrawListener() {
            public boolean onPreDraw() {
                if (CenterEdgeItemsRecyclerView.this.mCenterEdgeItemsWhenThereAreChildren && CenterEdgeItemsRecyclerView.this.getChildCount() > 0) {
                    CenterEdgeItemsRecyclerView.this.setupCenteredPadding();
                    CenterEdgeItemsRecyclerView.this.mCenterEdgeItemsWhenThereAreChildren = false;
                }
                
                return true;
            }
        };
        this.setLayoutManager(new LinearLayoutManager(context, VERTICAL, false));
    }
    
    public boolean onGenericMotionEvent(MotionEvent ev) {
        LayoutManager layoutManager = this.getLayoutManager();
        return layoutManager != null && !this.isLayoutFrozen() && super.onGenericMotionEvent(ev);
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.getViewTreeObserver().addOnPreDrawListener(this.mPaddingPreDrawListener);
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.getViewTreeObserver().removeOnPreDrawListener(this.mPaddingPreDrawListener);
    }
    
    public void setCenterEdgeItems(boolean centerEdgeItems) {
        this.mCenterEdgeItems = centerEdgeItems;
        if (this.mCenterEdgeItems) {
            if (this.getChildCount() > 0) {
                this.setupCenteredPadding();
            } else {
                this.mCenterEdgeItemsWhenThereAreChildren = true;
            }
        } else {
            this.setupOriginalPadding();
            this.mCenterEdgeItemsWhenThereAreChildren = false;
        }
    }
    
    private void setupCenteredPadding() {
        if (this.mCenterEdgeItems && this.getChildCount() >= 1) {
            View child = this.getChildAt(0);
            int height = child.getHeight();
            int desiredPadding = (int) ((float) this.getHeight() * 0.5F - (float) height * 0.5F);
            if (this.getPaddingTop() != desiredPadding) {
                this.mOriginalPaddingTop = this.getPaddingTop();
                this.mOriginalPaddingBottom = this.getPaddingBottom();
                this.setPadding(this.getPaddingLeft(), desiredPadding, this.getPaddingRight(), desiredPadding);
                View focusedChild = this.getFocusedChild();
                int focusedPosition = focusedChild != null ? this.getLayoutManager().getPosition(focusedChild) : 0;
                this.getLayoutManager().scrollToPosition(focusedPosition);
            }
            
        } else {
            Log.w(TAG, "No children available");
        }
    }
    
    private void setupOriginalPadding() {
        if (this.mOriginalPaddingTop != NO_VALUE) {
            this.setPadding(this.getPaddingLeft(), this.mOriginalPaddingTop, this.getPaddingRight(), this.mOriginalPaddingBottom);
        }
    }
    
    abstract static class ChildLayoutManager extends LinearLayoutManager {
        private final float factor;
        
        public ChildLayoutManager(Context context) {
            super(context, LinearLayoutManager.VERTICAL, false);
            factor = 1.0f;
        }
        
        ChildLayoutManager(Context context, float factor) {
            super(context, LinearLayoutManager.VERTICAL, false);
            this.factor = factor;
        }
        
        @Override
        public int scrollVerticallyBy(int dy, Recycler recycler, State state) {
            int scrolled = super.scrollVerticallyBy(dy, recycler, state);
            this.updateLayout();
            return scrolled;
        }
        
        @Override
        public void onLayoutChildren(Recycler recycler, State state) {
            super.onLayoutChildren(recycler, state);
            if (this.getChildCount() != 0) {
                this.updateLayout();
            }
        }
        
        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
            
            final LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                
                @Override
                public PointF computeScrollVectorForPosition(int targetPosition) {
                    return ChildLayoutManager.this.computeScrollVectorForPosition(targetPosition);
                }
                
                @Override
                protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                    return super.calculateSpeedPerPixel(displayMetrics) * factor;
                }
            };
            
            linearSmoothScroller.setTargetPosition(position);
            startSmoothScroll(linearSmoothScroller);
        }
        
        private void updateLayout() {
            for (int count = 0; count < this.getChildCount(); ++count) {
                View child = this.getChildAt(count);
                this.updateChild(child, (CenterEdgeItemsRecyclerView) child.getParent());
            }
            
        }
        
        @Override
        public boolean canScrollHorizontally() {
            return false;
        }
        
        public abstract void updateChild(View var1, CenterEdgeItemsRecyclerView var2);
    }
}
