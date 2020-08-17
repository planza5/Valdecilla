package com.plm.valdecilla;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.hardware.SensorManager;

import com.plm.valdecilla.model.Node;
import com.plm.valdecilla.model.Path;

public class SubCanvasViewDrawer {
    private float zoom,width,height,minX,minY,maxX,maxY;
    private static final Paint painterStrokeNodes = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint painterFillNodes = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint painterFillBk=new Paint(Paint.ANTI_ALIAS_FLAG);



    static{
        painterFillNodes.setColor(Color.WHITE);
        painterStrokeNodes.setColor(Color.BLACK);
        painterStrokeNodes.setStrokeWidth(0.5f);
        painterFillBk.setColor(Color.rgb(250,250,200));
        painterFillNodes.setStyle(Paint.Style.FILL);
        painterStrokeNodes.setStyle(Paint.Style.STROKE);
    }

    public void drawSubCanvasView(Canvas canvas, float dx, float dy,AppState state) {
        //canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight(),painterFillBk);
        width=canvas.getWidth()-Ctes.SUBCANVAS_MARGIN;
        height=canvas.getHeight()-Ctes.SUBCANVAS_MARGIN;

        minX=Float.MAX_VALUE;
        maxX=Float.MIN_VALUE;
        minY=Float.MAX_VALUE;
        maxY=Float.MIN_VALUE;


            for (Node one : state.app.nodes) {
                if (one.x < minX) {
                    minX = one.x;
                }
                if (one.x > maxX) {
                    maxX = one.x;
                }
                if (one.y < minY) {
                    minY = one.y;
                }if (one.y > maxY) {
                    maxY = one.y;
                    }
                }


            float cx=Math.abs(maxX-minX);
            float cy=Math.abs(maxY-minY);

            zoom  = width/cx;

            if(zoom*cy>height){
                zoom=height/cy;
            }

            if(zoom>1){
                zoom=1f;
            }
            float radius=Ctes.RADIUS*zoom;

            for(Node one:state.app.nodes){
                one.sx=(one.x-minX)*zoom+width/2-cx*zoom/2+Ctes.SUBCANVAS_MARGIN/2;
                one.sy=(one.y-minY)*zoom+height/2-cy*zoom/2+Ctes.SUBCANVAS_MARGIN/2;
            }


            painterStrokeNodes.setStrokeWidth(zoom*10);

            for(Path path:state.app.paths){
                if (path.colors.get(0) == 0) {
                    painterStrokeNodes.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
                }else{
                    painterStrokeNodes.setPathEffect(null);
                }

                canvas.drawLine(path.a.sx,path.a.sy,path.b.sx,path.b.sy,painterStrokeNodes);
            }

            painterStrokeNodes.setPathEffect(null);


            for(Node one:state.app.nodes) {
                if(one.name.length()>0 || one.subnames.length()>0) {
                    canvas.drawCircle(one.sx, one.sy, radius, painterFillNodes);
                    canvas.drawCircle(one.sx, one.sy, radius, painterStrokeNodes);
                }else{
                    canvas.drawCircle(one.sx, one.sy, radius/2, painterFillBk);
                    canvas.drawCircle(one.sx, one.sy, radius/2, painterStrokeNodes);
                }
            }
            //canvas.drawLine(0,0,canvas.getWidth(),0,painterStrokeNodes);
            //canvas.drawLine(0,canvas.getHeight()-1,canvas.getWidth(),canvas.getHeight()-1,painterStrokeNodes);

        }

    public float getZoom() {
            return zoom;
        }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public float getMinX(){
        return minX;
    }

    public float getMinY(){
        return minY;
    }

    public float getMaxX(){
        return maxX;
    }

    public float getMaxY(){
        return maxY;
    }
}
