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

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;


public class CanvasViewHandler {
    private final AppState state;
    public boolean canEdit=true;

    public CanvasViewHandler(AppState state) {
        this.state=state;
        changed();
    }

    private void click(View view, MotionEvent event) {
        //click
        Node clicked2 = getClickedNode(view, event.getX(), event.getY());
        Node newNode = null;
        Path intersectPath = IntersectionUtils.getIntersectionPath(
                state, state.app.paths, view, new Point(event.getX(), event.getY())
        );

        if (clicked2 == null || intersectPath != null) {
            Point p = Utils.traRoTra(event.getX(), event.getY(), view.getWidth() / 2, view.getHeight() / 2, state.dx, -state.dy, -AppState.angle);

            p.x = Math.round((p.x) / Ctes.GRID) * Ctes.GRID;
            p.y = Math.round((p.y) / Ctes.GRID) * Ctes.GRID;

            if (canEdit) {
                newNode = new Node(p.x, p.y);
                state.app.nodes.add(newNode);
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

            state.app.paths.remove(intersectPath);
            state.app.paths.add(pa);
            state.app.paths.add(pb);
        }
    }

    public void handleUp(View view,MotionEvent event){
        state.currentX=event.getX();
        state.currentY=event.getY();

        state.shadow.visible=false;
        int distance= MathUtils.getDistance(state.lastX,state.lastY,event.getX(),event.getY());

        if(distance<50){
            click(view, event);
        }else{
            drag(view, event);
        }
    }

    private void dragNodeInView(View view, MotionEvent event) {
        Point p = new Point(event.getX(), event.getY());


        p = Utils.traRoTra(p.x, p.y, view.getWidth() / 2, view.getHeight() / 2, -AppState.angle);
        p.x = Math.round((p.x + state.dx) / Ctes.GRID) * Ctes.GRID;
        p.y = Math.round((p.y - state.dy) / Ctes.GRID) * Ctes.GRID;

        boolean existsNode = false;

        for (Node one : state.app.nodes) {
            if (one.x == p.x && one.y == p.y) {
                existsNode = true;
                break;
            }
        }

        if (canEdit && !existsNode) {
            state.clicked1.x = p.x;
            state.clicked1.y = p.y;
            changed();
        }
    }

    private void dragNodeInNode(View view, Node clicked1, Node clicked2, MotionEvent event) {
        Path path = null;

        for (Path one : state.app.paths) {
            if ((one.a == clicked1 && one.b == clicked2) || (one.a == clicked2 && one.b == clicked1)) {
                path = one;
                break;
            }
        }

        if (path == null) {
            path = new Path();
            path.a = clicked1;
            path.b = clicked2;
            state.app.paths.add(path);
        }


        if (!path.colors.contains(state.selectedColor)) {
            path.colors.add(state.selectedColor);
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
        Point p1 = new Point(state.lastX, state.lastY);
        Point p2 = new Point(event.getX(), event.getY());

        Node nodeToRemove = null;
        Path pathToRemove = null;

        nodeToRemove = IntersectionUtils.getIntersection(state, state.app.nodes, view, p1, p2);

        if (nodeToRemove != null) {
            removePaths(state, nodeToRemove);
            state.app.nodes.remove(nodeToRemove);
        } else {
            pathToRemove = IntersectionUtils.getIntersectionPath(state, state.app.paths, view, p1, p2);

            if (pathToRemove != null) {
                boolean f = pathToRemove.colors.remove(new Integer(state.selectedColor));

                if (pathToRemove.colors.size() == 0) {
                    state.app.paths.remove(pathToRemove);
                }
            }
        }

        if (nodeToRemove == null && pathToRemove == null) {
            p1 = Utils.traRoTra(p1.x, p1.y, view.getWidth() / 2, view.getHeight() / 2, -AppState.angle);
            p2 = Utils.traRoTra(p2.x, p2.y, view.getWidth() / 2, view.getHeight() / 2, -AppState.angle);
            p1 = Utils.translate(p1.x, p1.y, -state.dx, state.dy);
            p2 = Utils.translate(p2.x, p2.y, -state.dx, state.dy);

            state.dx -= p2.x - p1.x;
            state.dy += p2.y - p1.y;
        }
    }

    private void drag(View view, MotionEvent event) {
        //drag
        Node clicked2 = getClickedNode(view, event.getX(), event.getY());

        if (state.clicked1 != null && clicked2 == null) {
            dragNodeInView(view, event);
        } else if (state.clicked1 != null && clicked2 != null && canEdit) {
            dragNodeInNode(view, state.clicked1, clicked2, event);
        } else if (clicked2 == null && state.clicked1 == null && canEdit) {
            dragViewInView(view, event);
        }
    }

    private void removePaths(AppState state, Node node) {
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
        boolean doubleclick=currentTime-state.lastTime<250;


        state.lastTime=currentTime;
        state.lastX=event.getX();
        state.lastY=event.getY();
        state.clicked1=getClickedNode(view, state.lastX,state.lastY);

        return doubleclick;
    }

    public Point getAngled(View view,float x, float y) {
        return Utils.traRoTra(x, y, view.getWidth() / 2, view.getHeight() / 2, state.dx, -state.dy, AppState.angle);
    }

    public void handleMove(View view,MotionEvent event){
        if(state.clicked1!=null) {
            Point p=getAngled(view,event.getX(),event.getY());
            state.shadow.x = event.getX();
            state.shadow.y = event.getY();
            state.connected = getClickedNode(view,event.getX(),event.getY())!=null;
            state.shadow.visible=true;
        }else{
            state.connected=false;
        }
    }

    private Node getClickedNode(View view,float mouseX,float mouseY){
        for(Node node:state.app.nodes){
            if(isNodeClicked(view,node,mouseX,mouseY)){
                return node;
            }
        }

        return null;
    }



    private boolean isNodeClicked(View view,Node node, float x, float y){
        Point p = Utils.traRoTra(x, y, view.getWidth() / 2, view.getHeight() / 2, state.dx, -state.dy, -AppState.angle);
        return p.x>node.x-Ctes.RADIUS && p.x<node.x+Ctes.RADIUS && p.y>node.y-Ctes.RADIUS && p.y<node.y+Ctes.RADIUS;

    }



    public void buildAlert(Context context, View view) {
        final Node node=state.clicked1;

        if(node==null){
            return;
        }

        final ViewGroup viewGroup = view.findViewById(android.R.id.content);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View dialog=view.inflate(context,R.layout.node_dialog,viewGroup);
        final EditText textPiso = (EditText) dialog.findViewById(R.id.nodeDialogEditTextPiso);
        textPiso.setText(node.piso == null ? "" : node.piso);


        final EditText textName = (EditText)dialog.findViewById(R.id.nodeDialogEditTextNodeName);
        textName.setText(state.clicked1.name);
        final EditText textSubName = (EditText)dialog.findViewById(R.id.nodeDialogEditTextSubNodeName);
        textSubName.setText(state.clicked1.subnames);
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
        String json= GsonUtils.toJson(state.app);

        if(AppState.history.size()==Ctes.MAX_STACK_SIZE){
            AppState.history.remove(0);
        }

        int toRemove= AppState.history.size()- AppState.idx;

        for(int i=0;i<toRemove-1;i++){
            AppState.history.remove(AppState.history.size()-1);
        }

        AppState.history.push(json);
        AppState.idx=AppState.history.size()-1;
    }
}
