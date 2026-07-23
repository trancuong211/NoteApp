package com.example.noteapp.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.noteapp.LoginActivity;
import com.example.noteapp.R;
import com.example.noteapp.util.DataManager;
import com.example.noteapp.util.UserManager;
import com.example.noteapp.viewmodel.UserViewModel;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment {

    private SwitchMaterial switchDarkMode;
    private SwitchMaterial switchNotifications;
    private TextView tvProfileName;
    private TextView tvProfileAvatar;
    private UserViewModel userViewModel;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    exportData();
                } else {
                    Toast.makeText(getContext(), "Cần cấp quyền truy cập bộ nhớ để xuất dữ liệu", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        loadUserProfile();
        setupListeners(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserProfile();
    }

    private void initViews(View view) {
        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        switchNotifications = view.findViewById(R.id.switch_notifications);
        tvProfileName = view.findViewById(R.id.tv_profile_name);
        tvProfileAvatar = view.findViewById(R.id.tv_profile_avatar);
    }

    private void loadUserProfile() {
        if (getContext() == null) return;
        SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", 0);
        String fullName = prefs.getString("full_name", "");

        if (!fullName.isEmpty()) {
            tvProfileName.setText(fullName);
            String[] parts = fullName.split("\\s+");
            if (parts.length >= 2) {
                String initials = "" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0);
                tvProfileAvatar.setText(initials.toUpperCase());
            } else if (!fullName.isEmpty()) {
                tvProfileAvatar.setText(String.valueOf(fullName.charAt(0)).toUpperCase());
            }
        } else {
            tvProfileName.setText("Chưa đăng nhập");
            tvProfileAvatar.setText("?");
        }
    }

    private void setupListeners(View view) {
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) ->
                Toast.makeText(getContext(), isChecked ? "Đã bật chế độ tối" : "Đã tắt chế độ tối", Toast.LENGTH_SHORT).show());

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) ->
                Toast.makeText(getContext(), isChecked ? "Đã bật thông báo" : "Đã tắt thông báo", Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.card_profile).setOnClickListener(v -> {
            EditProfileDialogFragment dialog = new EditProfileDialogFragment();
            dialog.show(getParentFragmentManager(), "EditProfileDialog");
        });

        view.findViewById(R.id.card_reminder_default).setOnClickListener(v ->
                Toast.makeText(getContext(), "Coming soon", Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.card_profile_info).setOnClickListener(v -> {
            EditProfileDialogFragment dialog = new EditProfileDialogFragment();
            dialog.show(getParentFragmentManager(), "EditProfileDialog");
        });

        view.findViewById(R.id.card_security).setOnClickListener(v ->
                Toast.makeText(getContext(), "Coming soon", Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.card_delete_account).setOnClickListener(v -> showDeleteAccountDialog());

        view.findViewById(R.id.btn_logout).setOnClickListener(v -> {
            SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", 0);
            prefs.edit().clear().apply();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        view.findViewById(R.id.btn_export).setOnClickListener(v -> checkPermissionAndExport());
        view.findViewById(R.id.btn_import).setOnClickListener(v -> showImportDialog());
    }

    private void checkPermissionAndExport() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED) {
                exportData();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                exportData();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    private void exportData() {
        Toast.makeText(getContext(), "Đang xuất dữ liệu...", Toast.LENGTH_SHORT).show();
        DataManager.exportData(requireContext(), new DataManager.OnResultListener() {
            @Override
            public void onSuccess(String message) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show());
            }

            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show());
            }
        });
    }

    private void showImportDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Nhập dữ liệu")
                .setMessage("Dữ liệu hiện tại sẽ được giữ lại. Dữ liệu từ file backup sẽ được thêm vào.\n\nFile: Documents/noteapp_backup.json")
                .setPositiveButton("Nhập", (dialog, which) -> importData())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void importData() {
        Toast.makeText(getContext(), "Đang nhập dữ liệu...", Toast.LENGTH_SHORT).show();
        DataManager.importData(requireContext(), new DataManager.OnResultListener() {
            @Override
            public void onSuccess(String message) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show());
            }

            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show());
            }
        });
    }

    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa tài khoản")
                .setMessage("Bạn có chắc chắn muốn xóa tài khoản? Tất cả dữ liệu (nhiệm vụ, nhắc nhở, ghi chú) sẽ bị xóa vĩnh viễn.")
                .setPositiveButton("Xóa", (dialog, which) -> deleteAccount())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteAccount() {
        int userId = UserManager.getUserId(requireContext());
        userViewModel.deleteById(userId, () -> requireActivity().runOnUiThread(() -> {
            SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", 0);
            prefs.edit().clear().apply();
            Toast.makeText(getContext(), "Đã xóa tài khoản", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        }));
    }
}
