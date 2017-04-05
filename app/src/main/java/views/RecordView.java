package views;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.p14137775.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import classes.ExerciseRecord;
import classes.Set;
import wrappers.SQLWrapper;

public class RecordView extends LinearLayout{
    public RecordView(Context context, ExerciseRecord record) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_recordview, this);
        init(record);
    }

    public RecordView(Context context, AttributeSet attrs, ExerciseRecord record) {
        super (context, attrs);
        init(record);
    }

    private void init(final ExerciseRecord record) {
        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        TextView date = (TextView) findViewById(R.id.date);
        date.setText(record.getTime());

        try {
            JSONArray sets = new JSONArray(record.getSets());
            for (int i = 0; i < sets.length(); i++) {
                JSONObject jobj = sets.getJSONObject(i);
                Set set = new Set(jobj);
                container.addView(new SetRecordView(getContext(), set));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ImageView delete = (ImageView) findViewById(R.id.delete);
        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setMessage("Do you want to delete this record?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                new SQLWrapper(getContext(), getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE)).deleteRecord(record.getId(), record.getTime());
                                deleteView();
                            }})
                        .setNegativeButton("No", null).show();
            }
        });
    }

    private void deleteView() {
        ((ViewGroup)getParent()).removeView(this);
    }
}