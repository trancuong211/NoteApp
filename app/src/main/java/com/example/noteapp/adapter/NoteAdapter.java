package com.example.noteapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.noteapp.R;
import com.example.noteapp.model.Note;
import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notes = new ArrayList<>();
    private OnNoteClickListener listener;

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
        void onDeleteClick(Note note);
    }

    public NoteAdapter(OnNoteClickListener listener) {
        this.listener = listener;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.tvTitle.setText(note.getTitle());
        holder.tvContent.setText(note.getContent());

        String category = note.getCategory() != null ? note.getCategory() : "";
        holder.tvCategory.setText(getCategoryEmoji(category) + " " + category);
        holder.tvCategory.setBackgroundResource(getCategoryBackground(category));
        holder.tvCategory.setTextColor(holder.itemView.getContext().getResources().getColor(getCategoryTextColor(category)));

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(note);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNoteClick(note);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    private String getCategoryEmoji(String category) {
        if (category == null) category = "";
        switch (category) {
            case "Work": return "\uD83D\uDCBC";
            case "Personal": return "\uD83D\uDC64";
            case "Study": return "\uD83D\uDCDA";
            case "Health": return "\u2764\uFE0F";
            default: return "\uD83D\uDCCB";
        }
    }

    private int getCategoryBackground(String category) {
        if (category == null) category = "";
        switch (category) {
            case "Work": return R.drawable.bg_tag_work;
            case "Personal": return R.drawable.bg_tag_personal;
            case "Study": return R.drawable.bg_tag_study;
            case "Health": return R.drawable.bg_tag_health;
            default: return R.drawable.bg_tag_work;
        }
    }

    private int getCategoryTextColor(String category) {
        if (category == null) category = "";
        switch (category) {
            case "Work": return R.color.tag_work_text;
            case "Personal": return R.color.tag_personal_text;
            case "Study": return R.color.tag_study_text;
            case "Health": return R.color.tag_health_text;
            default: return R.color.tag_work_text;
        }
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvContent;
        TextView tvCategory;
        ImageView btnDelete;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_note_title);
            tvContent = itemView.findViewById(R.id.tv_note_content);
            tvCategory = itemView.findViewById(R.id.tv_note_category);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
