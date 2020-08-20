package com.plm.valdecilla;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.text.TextPaint;

import com.plm.valdecilla.model.Node;
import com.plm.valdecilla.model.Path;
import com.plm.valdecilla.utils.Utils;

import java.util.Collections;

public class CanvasViewDrawer {

    private static final Paint painterFillArrow = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint painterStrokePaths = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint painterStrokeNodes = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint painterStrokeWhite = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint painterFillNodes = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint painterStrokeShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint painterStrokeGrid = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint painterText= new TextPaint();
    private static final float STROKE_NODES = 12;
    private static final DashPathEffect dottedEffect=new DashPathEffect(new float[] {10,20}, 0);
    private final AppContext context;
    private android.graphics.Path arrow;

    public CanvasViewDrawer(AppContext context) {
        this.context = context;
    }


    public void drawNodes(Canvas canvas) {
        for (Node node : context.app.nodes) {
            //trasladamos al origen 0,0
            Point p = Utils.traRoTra(node.x - context.dx, node.y + context.dy, canvas.getWidth() / 2, canvas.getHeight() / 2, context.angle);
            //Utils.translate(p,-context.dx,context.dy);
            canvas.drawCircle(p.x, p.y, Ctes.RADIUS, painterFillNodes);

            if (node.selected) {
                painterStrokeNodes.setColor(Color.RED);
            } else {
                painterStrokeNodes.setColor(Color.BLACK);
            }

            canvas.drawCircle(p.x, p.y, Ctes.RADIUS, painterStrokeNodes);
            float width = painterText.measureText(node.name);
            canvas.drawText(node.name,p.x-width/2,p.y-60,painterText);

            String words[]=node.subnames.split("\n");

            for(int i=0;i<words.length;i++){
                width = painterText.measureText(words[i]);
                canvas.drawText(words[i],p.x-width/2,p.y+90+40*i,painterText);
            }
        }


        if (context.p1 != null && context.p2 != null && context.p3 != null) {
            context.p1 = Utils.traRoTra(context.p1.x - context.dx, context.p1.y + context.dy, canvas.getWidth() / 2, canvas.getHeight() / 2, context.angle);
            context.p2 = Utils.traRoTra(context.p2.x - context.dx, context.p2.y + context.dy, canvas.getWidth() / 2, canvas.getHeight() / 2, context.angle);
            context.p3 = Utils.traRoTra(context.p3.x - context.dx, context.p3.y + context.dy, canvas.getWidth() / 2, canvas.getHeight() / 2, context.angle);
            context.p4 = Utils.traRoTra(context.p4.x - context.dx, context.p4.y + context.dy, canvas.getWidth() / 2, canvas.getHeight() / 2, context.angle);
            context.p5 = Utils.traRoTra(context.p5.x - context.dx, context.p5.y + context.dy, canvas.getWidth() / 2, canvas.getHeight() / 2, context.angle);

            //canvas.drawCircle(context.p1.x, context.p1.y, 10, painterFillBk);
            canvas.drawCircle(context.p2.x, context.p2.y, 20, painterStrokeNodes);
            canvas.drawCircle(context.p3.x, context.p3.y, 20, painterStrokeNodes);
            canvas.drawCircle(context.p4.x, context.p4.y, 5, painterStrokeNodes);
            canvas.drawCircle(context.p5.x, context.p5.y, 5, painterStrokeNodes);
        }
    }


    public void drawShadow(Canvas canvas) {
        if (context.clicked1 != null && context.shadow != null && context.shadow.visible) {
            painterStrokeShadow.setPathEffect(context.connected ? null : dottedEffect);
            painterStrokeShadow.setColor(context.connected ? Color.BLACK : Color.GRAY);
            Point p1 = new Point(context.clicked1.x - context.dx, context.clicked1.y + context.dy);
            p1 = Utils.traRoTra(p1.x, p1.y, canvas.getWidth() / 2, canvas.getHeight() / 2, context.angle);
            //Point p2=Utils.traRoTra(context.shadow.x,context.shadow.y,canvas.getWidth()/2,canvas.getHeight()/2,context.dx,context.dy,AppState.angle);

            canvas.drawLine(p1.x, p1.y, context.shadow.x, context.shadow.y, painterStrokeShadow);
            canvas.drawCircle(context.shadow.x, context.shadow.y, Ctes.RADIUS, painterStrokeShadow);
        }
    }


    public void drawPaths(Canvas canvas) {
        for (Path one : context.app.paths) {
            Collections.sort(one.colors);

            Point p1 = new Point(one.a.x - context.dx, one.a.y + context.dy);
            Point p2 = new Point(one.b.x - context.dx, one.b.y + context.dy);

            p1 = Utils.traRoTra(p1.x, p1.y, canvas.getWidth() / 2, canvas.getHeight() / 2, context.angle);
            p2 = Utils.traRoTra(p2.x, p2.y, canvas.getWidth() / 2, canvas.getHeight() / 2, context.angle);

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

    public void drawGrid(Canvas canvas) {

        if (arrow == null) {
            arrow = new android.graphics.Path();
            float w = canvas.getWidth();
            float h = canvas.getHeight();
            arrow.reset();

            arrow.moveTo(w / 2 - 80, h / 2 + 200);
            arrow.lineTo(w / 2 - 80, h / 2);
            arrow.lineTo(w / 2 - 150, h / 2);

            arrow.lineTo(w / 2, h / 2 - 200);//punta

            arrow.lineTo(w / 2 + 150, h / 2);
            arrow.lineTo(w / 2 + 80, h / 2);
            arrow.lineTo(w / 2 + 80, h / 2 + 200);

            arrow.lineTo(w / 2 - 80, h / 2 + 200);
        }

        canvas.drawPath(arrow, painterFillArrow);

        float incx = context.dx % Ctes.GRID;
        float incy = context.dy % Ctes.GRID;

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
        painterFillArrow.setColor(Color.rgb(250 - 10, 250 - 10, 200 - 10));
    }




}
