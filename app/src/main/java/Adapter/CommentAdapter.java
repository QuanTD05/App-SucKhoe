package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import fpoly.md19304.app_moblie.R;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Map<String, Object>> comments;

    public CommentAdapter(Context context, List<Map<String, Object>> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Map<String, Object> commentData = comments.get(position);
        String email = (String) commentData.get("email");
        String comment = (String) commentData.get("comment");

        holder.emailTextView.setText(email);
        holder.commentTextView.setText(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void setComments(List<Map<String, Object>> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView emailTextView;
        TextView commentTextView;

        CommentViewHolder(View itemView) {
            super(itemView);
            emailTextView = itemView.findViewById(R.id.commenterEmailTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
        }
    }
}
