package com.example.noteapp.fragments;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
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
import androidx.appcompat.app.AppCompatDelegate;
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
    private TextView tvReminderDefaultTime;
    private UserViewModel userViewModel;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    exportData();
                } else {
                    Toast.makeText(getContext(), "Cần cấp quyền truy cập bộ nhớ để xuất dữ liệu", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<String> requestManageStorageLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    exportData();
                } else {
                    Toast.makeText(getContext(), "Cần cấp quyền quản lý bộ nhớ để xuất dữ liệu", Toast.LENGTH_SHORT).show();
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
        tvReminderDefaultTime = view.findViewById(R.id.tv_reminder_default_time);

        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", 0);
        switchDarkMode.setChecked(prefs.getBoolean("dark_mode", true));
        switchNotifications.setChecked(prefs.getBoolean("notifications_enabled", true));

        int reminderMinutes = prefs.getInt("reminder_default_minutes", 30);
        tvReminderDefaultTime.setText(getReminderText(reminderMinutes));
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
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", 0);
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            Toast.makeText(getContext(), isChecked ? "Đã bật chế độ tối (áp dụng khi mở lại app)" : "Đã tắt chế độ tối (áp dụng khi mở lại app)", Toast.LENGTH_SHORT).show();
        });

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", 0);
            prefs.edit().putBoolean("notifications_enabled", isChecked).apply();
            if (!isChecked) {
                NotificationManager nm = requireContext().getSystemService(NotificationManager.class);
                if (nm != null) {
                    nm.cancelAll();
                }
            }
            Toast.makeText(getContext(), isChecked ? "Đã bật thông báo" : "Đã tắt thông báo", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.card_profile).setOnClickListener(v -> {
            EditProfileDialogFragment dialog = new EditProfileDialogFragment();
            dialog.show(getParentFragmentManager(), "EditProfileDialog");
        });

        view.findViewById(R.id.card_reminder_default).setOnClickListener(v -> showReminderDefaultDialog());

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
            if (Environment.isExternalStorageManager()) {
                exportData();
            } else {
                try {
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(android.net.Uri.parse("package:" + requireContext().getPackageName()));
                    requestManageStorageLauncher.launch(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
                } catch (Exception e) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    requestManageStorageLauncher.launch(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
                }
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
                if (isAdded()) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onError(String error) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show());
                }
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
                if (isAdded()) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onError(String error) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show());
                }
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

    private void showReminderDefaultDialog() {
        String[] options = {"5 phút trước", "10 phút trước", "15 phút trước", "30 phút trước", "60 phút trước"};
        int[] values = {5, 10, 15, 30, 60};

        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", 0);
        int current = prefs.getInt("reminder_default_minutes", 30);
        int checkedItem = 3;
        for (int i = 0; i < values.length; i++) {
            if (values[i] == current) {
                checkedItem = i;
                break;
            }
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Nhắc nhở mặc định")
                .setSingleChoiceItems(options, checkedItem, (dialog, which) -> {
                    prefs.edit().putInt("reminder_default_minutes", values[which]).apply();
                    tvReminderDefaultTime.setText(getReminderText(values[which]));
                    dialog.dismiss();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private String getReminderText(int minutes) {
        if (minutes < 60) return minutes + " phút trước";
        return (minutes / 60) + " giờ trước";
    }
}
