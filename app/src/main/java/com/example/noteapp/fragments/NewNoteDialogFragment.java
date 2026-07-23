package com.example.noteapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.noteapp.R;
import com.example.noteapp.model.Note;
import com.example.noteapp.util.UserManager;
import com.example.noteapp.viewmodel.NoteViewModel;

public class NewNoteDialogFragment extends DialogFragment {

    private String selectedCategory = "Work";
    private NoteViewModel noteViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_new_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        noteViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);

        ImageView btnClose = view.findViewById(R.id.btn_close);
        EditText etNoteTitle = view.findViewById(R.id.et_note_title);
        EditText etNoteContent = view.findViewById(R.id.et_note_content);
        TextView btnSaveNote = view.findViewById(R.id.btn_save_note);

        // Category chips
        TextView chipWork = view.findViewById(R.id.chip_work);
        TextView chipPersonal = view.findViewById(R.id.chip_personal);
        TextView chipStudy = view.findViewById(R.id.chip_study);
        TextView chipHealth = view.findViewById(R.id.chip_health);

        // Close button
        btnClose.setOnClickListener(v -> dismiss());

        // Category click listeners
        View.OnClickListener categoryClickListener = v -> {
            resetCategoryChips(chipWork, chipPersonal, chipStudy, chipHealth);
            int id = v.getId();
            if (id == R.id.chip_work) {
                selectedCategory = "Work";
                chipWork.setBackgroundResource(R.drawable.bg_chip_category_selected);
                chipWork.setTextColor(getResources().getColor(R.color.tag_work_text));
            } else if (id == R.id.chip_personal) {
                selectedCategory = "Personal";
                chipPersonal.setBackgroundResource(R.drawable.bg_chip_category_selected);
                chipPersonal.setTextColor(getResources().getColor(R.color.tag_personal_text));
            } else if (id == R.id.chip_study) {
                selectedCategory = "Study";
                chipStudy.setBackgroundResource(R.drawable.bg_chip_category_selected);
                chipStudy.setTextColor(getResources().getColor(R.color.tag_study_text));
            } else if (id == R.id.chip_health) {
                selectedCategory = "Health";
                chipHealth.setBackgroundResource(R.drawable.bg_chip_category_selected);
                chipHealth.setTextColor(getResources().getColor(R.color.tag_health_text));
            }
        };

        chipWork.setOnClickListener(categoryClickListener);
        chipPersonal.setOnClickListener(categoryClickListener);
        chipStudy.setOnClickListener(categoryClickListener);
        chipHealth.setOnClickListener(categoryClickListener);

        // Save Note button
        btnSaveNote.setOnClickListener(v -> {
            String title = etNoteTitle.getText().toString().trim();
            String content = etNoteContent.getText().toString().trim();
            if (title.isEmpty()) {
                etNoteTitle.setError("Please enter a note title");
                return;
            }
            Note newNote = new Note(title, content, selectedCategory);
            newNote.setUserId(UserManager.getUserId(requireContext()));
            noteViewModel.insert(newNote);
            dismiss();
        });
    }

    private void resetCategoryChips(TextView... chips) {
        for (TextView chip : chips) {
            chip.setBackgroundResource(R.drawable.bg_chip_category_default);
            chip.setTextColor(getResources().getColor(R.color.text_secondary));
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
