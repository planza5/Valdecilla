package com.plm.valdecilla;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class SubCanvasView extends View implements View.OnTouchListener{
    private SubCanvasViewDrawer drawer;
    private AppState state;
    private float dx,dy;
    private SubCanvasListener listener;


    public SubCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    public void addListener(SubCanvasListener listener){
        this.listener=listener;
    }

    public void setState(AppState state) {
        this.state=state;
    }

    public void setDrawer(SubCanvasViewDrawer drawer) {
        this.drawer=drawer;
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawer.drawSubCanvasView(canvas,dx,dy,state);
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action=event.getAction();

        if(action==MotionEvent.ACTION_UP || action==MotionEvent.ACTION_MOVE){
            if(listener!=null){
                float thewidth = Math.abs(drawer.getMaxX() - drawer.getMinX());
                float theheight = Math.abs(drawer.getMaxY() - drawer.getMinY());
                //listener.touchSubcanvas(drawer.getZoom(),event.getX(),event.getY(),drawer.getWidth(),drawer.getHeight());
                listener.touchSubcanvas(event.getX(), event.getY(), drawer.getZoom());
            }
        }


        return true;
    }



}
