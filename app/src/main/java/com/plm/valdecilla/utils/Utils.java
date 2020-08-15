package com.plm.valdecilla.utils;

import android.graphics.Canvas;
import android.view.View;

import com.plm.valdecilla.AppState;
import com.plm.valdecilla.Point;
import com.plm.valdecilla.model.Node;

public class Utils {
    public static Point traRoTra(Point p, float originx, float originy, float dx, float dy, double degrees) {
        p.x = p.x - originx;
        p.y = p.y - originy;
        p = rotate(degrees, p);
        p.x = p.x + originx + dx;
        p.y = p.y + originy + dy;
        return p;
    }

    public static Point traRoTra(float x, float y, float originx, float originy, float dx, float dy, double degrees) {
        Point p = new Point(x - originx, y - originy);
        p = rotate(degrees, p);
        p.x = p.x + originx + dx;
        p.y = p.y + originy + dy;
        return p;
    }

    public static Point traRoTra(Point p, float originx, float originy, double degrees) {
        p.x = p.x - originx;
        p.y = p.y - originy;
        p = rotate(degrees, p);
        p.x = p.x + originx;
        p.y = p.y + originy;
        return p;
    }

    public static Point traRoTra(float x, float y, float originx, float originy, double degrees){
        Point p=new Point(x-originx,y-originy);
        p = rotate(degrees, p);
        p.x=p.x+originx;
        p.y=p.y+originy;
        return p;
    }


    public static Point translate(float x, float y, float dx, float dy){
        return new Point(x-dx,y-dy);
    }

    public static Point rotate(double degrees, Point p) {
        double angle = Math.atan2(p.y, p.x) + Math.toRadians(degrees);

        float distance = (float) Math.sqrt(
                Math.pow(p.x, 2) + Math.pow(p.y, 2)
        );

        p.x = (float) (distance * Math.cos(angle));
        p.y = (float) (distance * Math.sin(angle));

        return p;

    }

    public static Point rotate(double degrees, float x, float y) {
        double angle=Math.atan2(y,x) + Math.toRadians(degrees);

        float distance=(float) Math.sqrt(
                Math.pow(x,2)+Math.pow(y,2)
        );

        float rotx=(float)(distance*Math.cos(angle));
        float roty=(float)(distance*Math.sin(angle));

        return new Point(rotx,roty);

    }
}
