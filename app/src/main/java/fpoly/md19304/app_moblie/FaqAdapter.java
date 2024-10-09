package fpoly.md19304.app_moblie;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FaqAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> items;

    public FaqAdapter(Context context, List<String> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String item = getItem(position);

        if (item != null && item.startsWith("  ")) {
            // Đây là câu hỏi
            if (convertView == null || convertView.findViewById(R.id.text_view_question) == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_faq, parent, false);
            }
            TextView textView = convertView.findViewById(R.id.text_view_question);
            textView.setText(item.trim());
        } else {
            // Đây là tiêu đề
            if (convertView == null || convertView.findViewById(R.id.faq_header_text) == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_faq_header, parent, false);
            }
            TextView textView = convertView.findViewById(R.id.faq_header_text);
            textView.setText(item);
        }

        return convertView;
    }
}