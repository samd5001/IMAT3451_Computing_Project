package views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.p14137775.myapplication.R;

public class NoRecordView extends LinearLayout {
    public NoRecordView(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_norecords, this);
    }

    public NoRecordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_norecords, this);
    }
}
