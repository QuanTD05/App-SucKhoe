package fpoly.md19304.app_moblie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StepLogAdapter extends RecyclerView.Adapter<StepLogAdapter.StepLogViewHolder> {

    private Context context;
    private List<StepLog> stepLogList;
    private StepLogDAO stepLogDAO;

    public StepLogAdapter(Context context, List<StepLog> stepLogList) {
        this.context = context;
        this.stepLogList = stepLogList;
        this.stepLogDAO = new StepLogDAO(context);
    }

    @NonNull
    @Override
    public StepLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.step_log_item, parent, false);
        return new StepLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepLogViewHolder holder, int position) {
        StepLog stepLog = stepLogList.get(position);
        holder.txtDate.setText("Ngày: " + stepLog.getDate());
        holder.txtSteps.setText("Số bước chân: " + stepLog.getSteps());
        holder.txtStepGoal.setText("Mục tiêu bước chân: " + stepLog.getStepGoal());

        // Cập nhật trạng thái mục tiêu
        if (stepLog.getSteps() >= stepLog.getStepGoal()) {
            holder.txtStatus.setText("Trạng thái: Đã đạt mục tiêu!");
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            holder.txtStatus.setText("Trạng thái: Chưa đạt mục tiêu");
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.red));
        }

        // Sự kiện xóa mục
        holder.btnDelete.setOnClickListener(v -> {
            deleteStepLog(position);
        });
    }

    @Override
    public int getItemCount() {
        return stepLogList.size();
    }

    private void deleteStepLog(int position) {
        StepLog stepLog = stepLogList.get(position);

        stepLogList.remove(position);
        notifyItemRemoved(position);
        Toast.makeText(context, "Đã xóa bản ghi bước chân!", Toast.LENGTH_SHORT).show();
    }

    public static class StepLogViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate, txtSteps, txtStepGoal, txtStatus;
        ImageButton btnDelete;

        public StepLogViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtSteps = itemView.findViewById(R.id.txtSteps);
            txtStepGoal = itemView.findViewById(R.id.txtStepGoal);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
