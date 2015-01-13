package com.michaelnares.todolist;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Michael Nares on 01/01/2015.
 */
public class ToDoContentProvider extends ContentProvider
{
    public static final Uri CONTENT_URI = Uri.parse("content://com.michaelnares.todoprovider/todoitems");
    public static final String KEY_ID = "_id";
    public static final String KEY_TASK = "task";
    public static final String KEY_CREATION_DATE = "creation_date";

    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private final Context context = getContext();
    private final ContentResolver contentResolver = context.getContentResolver();
    private static final int ALLROWS = 1;
    private static final int SINGLE_ROW = 2;
    private static final UriMatcher uriMatcher;
    private final SQLiteDatabase db = mySQLiteOpenHelper.getWritableDatabase();

    //Populates the UriMatcher, so a request ending in 'todoitems will respond to a request for all items,
    // and 'todoitems/[rowID]' represents a single row
    static
    {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.michaelnares.todoprovider", "todoitems", ALLROWS);
        uriMatcher.addURI("com.michaelnares.todoprovider", "todoitems/#", SINGLE_ROW);
    }

    @Override
    public boolean onCreate()
    {
    // creates new database
    mySQLiteOpenHelper = new MySQLiteOpenHelper(getContext(), MySQLiteOpenHelper.DATABASE_NAME, null, MySQLiteOpenHelper.DATABASE_VERSION);

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
    String groupBy = null;
    String having = null;

    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
    queryBuilder.setTables(MySQLiteOpenHelper.DATABASE_TABLE);

    // If this is a row query, limit the result set to the passed in row.
    switch (uriMatcher.match(uri))
        {
        case SINGLE_ROW:
            String rowID = uri.getPathSegments().get(1);
            queryBuilder.appendWhere(KEY_ID + "=" + rowID);
            default: break;
        }

        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, groupBy, having, sortOrder);

        return cursor;
    }

    @Override
    public String getType(Uri uri)
    {
        switch (uriMatcher.match(uri))
        {
            case ALLROWS: return "vnd.android.cursor.dir/vnd.michaelnares.todos";
            case SINGLE_ROW: return "vnd.android.cursor.item/vnd.michaelnares.todos";
            default: throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues)
    {
        // Used to specify the name of the column that can be set to null.
        String nullColumnHack = null;

        long id = db.insert(MySQLiteOpenHelper.DATABASE_TABLE, nullColumnHack, contentValues);

        if (id > -1)
        {
            Uri insertedId = ContentUris.withAppendedId(CONTENT_URI, id);
            contentResolver.notifyChange(insertedId, null);

            return insertedId;
        }
        else return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        //If this is a row URL, limit the deletion to the specified row.
            switch (uriMatcher.match(uri))
                {
                    case SINGLE_ROW:
                    String rowID = uri.getPathSegments().get(1);
                    selection = KEY_ID + "=" + rowID
                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
                        default:break;
                }

        // To return the number of deleted items, you must specify a where clause.  To delete all rows and return a value, pass in "1".
        if (selection == null) selection = "1";

        int deleteCount = db.delete(MySQLiteOpenHelper.DATABASE_TABLE, selection, selectionArgs);

        contentResolver.notifyChange(uri, null);

        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs)
    {
        switch (uriMatcher.match(uri))
        {
            case SINGLE_ROW:
                String rowId = uri.getPathSegments().get(1);
                selection = KEY_ID + "=" + rowId
                + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
                default: break;
        }

        int updateCount = db.update(MySQLiteOpenHelper.DATABASE_TABLE, contentValues, selection, selectionArgs);

        contentResolver.notifyChange(uri, null);

        return updateCount;

    }

    private static class MySQLiteOpenHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "todoDatabase.db";
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_TABLE = "todoItemTable";
        private static final String DATABASE_CREATE = "create table " + DATABASE_TABLE + " (" + KEY_ID
        + " integer primary key autoincrement, " + KEY_TASK + " text not null, " + KEY_CREATION_DATE + "long);";

        public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
        {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase)
        {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2)
        {
        //log the version upgrade
        Log.w("TaskDBAdapter", "Upgrading from version " + i + " to " + i2 + ", which will destroy all old data.");
        sqLiteDatabase.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);
        onCreate(sqLiteDatabase);
        }
    }
}
