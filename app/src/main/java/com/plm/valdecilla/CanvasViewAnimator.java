package com.plm.valdecilla;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;

import android.animation.ValueAnimator.*;
import android.graphics.Path;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.plm.valdecilla.model.Node;


public class CanvasViewAnimator {
    private final CanvasView view;
    private final AppContext appContext;
    private final ITaskCallback callback;

    public CanvasViewAnimator(AppContext appContext, CanvasView view, ITaskCallback callback) {
        this.view = view;
        this.appContext = appContext;
        this.callback = callback;
    }

    public void animateAngle(float angleInit, float angleEnd) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(angleInit, angleEnd);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator()); // increase the speed first and then decrease
        valueAnimator.setDuration(250);
        valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                appContext.angle = (float) animation.getAnimatedValue();
                view.invalidate();
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                appContext.animatingAngle = false;
                callback.endTask(Ctes.INC_DEC_ANGLE_TASK);
            }
        });

        appContext.animatingAngle = true;
        valueAnimator.start();
    }


    public void animateNewNode(Object value) {
        appContext.animatedNode = (Node) value;

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, Ctes.RADIUS);
        valueAnimator.setDuration(250);
        valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                appContext.animatedNode.name = "";
                appContext.animatedRadius = (float) animation.getAnimatedValue();
                view.invalidate();
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                appContext.animatedNode.name = "New";
                view.invalidate();
                appContext.animatedNode = null;
                appContext.animatingAngle = false;
                callback.endTask(Ctes.NEW_NODE_TASK);
            }
        });

        valueAnimator.start();
    }

    public void animateDxDyScreen(Float dx1, Float dy1, Float dx2, Float dy2) {
        Path path = new Path();
        path.reset();
        path.moveTo(appContext.dx, appContext.dy);
        path.lineTo(dx2, dy2);


        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(appContext, "dx", "dy", path);
        objectAnimator.addUpdateListener(new
                                                 AnimatorUpdateListener() {
                                                     @Override
                                                     public void onAnimationUpdate(ValueAnimator animation) {
                                                         appContext.dx = (Float) animation.getAnimatedValue("dx");
                                                         appContext.dy = (Float) animation.getAnimatedValue("dy");
                                                         view.invalidate();
                                                     }
                                                 });

        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                callback.endTask(Ctes.SCROLL_SCREEN_TASK);
            }
        });

        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());


        objectAnimator.setDuration(300);
        objectAnimator.start();
    }

    public void animateMoveNode(Object object, Float x, Float y) {
        Node node = (Node) object;
        Path path = new Path();
        path.reset();
        path.moveTo(node.x, node.y);
        path.lineTo(x, y);


        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(node, "x", "y", path);
        objectAnimator.addUpdateListener(new
                                                 AnimatorUpdateListener() {
                                                     @Override
                                                     public void onAnimationUpdate(ValueAnimator animation) {
                                                         view.invalidate();
                                                     }
                                                 });

        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                callback.endTask(Ctes.MOVE_NODE_TASK);
            }
        });

        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());


        objectAnimator.setDuration(300);
        objectAnimator.start();
    }
}
