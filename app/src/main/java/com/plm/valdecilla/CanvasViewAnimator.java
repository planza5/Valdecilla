package com.plm.valdecilla;


import android.animation.ValueAnimator;

import android.animation.ValueAnimator.*;
import android.view.animation.AccelerateDecelerateInterpolator;

public class CanvasViewAnimator {
    private final CanvasView view;
    private final AppContext appContext;
    private float angleInit;
    private float angleEnd;

    public CanvasViewAnimator(AppContext appContext, CanvasView view) {
        this.view = view;
        this.appContext = appContext;
    }

    public void animateAngle(float angleInit, float angleEnd) {
        this.angleInit = angleInit;
        this.angleEnd = angleEnd;

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(angleInit, angleEnd);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator()); // increase the speed first and then decrease
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                appContext.angle = (float) animation.getAnimatedValue();
                view.invalidate();
            }
        });

        valueAnimator.start();
    }


}
