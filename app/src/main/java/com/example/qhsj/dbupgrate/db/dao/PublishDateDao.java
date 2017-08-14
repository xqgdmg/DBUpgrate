package com.example.qhsj.dbupgrate.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.qhsj.dbupgrate.db.table.Table2;
import com.example.qhsj.dbupgrate.db.utils.DatabaseManager;
import com.example.qhsj.dbupgrate.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 建议每张表对应一个 Dao 类，Dao 类主要封装对该表的基本操作，外部只能通过 Dao 类操作数据库对象，对数据库的内部实现并不清楚
 */
public class PublishDateDao {

    private static final String TAG = PublishDateDao.class.getSimpleName();

    private static final String URI = "content://" + DatabaseManager.AUTHORITY + Table2.getInstance().getName();


    public static final List<String> queryAll(final Context context) {
        Uri uri = Uri.parse(URI);
        String[] projection = new String[]{
            Table2.DATE
        };
        Cursor c = context.getContentResolver().query(uri, projection,null, null, Table2.DATE + " DESC");
        List<String> result = new ArrayList<>();
        try {
            if (c.moveToFirst()) {
                LogUtils.e(TAG, "PublishDateDao-->queryAll(): " + c.getCount());
                for (int i = 0; i < c.getCount(); i++) {
                    c.moveToPosition(i);
                    result.add(c.getString(0));
                }
            }
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }
        return result;
    }

    public static final Uri insert(Context context, String date) {
        Uri uri = Uri.parse(URI);
        ContentValues values = new ContentValues();
        values.put(Table2.DATE, date);

        Uri returnUri = context.getContentResolver().insert(uri, values);
        return returnUri;
    }

    public static final int deleteAll(Context context) {
        Uri uri = Uri.parse(URI);
        int result = context.getContentResolver().delete(uri, null, null);
        return result;
    }

}
