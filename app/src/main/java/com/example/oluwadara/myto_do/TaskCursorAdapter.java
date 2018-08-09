package com.example.oluwadara.myto_do;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.oluwadara.myto_do.data.TaskContract.TaskEntry;

public class TaskCursorAdapter extends CursorAdapter {

    public TaskCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_task, viewGroup, false);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Initialize text views to inflate
        TextView titleTextView = view.findViewById(R.id.textView_title);
        TextView startDateTextView = view.findViewById(R.id.textView_startDate);
        TextView endDateTextView = view.findViewById(R.id.textView_endDate);
        TextView startTimeTextView = view.findViewById(R.id.textView_startTime);
        TextView endTimeTextView = view.findViewById(R.id.textView_endTime);
        TextView commentTextView = view.findViewById(R.id.textView_comment);

        //Extract properties from cursor
        String title = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_TASK_TITLE));
        String startDate = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_START_DATE));
        String endDate = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_END_DATE));
        String startTime = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_START_TIME));
        String endTime = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_END_TIME));
        String comment = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_COMMENT));

        //Populate text views with extracted properties
        titleTextView.setText(title);
        startDateTextView.setText(startDate);
        endDateTextView.setText(endDate);
        startTimeTextView.setText(startTime);
        endTimeTextView.setText(endTime);
        if (comment.equals("")) {
            commentTextView.setVisibility(View.GONE);
        } else {
            commentTextView.setText(comment);
        }

    }
}
