package com.plm.valdecilla.model;

public class Box {
    float minX = 0;
    float maxX = 0;
    float minY = 0;
    float maxY = 0;
    String name;

    public Box(float minX, float maxX, float minY, float maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

}
