package com.plm.valdecilla;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.plm.valdecilla.model.Node;


public class CanvasView extends View implements View.OnTouchListener, SubCanvasListener {
    private final Context context;
    private CanvasViewDrawer drawer;
    private AppState state;
    private CanvasViewHandler handler;
    private SubCanvasView subCanvasView;
    private int canvasWidth;
    private int canvasHeight;
    private Canvas canvas;

    public CanvasView(Context context, AttributeSet attrs){
        super(context,attrs);
        this.context=context;
        setOnTouchListener(this);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvasWidth=canvas.getWidth();
        canvasHeight=canvas.getHeight();

        canvas.save();
        canvas.translate(canvas.getWidth()/2,canvas.getHeight()/2);
        canvas.rotate(AppState.angle);
        canvas.translate(-canvas.getWidth()/2,-canvas.getHeight()/2);

        drawer.drawGrid(canvas,state);
        canvas.restore();
        //drawer.drawGrid(canvas,state);
        drawer.drawPruebas(canvas,state);
        drawer.drawPaths(canvas,state);
        drawer.drawShadow(canvas,state);
        drawer.drawNodes(canvas,state);
        drawer.drawPruebas(canvas,state);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action=event.getAction();

        if(action== MotionEvent.ACTION_UP){
            handler.handleUp(v,event);
        } else if(action==MotionEvent.ACTION_MOVE){
            handler.handleMove(v,event);
        } else if(action==MotionEvent.ACTION_DOWN){
            boolean doubleClick=handler.handleDown(v,event);

            if(doubleClick && state.clicked1!=null){
                handler.buildAlert(context,this);
            }
        }

        this.invalidate();
        subCanvasView.invalidate();

        return true;
    }



    public void setSubCanvasView(SubCanvasView subCanvasView) {
        this.subCanvasView=subCanvasView;
    }

    public void setDrawer(CanvasViewDrawer drawer) {
        this.drawer=drawer;
    }

    public void setState(AppState state) {
        this.state=state;
    }

    public void setHandler(CanvasViewHandler handler) { this.handler=handler; }

    @Override
    public void touchSubcanvas(float subx, float suby, float thezoom) {
        float sc_minX = Float.MAX_VALUE;
        float sc_maxX = Float.MIN_VALUE;
        float sc_minY = Float.MAX_VALUE;
        float sc_maxY = Float.MIN_VALUE;

        for (Node one : state.app.nodes) {
            if (one.sx < sc_minX) {
                sc_minX = one.sx;
            }
            if (one.sx > sc_maxX) {
                sc_maxX = one.sx;
            }
            if (one.sy < sc_minY) {
                sc_minY = one.sy;
            }
            if (one.sy > sc_maxY) {
                sc_maxY = one.sy;
            }
        }


        float c_minX = Float.MAX_VALUE;
        float c_maxX = Float.MIN_VALUE;
        float c_minY = Float.MAX_VALUE;
        float c_maxY = Float.MIN_VALUE;

        for (Node one : state.app.nodes) {
            if (one.x < c_minX) {
                c_minX = one.x;
            }
            if (one.x > c_maxX) {
                c_maxX = one.x;
            }
            if (one.y < c_minY) {
                c_minY = one.y;
            }
            if (one.y > c_maxY) {
                c_maxY = one.y;
            }
        }

        float subcanvas_width = Math.abs(sc_maxX - sc_minX);
        float subcanvas_height = Math.abs(sc_maxY - sc_minY);
        float canvas_width = Math.abs(c_maxX - c_minX);
        float canvas_height = Math.abs(c_maxY - c_minY);


        float xtpc = subcanvas_width == 0 ? 0 : (subx - sc_minX) * 100 / subcanvas_width;
        float ytpc = subcanvas_height == 0 ? 0 : (suby - sc_minY) * 100 / subcanvas_height;

        float xreal = xtpc * canvas_width / 100;
        float yreal = ytpc * canvas_height / 100;

        state.dx = xreal + c_minX - canvasWidth / 2;
        state.dy = -yreal - c_minY + canvasHeight / 2;



        invalidate();
        subCanvasView.invalidate();
    }
}

