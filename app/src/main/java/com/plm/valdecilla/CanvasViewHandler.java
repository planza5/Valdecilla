package com.plm.valdecilla;


import android.content.Context;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.plm.valdecilla.model.Node;
import com.plm.valdecilla.model.Path;
import com.plm.valdecilla.utils.GsonUtils;
import com.plm.valdecilla.utils.IntersectionUtils;
import com.plm.valdecilla.utils.MathUtils;
import com.plm.valdecilla.utils.Utils;

import java.util.Comparator;
import java.util.Iterator;


public class CanvasViewHandler {
    private AppContext appContext;
    public boolean canEdit=true;

    public CanvasViewHandler(AppContext appContext) {
        this.appContext = appContext;
        changed();
    }

    public void click(View view, MotionEvent event) {
        //click
        Node clicked2 = getClickedNode(view, event.getX(), event.getY());
        Node newNode = null;

        Path intersectPath = IntersectionUtils.getIntersectionPath(
                appContext, view, new Point(event.getX(), event.getY())
        );


        if (clicked2 == null || intersectPath != null) {
            Point p = Utils.traRoTra(event.getX(), event.getY(), view.getWidth() / 2, view.getHeight() / 2, appContext.dx, -appContext.dy, -appContext.angle);

            /*p.x = Math.round((p.x) / Ctes.GRID) * Ctes.GRID;
            p.y = Math.round((p.y) / Ctes.GRID) * Ctes.GRID;*/

            if (canEdit) {
                newNode = new Node(p.x, p.y);
                appContext.app.nodes.add(newNode);
                changed();
            }
        }

        if (intersectPath != null) {
            Node a = intersectPath.a;
            Node b = intersectPath.b;

            Path pa = new Path();
            pa.a = a;
            pa.b = newNode;
            intersectPath.cloneColors(pa);

            Path pb = new Path();
            pb.a = newNode;
            pb.b = b;
            intersectPath.cloneColors(pb);

            appContext.app.paths.remove(intersectPath);
            appContext.app.paths.add(pa);
            appContext.app.paths.add(pb);
        }
    }

    public void handleUp(View view,MotionEvent event){


        appContext.shadow.visible = false;

        int distance = MathUtils.getDistance(appContext.lastX, appContext.lastY, event.getX(), event.getY());

        if (distance > 50) {
            drag(view, event);
        }
    }

    private void dragNodeInView(View view, MotionEvent event) {
        Point p = new Point(event.getX(), event.getY());


        p = Utils.traRoTra(p.x, p.y, view.getWidth() / 2, view.getHeight() / 2, appContext.dx, -appContext.dy, -appContext.angle);
        /*p.x = Math.round((p.x) / Ctes.GRID) * Ctes.GRID;
        p.y = Math.round((p.y) / Ctes.GRID) * Ctes.GRID;*/

        boolean existsNode = false;

        for (Node one : appContext.app.nodes) {
            if (one.x == p.x && one.y == p.y) {
                existsNode = true;
                break;
            }
        }

        if (canEdit && !existsNode) {
            appContext.clicked1.x = p.x;
            appContext.clicked1.y = p.y;
            changed();
        }
    }

    private void dragNodeInNode(View view, Node clicked1, Node clicked2, MotionEvent event) {
        Path path = null;

        for (Path one : appContext.app.paths) {
            if ((one.a == clicked1 && one.b == clicked2) || (one.a == clicked2 && one.b == clicked1)) {
                path = one;
                break;
            }
        }

        if (path == null) {
            path = new Path();
            path.a = clicked1;
            path.b = clicked2;
            appContext.app.paths.add(path);
        }


        if (!path.colors.contains(appContext.selectedColor)) {
            path.colors.add(appContext.selectedColor);
        }

        if (path.colors.size() > 1 && path.colors.contains(0)) {
            path.colors.remove(0);
        }

        path.colors.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer i1, Integer i2) {
                return i1.compareTo(i2);
            }
        });


    }

    private void dragViewInView(View view, MotionEvent event) {
        Point p1 = new Point(appContext.lastX, appContext.lastY);
        Point p2 = new Point(event.getX(), event.getY());

        Node nodeToRemove = null;
        Path pathToRemove = null;

        nodeToRemove = IntersectionUtils.getIntersection(appContext, view, p1, p2);

        if (nodeToRemove != null) {
            removePaths(appContext, nodeToRemove);
            appContext.app.nodes.remove(nodeToRemove);
        } else {
            pathToRemove = IntersectionUtils.getIntersectionPath(appContext, view, p1, p2);

            if (pathToRemove != null) {
                boolean f = pathToRemove.colors.remove(new Integer(appContext.selectedColor));

                if (pathToRemove.colors.size() == 0) {
                    appContext.app.paths.remove(pathToRemove);
                }
            }
        }

        if (nodeToRemove == null && pathToRemove == null) {
            p1 = Utils.traRoTra(p1.x, p1.y, view.getWidth() / 2, view.getHeight() / 2, -appContext.angle);
            p2 = Utils.traRoTra(p2.x, p2.y, view.getWidth() / 2, view.getHeight() / 2, -appContext.angle);
            p1 = Utils.translate(p1.x, p1.y, -appContext.dx, appContext.dy);
            p2 = Utils.translate(p2.x, p2.y, -appContext.dx, appContext.dy);

            appContext.dx -= p2.x - p1.x;
            appContext.dy += p2.y - p1.y;
        }
    }

    public void drag(View view, MotionEvent event1) {
        //drag
        Node clicked2 = getClickedNode(view, event1.getX(), event1.getY());

        if (appContext.clicked1 != null && clicked2 == null) {
            dragNodeInView(view, event1);
        } else if (appContext.clicked1 != null && clicked2 != null && canEdit) {
            dragNodeInNode(view, appContext.clicked1, clicked2, event1);
        } else if (clicked2 == null && appContext.clicked1 == null && canEdit) {
            dragViewInView(view, event1);
        }
    }

    private void removePaths(AppContext state, Node node) {
        Iterator <Path>it=state.app.paths.iterator();

        while(it.hasNext()){
            Path path=it.next();

            if(path.a==node || path.b==node){
                it.remove();
            }
        }
    }


    public boolean handleDown(View view,MotionEvent event){
        long currentTime=System.currentTimeMillis();
        boolean doubleclick = currentTime - appContext.lastTime < 250;


        appContext.lastTime = currentTime;
        appContext.lastX = event.getX();
        appContext.lastY = event.getY();
        appContext.clicked1 = getClickedNode(view, appContext.lastX, appContext.lastY);

        return doubleclick;
    }



    public void handleMove(View view,MotionEvent event){
        if (appContext.clicked1 != null) {
            appContext.shadow.x = event.getX();
            appContext.shadow.y = event.getY();
            appContext.connected = getClickedNode(view, event.getX(), event.getY()) != null;
            appContext.shadow.visible = true;
        }else{
            appContext.connected = false;
        }
    }

    private Node getClickedNode(View view,float mouseX,float mouseY){
        for (Node node : appContext.app.nodes) {
            if(isNodeClicked(view,node,mouseX,mouseY)){
                return node;
            }
        }

        return null;
    }



    private boolean isNodeClicked(View view,Node node, float x, float y){
        Point p = Utils.traRoTra(x, y, view.getWidth() / 2, view.getHeight() / 2, appContext.dx, -appContext.dy, -appContext.angle);
        return p.x>node.x-Ctes.RADIUS && p.x<node.x+Ctes.RADIUS && p.y>node.y-Ctes.RADIUS && p.y<node.y+Ctes.RADIUS;

    }



    public void buildAlert(Context context, View view) {
        final Node node = appContext.clicked1;

        if(node==null){
            return;
        }

        final ViewGroup viewGroup = view.findViewById(android.R.id.content);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View dialog=view.inflate(context,R.layout.node_dialog,viewGroup);
        final EditText textPiso = (EditText) dialog.findViewById(R.id.nodeDialogEditTextPiso);
        textPiso.setText(node.piso == null ? "" : node.piso);


        final EditText textName = (EditText)dialog.findViewById(R.id.nodeDialogEditTextNodeName);
        textName.setText(appContext.clicked1.name);
        final EditText textSubName = (EditText)dialog.findViewById(R.id.nodeDialogEditTextSubNodeName);
        textSubName.setText(appContext.clicked1.subnames);
        final Button buttonOk=(Button)dialog.findViewById(R.id.nodeDialogOk);
        final Button buttonCancel=(Button)dialog.findViewById(R.id.nodeDialogCancel);

        textSubName.setSelectAllOnFocus(true);
        textSubName.selectAll();
        textSubName.setSingleLine(false);
        textSubName.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        textSubName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        textSubName.setLines(1);
        textSubName.setMaxLines(10);
        textSubName.setVerticalScrollBarEnabled(true);
        textSubName.setMovementMethod(ScrollingMovementMethod.getInstance());
        textSubName.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        builder.setView(dialog);
        final AlertDialog ad=builder.show();


        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textPiso.getText().toString().length() > 0) {
                    node.piso = textPiso.getText().toString();
                }


                node.name=textName.getText().toString();
                node.subnames=textSubName.getText().toString();
                ad.dismiss();
            }
        });


    }

    public void changed(){
        String json = GsonUtils.toJson(appContext.app);

        if (appContext.history.size() == Ctes.MAX_STACK_SIZE) {
            appContext.history.remove(0);
        }

        int toRemove = appContext.history.size() - appContext.historyIndex;

        for(int i=0;i<toRemove-1;i++){
            appContext.history.remove(appContext.history.size() - 1);
        }

        appContext.history.push(json);
        appContext.historyIndex = appContext.history.size() - 1;
    }

}
