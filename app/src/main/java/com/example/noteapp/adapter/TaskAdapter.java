package com.example.noteapp.adapter;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.noteapp.R;
import com.example.noteapp.model.Task;
import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks = new ArrayList<>();
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
        void onTaskDoneChanged(Task task, boolean isDone);
        void onDeleteClick(Task task);
    }

    public TaskAdapter(OnTaskClickListener listener) {
        this.listener = listener;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);

        // Status indicator color
        GradientDrawable statusDrawable = new GradientDrawable();
        statusDrawable.setShape(GradientDrawable.OVAL);
        int statusColor;
        boolean isDone = "done".equals(task.getStatus());
        switch (task.getStatus()) {
            case "done":
                statusColor = Color.parseColor("#00D68F");
                statusDrawable.setColor(statusColor);
                break;
            case "inprogress":
                statusColor = Color.parseColor("#F59E0B");
                statusDrawable.setColor(statusColor);
                break;
            default: // "todo"
                statusColor = Color.parseColor("#6B6B9A");
                statusDrawable.setStroke(2, Color.parseColor("#6B6B9A"));
                statusDrawable.setColor(Color.TRANSPARENT);
                break;
        }
        holder.statusIndicator.setBackground(statusDrawable);

        // Title
        holder.tvTitle.setText(task.getTitle());
        if (isDone) {
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvTitle.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_muted));
        } else {
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvTitle.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_primary));
        }

        // Category chip
        holder.tvCategory.setText(getCategoryLabel(task.getCategory()));
        holder.tvCategory.setBackgroundResource(getCategoryBackground(task.getCategory()));
        holder.tvCategory.setTextColor(holder.itemView.getContext().getResources().getColor(getCategoryTextColor(task.getCategory())));

        // Priority chip
        holder.tvPriority.setText(getPriorityLabel(task.getPriority()));
        holder.tvPriority.setBackgroundResource(getPriorityBackground(task.getPriority()));
        holder.tvPriority.setTextColor(holder.itemView.getContext().getResources().getColor(getPriorityTextColor(task.getPriority())));

        // Deadline
        String deadline = task.getDeadline();
        if (deadline != null && !deadline.isEmpty()) {
            holder.rowDeadline.setVisibility(View.VISIBLE);
            holder.tvDeadline.setText(deadline);
        } else {
            holder.rowDeadline.setVisibility(View.GONE);
        }

        // Done checkbox equivalent — tap status indicator to toggle
        holder.statusIndicator.setOnClickListener(v -> {
            if (listener != null) {
                boolean newDone = !"done".equals(task.getStatus());
                listener.onTaskDoneChanged(task, newDone);
            }
        });

        // Delete button
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(task);
            }
        });

        // Item click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskClick(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    private String getCategoryLabel(String category) {
        if (category == null) return "📋 Khác";
        switch (category.toLowerCase()) {
            case "work": return "💼 Công việc";
            case "personal": return "👤 Cá nhân";
            case "study": return "📚 Học tập";
            case "health": return "❤️ Sức khỏe";
            default: return "📋 Khác";
        }
    }

    private int getCategoryBackground(String category) {
        if (category == null) return R.drawable.bg_tag_work;
        switch (category.toLowerCase()) {
            case "work": return R.drawable.bg_tag_work;
            case "personal": return R.drawable.bg_tag_personal;
            case "study": return R.drawable.bg_tag_study;
            case "health": return R.drawable.bg_tag_health;
            default: return R.drawable.bg_tag_work;
        }
    }

    private int getCategoryTextColor(String category) {
        if (category == null) return R.color.tag_work_text;
        switch (category.toLowerCase()) {
            case "work": return R.color.tag_work_text;
            case "personal": return R.color.tag_personal_text;
            case "study": return R.color.tag_study_text;
            case "health": return R.color.tag_health_text;
            default: return R.color.tag_work_text;
        }
    }

    private String getPriorityLabel(String priority) {
        if (priority == null) return "Thấp";
        switch (priority.toLowerCase()) {
            case "high": return "Cao";
            case "medium": return "Vừa";
            case "low": return "Thấp";
            default: return "Thấp";
        }
    }

    private int getPriorityBackground(String priority) {
        if (priority == null) return R.drawable.bg_tag_low;
        switch (priority.toLowerCase()) {
            case "high": return R.drawable.bg_tag_high;
            case "medium": return R.drawable.bg_tag_medium;
            case "low": return R.drawable.bg_tag_low;
            default: return R.drawable.bg_tag_low;
        }
    }

    private int getPriorityTextColor(String priority) {
        if (priority == null) return R.color.tag_low_text;
        switch (priority.toLowerCase()) {
            case "high": return R.color.tag_high_text;
            case "medium": return R.color.tag_medium_text;
            case "low": return R.color.tag_low_text;
            default: return R.color.tag_low_text;
        }
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        View statusIndicator;
        TextView tvTitle;
        TextView tvCategory;
        TextView tvPriority;
        LinearLayout rowDeadline;
        TextView tvDeadline;
        ImageView btnDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            statusIndicator = itemView.findViewById(R.id.status_indicator);
            tvTitle = itemView.findViewById(R.id.tv_task_title);
            tvCategory = itemView.findViewById(R.id.tv_task_category);
            tvPriority = itemView.findViewById(R.id.tv_task_priority);
            rowDeadline = itemView.findViewById(R.id.row_deadline);
            tvDeadline = itemView.findViewById(R.id.tv_task_deadline);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
