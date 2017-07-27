package com.example.qhsj.dbupgrate.db.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.qhsj.dbupgrate.utils.LogUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * Created by dasu on 2017/4/11.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = MyDatabaseHelper.class.getSimpleName();

    private static final String TEMP_SUFFIX = "_temp_";

    private SQLiteDatabase mReadableDB = null; // readable database
    private SQLiteDatabase mWritableDB = null; // writable database

    private static MyDatabaseHelper mDbHelper;

    private MyDatabaseHelper(Context context) {
        super(context, DatabaseManager.DB_NAME, null, DatabaseManager.DB_VERSION);
    }

    // 单例
    public static synchronized MyDatabaseHelper getInstance(Context context) {
        if (mDbHelper == null) {
            mDbHelper = new MyDatabaseHelper(context);
        }
        return mDbHelper;
    }



    /**
     * 支持下列的数据库升级操作:
     * 1.添加新表
     * 2.删除旧表
     * 3.在旧表里添加新的列属性
     *   新增的列将会以默认的值填充，如果没有设置默认的值，那么会以null填充，默认的值在创建表的sql语句里设置
     * 4.在旧表里删除列属性
     *
     * 以上是所有的表默认的升级操作，如果有针对某张表或某个版本的特定升级需求，那么需要重写那张表的 onUpgrade()，
     * 覆盖基类默认的升级操作。
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtils.e(TAG, "onUpgrade(oldVersion = " + oldVersion + ", newVersion = " + newVersion + ")...");
        //获取旧版本的所有表
        Collection<String> oldTables = BaseDbTable.listTables(db);
        if (oldTables == null || oldTables.size() == 0) {
            LogUtils.e(TAG, "onUpgrade(): no existing tables; calling onCreate()...");
            onCreate(db);
            return;
        }

        //获取所有新表
        Set<String> newTables = DatabaseManager.mAllTables.keySet();

        try {
            db.beginTransaction();
            //删除没有在新版本的数据库里出现的旧表
            HashSet<String> removedTables = new HashSet<String>();
            for (String table : oldTables) {
                if (!newTables.contains(table)) {
                    LogUtils.e(TAG, "onUpgrade(): remove table: " + table);
                    BaseDbTable.dropTable(db, table);
                    removedTables.add(table);
                }
            }
            oldTables.removeAll(removedTables);

            //创建新表或对旧表升级
            BaseDbTable curTable;
            for (String table : newTables) {
                curTable = DatabaseManager.mAllTables.get(table);
                //判断该表是否是新表，新表则创建，旧表则升级
                if (oldTables.contains(table)) {
                    String tempName = getTempTableName(table, oldTables, newTables);
                    curTable.onUpgrade(db, oldVersion, newVersion, tempName);
                } else {
                    curTable.onCreate(db);
                }
            }
            db.setTransactionSuccessful();
        } catch (Throwable e) {
            LogUtils.e(TAG, "onUpgrade(): DB upgrade failed:", e);
            throw new RuntimeException("DB upgrade failed: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }


    /**
     * 获取表的临时命名，方便在升级表时进行拷贝操作
     * 确保临时表名不和任何表名起冲突
     */
    private String getTempTableName(String tableName, Collection<String> oldTableNames, Set<String> newTableNames) {
        String base = tableName + TEMP_SUFFIX;
        if (!oldTableNames.contains(base) && !newTableNames.contains(base)) {
            return base;
        }
        Random random = new Random();
        String tempName;
        for (; ; ) {
            tempName = base + random.nextInt();
            if (!oldTableNames.contains(tempName) && !newTableNames.contains(tempName)) {
                return tempName;
            }
        }
    }


    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
        if (mReadableDB == null || !mReadableDB.isOpen()) {
            try {
                mReadableDB = super.getReadableDatabase();
            } catch (SQLiteException e) {
                mReadableDB = null;
                LogUtils.e(TAG, "getReadableDatabase(): Error opening", e);
            }
        }
        return mReadableDB;
    }

    /**
     * 只会在第一次执行，db创建之后再也不执行
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtils.e(TAG, "onCreate()...");
        Collection<BaseDbTable> tables = DatabaseManager.mAllTables.values();
        Iterator<BaseDbTable> iterator = tables.iterator();
        try {
            db.beginTransaction();// 开始事务
            while (iterator.hasNext()) {
                iterator.next().onCreate(db);
            }
            db.setTransactionSuccessful();// 标志事务成功
        } catch (Throwable e) {
            throw new RuntimeException("DB creation failed: " + e.getMessage());
        } finally {
            db.endTransaction();// 停止事务
        }
    }


    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        if (mWritableDB == null || !mWritableDB.isOpen() || mWritableDB.isReadOnly()) {
            try {
                mWritableDB = super.getWritableDatabase();
            } catch (SQLiteException e) {
                mWritableDB = null;
                LogUtils.e(TAG, "getWritableDatabase(): Error", e);
            }
        }
        return mWritableDB;
    }
}
