package com.plm.valdecilla;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
    private final CanvasView canvasView;
    private final CanvasViewHandler handler;
    private final SubCanvasView subCanvasView;

    public MyGestureListener(CanvasView canvasView, SubCanvasView subCanvasView, CanvasViewHandler handler) {
        this.canvasView = canvasView;
        this.subCanvasView = subCanvasView;
        this.handler = handler;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        System.out.println("double tap");
        return super.onDoubleTap(e);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        handler.click(canvasView, e);
        canvasView.invalidate();
        subCanvasView.invalidate();
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        System.out.println("onDoubleTapEvent");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        System.out.println("onScroll");
        return super.onScroll(e1, e2, distanceX, distanceY);
    }

    @Override
    public void onLongPress(MotionEvent e) {
        System.out.println("onLongPress");
        super.onLongPress(e);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        System.out.println("onFling");
        return super.onFling(e1, e2, velocityX, velocityY);
    }

}
