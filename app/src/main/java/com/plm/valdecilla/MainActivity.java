package com.plm.valdecilla;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.plm.valdecilla.model.App;
import com.plm.valdecilla.utils.GsonUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity{
    private AppContext appContext = new AppContext();
    private CanvasView canvasView;
    private SubCanvasView subCanvasView;
    private CanvasViewHandler canvasHandler;

    private static final int SAVE = 1111;
    private static final int LOAD = 2222;

    private ToggleButton toggleEditButton=null;

    private GestureDetector mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        canvasHandler = new CanvasViewHandler(appContext);
        canvasView=findViewById(R.id.canvas1);
        subCanvasView=findViewById(R.id.canvas2);

        subCanvasView.setState(appContext);


        canvasView.setState(appContext);
        canvasView.setDrawer(new CanvasViewDrawer(appContext));
        canvasView.setHandler(canvasHandler);
        canvasView.setSubCanvasView(subCanvasView);
        subCanvasView.addListener(canvasView);
        subCanvasView.setDrawer(new SubCanvasViewDrawer(appContext));

        mDetector = new GestureDetector(this, new MyGestureListener(canvasView, subCanvasView, canvasHandler));
        canvasView.setOnTouchListener(touchListener);

        final List<String> items=new ArrayList();

        for(int i = 0; i< Ctes.COLORS.length; i++){
            items.add("");
        }

        final Spinner spinner=(Spinner)findViewById(R.id.spinner);
        spinner.setBackgroundColor(Ctes.COLORS[0]);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                appContext.selectedColor = position;
                spinner.setBackground(getDrawable(R.drawable.custom_border));
                spinner.setBackgroundColor(Ctes.COLORS[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner.setBackground(getDrawable(R.drawable.custom_border));
                spinner.setBackgroundColor(Ctes.COLORS[0]);
            }
        });

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,R.layout.color_item,items){
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view=super.getDropDownView(position, convertView, parent);
                view.setBackground(getDrawable(R.drawable.custom_border));
                view.setBackgroundColor(Ctes.COLORS[position]);
                return view;
            }
        };

        spinner.setAdapter(adapter);

        toggleEditButton=(ToggleButton)findViewById(R.id.editButton);
        toggleEditButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                canvasHandler.canEdit=!isChecked;
            }
        });



    }


    public void clickSaveAs(View view) {
        Intent intent = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_CREATE_DOCUMENT);

        startActivityForResult(Intent.createChooser(intent, "Save file"), SAVE);
    }

    private Uri fileUri =null;

    public void clickSave(View view) {
        if(fileUri==null){
            clickSaveAs(view);
        }else{
            saveState();
        }
    }

    public void clickLoad(View view) {
        Intent intent = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Open file"), LOAD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SAVE && resultCode == RESULT_OK) {
            fileUri = data.getData();

            saveState();
        } else if(requestCode == LOAD && resultCode == RESULT_OK) {
            fileUri = data.getData();
            StringBuilder sb=new StringBuilder();

            try (InputStream in = getContentResolver().openInputStream(fileUri)) {
                int content;

                while ((content = in.read()) != -1) {
                    sb.append((char)content);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            App app= GsonUtils.fromJson(sb.toString());
            appContext.app.paths = app.paths;
            appContext.app.nodes = app.nodes;
            canvasView.invalidate();

            new AlertDialog.Builder(this)
                    .setMessage("File loaded!!").show();

        }
    }

    private void saveState(){
        App app=new App();
        app.nodes = appContext.app.nodes;
        app.paths = appContext.app.paths;

        try {
            String json=GsonUtils.toJson(app);
            OutputStream os=getContentResolver().openOutputStream(fileUri);
            Writer out=new OutputStreamWriter(os,"ISO-8859-1");
            out.write(new String(json.getBytes()));
            out.flush();
            out.close();
            os.close();

            new AlertDialog.Builder(this)
                    .setMessage("File saved!!").show();
        }
        catch (Exception e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private int idx=0;

    public void undo(View view){
        if (appContext.historyIndex == 0) {
            return;
        }


        appContext.historyIndex--;

        appContext.app = GsonUtils.fromJson(appContext.history.get(appContext.historyIndex));
        canvasView.invalidate();
        subCanvasView.invalidate();
    }

    public void redo(View view){
        if (appContext.historyIndex == appContext.history.size() - 1) {
            return;
        }

        appContext.historyIndex++;

        appContext.app = GsonUtils.fromJson(appContext.history.get(appContext.historyIndex));
        canvasView.invalidate();
        subCanvasView.invalidate();


    }

    public void rotate90right(View view) {
        appContext.angle += Ctes.INC_ANGLE;

        if (appContext.angle == 360) {
            appContext.angle = 0;
        }

        System.out.println(appContext.angle);
        canvasView.invalidate();
    }

    public void rotate90left(View view) {
        appContext.angle -= Ctes.INC_ANGLE;

        if (appContext.angle == -45) {
            appContext.angle = 315;
        }

        System.out.println(appContext.angle);
        canvasView.invalidate();
    }


    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                canvasHandler.handleUp(v, event);
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                canvasHandler.handleDown(v, event);
            }

            boolean m = mDetector.onTouchEvent(event);

            canvasView.invalidate();
            subCanvasView.invalidate();
            return m;
        }
    };

    public void clickTest(View view) {
        appContext.p1 = null;
        appContext.p2 = null;
        appContext.p3 = null;
        appContext.p4 = null;
        appContext.p5 = null;
        appContext.dx = 0;
        appContext.dy = 0;
        appContext.angle = 0;
        canvasView.invalidate();
        subCanvasView.invalidate();
    }
}
