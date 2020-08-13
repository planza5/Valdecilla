package com.plm.valdecilla.model;

public class Path implements Comparable<Path> {
    public Node a;
    public Node b;
    public int color;
    public boolean visible=true;

    public boolean match(int color, Node a, Node b){
        return color==this.color && ((this.a==a && this.b==b) || ((this.a==b && this.b==a)));
    }

    public boolean match(Node a, Node b){
        return ((this.a==a && this.b==b) || ((this.a==b && this.b==a)));
    }


    @Override
    public int compareTo(Path p) {
        if(p.color>this.color){
            return -1;
        }else if(p.color<this.color){
            return +1;
        }

        return 0;
    }


}
