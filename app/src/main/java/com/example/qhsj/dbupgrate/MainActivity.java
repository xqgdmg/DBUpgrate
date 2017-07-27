package com.example.qhsj.dbupgrate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.qhsj.dbupgrate.db.utils.MyDatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListener();
    }

    private void initListener() {
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatabaseHelper.getInstance(getApplicationContext());
                Log.e("chris","onClick");
            }
        });
    }

    private void initView() {
        tv = (TextView) findViewById(R.id.tv);
    }
}
