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
    public Node animatedNode;
    public float animatedRadius;
    public boolean animatingAngle;


    public void setDx(float value) {
        dx = value;
    }

    public void setDy(float value) {
        dy = value;
    }

//    public Point p1, p2, p3,p4, p5;

}
