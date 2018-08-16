package com.example.oluwadara.myto_do.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.oluwadara.myto_do.data.TaskContract.TaskEntry;

class TaskDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tasks.db";

    //Constructor
    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null,
                DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        //Create a string that contains the SQL statement to create the tasks table
        String SQL_CREATE_TASKS_TABLE = "CREATE TABLE " + TaskEntry.TABLE_NAME + " ("
                + TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TaskEntry.COLUMN_TASK_TITLE + " TEXT NOT NULL, "
                + TaskEntry.COLUMN_ALL_DAY + " INTEGER NOT NULL, "
                + TaskEntry.COLUMN_START_DATE + " INTEGER NOT NULL, "
                + TaskEntry.COLUMN_END_DATE + " INTEGER, "
                + TaskEntry.COLUMN_START_TIME + " INTEGER NOT NULL, "
                + TaskEntry.COLUMN_END_TIME + " INTEGER, "
                + TaskEntry.COLUMN_REPEAT + " INTEGER, "
                + TaskEntry.COLUMN_REMINDER + " INTEGER NOT NULL, "
                + TaskEntry.COLUMN_COMMENT + " TEXT);";

        Log.d("Statement>>>", SQL_CREATE_TASKS_TABLE);

        database.execSQL(SQL_CREATE_TASKS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }

}
