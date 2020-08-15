package com.plm.valdecilla.utils;

import android.graphics.Point;
import android.view.View;

import com.plm.valdecilla.AppState;
import com.plm.valdecilla.Ctes;
import com.plm.valdecilla.model.Node;
import com.plm.valdecilla.model.Path;

import java.util.Iterator;
import java.util.List;

public class IntersectionUtils
{
    public static Path getIntersectionPath(AppState state, List<Path> paths, View view, com.plm.valdecilla.Point screenPoint) {
        Path toFound = null;
        com.plm.valdecilla.Point p = Utils.traRoTra(screenPoint, view.getWidth() / 2, view.getHeight() / 2, AppState.angle);
        p.x = p.x + state.dx;
        p.y = p.y - state.dy;

        for (Path path : paths) {
            double angle = Math.atan2(path.a.y - path.b.y, path.b.x - path.a.x);
            double normal = angle + Math.PI / 2;

            float tx1 = (int) (p.x + Ctes.RADIUS * Math.cos(normal));
            float ty1 = (int) (p.y - Ctes.RADIUS * Math.sin(normal));
            float tx2 = (int) (p.x + Ctes.RADIUS * Math.cos(normal + Math.PI));
            float ty2 = (int) (p.y - Ctes.RADIUS * Math.sin(normal + Math.PI));

            if (IntersectionUtils.doIntersect(tx1, ty1, tx2, ty2, path.a.x, path.a.y, path.b.x, path.b.y)) {
                toFound = path;
                break;
            }

        }

        return toFound;
    }


    public static Path getIntersectionPath(AppState state, List<Path> paths, View view, com.plm.valdecilla.Point p1, com.plm.valdecilla.Point p2){
        float tx1,tx2,tx3,tx4;
        float ty1,ty2,ty3,ty4;
        Path toFound=null;

        Iterator<Path> it2=state.app.paths.iterator();

        p1=Utils.traRoTra(p1.x,p1.y,view.getWidth()/2,view.getHeight()/2,-AppState.angle);
        p2=Utils.traRoTra(p2.x,p2.y,view.getWidth()/2,view.getHeight()/2,-AppState.angle);
        p1=Utils.translate(p1.x,p1.y,-state.dx,state.dy);
        p2=Utils.translate(p2.x,p2.y,-state.dx,state.dy);

        while(it2.hasNext()){
            Path path=it2.next();

            tx1=(int)(path.a.x);
            ty1=(int)(path.a.y);

            tx2=(int)(path.b.x);
            ty2=(int)(path.b.y);


            if(IntersectionUtils.doIntersect(tx1,ty1,tx2,ty2,p1.x,p1.y,p2.x,p2.y)){
                toFound=path;
                break;
            }

        }

        return toFound;
    }

    public static Node getIntersection(AppState state, List<Node> nodes, View view, com.plm.valdecilla.Point p1, com.plm.valdecilla.Point p2){
        float tx1,tx2,tx3,tx4;
        float ty1,ty2,ty3,ty4;
        Node toFound=null;

        Iterator<Node> it1= state.app.nodes.iterator();

        p1=Utils.traRoTra(p1.x,p1.y,view.getWidth()/2,view.getHeight()/2,-AppState.angle);
        p2=Utils.traRoTra(p2.x,p2.y,view.getWidth()/2,view.getHeight()/2,-AppState.angle);
        p1=Utils.translate(p1.x,p1.y,-state.dx,state.dy);
        p2=Utils.translate(p2.x,p2.y,-state.dx,state.dy);

        int angle=MathUtils.getNormal(p1.x,p1.y,p2.x,p2.y);

        while(it1.hasNext()){
            Node node=it1.next();

            tx1 = (float) (node.x + Ctes.RADIUS * Math.cos(Math.toRadians(angle)));
            ty1 = (float) (node.y - Ctes.RADIUS * Math.sin(Math.toRadians(angle)));

            tx2 = (float) (node.x + Ctes.RADIUS * Math.cos(Math.toRadians(angle + 180)));
            ty2 = (float) (node.y - Ctes.RADIUS * Math.sin(Math.toRadians(angle + 180)));


            if(IntersectionUtils.doIntersect(tx1,ty1,tx2,ty2,p1.x,p1.y,p2.x,p2.y)){
                toFound=node;
                break;
            }
        }
        return toFound;
    }

    // Given three colinear points p, q, r, the function checks if
// point q lies on line segment 'pr'
    static boolean onSegment(Point p, Point q, Point r)
    {
        if (q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) &&
                q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y))
            return true;

        return false;
    }

    // To find orientation of ordered triplet (p, q, r).
// The function returns following values
// 0 --> p, q and r are colinear
// 1 --> Clockwise
// 2 --> Counterclockwise
    static int orientation(Point p, Point q, Point r)
    {
        // See https://www.geeksforgeeks.org/orientation-3-ordered-points/
        // for details of below formula.
        int val = (q.y - p.y) * (r.x - q.x) -
                (q.x - p.x) * (r.y - q.y);

        if (val == 0) return 0; // colinear

        return (val > 0)? 1: 2; // clock or counterclock wise
    }

    // The main function that returns true if line segment 'p1q1'
// and 'p2q2' intersect.

    public static boolean doIntersect(float x1,float y1,float x2, float y2,float x3,float y3,float x4,float y4){
        return doIntersect(new Point((int)x1,(int)y1),new Point((int)x2,(int)y2),new Point((int)x3,(int)y3),new Point((int)x4,(int)y4));
    }

    public static boolean doIntersect(Point p1, Point q1, Point p2, Point q2)
    {
        // Find the four orientations needed for general and
        // special cases
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);

        // General case
        if (o1 != o2 && o3 != o4)
            return true;

        // Special Cases
        // p1, q1 and p2 are colinear and p2 lies on segment p1q1
        if (o1 == 0 && onSegment(p1, p2, q1)) return true;

        // p1, q1 and q2 are colinear and q2 lies on segment p1q1
        if (o2 == 0 && onSegment(p1, q2, q1)) return true;

        // p2, q2 and p1 are colinear and p1 lies on segment p2q2
        if (o3 == 0 && onSegment(p2, p1, q2)) return true;

        // p2, q2 and q1 are colinear and q1 lies on segment p2q2
        if (o4 == 0 && onSegment(p2, q1, q2)) return true;

        return false; // Doesn't fall in any of the above cases
    }



}

// This code is contributed by Princi Singh
