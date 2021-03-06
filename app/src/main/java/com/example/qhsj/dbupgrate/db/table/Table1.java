package com.example.qhsj.dbupgrate.db.table;

import android.database.sqlite.SQLiteDatabase;

import com.example.qhsj.dbupgrate.db.utils.BaseDbTable;

/**
 * 存储Gank.io上的干货，干货包括github开源项目，技术文章，视频，图片等
 *
 * 创建表，获取表的实例，获取表名
 */
public class Table1 extends BaseDbTable {
    public static final String TABLE_NAME = "table1";

    // 父类方法
    @Override
    public String getName() {
        return TABLE_NAME;
    }

    // 父类方法
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    private static Table1 sInstance = new Table1();

    private Table1() {
    }

    public synchronized static Table1 getInstance() {
        return sInstance;
    }

    //列属性
    public static final String _ID = "_id";
    public static final String CREATED_AT = "created_at";
    public static final String DESC = "description";
    public static final String PUBLISHEDAT = "published_at";
    public static final String IMAGES = "images";
    public static final String TYPE = "type";  //区分干货类型
    public static final String SOURCE = "source";
    public static final String URL = "url";  //干货跳转链接
    public static final String WHO = "who";
    public static final String USED = "used";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                    + _ID + " integer primary key autoincrement, "
                    + CREATED_AT + " INTEGER, "
                    + DESC + " TEXT, "
                    + PUBLISHEDAT + " INTEGER, "
                    + IMAGES + " TEXT, "
                    + TYPE + " TEXT, "
                    + SOURCE + " TEXT, "
                    + URL + " TEXT, "
                    + WHO + " TEXT, "
                    + USED + " TEXT"
                    + ")";

}
