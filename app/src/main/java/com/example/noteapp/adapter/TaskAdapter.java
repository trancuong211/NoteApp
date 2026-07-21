package com.example.noteapp.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
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
        void onTaskClick(int position);
        void onTaskDoneChanged(int position, boolean isDone);
        void onDeleteClick(int position);
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
        holder.tvTitle.setText(task.getTitle());

        // Set category tag
        holder.tvCategory.setText(getCategoryEmoji(task.getCategory()) + " " + task.getCategory());
        holder.tvCategory.setBackgroundResource(getCategoryBackground(task.getCategory()));
        holder.tvCategory.setTextColor(holder.itemView.getContext().getResources().getColor(getCategoryTextColor(task.getCategory())));

        // Set priority tag
        holder.tvPriority.setText(getPriorityEmoji(task.getPriority()) + " " + task.getPriority());
        holder.tvPriority.setBackgroundResource(getPriorityBackground(task.getPriority()));
        holder.tvPriority.setTextColor(holder.itemView.getContext().getResources().getColor(getPriorityTextColor(task.getPriority())));

        // Set done state
        holder.cbDone.setChecked(task.isDone());
        if (task.isDone()) {
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvTitle.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_muted));
        } else {
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvTitle.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_primary));
        }

        holder.cbDone.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskDoneChanged(holder.getAdapterPosition(), holder.cbDone.isChecked());
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    private String getCategoryEmoji(String category) {
        switch (category) {
            case "Work": return "💼";
            case "Personal": return "👤";
            case "Study": return "📚";
            case "Health": return "❤️";
            default: return "📋";
        }
    }

    private int getCategoryBackground(String category) {
        switch (category) {
            case "Work": return R.drawable.bg_tag_work;
            case "Personal": return R.drawable.bg_tag_personal;
            case "Study": return R.drawable.bg_tag_study;
            case "Health": return R.drawable.bg_tag_health;
            default: return R.drawable.bg_tag_work;
        }
    }

    private int getCategoryTextColor(String category) {
        switch (category) {
            case "Work": return R.color.tag_work_text;
            case "Personal": return R.color.tag_personal_text;
            case "Study": return R.color.tag_study_text;
            case "Health": return R.color.tag_health_text;
            default: return R.color.tag_work_text;
        }
    }

    private String getPriorityEmoji(String priority) {
        switch (priority) {
            case "High": return "🔴";
            case "Medium": return "🟠";
            case "Low": return "🟢";
            default: return "⚪";
        }
    }

    private int getPriorityBackground(String priority) {
        switch (priority) {
            case "High": return R.drawable.bg_tag_high;
            case "Medium": return R.drawable.bg_tag_medium;
            case "Low": return R.drawable.bg_tag_low;
            default: return R.drawable.bg_tag_high;
        }
    }

    private int getPriorityTextColor(String priority) {
        switch (priority) {
            case "High": return R.color.tag_high_text;
            case "Medium": return R.color.tag_medium_text;
            case "Low": return R.color.tag_low_text;
            default: return R.color.tag_high_text;
        }
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbDone;
        TextView tvTitle;
        TextView tvCategory;
        TextView tvPriority;
        ImageView btnDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cbDone = itemView.findViewById(R.id.cb_task_done);
            tvTitle = itemView.findViewById(R.id.tv_task_title);
            tvCategory = itemView.findViewById(R.id.tv_task_category);
            tvPriority = itemView.findViewById(R.id.tv_task_priority);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
