# NoteApp - Application Management Tasks & Notes

## Overview
NoteApp là ứng dụng quản lý công việc và ghi chú trên Android, được xây dựng bằng Java với giao diện Material Design dark theme.

## Features

### Home Screen
- Hiển thị thông tin người dùng
- Thống kê tiến độ công việc hôm nay (số task done, pending)
- Quick Actions: New Task, New Note, Calendar, Reminders
- Danh sách Recent Tasks và Recent Notes

### Tasks Screen
- Quản lý danh sách công việc
- Thêm task mới (title, category, priority)
- Đánh dấu task hoàn thành (checkbox)
- Xóa task
- Xem chi tiết task (nhấn vào task)
- Bộ lọc theo category và priority

### Notes Screen
- Quản lý danh sách ghi chú
- Thêm note mới (title, content, category)
- Xóa note
- Xem chi tiết note (nhấn vào note)
- Bộ lọc theo category

### Statistics Screen
- Tổng quan: Total Tasks, Completed, Notes
- Completion Rate với progress bar
- Weekly Activity - Biểu đồ cột tương tác (nhấn để xem số done)
- Biểu đồ tròn theo Category (Work, Personal, Study, Health)
- Biểu đồ tròn theo Priority (High, Medium, Low)

### Settings Screen
- Cài đặt ứng dụng

## Project Structure

```
app/src/main/java/com/example/noteapp/
├── MainActivity.java              # Main Activity với BottomNavigationView
├── model/
│   ├── Task.java                  # Model class cho Task
│   └── Note.java                  # Model class cho Note
├── viewmodel/
│   ├── TaskViewModel.java         # ViewModel chia sẻ dữ liệu Task
│   └── NoteViewModel.java         # ViewModel chia sẻ dữ liệu Note
├── adapter/
│   ├── TaskAdapter.java           # RecyclerView Adapter cho Task
│   └── NoteAdapter.java           # RecyclerView Adapter cho Note
├── view/
│   └── PieChartView.java          # Custom View vẽ biểu đồ tròn
└── fragments/
    ├── HomeFragment.java          # Fragment màn hình chính
    ├── TasksFragment.java         # Fragment quản lý tasks
    ├── NotesFragment.java         # Fragment quản lý notes
    ├── StatsFragment.java         # Fragment thống kê
    ├── SettingsFragment.java      # Fragment cài đặt
    ├── NewTaskDialogFragment.java # Dialog thêm task mới
    ├── NewNoteDialogFragment.java # Dialog thêm note mới
    ├── TaskDetailDialogFragment.java # Dialog chi tiết task
    └── NoteDetailDialogFragment.java # Dialog chi tiết note
```

## Layouts

```
app/src/main/res/layout/
├── activity_main.xml              # Layout chính với BottomNav + FAB
├── fragment_home.xml              # Layout màn hình Home
├── fragment_tasks.xml             # Layout màn hình Tasks
├── fragment_notes.xml             # Layout màn hình Notes
├── fragment_stats.xml             # Layout màn hình Statistics
├── fragment_settings.xml          # Layout màn hình Settings
├── item_task.xml                  # Layout item task trong RecyclerView
├── item_note.xml                  # Layout item note trong RecyclerView
├── dialog_new_task.xml            # Layout dialog thêm task
├── dialog_new_note.xml            # Layout dialog thêm note
├── dialog_task_detail.xml         # Layout dialog chi tiết task
└── dialog_note_detail.xml         # Layout dialog chi tiết note
```

## Drawables

```
app/src/main/res/drawable/
├── bg_dialog.xml                  # Background dialog
├── bg_input_field.xml             # Background ô input
├── bg_button_primary.xml          # Background nút chính
├── bg_close_button.xml            # Background nút đóng
├── bg_task_item.xml               # Background item task/note
├── bg_delete_button.xml           # Background nút xóa
├── bg_tooltip.xml                 # Background tooltip
├── bg_bar_chart.xml               # Background cột biểu đồ
├── ic_delete.xml                  # Icon thùng rác
├── bg_chip_category_selected.xml  # Background chip category được chọn
├── bg_chip_category_default.xml   # Background chip category mặc định
├── bg_chip_priority_default.xml   # Background chip priority mặc định
├── bg_chip_priority_selected_high.xml   # Background chip High
├── bg_chip_priority_selected_medium.xml # Background chip Medium
├── bg_chip_priority_selected_low.xml    # Background chip Low
├── bg_tag_work.xml                # Tag background Work
├── bg_tag_personal.xml            # Tag background Personal
├── bg_tag_study.xml               # Tag background Study
├── bg_tag_health.xml              # Tag background Health
├── bg_tag_high.xml                # Tag background High priority
├── bg_tag_medium.xml              # Tag background Medium priority
├── bg_tag_low.xml                 # Tag background Low priority
└── ... (các drawable khác)
```

## Colors

| Name               | Hex        | Mô tả                    |
|--------------------|------------|--------------------------|
| bg_dark            | #0F0F1E    | Nền chính                |
| bg_card            | #1A1A2E    | Nền card                 |
| text_primary       | #FFFFFF    | Chữ chính                |
| text_secondary     | #808080    | Chữ phụ                  |
| accent_green       | #00E676    | Màu chính (Work, Done)   |
| accent_purple      | #7C4DFF    | Màu phụ (Notes)          |
| accent_blue        | #448AFF    | Màu Info, Study          |
| accent_red         | #FF5252    | Màu Xóa, Health, High    |
| accent_orange      | #FF9800    | Màu Medium               |
| tag_work_text      | #448AFF    | Chữ tag Work             |
| tag_personal_text  | #B388FF    | Chữ tag Personal         |
| tag_study_text     | #00E676    | Chữ tag Study            |
| tag_health_text    | #FF5252    | Chữ tag Health           |
| tag_high_text      | #FF5252    | Chữ tag High             |
| tag_medium_text    | #FFC107    | Chữ tag Medium           |
| tag_low_text       | #00E676    | Chữ tag Low              |

## Data Models

### Task
```java
public class Task {
    private String title;       // Tiêu đề task
    private String category;    // Category: Work, Personal, Study, Health
    private String priority;    // Priority: High, Medium, Low
    private boolean isDone;     // Trạng thái hoàn thành
}
```

### Note
```java
public class Note {
    private String title;       // Tiêu đề note
    private String content;     // Nội dung note
    private String category;    // Category: Work, Personal, Study, Health
    private long createdAt;     // Thời gian tạo
}
```

## Technologies

- **Language**: Java
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Compile SDK**: 37
- **UI**: XML Layouts với Material Components
- **Architecture**: Fragment-based Navigation
- **ViewModel**: LiveData + ViewModel (Jetpack)
- **RecyclerView**: Adapter pattern

## Build & Run

1. Clone project
2. Open với Android Studio
3. Sync Gradle
4. Run trên emulator hoặc device

```bash
./gradlew assembleDebug
```

## Dependencies

```kotlin
implementation(libs.androidx.appcompat)
implementation(libs.material)
implementation(libs.androidx.constraintlayout)
implementation(libs.androidx.core.ktx)
implementation(libs.androidx.lifecycle.runtime.ktx)
implementation(libs.androidx.lifecycle.viewmodel.ktx)
```

## Screenshots

### Home Screen
- Header với tên người dùng
- Progress Card với biểu đồ tròn
- Quick Actions (New Task, New Note, Calendar, Reminders)
- Recent Tasks và Recent Notes

### Tasks Screen
- Thanh tìm kiếm
- Bộ lọc category và priority
- Danh sách task với checkbox, tag, nút xóa

### Notes Screen
- Thanh tìm kiếm
- Bộ lọc category
- Danh sách note với tag, nút xóa

### Statistics Screen
- Summary Cards (Total, Completed, Notes)
- Completion Rate với progress bar
- Weekly Activity bar chart (tương tác)
- Biểu đồ tròn Category và Priority

## Author

Trần Minh Khoa

## License

MIT License
