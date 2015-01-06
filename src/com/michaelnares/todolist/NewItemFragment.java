package com.michaelnares.todolist;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by michael on 18/11/2014.
 */
public class NewItemFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.todolist_item, container, false);
        final EditText myEditText = (EditText)view.findViewById(R.id.myEditText);
        myEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER) || (keyCode == KeyEvent.KEYCODE_ENTER) )
                    {
                        String newItem = myEditText.getText().toString();
                        onNewItemAddedListener.onNewItemAdded(newItem);
                        myEditText.setText("");
                        return true;
                    }
                }
                return false;
            }
        });
        return view;
    } //onCreateView method ends here

    public interface OnNewItemAddedListener
    {
    public void onNewItemAdded(String newItem);
    }

    private OnNewItemAddedListener onNewItemAddedListener;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            onNewItemAddedListener = (OnNewItemAddedListener) activity;
        }
        catch (ClassCastException e)
        {
            Log.e("com.michaelnares.todolist", activity.toString() + " must implement OnNewItemAddedListener");
        }

    }

}
