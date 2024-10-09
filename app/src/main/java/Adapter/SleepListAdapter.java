package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import DTO.sleepDTO;
import fpoly.md19304.app_moblie.R;

public class SleepListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<sleepDTO> sleepList;
    private LayoutInflater inflater;

    public SleepListAdapter(Context context, ArrayList<sleepDTO> sleepList) {
        this.context = context;
        this.sleepList = sleepList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return sleepList.size();
    }

    @Override
    public Object getItem(int position) {
        return sleepList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_sleep_ngu, parent, false);
        }

        TextView tvDate = convertView.findViewById(R.id.tv_date);
        TextView tvSleepTime = convertView.findViewById(R.id.tv_sleep_time);
        TextView tvWakeUpTime = convertView.findViewById(R.id.tv_wake_up_time);
        TextView tvDuration = convertView.findViewById(R.id.tv_duration);

        sleepDTO sleepRecord = sleepList.get(position);

        tvDate.setText(sleepRecord.getDate());
        tvSleepTime.setText(sleepRecord.getSleepTime());
        tvWakeUpTime.setText(sleepRecord.getWakeUpTime());
        tvDuration.setText(sleepRecord.getDuration());

        return convertView;
    }
}
