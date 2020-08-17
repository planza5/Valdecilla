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
    private static CanvasView canvasView;
    private static SubCanvasView subCanvasView;
    private static final AppState state=new AppState();
    private static final CanvasViewDrawer drawer=new CanvasViewDrawer();
    private static final SubCanvasViewDrawer subdrawer=new SubCanvasViewDrawer();
    private static CanvasViewHandler canvasHandler;

    private static final int SAVE = 1111;
    private static final int LOAD = 2222;

    private ToggleButton toggleEditButton=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        state.screenHeight =displayMetrics.heightPixels;
        state.screenWidth = displayMetrics.widthPixels;
        canvasHandler=new CanvasViewHandler(state);
        canvasView=findViewById(R.id.canvas1);
        subCanvasView=findViewById(R.id.canvas2);

        subCanvasView.setState(state);
        subCanvasView.setDrawer(subdrawer);

        canvasView.setState(state);
        canvasView.setDrawer(drawer);
        canvasView.setHandler(canvasHandler);
        canvasView.setSubCanvasView(subCanvasView);
        subCanvasView.addListener(canvasView);



        final List<String> items=new ArrayList();

        for(int i = 0; i< Ctes.COLORS.length; i++){
            items.add("");
        }

        final Spinner spinner=(Spinner)findViewById(R.id.spinner);
        spinner.setBackgroundColor(Ctes.COLORS[0]);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                state.selectedColor=position;
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
            state.app.paths=app.paths;
            state.app.nodes=app.nodes;
            canvasView.invalidate();

            new AlertDialog.Builder(this)
                    .setMessage("File loaded!!").show();

        }
    }

    private void saveState(){
        App app=new App();
        app.nodes=state.app.nodes;
        app.paths=state.app.paths;

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
        if(AppState.idx==0){
            return;
        }


        AppState.idx--;

        state.app=GsonUtils.fromJson(AppState.history.get(AppState.idx));
        canvasView.invalidate();
        subCanvasView.invalidate();
    }

    public void redo(View view){
        if(AppState.idx==AppState.history.size()-1){
            return;
        }

        AppState.idx++;

        state.app=GsonUtils.fromJson(AppState.history.get(AppState.idx));
        canvasView.invalidate();
        subCanvasView.invalidate();


    }

    public void rotate90right(View view) {
        state.angle+=Ctes.INC_ANGLE;

        if(state.angle==360){
            state.angle=0;
        }

        System.out.println(state.angle);
        canvasView.invalidate();
    }

    public void rotate90left(View view) {
        state.angle-=Ctes.INC_ANGLE;

        if(state.angle==-45){
            state.angle=315;
        }

        System.out.println(state.angle);
        canvasView.invalidate();
    }

    public void clickTest(View view) {
        AppState.dx=0;
        AppState.dy=0;
        AppState.angle=0;
        canvasView.invalidate();
        subCanvasView.invalidate();
    }
}
