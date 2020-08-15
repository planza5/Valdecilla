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



}
