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
    private static final int INTER_LINES_WIDTH = 20;


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
        List<List<Path>> list=new ArrayList<>();

        for(Path path: state.app.paths){
            path.visible=true;
        }

        for(int i = 0; i< state.app.paths.size(); i++){
            Path path1=state.app.paths.get(i);

            if(!path1.visible){
                continue;
            }

            List<Path> toDraw=new ArrayList<>();

            for(int j=i;j<state.app.paths.size();j++){
                Path path2=state.app.paths.get(j);
                if(path2.visible && path2!=path1 && path1.match(path2.a,path2.b)){
                    toDraw.add(path2);
                    path2.visible=false;
                }
            }

            list.add(toDraw);

            if(path1.visible) {
                toDraw.add(path1);
                path1.visible=false;
            }

        }


        for(List<Path> pathList:list){
            //Ordenamos por color cada path
            pathList.sort(new Comparator<Path>() {
                @Override
                public int compare(Path p1, Path p2) {
                if(p1.color>p2.color){
                    return -1;
                }else if(p1.color<p2.color){
                    return +1;
                }

                return 0;
                }
            });

            //propiedades comunes para la lista de paths a tratar
            Path first=pathList.get(0);
            int angle= MathUtils.getAngle(first.a,first.b);
            float distance=MathUtils.getDistance(first.a.x,first.a.y,first.b.x,first.b.y);


            float totalwidth=(pathList.size()-1)* INTER_LINES_WIDTH;

            float initx=(float)(first.a.x-totalwidth/2*Math.cos(Math.toRadians(angle+90)));
            float inity=(float)(first.a.y+totalwidth/2*Math.sin(Math.toRadians(angle+90)));

            float ax,ay,bx,by;


            for(int idx=0;idx<pathList.size();idx++) {
                ax=(float)(initx+idx* INTER_LINES_WIDTH *Math.cos(Math.toRadians(angle+90)));
                ay=(float)(inity-idx* INTER_LINES_WIDTH *Math.sin(Math.toRadians(angle+90)));
                bx = (float) (ax + distance * Math.cos(Math.toRadians(angle)));
                by = (float) (ay - distance * Math.sin(Math.toRadians(angle)));

                int colorIdx=pathList.get(idx).color;

                if(colorIdx==0){
                    painterStrokePaths.setColor(Color.BLACK);
                    painterStrokePaths.setPathEffect(dottedEffect);
                    painterStrokePaths.setStrokeWidth(5);
                }else{
                    painterStrokePaths.setPathEffect(null);
                    painterStrokePaths.setColor(Ctes.COLORS[colorIdx]);
                    painterStrokePaths.setStrokeWidth(INTER_LINES_WIDTH -5);
                }



                //aplicamos angulos
                Point p1=new Point(ax-state.dx, ay+state.dy);
                Point p2=new Point(bx-state.dx, by+state.dy);
                p1=Utils.traRoTra(p1.x,p1.y,canvas.getWidth()/2,canvas.getHeight()/2,AppState.angle);
                p2=Utils.traRoTra(p2.x,p2.y,canvas.getWidth()/2,canvas.getHeight()/2,AppState.angle);
                canvas.drawLine(p1.x,
                        p1.y,
                        p2.x,
                        p2.y,
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
        painterStrokePaths.setStrokeWidth(STROKE_NODES);
        painterStrokePaths.setColor(Color.BLACK);

        painterStrokeNodes.setColor(Color.BLACK);
        painterStrokePaths.setColor(Color.BLACK);
        painterStrokePaths.setStrokeWidth(1);
        painterStrokeWhite.setColor(Color.WHITE);
        painterFillBk.setColor(Color.rgb(250,250,200));
    }




}
