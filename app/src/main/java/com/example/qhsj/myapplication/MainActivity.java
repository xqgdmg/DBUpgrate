package com.example.qhsj.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.qhsj.myapplication.db.table.PublishDateTable;
import com.example.qhsj.myapplication.db.table.Table1;
import com.example.qhsj.myapplication.db.utils.DatabaseManager;
import com.example.qhsj.myapplication.db.utils.MyDatabaseHelper;

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

                Log.e("chris","onClick");
            }
        });
    }

    private void initView() {
        tv = (TextView) findViewById(R.id.tv);
    }
}
