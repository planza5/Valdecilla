package com.plm.valdecilla;

import android.graphics.Color;

public class Ctes {
    public static final int PATH_LINES_STROKE_WIDTH = 15;
    public static final float GRID = 200;
    public static final int INC_ANGLE=15;
    public static final float RADIUS=45;
    public static final int MAX_STACK_SIZE = 6;
    public static final float SUBCANVAS_MARGIN =200;
    public static final int[] COLORS ={Color.rgb(250,250,200),Color.WHITE,Color.BLUE,Color.RED,Color.YELLOW,Color.GREEN,Color.CYAN, Color.BLACK,Color.DKGRAY,Color.LTGRAY,Color.MAGENTA,Color.rgb(255,165,0),Color.rgb(128,64,0)};
    public static final String[] COLORS_DESC ={"","blanca","azul","roja","amarilla","verde","azul claro","negro","gris","gris claro","rosa","naranja","marrón"};

    //TASK
    public static final int NEW_NODE_TASK = 1;
    public static final int SCROLL_SCREEN_TASK = 2;
    public static final int INC_DEC_ANGLE_TASK = 3;
    public static final int MOVE_NODE_TASK = 4;


    public static final int CONNECTED_NODES_TASK = 4;
}
