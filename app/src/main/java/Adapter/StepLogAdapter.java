package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import DTO.vanDongDTO;
import fpoly.md19304.app_moblie.R;

public class StepLogAdapter extends BaseAdapter {
    private Context context;
    private List<vanDongDTO> logs;

    public StepLogAdapter(Context context, List<vanDongDTO> logs) {
        this.context = context;
        this.logs = logs;
    }

    @Override
    public int getCount() {
        return logs.size();
    }

    @Override
    public Object getItem(int position) {
        return logs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_step_log, parent, false);
        }

        TextView dateTextView = convertView.findViewById(R.id.date_text_view);
        TextView stepsTextView = convertView.findViewById(R.id.steps_text_view);

        vanDongDTO log = logs.get(position);
        dateTextView.setText(log.getDate());
        stepsTextView.setText("Steps: " + log.getSteps());

        return convertView;
    }
}
