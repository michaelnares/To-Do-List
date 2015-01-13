package com.michaelnares.todolist;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Michael Nares on 01/01/2015.
 */
public class ToDoItem
{
    private String task;
    private Date created;

    public String getTask()
    {
        return task;
    }

    public Date getCreated()
    {
        return created;
    }

    public ToDoItem(String task)
    {
        this(task, new Date(System.currentTimeMillis()));
    }

    public ToDoItem(String task, Date created)
    {
        this.task = task;
        this.created = created;
    }
    @Override
    public String toString()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        String dateString = sdf.format(created);
        return "(" + dateString + ") " + task;
    }
}
