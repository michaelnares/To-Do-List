package com.michaelnares.todolist;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class ToDoListActivity extends Activity implements NewItemFragment.OnNewItemAddedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private ArrayAdapter<ToDoItem> aa;
    private ArrayList<ToDoItem> toDoItems;
    private final Context context = this;
    private final ContentResolver cr = getContentResolver();
    private final ContentValues vales = new ContentValues();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //get references to the Fragments
        FragmentManager fm = getFragmentManager();
        ToDoListFragment toDoListFragment = (ToDoListFragment)fm.findFragmentById(R.id.ToDoListFragment);

        //create the array list of to do items
        toDoItems = new ArrayList<ToDoItem>();
        int resID = R.layout.todolist_item;
        aa = new ArrayAdapter<ToDoItem>(context, resID, toDoItems);

        //bind the array adapter to the listview
        toDoListFragment.setListAdapter(aa);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onNewItemAdded(String newItem)
    {
        vales.put(ToDoContentProvider.KEY_TASK, newItem);
        cr.insert(ToDoContentProvider.CONTENT_URI, vales);
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        CursorLoader loader = new CursorLoader(context, ToDoContentProvider.CONTENT_URI, null, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        int keyTaskIndex = cursor.getColumnIndexOrThrow(ToDoContentProvider.KEY_TASK);

        toDoItems.clear();
        while (cursor.moveToNext())
        {
            ToDoItem newItem = new ToDoItem(cursor.getString(keyTaskIndex));
            toDoItems.add(newItem);
            aa.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {

    }
} // ends class
