package com.plm.valdecilla;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
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
import com.plm.valdecilla.utils.IntersectionUtils;
import com.plm.valdecilla.utils.MathUtils;
import com.plm.valdecilla.utils.Utils;

import java.util.Iterator;


public class CanvasViewHandler{
    private final AppState state;
    public boolean canEdit=true;

    public CanvasViewHandler(AppState state) {
        this.state=state;
        changed();
    }


    public void handleUp(View view,MotionEvent event){
        state.currentX=event.getX();
        state.currentY=event.getY();

        state.shadow.visible=false;
        int distance= MathUtils.getDistance(state.lastX,state.lastY,event.getX(),event.getY());

        if(distance<50){
            //click
            Node clicked2=getClickedNode(view,event.getX(),event.getY());

            if(clicked2==null) {

                /*Point p=Utils.translate(event.getX(),event.getY(),view.getWidth()/2,view.getHeight()/2);
                p=Utils.rotate(-AppState.angle,p.x,p.y);
                p=Utils.translate(p.x+state.dx,p.y-state.dy,-view.getWidth()/2,-view.getHeight()/2);*/

                Point p=Utils.traRoTra(event.getX(),event.getY(),view.getWidth()/2,view.getHeight()/2,state.dx,state.dy,-AppState.angle);

                p.x=Math.round((p.x)/Ctes.GRID)*Ctes.GRID;
                p.y=Math.round((p.y)/Ctes.GRID)*Ctes.GRID;

                if(canEdit) {
                    Node node = new Node(p.x, p.y);
                    state.app.nodes.add(node);
                    changed();
                }
            }
        }else{
            //drag
            Node clicked2=getClickedNode(view,event.getX(),event.getY());

            if(clicked2==null && state.clicked1 !=null){
                Point p=new Point(event.getX(),event.getY());


                p=Utils.traRoTra(p.x,p.y,view.getWidth()/2,view.getHeight()/2,-AppState.angle);
                p.x=Math.round((p.x+state.dx)/Ctes.GRID)*Ctes.GRID;
                p.y=Math.round((p.y-state.dy)/Ctes.GRID)*Ctes.GRID;

                if(canEdit) {
                    state.clicked1.x = p.x;
                    state.clicked1.y = p.y;
                    changed();
                }
            }else if(clicked2!=null && state.clicked1 !=null){
                if(!canEdit){
                    return;
                }

                Path path=state.getPath(state.selectedColor,state.clicked1,clicked2);

                if(path==null) {
                    path = new Path();
                    path.a = state.clicked1;
                    path.b = clicked2;
                    path.color = state.selectedColor;
                    state.app.paths.add(path);
                    changed();
                }else{
                    state.app.paths.remove(path);
                    changed();
                }
            }else if(clicked2==null && state.clicked1==null){
                float tx1,tx2,tx3,tx4;
                float ty1,ty2,ty3,ty4;

                //ver si cruzamos
                Node toDelete=null;

                Iterator<Node> it1= state.app.nodes.iterator();
                boolean intersect1=false;


                //Point p1=Utils.traRoTra(state.lastX,state.lastY,view.getWidth()/2,view.getHeight()/2,-AppState.angle);
                //Point p2=Utils.traRoTra(event.getX(),event.getY(),view.getWidth()/2,view.getHeight()/2,-AppState.angle);
                Point p1=new Point(state.lastX,state.lastY);
                Point p2=new Point(event.getX(),event.getY());


                p1=Utils.traRoTra(p1.x,p1.y,view.getWidth()/2,view.getHeight()/2,-AppState.angle);
                //p1=Utils.translate(p1.x,p1.y,-state.dx,state.dy);
                p2=Utils.traRoTra(p2.x,p2.y,view.getWidth()/2,view.getHeight()/2,-AppState.angle);
                //p2=Utils.translate(p1.x,p1.y,-state.dx,state.dy);
                p1=Utils.translate(p1.x,p1.y,-state.dx,state.dy);
                p2=Utils.translate(p2.x,p2.y,-state.dx,state.dy);
                AppState.ptest1=p1;
                AppState.ptest2=p2;

                int angle=MathUtils.getNormal(p1.x,p1.y,p2.x,p2.y);



                while(it1.hasNext()){
                    Node node=it1.next();

                    tx1=(int)(node.x + Ctes.RADIUS*Math.cos(Math.toRadians(angle)));
                    ty1=(int)(node.y - Ctes.RADIUS*Math.sin(Math.toRadians(angle)));

                    tx2=(int)(node.x + Ctes.RADIUS*Math.cos(Math.toRadians(angle+180)));
                    ty2=(int)(node.y - Ctes.RADIUS*Math.sin(Math.toRadians(angle+180)));

                    intersect1= IntersectionUtils.doIntersect(tx1,ty1,tx2,ty2,p1.x,p1.y,p2.x,p2.y);

                    if(intersect1 && canEdit){
                        state.remove(node);
                        changed();
                        break;
                    }
                }

                Iterator<Path> it2=state.app.paths.iterator();
                boolean intersect2=false;

                while(it2.hasNext()){
                    Path path=it2.next();

                    tx1=(int)(path.a.x);
                    ty1=(int)(path.a.y);

                    tx2=(int)(path.b.x);
                    ty2=(int)(path.b.y);

                    intersect2= IntersectionUtils.doIntersect(tx1,ty1,tx2,ty2,p1.x,p1.y,p2.x,p2.y);

                    if(intersect2 && canEdit){
                        state.remove(path);
                        changed();
                        break;
                    }

                }

                if(!intersect2 && !intersect1) {
                    state.dx = state.dx - (p2.x-p1.x);
                    state.dy = state.dy + (p2.y-p1.y);
                    changed();
                }

            } else if (state.clicked1 != null && state.clicked1 == clicked2) {
                state.app.nodes.remove(state.clicked1);
                changed();
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
        return Utils.traRoTra(x,y,view.getWidth()/2,view.getHeight()/2,state.dx,state.dy,AppState.angle);
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
        Point p=Utils.traRoTra(x,y,view.getWidth()/2,view.getHeight()/2,state.dx,state.dy,-AppState.angle);
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
                node.name=textName.getText().toString();
                node.subnames=textSubName.getText().toString();
                ad.dismiss();
            }
        });


    }

    public void changed(){
        String json=GsonUtils.toJson(state.app);

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
