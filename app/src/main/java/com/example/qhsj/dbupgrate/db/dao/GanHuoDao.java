package com.example.qhsj.dbupgrate.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.qhsj.dbupgrate.db.bean.GanHuoEntity;
import com.example.qhsj.dbupgrate.db.table.Table1;
import com.example.qhsj.dbupgrate.db.utils.DatabaseManager;
import com.example.qhsj.dbupgrate.utils.LogUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 建议每张表对应一个 Dao 类，Dao 类主要封装对该表的基本操作，外部只能通过 Dao 类操作数据库对象，对数据库的内部实现并不清楚
 *
 * 内容提供者
 */
public class GanHuoDao {

    private static final String TAG = GanHuoDao.class.getSimpleName();

    private static final String URI = "content://" + DatabaseManager.AUTHORITY + Table1.getInstance().getName();

    /**
     *  通过id查询
     */
    public static final GanHuoEntity queryById(final Context context, String id) {
        Uri uri = Uri.parse(URI);
        String[] projection = new String[]{
                Table1.DESC,
                Table1.PUBLISHEDAT,
                Table1.TYPE,
                Table1.URL,
                Table1.IMAGES,
                Table1.WHO,
                Table1._ID
        };
        String where = Table1._ID + " = '" + id + "'";
        Cursor c = context.getContentResolver().query(uri, projection, where, null, null);
        GanHuoEntity result = null;
        try {
            if (c.moveToFirst()) {
                result = new GanHuoEntity();
                result.setDesc(c.getString(0));
                result.setPublishedAt(new Date(c.getLong(1)));
                result.setType(c.getString(2));
                result.setUrl(c.getString(3));
                result.setImages(Arrays.asList(c.getString(4).split(",")));
                result.setWho(c.getString(5));
                result.set_id(c.getString(6));
            }
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }
        return result;
    }

    /**
     * 查询所有
     */
    public static final List<GanHuoEntity> queryAll(final Context context) {
        Uri uri = Uri.parse(URI);
        String[] projection = new String[]{
                Table1.DESC,
                Table1.PUBLISHEDAT,
                Table1.TYPE,
                Table1.URL,
                Table1.IMAGES,
                Table1.WHO,
                Table1._ID

        };
        Cursor c = context.getContentResolver().query(uri, projection,null, null, Table1.PUBLISHEDAT + " DESC");
        List<GanHuoEntity> result = new ArrayList<>();
        try {
            if (c.moveToFirst()) {
                LogUtils.d(TAG, "GanHuoDao-->queryAll(): " + c.getCount());
                for (int i = 0; i < c.getCount(); i++) {
                    c.moveToPosition(i);
                    GanHuoEntity ganHuoEntity = new GanHuoEntity();
                    ganHuoEntity.setDesc(c.getString(0));
                    ganHuoEntity.setPublishedAt(new Date(c.getLong(1)));
                    ganHuoEntity.setType(c.getString(2));
                    ganHuoEntity.setUrl(c.getString(3));
                    ganHuoEntity.setImages(Arrays.asList(c.getString(4).split(",")));
                    ganHuoEntity.setWho(c.getString(5));
                    ganHuoEntity.set_id(c.getString(6));
                    result.add(ganHuoEntity);
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

    /**
     * 添加
     */
    public static Uri insert(Context context, GanHuoEntity blog) {
        Uri uri = Uri.parse(URI);
        //插入前先把旧数据删掉，如果有的话
        if (queryById(context, blog.get_id()) != null) {
            String where = Table1._ID + " = " + blog.get_id();
            context.getContentResolver().delete(uri, where, null);
        }
        ContentValues values = new ContentValues();
        values.put(Table1._ID, blog.get_id());
        values.put(Table1.CREATED_AT, blog.getCreatedAt().getTime());
        values.put(Table1.DESC, blog.getDesc());
        values.put(Table1.PUBLISHEDAT, blog.getPublishedAt().getTime());
        values.put(Table1.SOURCE, blog.getSource());
        values.put(Table1.IMAGES, Arrays.toString(blog.getImages().toArray()));
        values.put(Table1.TYPE, blog.getType());
        values.put(Table1.URL, blog.getUrl());
        values.put(Table1.USED, String.valueOf(blog.isUsed()));
        values.put(Table1.WHO, blog.getWho());

        Uri returnUri = context.getContentResolver().insert(uri, values);
        return returnUri;
    }

    /**
     * 删除所有
     */
    public static int deleteAll(Context context) {
        Uri uri = Uri.parse(URI);
        int result = context.getContentResolver().delete(uri, null, null);
        return result;
    }

}
