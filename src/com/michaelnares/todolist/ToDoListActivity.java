package com.michaelnares.todolist;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class ToDoListActivity extends Activity implements NewItemFragment.OnNewItemAddedListener {

    private ArrayAdapter<String> aa;
    private ArrayList<String> toDoItems;


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
        toDoItems = new ArrayList<String>();

        aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, toDoItems);

        //bind the array adapter to the listview
        toDoListFragment.setListAdapter(aa);
    }

    @Override
    public void onNewItemAdded(String newItem)
    {
        toDoItems.add(newItem);
        aa.notifyDataSetChanged();
    }
} // ends class
