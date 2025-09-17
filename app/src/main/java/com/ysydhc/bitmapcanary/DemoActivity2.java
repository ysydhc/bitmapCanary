package com.ysydhc.bitmapcanary;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class DemoActivity2 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo2);

        findViewById(R.id.nine_path).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
            }
        });
    }
}
