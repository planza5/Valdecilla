package com.plm.valdecilla.utils;

import com.plm.valdecilla.model.Node;

public class MathUtils {

    public static int getDistance(float x1, float y1, float x2, float y2){
        return (int)Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
    }

    public static int getNormal(float x1,float y1,float x2,float y2){
        float angle=getAngle(x1,y1,x2,y2);

        angle+=90;

        if(angle>359){
            return (int)(angle-360);
        }

        return (int)angle;
    }

    public static int getAngle(float x1,float y1,float x2,float y2){
        double angle =  Math.toDegrees(Math.atan2(
                y2-y1,
                x2-x1));


        if(angle<=0 && angle <=90){
            angle=-1*angle;
        }else{
            angle=360-angle;
        }

        return (int)angle;
    }

    public static int getAngle(Node n1, Node n2){

        return getAngle(n1.x,n1.y,n2.x,n2.y);

        /*double angle1=angle+45>359?315:angle-angle%45;
        double angle2=angle+45>359?360:angle1+45;

        System.out.println("Vamos a ver...."+angle+" ("+angle1+"->"+angle2+")");
        angle=Math.abs(angle-angle1)<Math.abs(angle-angle2)?(int)angle1:(int)angle2;

        return angle==360?0:(int)angle;*/
    }

}
