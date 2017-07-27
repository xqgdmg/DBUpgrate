package com.example.qhsj.dbupgrate.db.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.text.TextUtils;
import com.example.qhsj.dbupgrate.utils.LogUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * 所有的数据库表都得实现该类，该类提供了数据库表升级的默认操作
 * 如果该升级操作不满足需求，子类可以重写自己实现特定的升级操作
 * 具体支持的升级操作见下文注释
 *
 * BaseColumns 有 _id 和 _count
 */
public abstract class BaseDbTable implements BaseColumns {

    private static final String TAG = "chris";

    /**
     * 数据库表名
     *
     * @return
     */
    public abstract String getName();

    /**
     * 数据库创建时，创建数据库表
     *
     * @param db
     */
    public abstract void onCreate(SQLiteDatabase db);

    //sqlite 会将所有的表信息存在 sqlite_master 这张表里，下面 sql 的作用是查询所有的表名，可用于获取所有旧的数据库表
    //方便升级时参照新的数据库表做相应操作
    private static final String SQL_SELECT_ALL_TABLES =
            "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' AND name NOT LIKE 'android%'";
    //sqlite_master 表中存储数据库表名的列属性名称
    private static final String COLUMN_TABLE_NAME = "name";
    //查询某张表的所有列属性名称
    private static final String SQL_SELECT_TABLE_ALL_COLUMNS = "SELECT * FROM %s LIMIT 1";
    //删除某张表
    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS %s";
    //更改某张表的表名
    private static final String SQL_ALTER_TABLE_NAME = "ALTER TABLE %s RENAME TO %s";
    //嵌套sql语句，用于将旧表里的数据全部拷贝到新表里，所以第2，3个%s应传入相同的字符串
    private static final String SQL_COPY_TABLE_DATA = "INSERT INTO %s (%s) SELECT %s FROM %s";

    /**
     * 数据库的升级实现
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     * @param tempName
     */
    protected void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion, String tempName) {
        LogUtils.e(TAG, getName() + ".onUpgrade(oldVersion = " + oldVersion + ", newVersion = " + newVersion + ", tempName = " + tempName + ")");

        //先将旧表改名，方便建新表，因为同一张表升级表名一般不会变
        renameTable(db, getName(), tempName);
        //创建新表
        onCreate(db);
        //拷贝旧表数据到新表
        copyTableData(db, tempName, getName());
        //删除旧表
        dropTable(db, tempName);

    }

    /**
     * 查询数据库中的所有表
     *
     * @param db
     * @return
     */
    static Collection<String> listTables(SQLiteDatabase db) {
        LogUtils.e(TAG, "listTables()...");
        Cursor cursor = db.rawQuery(SQL_SELECT_ALL_TABLES, null);
        if (cursor == null || !cursor.moveToFirst()) {
            if (cursor != null) {
                cursor.close();
            }
            LogUtils.e(TAG, "listTables(): there are no tables in db.");
            return null;
        }

        int tableNameIndex = cursor.getColumnIndex(COLUMN_TABLE_NAME);
        HashSet<String> tables = new HashSet<String>(cursor.getCount());
        do {
            tables.add(cursor.getString(tableNameIndex));
        } while (cursor.moveToNext());
        cursor.close();
        LogUtils.e(TAG, "listTables(): " + tables);
        return tables;
    }

    /**
     * 查询 table 表中所有的列属性名称
     *
     * @param db
     * @param table
     * @return
     */
    static List<String> listColumns(SQLiteDatabase db, String table) {
        LogUtils.e(TAG, "listColumns(" + table + ")...");
        //cursor 返回的是指向第一个行及列属性那行的数据
        Cursor cursor = db.rawQuery(String.format(SQL_SELECT_TABLE_ALL_COLUMNS, table), null);
        if (cursor == null) {
            LogUtils.e(TAG, "listColumns(" + table + "): no columns in table " + table);
            return null;
        }
        List<String> columns = Arrays.asList(cursor.getColumnNames());
        cursor.close();
        return columns;
    }

    /**
     * 删除某张表
     *
     * @param db
     * @param table
     */
    static void dropTable(SQLiteDatabase db, String table) {
        LogUtils.e(TAG, "dropTable(" + table + ")...");
        db.execSQL(String.format(SQL_DROP_TABLE, table));
    }

    /**
     * 重命名某张表
     *
     * @param db
     * @param oldName
     * @param newName
     */
    static void renameTable(SQLiteDatabase db, String oldName, String newName) {
        LogUtils.e(TAG, "renameTable(" + oldName + ", " + newName + ")...");
        db.execSQL(String.format(SQL_ALTER_TABLE_NAME, oldName, newName));
    }

    /**
     * 拷贝 oldTable 表里的数据到 newTable
     *
     * @param db
     * @param oldTable
     * @param newTable
     */
    static void copyTableData(SQLiteDatabase db, String oldTable, String newTable) {
        LogUtils.e(TAG, "copyTableData(" + oldTable + ", " + newTable + ")...");
        //先将新表数据清空
        db.delete(newTable, null, null);
        //然后获取新旧表的列属性，查看旧表里哪些列属性被删除了
        ArrayList<String> oldColumns = new ArrayList<String>(listColumns(db, oldTable));
        List<String> newColumns = listColumns(db, newTable);

        // 仅保留包含在指定集合中的此列表中的元素。换句话说，从该列表中移除所有未包含在指定集合中的元素。
        oldColumns.retainAll(newColumns);
        //根据公共的列属性对旧表数据进行拷贝
        String commonColumns = TextUtils.join(",", oldColumns);// 返回一个字符串，包含令牌加入分隔符。
        LogUtils.e(TAG, "copyTableData: Common columns: " + commonColumns);
        db.execSQL(String.format(SQL_COPY_TABLE_DATA, newTable, commonColumns, commonColumns, oldTable));
    }
}
