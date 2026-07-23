package com.example.noteapp.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.noteapp.R;
import com.example.noteapp.model.User;
import com.example.noteapp.util.UserManager;
import com.example.noteapp.viewmodel.UserViewModel;

public class EditProfileDialogFragment extends DialogFragment {

    private UserViewModel userViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        EditText etFullName = view.findViewById(R.id.et_full_name);
        EditText etPhone = view.findViewById(R.id.et_phone);
        EditText etEmail = view.findViewById(R.id.et_email);

        int userId = UserManager.getUserId(requireContext());
        userViewModel.getById(userId, user -> {
            if (user != null) {
                requireActivity().runOnUiThread(() -> {
                    etFullName.setText(user.getFullName());
                    etPhone.setText(user.getPhone());
                    etEmail.setText(user.getEmail());
                });
            }
        });

        view.findViewById(R.id.btn_cancel).setOnClickListener(v -> dismiss());

        view.findViewById(R.id.btn_save).setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            if (fullName.isEmpty()) {
                etFullName.setError("Vui lòng nhập họ tên");
                etFullName.requestFocus();
                return;
            }

            if (email.isEmpty()) {
                etEmail.setError("Vui lòng nhập email");
                etEmail.requestFocus();
                return;
            }

            userViewModel.getById(userId, existingUser -> {
                if (existingUser != null) {
                    existingUser.setFullName(fullName);
                    existingUser.setPhone(phone);
                    existingUser.setEmail(email);

                    userViewModel.update(existingUser, () -> {
                        requireActivity().runOnUiThread(() -> {
                            SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", 0);
                            prefs.edit()
                                    .putString("full_name", fullName)
                                    .putString("email", email)
                                    .putString("phone", phone)
                                    .apply();

                            Toast.makeText(getContext(), "Đã cập nhật thông tin", Toast.LENGTH_SHORT).show();
                            dismiss();
                        });
                    });
                }
            });
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setGravity(android.view.Gravity.CENTER);
        }
    }
}
