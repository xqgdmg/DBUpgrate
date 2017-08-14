package com.example.qhsj.dbupgrate.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.qhsj.dbupgrate.db.utils.DatabaseManager;
import com.example.qhsj.dbupgrate.db.utils.MyDatabaseHelper;
import com.example.qhsj.dbupgrate.utils.LogUtils;

/**
 * 记得在清单文件注册
 */
public class GanHuoContentProvider extends ContentProvider {

    private static final String TAG = GanHuoContentProvider.class.getSimpleName();
    private MyDatabaseHelper mMyDatabaseHelper;

    @Override
    public boolean onCreate() {
        mMyDatabaseHelper = MyDatabaseHelper.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor = null;
        SQLiteDatabase db = mMyDatabaseHelper.getReadableDatabase();
        String tableName = DatabaseManager.matchUri(uri);
        try {
            db.beginTransaction();
            cursor = db.query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
        } catch (SQLException e) {
            LogUtils.e(TAG, "query " + tableName + " error: " + e);
        } finally {
            db.endTransaction();
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri returnUri = null;
        SQLiteDatabase db = mMyDatabaseHelper.getWritableDatabase();
        String tableName = DatabaseManager.matchUri(uri);
        long rowId = -1;
        try {
            db.beginTransaction();
            rowId = db.replace(tableName, null, values);
            return rowId > 0
                    ? ContentUris.withAppendedId(uri, rowId)
                    : ContentUris.withAppendedId(uri, -1);
        } catch (SQLException e) {
            LogUtils.e(TAG, "insert " + tableName + " error: " + e);
        } finally {
            db.endTransaction();
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mMyDatabaseHelper.getWritableDatabase();
        String tableName = DatabaseManager.matchUri(uri);
        int count = 0;
        try {
            db.beginTransaction();
            count = db.delete(tableName, selection, selectionArgs);
        } catch (SQLException e) {
            LogUtils.e(TAG, "delete " + tableName + " error: " + e);
        } finally {
            db.endTransaction();
        }
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mMyDatabaseHelper.getWritableDatabase();
        int count = 0;
        String tableName = DatabaseManager.matchUri(uri);
        try {
            db.beginTransaction();
            count = db.update(tableName, values, selection, selectionArgs);
        } catch (SQLException e) {
            LogUtils.e(TAG, "update " + tableName + " error: " + e);
        } finally {
            db.endTransaction();
        }
        return count;
    }
}
