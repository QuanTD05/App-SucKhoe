package Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import DTO.longBietOnDTO;
import fpoly.md19304.app_moblie.R;

public class mentalAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<longBietOnDTO> listLBO;
    private OnItemEditListener onItemEditListener;

    public mentalAdapter(Context context, ArrayList<longBietOnDTO> listLBO, OnItemEditListener onItemEditListener) {
        this.context = context;
        this.listLBO = listLBO;
        this.onItemEditListener = onItemEditListener;
    }

    @Override
    public int getCount() {
        return listLBO.size();
    }

    @Override
    public Object getItem(int i) {
        return listLBO.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View v, ViewGroup parent) {
        View row;
        if (v != null) {
            row = v;
        } else {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.item_longbieton, null);
        }

        longBietOnDTO objCat = listLBO.get(i);
        TextView tvNgay = row.findViewById(R.id.tv_ngay);
        TextView tvDieuBietOn = row.findViewById(R.id.tv_dieubieton);
        ImageView btnUpdate = row.findViewById(R.id.btn_upadte2);

        // Định dạng ngày tháng
        SimpleDateFormat sdf = new SimpleDateFormat(" HH:mm dd/MM/yyyy ", Locale.getDefault());

        String formattedNgay = sdf.format(objCat.getNgay());

        tvNgay.setText(formattedNgay);
        tvDieuBietOn.setText(objCat.getDieuBietOn());

        btnUpdate.setOnClickListener(v1 -> {
            if (onItemEditListener != null) {
                onItemEditListener.onItemEdit(i);
            }
        });

        return row;
    }

    // Method to update an item
    public void updateItem(int position, longBietOnDTO newItem) {
        if (position >= 0 && position < listLBO.size()) {
            listLBO.set(position, newItem);
            notifyDataSetChanged();
        }
    }

    // Interface for handling edit button clicks
    public interface OnItemEditListener {
        void onItemEdit(int position);
    }
}
