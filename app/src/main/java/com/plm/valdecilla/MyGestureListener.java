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
        handler.doubleClick(canvasView, e);
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
    public void onLongPress(MotionEvent e) {
        System.out.println("onLongPress");
        super.onLongPress(e);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        handler.handleMove(canvasView, e2);
        return true;
    }

    /*@Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        System.out.println("onFling");
        handler.drag(canvasView,e1,e2);
        canvasView.invalidate();
        return true;
    }*/

}
