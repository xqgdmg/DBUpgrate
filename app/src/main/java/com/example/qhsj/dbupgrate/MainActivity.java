package com.example.qhsj.dbupgrate;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.qhsj.dbupgrate.db.table.Table1;
import com.example.qhsj.dbupgrate.db.table.Table2;
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

                Log.e("chris","onClick");

                ContentValues values = new ContentValues();
                values.put("description", "description");// 写中文会乱码？？

                MyDatabaseHelper helper = MyDatabaseHelper.getInstance(getApplicationContext());

                // 这里有个很坑爹的地方，不往里面添加至，直接看不到 database 的文件夹，什么都没有！！！
                // MyDatabaseHelper 也不执行，草泥马。。。。
                helper.getWritableDatabase().insert(Table1.TABLE_NAME,null,values);
                values.clear();

                values.put("date", "1970.01.01");
                helper.getWritableDatabase().insert(Table2.TABLE_NAME,null,values);

            }
        });
    }

    private void initView() {
        tv = (TextView) findViewById(R.id.tv);
    }
}
