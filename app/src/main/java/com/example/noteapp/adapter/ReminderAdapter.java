package com.example.noteapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.noteapp.R;
import com.example.noteapp.model.Reminder;
import java.util.ArrayList;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> reminders = new ArrayList<>();
    private OnReminderClickListener listener;

    public interface OnReminderClickListener {
        void onToggle(Reminder reminder);
        void onDelete(int id);
    }

    public ReminderAdapter(OnReminderClickListener listener) {
        this.listener = listener;
    }

    public void setReminders(List<Reminder> reminders) {
        this.reminders = reminders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);

        holder.tvIcon.setText(reminder.getIcon());
        holder.tvTitle.setText(reminder.getTitle());
        holder.tvTime.setText(reminder.getTime());
        holder.tvDate.setText(reminder.getDate());
        holder.tvRepeat.setText(reminder.getRepeat());

        holder.switchToggle.setOnCheckedChangeListener(null);
        holder.switchToggle.setChecked(reminder.isActive());
        holder.switchToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onToggle(reminder);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(reminder.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon;
        TextView tvTitle;
        TextView tvTime;
        TextView tvDate;
        TextView tvRepeat;
        SwitchCompat switchToggle;
        ImageView btnDelete;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tv_icon);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvRepeat = itemView.findViewById(R.id.tv_repeat);
            switchToggle = itemView.findViewById(R.id.switch_toggle);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
