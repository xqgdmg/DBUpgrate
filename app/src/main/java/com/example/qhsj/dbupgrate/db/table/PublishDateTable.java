package com.example.qhsj.dbupgrate.db.table;

import android.database.sqlite.SQLiteDatabase;

import com.example.qhsj.dbupgrate.db.utils.BaseDbTable;

/**
 * Created by dasu on 2017/4/12.
 *
 * 发布过干货文章的日期
 */
public class PublishDateTable extends BaseDbTable {
    public static final String TABLE_NAME = "publish_date_table";

    @Override
    public String getName() {
        return TABLE_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    private static PublishDateTable sInstance = new PublishDateTable();

    private PublishDateTable() {}

    public synchronized static PublishDateTable getInstance() {
        return sInstance;
    }

    //列属性
    public static final String DATE = "date";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                    + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + DATE + " TEXT UNIQUE, "
                    + ");";

}
