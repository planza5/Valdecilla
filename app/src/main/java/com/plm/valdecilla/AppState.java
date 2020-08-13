package com.plm.valdecilla;

import android.graphics.Color;

import com.plm.valdecilla.model.App;
import com.plm.valdecilla.model.Node;
import com.plm.valdecilla.model.Path;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class AppState {
    public static List<Point> pruebas=new ArrayList();

    public static int angle=0;
    public static Stack<String> history=new Stack();
    public static int historyPointer=0;
    public static int idx;
    public static Point ptest1;
    public static Point ptest2;

    public App app=new App();
    public boolean connected;
    public Node clicked1;
    public Node shadow=new Node();
    public static float dx,dy;
    public long lastTime;
    public float currentX,lastX;
    public float currentY,lastY;
    public int selectedColor;
    public boolean showingAlert;
    public int screenWidth;
    public int screenHeight;

    public void remove(Node node) {
        Iterator<Path> it=app.paths.iterator();

        while(it.hasNext()){
            Path next=it.next();

            if(next!=null && next.a==node || next.b==node){
                it.remove();
            }
        }

        app.nodes.remove(node);
    }

    public void remove(Path path) {
        Iterator<Path> it=app.paths.iterator();

        while(it.hasNext()){
            Path next=it.next();

            if(next.match(selectedColor,path.a,path.b)){
                it.remove();
            }
        }
    }

    public Path getPath(int color,Node node1, Node node2){
        Path path=null;

        for(Path one:app.paths){
            if(one.match(color,node1,node2)){
                return one;
            }
        }

        return path;
    }
}
