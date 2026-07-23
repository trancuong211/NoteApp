# NoteApp - Application Management Tasks & Notes

## Overview
NoteApp là ứng dụng quản lý công việc và ghi chú trên Android, được xây dựng bằng Java với giao diện Material Design dark theme. Dữ liệu được lưu trữ vĩnh viễn bằng Room Database.

## Features

### Login & Register
- Đăng nhập bằng email/mật khẩu
- Đăng ký tài khoản mới (họ tên, SĐT, email, mật khẩu)
- Đăng nhập bằng Google/Facebook (UI)
- Quên mật khẩu
- Đồng ý điều khoản sử dụng

### Home Screen
- Chào mừng theo thời gian trong ngày (sáng/chiều/tối)
- Progress Card với circular progress bar (phần trăm hoàn thành)
- 3 stat pills: Chờ / Xong / Nhắc
- Quick Actions: Thêm, Lịch, Nhắc, Nhiệm vụ
- Nhiệm vụ sắp tới (tối đa 3 task chưa done)
- Nhắc nhở đang bật (tối đa 3 reminder active)

### Calendar Screen
- Xem lịch theo tháng (grid CN-T7)
- Chuyển tháng trước/sau
- Highlight ngày hôm nay (xanh lá)
- Hiển thị dot dưới ngày có task (xanh = done, xám = chưa done)
- Danh sách task theo ngày được chọn

### Tasks Screen
- Bộ lọc theo danh mục: Tất cả, Công việc, Cá nhân, Học tập, Sức khỏe
- Bộ lọc theo trạng thái: Tất cả, Chờ làm, Đang làm, Hoàn thành
- Nhóm task theo status với header có thể gập lại
- Thêm task mới với: tên, danh mục, ưu tiên, hạn chót, trạng thái
- Đánh dấu status bằng cách nhấn vào dot indicator
- Xóa task

### Reminders Screen
- Tổng quan: Tổng số, Đang bật, Đã tắt
- Nhắc nhở đang bật (green dot indicator)
- Nhắc nhở đã tắt (gray dot)
- Bật/tắt nhắc nhở bằng toggle switch
- Thêm nhắc nhở mới: tên, giờ, biểu tượng, màu sắc, lặp lại
- Xóa nhắc nhở

### Notes Screen
- Quản lý danh sách ghi chú
- Thêm note mới (title, content, category)
- Xóa note
- Xem chi tiết note

### Settings Screen
- Profile card với avatar
- Chế độ tối (toggle)
- Thông báo (toggle)
- Nhắc nhở mặc định
- Thông tin cá nhân, Bảo mật, Xóa tài khoản
- Đăng xuất

## Project Structure

```
app/src/main/java/com/example/noteapp/
├── MainActivity.java              # Main Activity với BottomNavigationView + FAB
├── LoginActivity.java             # Activity đăng nhập
├── RegisterActivity.java          # Activity đăng ký
├── data/
│   ├── AppDatabase.java           # Room Database singleton (version 1)
│   ├── TaskDao.java               # DAO cho bảng tasks
│   ├── ReminderDao.java           # DAO cho bảng reminders
│   └── NoteDao.java               # DAO cho bảng notes
├── model/
│   ├── Task.java                  # @Entity "tasks"
│   ├── Reminder.java              # @Entity "reminders"
│   └── Note.java                  # @Entity "notes"
├── viewmodel/
│   ├── TaskViewModel.java         # ViewModel dùng TaskDao (Room)
│   ├── ReminderViewModel.java     # ViewModel dùng ReminderDao (Room)
│   └── NoteViewModel.java         # ViewModel dùng NoteDao (Room)
├── adapter/
│   ├── TaskAdapter.java           # RecyclerView Adapter cho Task
│   ├── NoteAdapter.java           # RecyclerView Adapter cho Note
│   └── ReminderAdapter.java       # RecyclerView Adapter cho Reminder
├── view/
│   └── PieChartView.java          # Custom View vẽ biểu đồ tròn
└── fragments/
    ├── HomeFragment.java          # Fragment màn hình chính
    ├── CalendarFragment.java      # Fragment lịch tháng
    ├── TasksFragment.java         # Fragment quản lý tasks
    ├── NotesFragment.java         # Fragment quản lý notes
    ├── ReminderFragment.java      # Fragment nhắc nhở
    ├── StatsFragment.java         # Fragment thống kê
    ├── SettingsFragment.java      # Fragment cài đặt
    ├── NewTaskDialogFragment.java # Dialog thêm task mới
    ├── NewReminderDialogFragment.java # Dialog thêm nhắc nhở mới
    ├── NewNoteDialogFragment.java # Dialog thêm note mới
    ├── TaskDetailDialogFragment.java # Dialog chi tiết task
    └── NoteDetailDialogFragment.java # Dialog chi tiết note
```

## Layouts

```
app/src/main/res/layout/
├── activity_main.xml              # Layout chính với BottomNav + FAB
├── activity_login.xml             # Layout đăng nhập
├── activity_register.xml          # Layout đăng ký
├── fragment_home.xml              # Layout màn hình Home
├── fragment_calendar.xml          # Layout màn hình Calendar
├── fragment_tasks.xml             # Layout màn hình Tasks
├── fragment_notes.xml             # Layout màn hình Notes
├── fragment_reminder.xml          # Layout màn hình Reminders
├── fragment_stats.xml             # Layout màn hình Statistics
├── fragment_settings.xml          # Layout màn hình Settings
├── item_task.xml                  # Layout item task trong RecyclerView
├── item_note.xml                  # Layout item note trong RecyclerView
├── item_reminder.xml              # Layout item reminder
├── item_home_task.xml             # Layout item task trên Home
├── item_home_reminder.xml         # Layout item reminder trên Home
├── dialog_new_task.xml            # Dialog thêm task
├── dialog_new_reminder.xml        # Dialog thêm nhắc nhở
├── dialog_new_note.xml            # Dialog thêm note
├── dialog_task_detail.xml         # Dialog chi tiết task
└── dialog_note_detail.xml         # Dialog chi tiết note
```

## Colors

| Name               | Hex        | Mô tả                         |
|--------------------|------------|-------------------------------|
| bg_dark            | #0D0D1A    | Nền chính                     |
| bg_card            | #161628    | Nền card                      |
| text_primary       | #F0F0FF    | Chữ chính                     |
| text_secondary     | #6B6B9A    | Chữ phụ                       |
| accent_green       | #00D68F    | Màu chính (primary)           |
| accent_purple      | #7C3AED    | Màu phụ                      |
| accent_blue        | #38BDF8    | Màu Info, Study               |
| accent_red         | #FF6B6B    | Màu Xóa, Health, High         |
| accent_yellow      | #F59E0B    | Màu Medium, Reminder          |
| tag_work_text      | #00D68F    | Chữ tag Work                  |
| tag_personal_text  | #7C3AED    | Chữ tag Personal              |
| tag_study_text     | #38BDF8    | Chữ tag Study                 |
| tag_health_text    | #FF6B6B    | Chữ tag Health                |
| tag_high_text      | #FF6B6B    | Chữ tag High                  |
| tag_medium_text    | #F59E0B    | Chữ tag Medium                |
| tag_low_text       | #38BDF8    | Chữ tag Low                   |

## Data Models

### Task (Room Entity)
```java
@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;       // Tiêu đề task
    private String category;    // Category: work, personal, study, health
    private String priority;    // Priority: high, medium, low
    private String status;      // Status: todo, inprogress, done
    private String deadline;    // Hạn chót (text)
    private String dateKey;     // Ngày YYYY-MM-DD
}
```

### Reminder (Room Entity)
```java
@Entity(tableName = "reminders")
public class Reminder {
    @PrimaryKey
    private int id;
    private String title;       // Tiêu đề nhắc nhở
    private String time;        // Giờ nhắc
    private String date;        // Ngày/Nhịp lặp
    private boolean active;     // Đang bật/tắt
    private String repeat;      // Tùy chọn lặp lại
    private String icon;        // Biểu tượng emoji
    private String color;       // Màu sắc hex
}
```

### Note (Room Entity)
```java
@Entity(tableName = "notes")
public class Note {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;       // Tiêu đề note
    private String content;     // Nội dung note
    private String category;    // Category: work, personal, study, health
    private long createdAt;     // Thời gian tạo
}
```

## Database (Room)

```
app/src/main/java/com/example/noteapp/data/
├── AppDatabase.java       # Room Database singleton, version 1
├── TaskDao.java           # getAll, getByDate, insert, update, delete, count
├── ReminderDao.java       # getAll, getActive, getInactive, insert, update, delete
└── NoteDao.java           # getAll, insert, delete, count
```

- Database name: `noteapp_database`
- Version: 1
- Tables: `tasks`, `reminders`, `notes`
- ViewModels dùng `ExecutorService` để chạy query trên background thread
- LiveData từ Room tự động cập nhật UI khi data thay đổi

## Technologies

- **Language**: Java
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Compile SDK**: 37
- **UI**: XML Layouts với Material Components
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room Database (SQLite wrapper)
- **ViewModel**: LiveData + AndroidViewModel (Jetpack)
- **RecyclerView**: Adapter pattern
- **Navigation**: Fragment-based BottomNavigationView (5 tabs)

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
implementation(libs.androidx.room.runtime)
annotationProcessor(libs.androidx.room.compiler)
implementation("androidx.gridlayout:gridlayout:1.0.0")
```

## Changelog

### v1.1 - Design Update + Room Database
- **UI Update**: Giao diện mới theo Calendar Homepage Design
  - Login/Register screen với social login buttons
  - Home screen với circular progress, quick actions, upcoming tasks, active reminders
  - Calendar screen với month view, task dots, selected day tasks
  - Tasks screen với category/status filter chips, grouped sections
  - Reminders screen với toggle active/inactive, stats summary
  - Settings screen với profile card, toggles, account options
- **Navigation**: 5 tabs (Trang chủ, Lịch, Nhiệm vụ, Nhắc nhở, Cài đặt)
- **Task Model**: Thêm fields `status`, `deadline`, `dateKey`
- **Reminder Model**: Model mới với `title`, `time`, `date`, `active`, `repeat`, `icon`, `color`
- **Room Database**: Lưu dữ liệu vĩnh viễn
  - Tạo `data/` package với `AppDatabase`, `TaskDao`, `ReminderDao`, `NoteDao`
  - Models convert thành Room `@Entity` với `@PrimaryKey`
  - ViewModels extends `AndroidViewModel`, dùng DAO + `ExecutorService`
  - All CRUD operations chạy trên background thread

### v1.0 - Initial Release
- Login/Register screens
- Home screen với progress card
- Tasks management (CRUD)
- Notes management (CRUD)
- Statistics screen với pie charts
- Settings screen
- Dark theme UI

## Author

Trần Minh Khoa

## License

MIT License
