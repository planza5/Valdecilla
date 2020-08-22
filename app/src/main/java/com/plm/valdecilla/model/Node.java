package com.plm.valdecilla.model;

import com.plm.valdecilla.Ctes;

import java.util.Random;

public class Node {
    public String id;
    public float x;
    public float y;
    public boolean selected;
    public boolean visible=true;
    public String name="New";
    public String piso = "";
    public String subnames="";
    public float sx;
    public float sy;

    public Node(){

    }

    public Node(float x, float y){
        this.x=x;
        this.y=y;
        this.visible=true;
        this.id=Long.toString(new Random().nextLong());
    }

    public void setX(float value) {
        x = value;
    }

    public void setY(float value) {
        y = value;
    }

    public boolean isSelected(){
        return selected;
    }
}
