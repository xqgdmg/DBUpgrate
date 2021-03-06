package com.example.qhsj.dbupgrate.db.table;

import android.database.sqlite.SQLiteDatabase;

import com.example.qhsj.dbupgrate.db.utils.BaseDbTable;

/**
 * 发布过干货文章的日期
 *
 * 创建表，获取表的实例，获取表名
 */
public class Table2 extends BaseDbTable {
    public static final String TABLE_NAME = "table2";

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

    private static Table2 sInstance = new Table2();

    private Table2() {}

    public synchronized static Table2 getInstance() {
        return sInstance;
    }

    //列属性
    public static final String DATE = "date";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                    + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + DATE + " TEXT UNIQUE "
                    + ")";

}
