package com.plm.valdecilla.model;

import java.util.ArrayList;
import java.util.List;

public class Path {
    public Node a;
    public Node b;
    public List<Integer> colors = new ArrayList();
    public boolean visible=true;

    public void cloneColors(Path pa) {
        for(Integer color:colors){
            pa.colors.add(color);
        }
    }
}
