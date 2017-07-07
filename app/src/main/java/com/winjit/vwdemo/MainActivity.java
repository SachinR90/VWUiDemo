package com.winjit.vwdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.SnapHelper;
import android.view.Display;

public class MainActivity extends AppCompatActivity {
    CenterEdgeItemsRecyclerView recyclerView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (CenterEdgeItemsRecyclerView) findViewById(R.id.wrv);
        recyclerView.setCenterEdgeItems(true);
        HalfCurveLayoutManager manager = new HalfCurveLayoutManager(this, 7.0f);
        recyclerView.setLayoutManager(manager);
        SnapHelper helper = new LinearSnapHelper();
        // Set the adapter
        RVAdapter adapter = new RVAdapter(this);
        recyclerView.setAdapter(adapter);
        helper.attachToRecyclerView(recyclerView);
    }
    
    private static Point getScreenSize(Context activity) {
        Display display = ((Activity) activity).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }
    
    public static int getScreenWidth(Context activity) {
        return getScreenSize(activity).x;
    }
    
    public static int getScreenHeight(Context activity) {
        return getScreenSize(activity).y;
    }
}
