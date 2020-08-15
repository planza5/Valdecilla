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
    public void touchSubcanvas(float zoom, float x, float y, float width, float height,float minX, float minY, float maxX, float maxY) {
        width = Math.abs(maxX - minX);
        height = Math.abs(maxY - minY);

        x = x - width / 2;
        y = y - height / 2;

        x = x / zoom;
        y = y / zoom;

        x = x + getWidth() / 2;
        y = y + getHeight() / 2;

        state.dx = x;
        state.dy = y;
        invalidate();
    }
}

