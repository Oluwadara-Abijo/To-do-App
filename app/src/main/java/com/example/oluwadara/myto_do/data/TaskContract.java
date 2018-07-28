package com.example.oluwadara.myto_do.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class TaskContract {

    public static final String CONTENT_AUTHORITY = "com.example.oluwadara.myto_do";

    public static final String PATH_TASKS = "tasks";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private TaskContract() {
    }

    /*
      Inner class that defines constant values for the tasks database table
      Each entry in the table represents a single task
     */
    public static class TaskEntry implements BaseColumns {

        //The MIME type of the {@link #CONTENT_URI} for a list of tasks.
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TASKS;

         //The MIME type of the {@link #CONTENT_URI} for a single task.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TASKS;

        //Content URI
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TASKS);

        //Name of database table
        public static final String TABLE_NAME = "tasks";
        //Unique ID number (of type INTEGER) for the task
        public static final String _ID = BaseColumns._ID;
        //Title (of type TEXT) of task
        public static final String COLUMN_TASK_TITLE = "title";
        //Is task all day or not
        public static final String COLUMN_ALL_DAY = "all_day";
        //Task's start date
        public static final String COLUMN_START_DATE = "start_date";
        //Task's end date
        public static final String COLUMN_END_DATE = "end_date";
        //Task's start time
        public static final String COLUMN_START_TIME = "start_time";
        //Task's end time
        public static final String COLUMN_END_TIME = "end_time";
        //Is task on repeat
        public static final String COLUMN_REPEAT = "repeat";
        //Task reminder
        public static final String COLUMN_REMINDER = "reminder";
        //Task comment
        public static final String COLUMN_COMMENT = "comment";

        //Possible values for all day
        public static final int NOT_ALL_DAY = 0;
        public static final int ALL_DAY = 1;

        //Possible values for repeat
        public static final int DO_NOT_REPEAT = 0;
        public static final int REPEAT_DAILY = 1;
        public static final int REPEAT_WEEKLY = 2;
        public static final int REPEAT_MONTHLY = 3;
        public static final int REPEAT_YEARLY = 4;

        //Possible values for reminder on
        public static final int REMINDER_OFF = 0;
        public static final int REMINDER_ON_TIME = 1;
        public static final int REMINDER_10_MIN = 2;
        public static final int REMINDER_30_MIN = 3;
        public static final int REMINDER_1_HOUR = 4;
        public static final int REMINDER_1_DAY = 5;






    }
}
