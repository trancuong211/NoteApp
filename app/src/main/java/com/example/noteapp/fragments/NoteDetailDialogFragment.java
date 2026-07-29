package com.example.noteapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.noteapp.R;

public class NoteDetailDialogFragment extends DialogFragment {

    public static NoteDetailDialogFragment newInstance(String title, String content, String category) {
        NoteDetailDialogFragment fragment = new NoteDetailDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("content", content);
        args.putString("category", category);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_note_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView btnClose = view.findViewById(R.id.btn_close);
        TextView tvTitle = view.findViewById(R.id.tv_note_title);
        TextView tvCategory = view.findViewById(R.id.tv_note_category);
        TextView tvContent = view.findViewById(R.id.tv_note_content);
        TextView btnCloseDialog = view.findViewById(R.id.btn_close_dialog);

        if (getArguments() != null) {
            String title = getArguments().getString("title");
            String content = getArguments().getString("content");
            String category = getArguments().getString("category", "");

            tvTitle.setText(title);

            tvCategory.setText(getCategoryEmoji(category) + " " + category);
            tvCategory.setBackgroundResource(getCategoryBackground(category));
            tvCategory.setTextColor(getResources().getColor(getCategoryTextColor(category)));

            tvContent.setText(content);
        }

        btnClose.setOnClickListener(v -> dismiss());
        btnCloseDialog.setOnClickListener(v -> dismiss());
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

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setGravity(android.view.Gravity.BOTTOM);
        }
    }
}
