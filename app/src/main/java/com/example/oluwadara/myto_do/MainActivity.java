package com.example.oluwadara.myto_do;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private ActionMode mActionMode;

    private TaskCursorAdapter mCursorAdapter;

    private Uri mCurrentTaskUri;

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
                Intent intent = new Intent(MainActivity.this,
                        AddNewTaskActivity.class);
                //Pass the uri of the selected item to the add task activity
                mCurrentTaskUri = ContentUris.withAppendedId(TaskEntry.CONTENT_URI, id);
                intent.setData(mCurrentTaskUri);
                //Set on click listener on each list item
                startActivity(intent);
            }
        });

        //Enable the contextual action mode for individual views
        final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate a menu resource providing context menu items
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.context_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_delete_entry) {
                    showDeleteConfirmationDialog();
                    mode.finish();
                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mActionMode = null;
            }
        };

        //Set on item long click listener on the list view
        taskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                mCurrentTaskUri = ContentUris.withAppendedId(TaskEntry.CONTENT_URI, id);
                Log.d(">>>", "on Long Click: " + mCurrentTaskUri);
                if (mActionMode != null) {
                    return false;
                }
                // Start the CAB using the ActionMode.Callback defined above
                mActionMode = MainActivity.this.startSupportActionMode(mActionModeCallback);
                view.setSelected(true);
                return true;
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

    @NonNull
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
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    private void showDeleteConfirmationDialog() {
        Log.d(">>>", "Inside Dialog: " + mCurrentTaskUri);

        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                Log.d(">>>", "Before Delete: " + mCurrentTaskUri);

                deletePet();

                Log.d(">>>", "After Delete: " + mCurrentTaskUri);

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        if (mCurrentTaskUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentTaskUri, null,
                    null);
            //Show a toast message if the pet was deleted successfully or not
            if (rowsDeleted == 0) {
                Toast.makeText(this, R.string.editor_delete_task_failed,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.editor_delete_task_successful,
                        Toast.LENGTH_SHORT).show();
            }

        }
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
}
