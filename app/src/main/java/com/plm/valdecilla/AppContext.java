package com.plm.valdecilla;

import com.plm.valdecilla.model.App;
import com.plm.valdecilla.model.Node;

import java.util.ArrayDeque;
import java.util.Stack;

public class AppContext {

    public float dx;
    public float dy;
    public App app = new App();
    public float angle;
    public Node clicked1 = new Node();
    public Node clicked2 = new Node();
    public Node shadow = new Node();
    public boolean connected;
    public Integer selectedColor;
    public Stack<String> history = new Stack();
    public int historyIndex;
    public float lastY;
    public float lastX;
    public long lastTime;

    public Point p1, p2, p3;

    public Point p4, p5;
}
