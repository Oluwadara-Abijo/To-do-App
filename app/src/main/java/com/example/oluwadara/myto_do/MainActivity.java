package com.example.oluwadara.myto_do;

import android.content.ContentUris;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.oluwadara.myto_do.data.TaskContract.TaskEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private TaskCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set on click listener for addTask button
        FloatingActionButton addTask = findViewById(R.id.add_fab);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewToDo = new Intent(MainActivity.this,
                        AddNewTaskActivity.class);
                startActivity(addNewToDo);
            }
        });

        //Initialize adapter
        mCursorAdapter = new TaskCursorAdapter(this, null,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        //List view that shows list of tasks
        ListView taskListView = findViewById(R.id.listView);
        taskListView.setAdapter(mCursorAdapter);

        //Empty view that shows when there are no tasks
        View emptyView = findViewById(R.id.empty_view);
        taskListView.setEmptyView(emptyView);

        //Initialize the loader
        getSupportLoaderManager().initLoader(0, null, this);

        //Set on item click listener on the list view
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, AddNewTaskActivity.class);
                //Pass the uri of the selected item to the add task activity
                Uri currentTaskUri = ContentUris.withAppendedId(TaskEntry.CONTENT_URI, id);
                intent.setData(currentTaskUri);
                //Set on click listener on each list item
                startActivity(intent);
            }
        });
    }

    private void insertTask() {

        String currentDate = new SimpleDateFormat("EEE, MMM dd, yyyy",
                Locale.getDefault()).format(new Date());

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_TASK_TITLE, "Nanodegree Class");
        values.put(TaskEntry.COLUMN_ALL_DAY, TaskEntry.NOT_ALL_DAY);
        values.put(TaskEntry.COLUMN_START_DATE, currentDate);
        values.put(TaskEntry.COLUMN_END_DATE, currentDate);
        values.put(TaskEntry.COLUMN_START_TIME, "09:00");
        values.put(TaskEntry.COLUMN_END_TIME, "13:00");
        values.put(TaskEntry.COLUMN_REPEAT, TaskEntry.REPEAT_DAILY);
        values.put(TaskEntry.COLUMN_REMINDER, TaskEntry.REMINDER_10_MIN);
        values.put(TaskEntry.COLUMN_COMMENT, "Finish a lesson");

        // Insert the new row, returning the primary key value of the new row
        Uri newUri = getContentResolver().insert(TaskEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_dummy_data:
                Toast.makeText(this, R.string.task_added, Toast.LENGTH_SHORT).show();
                insertTask();
                return true;
            case R.id.action_delete_all_entries:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {BaseColumns._ID,
                TaskEntry.COLUMN_TASK_TITLE,
                TaskEntry.COLUMN_START_DATE,
                TaskEntry.COLUMN_END_DATE,
                TaskEntry.COLUMN_START_TIME,
                TaskEntry.COLUMN_END_TIME,
                TaskEntry.COLUMN_COMMENT
        };
        return new CursorLoader(this, TaskEntry.CONTENT_URI, projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
