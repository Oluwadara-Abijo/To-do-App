package com.example.oluwadara.myto_do.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.example.oluwadara.myto_do.data.TaskContract.TaskEntry;

public class TaskProvider extends ContentProvider {

    //Class tag
    private static final String TAG = TaskProvider.class.getSimpleName();

    //Database helper object
    private TaskDbHelper mDbHelper;

    //URI matcher code for the content URI for the tasks table
    private static final int TASKS = 100;

    //URI matcher code for the content URI for a single task in the tasks table
    private static final int TASK_ID = 101;

    //Creates a UriMatcher object.
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        //The calls to addURI() go here, for all of the content URI patterns that the provider
        //should recognize.

        uriMatcher.addURI(TaskContract.CONTENT_AUTHORITY, TaskContract.PATH_TASKS, TASKS);
        uriMatcher.addURI(TaskContract.CONTENT_AUTHORITY, TaskContract.PATH_TASKS + "/#", TASK_ID);
    }

    //Initialize the provider and the database helper object.
    @Override
    public boolean onCreate() {
        mDbHelper = new TaskDbHelper(getContext());
        return false;
    }

    //Perform the query for the given URI. Use the given projection, selection,
    // selection arguments, and sort order.
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = uriMatcher.match(uri);
        switch (match) {
            case TASKS:
                // For the TASKS code, query the tasks table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the tasks table.
                cursor = database.query(TaskEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case TASK_ID:
                // For the TASK_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.tasks/tasks/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = TaskEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the tasks table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(TaskEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                //No match found
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        //Set notification URI on cursor so cursor gets updated if data at the URI changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    //Returns the MIME type of data for the content URI.
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case TASKS:
                return TaskEntry.CONTENT_LIST_TYPE;
            case TASK_ID:
                return TaskEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    //Insert new data into the provider with the given ContentValues.
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case TASKS:
                return insertTask(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a task into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertTask(Uri uri, ContentValues values) {

        // Check that the title is not null
        String title = values.getAsString(TaskEntry.COLUMN_TASK_TITLE);
        if (title == null) {
            throw new IllegalArgumentException("Task requires a title");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(TaskEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(TAG, "Unable to insert row for : " + uri);

        }

        //Notify all listeners of data change for the task content uri
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    //Delete the data at the given selection and selection arguments.
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = uriMatcher.match(uri);
        switch (match) {
            case TASKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted =  database.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TASK_ID:
                // Delete a single row given by the ID in the URI
                selection = TaskEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    //Updates the data at the given selection and selection arguments, with the new ContentValues.
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = uriMatcher.match(uri);
        switch (match) {
            case TASKS:
                // Update all rows that match the selection and selection args
                return updateTask(uri, contentValues, selection, selectionArgs);
            case TASK_ID:
                // Update a single row given by the ID in the URI
                selection = TaskEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateTask(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    private int updateTask(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the title is to be updated, check that the new title value is not null.
        if (values.containsKey(TaskContract.TaskEntry.COLUMN_TASK_TITLE)) {
            String name = values.getAsString(TaskEntry.COLUMN_TASK_TITLE);
            if (name == null) {
                throw new IllegalArgumentException("task requires a name");
            }
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs);

        //Notify all listeners of data change for the task content uri
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
