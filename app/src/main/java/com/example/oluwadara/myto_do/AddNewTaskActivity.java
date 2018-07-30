package com.example.oluwadara.myto_do;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.oluwadara.myto_do.data.TaskContract.TaskEntry;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddNewTaskActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //UI Elements
    private EditText mTitleEditText;
    private TextView mStartDateTextView;
    private TextView mEndDateTextView;
    private TextView mStartTimeTextView;
    private TextView mEndTimeTextView;
    private CheckBox mAllDayCheckBox;
    private Spinner mRepeatSpinner;
    private Spinner mReminderSpinner;
    private EditText mCommentEditText;

    private int mAllDay = TaskEntry.NOT_ALL_DAY;
    private int mRepeat = TaskEntry.DO_NOT_REPEAT;
    private int mReminder = TaskEntry.REMINDER_OFF;

    private DatePickerDialog.OnDateSetListener mStartDateSetListener;
    private DatePickerDialog.OnDateSetListener mEndDateSetListener;
    private TimePickerDialog.OnTimeSetListener mStartTimeSetListener;
    private TimePickerDialog.OnTimeSetListener mEndTimeSetListener;

    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private Uri mCurrentTaskUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);

        mTitleEditText = findViewById(R.id.title_edit_text);
        mStartDateTextView = findViewById(R.id.start_date_text_view);
        mEndDateTextView = findViewById(R.id.end_date_text_view);
        mStartTimeTextView = findViewById(R.id.start_time_text_view);
        mEndTimeTextView = findViewById(R.id.end_time_text_view);
        mAllDayCheckBox = findViewById(R.id.all_day_check_box);
        mRepeatSpinner = findViewById(R.id.repeat_spinner);
        mReminderSpinner = findViewById(R.id.reminder_spinner);
        mCommentEditText = findViewById(R.id.comment_edit_text);

        //Set current date on the date text views
        String currentDate = dateFormat.format(new Date());
        mStartDateTextView.setText(currentDate);
        mEndDateTextView.setText(currentDate);

        //Set current time on the time text views
        mStartTimeTextView.setText(R.string.default_time);
        mEndTimeTextView.setText(R.string.default_time);

        pickStartDate();
        pickEndDate();
        pickStartTime();
        pickEndTime();
        isAllDay();
        setUpRepeatSpinner();
        setUpReminderSpinner();

        //Get the intent that starts the activity
        Intent intent = getIntent();
        mCurrentTaskUri = intent.getData();
        Log.d(">>>", "Uri: " + mCurrentTaskUri);
        if (mCurrentTaskUri == null) {
            setTitle(getString(R.string.add_new_task_activity_title));
        } else {
            setTitle(getString(R.string.edit_task_activity_title));
        }

        //Initialize loader
        getSupportLoaderManager().initLoader(0, null, this);

    }

    private void pickStartTime() {
        View.OnClickListener startTimeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog startTimePickerDialog = new TimePickerDialog(
                        AddNewTaskActivity.this,
                        mStartTimeSetListener, hour, minute,
                        true);
                startTimePickerDialog.show();
            }
        };
        mStartTimeTextView.setOnClickListener(startTimeClickListener);

        mStartTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                Log.d("Time>>>", "" + hour + minute);
                String timePicked = getTimeString(hour, minute);

                mStartTimeTextView.setText(timePicked);
            }
        };
    }

    private void pickEndTime() {
        View.OnClickListener endTimeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog endTimePickerDialog = new TimePickerDialog(
                        AddNewTaskActivity.this,
                        mEndTimeSetListener, hour, minute,
                        true);
                endTimePickerDialog.show();
            }
        };
        mEndTimeTextView.setOnClickListener(endTimeClickListener);

        mEndTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                Log.d("Time>>>", "" + hour + minute);
                String timePicked = getTimeString(hour, minute);
                mEndTimeTextView.setText(timePicked);
            }
        };
    }

    private void pickStartDate() {
        View.OnClickListener startDateClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddNewTaskActivity.this, mStartDateSetListener, year, month, day);
                datePickerDialog.show();
            }
        };
        mStartDateTextView.setOnClickListener(startDateClickListener);

        mStartDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month += 1;
                String startDate = getDateString(year, month, day);
                mStartDateTextView.setText(startDate);
                mEndDateTextView.setText(startDate);
            }
        };
    }

    private void pickEndDate() {
        View.OnClickListener endDateClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddNewTaskActivity.this, mEndDateSetListener, year, month, day);
                datePickerDialog.show();
            }
        };
        mEndDateTextView.setOnClickListener(endDateClickListener);

        mEndDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month += 1;
                String endDate = getDateString(year, month, day);
                mEndDateTextView.setText(endDate);
            }
        };
    }

    private String getDateString(int year, int month, int day) {
        String date = day + "/" + month + "/" + year;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date myDate = null;
        try {
            myDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return this.dateFormat.format(myDate);
    }

    private String getTimeString(int hour, int minute) {
        String time = hour + ":" + minute;

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date myTime = null;
        try {
            myTime = timeFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return this.timeFormat.format(myTime);
    }

    //Define checkbox menu_new_task
    private void isAllDay() {
        if (mAllDayCheckBox.isChecked()) {
            mAllDay = TaskEntry.ALL_DAY;
        } else {
            mAllDay = TaskEntry.NOT_ALL_DAY;
        }
    }

    //Set up spinner that allows user set repeat reminder on task
    private void setUpRepeatSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> repeatAdapter = ArrayAdapter.createFromResource(this,
                R.array.repeat_options_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        repeatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mRepeatSpinner.setAdapter(repeatAdapter);
        //Set the integer selected to the constant values
        mRepeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selection = (String) adapterView.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.repeat_daily))) {
                        mRepeat = TaskEntry.REPEAT_DAILY;
                    } else if (selection.equals(getString(R.string.repeat_weekly))) {
                        mRepeat = TaskEntry.REPEAT_WEEKLY;
                    } else if (selection.equals(getString(R.string.repeat_monthly))) {
                        mRepeat = TaskEntry.REPEAT_MONTHLY;
                    } else if (selection.equals(getString(R.string.repeat_yearly))) {
                        mRepeat = TaskEntry.REPEAT_YEARLY;
                    } else {
                        mRepeat = TaskEntry.DO_NOT_REPEAT;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mRepeat = TaskEntry.DO_NOT_REPEAT;
            }
        });
    }

    //Set up spinner that allows user set repeat reminder on task
    private void setUpReminderSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> reminderAdapter = ArrayAdapter.createFromResource(this,
                R.array.reminder_options_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        reminderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mReminderSpinner.setAdapter(reminderAdapter);
        //Set the integer selected to the constant values
        mReminderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selection = (String) adapterView.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.reminder_on_time))) {
                        mRepeat = TaskEntry.REMINDER_ON_TIME;
                    } else if (selection.equals(getString(R.string.reminder_10_min))) {
                        mRepeat = TaskEntry.REMINDER_10_MIN;
                    } else if (selection.equals(getString(R.string.reminder_30_min))) {
                        mRepeat = TaskEntry.REMINDER_30_MIN;
                    } else if (selection.equals(getString(R.string.reminder_1_hour))) {
                        mRepeat = TaskEntry.REMINDER_1_HOUR;
                    } else if (selection.equals(getString(R.string.reminder_1_day))) {
                        mRepeat = TaskEntry.REMINDER_1_DAY;
                    } else {
                        mRepeat = TaskEntry.REMINDER_OFF;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mRepeat = TaskEntry.REMINDER_OFF;
            }
        });
    }


    //Get task details from user and add task to database
    private void addTask() {
        String taskTitle = mTitleEditText.getText().toString().trim();
        String startDate = mStartDateTextView.getText().toString();
        String endDate = mEndDateTextView.getText().toString();
        String startTime = mStartTimeTextView.getText().toString();
        String endTime = mEndTimeTextView.getText().toString();
        String comment = mCommentEditText.getText().toString();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_TASK_TITLE, taskTitle);
        values.put(TaskEntry.COLUMN_ALL_DAY, mAllDay);
        values.put(TaskEntry.COLUMN_START_DATE, startDate);
        values.put(TaskEntry.COLUMN_END_DATE, endDate);
        values.put(TaskEntry.COLUMN_START_TIME, startTime);
        values.put(TaskEntry.COLUMN_END_TIME, endTime);
        values.put(TaskEntry.COLUMN_REPEAT, mRepeat);
        values.put(TaskEntry.COLUMN_REMINDER, mReminder);
        values.put(TaskEntry.COLUMN_COMMENT, comment);

        Uri newUri = getContentResolver().insert(TaskEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newUri == null) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, R.string.editor_insert_task_failed, Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, R.string.editor_insert_task_successful, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_new_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                addTask();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {BaseColumns._ID,
                TaskEntry.COLUMN_TASK_TITLE,
                TaskEntry.COLUMN_ALL_DAY,
                TaskEntry.COLUMN_START_DATE,
                TaskEntry.COLUMN_END_DATE,
                TaskEntry.COLUMN_START_TIME,
                TaskEntry.COLUMN_END_TIME,
                TaskEntry.COLUMN_REPEAT,
                TaskEntry.COLUMN_REMINDER,
                TaskEntry.COLUMN_COMMENT
        };
        return new CursorLoader(this, mCurrentTaskUri, projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            //Extract properties from cursor
            String title = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_TASK_TITLE));
            String startDate = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_START_DATE));
            String endDate = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_END_DATE));
            String startTime = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_START_TIME));
            String endTime = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_END_TIME));
            String comment = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_COMMENT));
            int allDay = cursor.getInt(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_ALL_DAY));
            int reminder = cursor.getInt(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_REMINDER));
            int repeat = cursor.getInt(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_REPEAT));


            //Populate text views with extracted properties
            mTitleEditText.setText(title);
            mStartDateTextView.setText(startDate);
            mEndDateTextView.setText(endDate);
            mStartTimeTextView.setText(startTime);
            mEndTimeTextView.setText(endTime);
            mCommentEditText.setText(comment);

            //All day is a checkbox, so map the value from the database to its state
            //Call setChecked so that checkbox shows all day option
            if (allDay == TaskEntry.ALL_DAY) {
                mAllDayCheckBox.setChecked(true);
            } else {
                mAllDayCheckBox.setChecked(false);
            }

            //Reminder is a spinner. Map the value from the database to one of the dropdown options
            //Then call setSelection() so that option is displayed on screen as the current selection
            switch (reminder) {
                case TaskEntry.REMINDER_ON_TIME:
                    mReminderSpinner.setSelection(1);
                    break;
                case TaskEntry.REMINDER_10_MIN:
                    mReminderSpinner.setSelection(2);
                    break;
                case TaskEntry.REMINDER_30_MIN:
                    mReminderSpinner.setSelection(3);
                    break;
                case TaskEntry.REMINDER_1_HOUR:
                    mReminderSpinner.setSelection(4);
                    break;
                case TaskEntry.REMINDER_1_DAY:
                    mReminderSpinner.setSelection(5);
                    break;
                default:
                    mReminderSpinner.setSelection(0);
                    break;
            }

            //Repeat is a spinner. Map the value from the database to one of the dropdown options
            //Then call setSelection() so that option is displayed on screen as the current selection
            switch (repeat) {
                case TaskEntry.REPEAT_DAILY:
                    mRepeatSpinner.setSelection(1);
                    break;
                case TaskEntry.REPEAT_WEEKLY:
                    mRepeatSpinner.setSelection(2);
                    break;
                case TaskEntry.REPEAT_MONTHLY:
                    mRepeatSpinner.setSelection(3);
                    break;
                case TaskEntry.REPEAT_YEARLY:
                    mRepeatSpinner.setSelection(4);
                    break;
                default:
                    mRepeatSpinner.setSelection(0);
                    break;
            }

        }


    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
