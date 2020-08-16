package com.plm.valdecilla;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.text.TextPaint;

import com.plm.valdecilla.model.Node;
import com.plm.valdecilla.model.Path;
import com.plm.valdecilla.utils.MathUtils;
import com.plm.valdecilla.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CanvasViewDrawer {

    private static final Paint painterFillBk=new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint painterStrokePaths = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint painterStrokeNodes = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint painterStrokeWhite = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint painterFillNodes = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint painterStrokeShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint painterStrokeGrid = new Paint(Paint.ANTI_ALIAS_FLAG);

    private static final Paint painterText= new TextPaint();
    private static final float STROKE_NODES = 12;
    private static final DashPathEffect dottedEffect=new DashPathEffect(new float[] {10,20}, 0);



    public void drawPruebas(Canvas canvas, AppState state){
        if(AppState.ptest1!=null && AppState.ptest2!=null) {
            canvas.drawLine(AppState.ptest1.x, AppState.ptest1.y, AppState.ptest2.x, AppState.ptest2.y, painterStrokeGrid);
        }
    }


    public void drawNodes(Canvas canvas, AppState state){
        for(Node node:state.app.nodes){
            //trasladamos al origen 0,0
            Point p=Utils.traRoTra(node.x-state.dx,node.y+state.dy,canvas.getWidth()/2,canvas.getHeight()/2,AppState.angle);
            //Utils.translate(p,-state.dx,state.dy);
            canvas.drawCircle(p.x, p.y, Ctes.RADIUS, painterFillNodes);
            canvas.drawCircle(p.x, p.y, Ctes.RADIUS, painterStrokeNodes);

            float width = painterText.measureText(node.name);
            canvas.drawText(node.name,p.x-width/2,p.y-60,painterText);

            String words[]=node.subnames.split("\n");

            for(int i=0;i<words.length;i++){
                width = painterText.measureText(words[i]);
                canvas.drawText(words[i],p.x-width/2,p.y+90+40*i,painterText);
            }


        }
    }


    public void drawShadow(Canvas canvas, AppState state){
        if(state.clicked1 !=null && state.shadow!=null && state.shadow.visible) {
            painterStrokeShadow.setPathEffect(state.connected ? null : dottedEffect);
            painterStrokeShadow.setColor(state.connected ? Color.BLACK : Color.GRAY);
            Point p1=new Point(state.clicked1.x-state.dx,state.clicked1.y+state.dy);
            p1=Utils.traRoTra(p1.x,p1.y,canvas.getWidth()/2,canvas.getHeight()/2,AppState.angle);
            //Point p2=Utils.traRoTra(state.shadow.x,state.shadow.y,canvas.getWidth()/2,canvas.getHeight()/2,state.dx,state.dy,AppState.angle);

            canvas.drawLine(p1.x, p1.y , state.shadow.x, state.shadow.y, painterStrokeShadow);
            canvas.drawCircle(state.shadow.x, state.shadow.y, Ctes.RADIUS, painterStrokeShadow);
        }
    }



    public void drawPaths(Canvas canvas, AppState state){
        for (Path one : state.app.paths) {
            Collections.sort(one.colors);

            Point p1 = new Point(one.a.x - state.dx, one.a.y + state.dy);
            Point p2 = new Point(one.b.x - state.dx, one.b.y + state.dy);

            p1 = Utils.traRoTra(p1.x, p1.y, canvas.getWidth() / 2, canvas.getHeight() / 2, AppState.angle);
            p2 = Utils.traRoTra(p2.x, p2.y, canvas.getWidth() / 2, canvas.getHeight() / 2, AppState.angle);

            double normal = Math.atan2(p2.y - p1.y, p2.x - p1.x) + Math.PI / 2;


            float delta = one.colors.size() / 2 + (one.colors.size() % 2 == 0 ? -.5f : 0f);

            for (int i = 0; i < one.colors.size(); i++) {

                if (one.colors.get(i) == 0) {
                    painterStrokePaths.setColor(Color.BLACK);
                    painterStrokePaths.setPathEffect(dottedEffect);
                } else {
                    painterStrokePaths.setColor(Ctes.COLORS[one.colors.get(i)]);
                    painterStrokePaths.setPathEffect(null);
                }


                canvas.drawLine((float) (p1.x + Math.cos(normal) * (i - delta) * Ctes.PATH_LINES_STROKE_WIDTH),
                        (float) (p1.y + Math.sin(normal) * (i - delta) * Ctes.PATH_LINES_STROKE_WIDTH),
                        (float) (p2.x + Math.cos(normal) * (i - delta) * Ctes.PATH_LINES_STROKE_WIDTH),
                        (float) (p2.y + Math.sin(normal) * (i - delta) * Ctes.PATH_LINES_STROKE_WIDTH),
                        painterStrokePaths);
            }


        }
    }

    public void drawGrid(Canvas canvas, AppState state) {

        //canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight(),painterFillBk);

        float incx=state.dx%Ctes.GRID;
        float incy=state.dy%Ctes.GRID;

        for(int i=-10;i<10;i++) {
            canvas.drawLine(-100, i*Ctes.GRID+incy, canvas.getWidth()+100, i*Ctes.GRID+incy, painterStrokeGrid);
        }

        for(int i=-10;i<10;i++) {
            canvas.drawLine(i*Ctes.GRID-incx, -100, i*Ctes.GRID-incx, canvas.getHeight()+100, painterStrokeGrid);
        }


    }

    static{
        painterStrokeGrid.setStyle(Paint.Style.STROKE);
        painterStrokeGrid.setStrokeWidth(0.5f);

        painterText.setTextSize(40);
        painterStrokeNodes.setStyle(Paint.Style.STROKE);
        painterStrokeNodes.setStrokeWidth(STROKE_NODES);
        painterStrokeNodes.setColor(Color.BLACK);
        painterStrokeNodes.setTextSize(50);


        painterFillNodes.setStyle(Paint.Style.FILL);
        painterFillNodes.setColor(Color.WHITE);

        painterStrokeShadow.setStyle(Paint.Style.STROKE);
        painterStrokeShadow.setStrokeWidth(STROKE_NODES);
        painterStrokeShadow.setColor(Color.BLACK);

        painterStrokePaths.setStyle(Paint.Style.STROKE);
        painterStrokePaths.setStrokeWidth(Ctes.PATH_LINES_STROKE_WIDTH);
        painterStrokePaths.setColor(Color.BLACK);

        painterStrokeWhite.setColor(Color.WHITE);
        painterFillBk.setColor(Color.rgb(250,250,200));
    }




}
