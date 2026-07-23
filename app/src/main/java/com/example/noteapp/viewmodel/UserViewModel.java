package com.example.noteapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.example.noteapp.data.AppDatabase;
import com.example.noteapp.data.UserDao;
import com.example.noteapp.model.User;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserViewModel extends AndroidViewModel {

    private final UserDao userDao;
    private final ExecutorService executor;

    public UserViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        userDao = db.userDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public interface UserCallback {
        void onResult(User user);
    }

    public interface UsersCallback {
        void onResult(List<User> users);
    }

    public void insert(User user, Runnable onComplete) {
        executor.execute(() -> {
            userDao.insert(user);
            if (onComplete != null) onComplete.run();
        });
    }

    public void getAllUsers(UsersCallback callback) {
        executor.execute(() -> {
            List<User> users = userDao.getAllUsers();
            if (callback != null) callback.onResult(users);
        });
    }

    public void update(User user, Runnable onComplete) {
        executor.execute(() -> {
            userDao.update(user);
            if (onComplete != null) onComplete.run();
        });
    }

    public void getById(int userId, UserCallback callback) {
        executor.execute(() -> {
            User user = userDao.getById(userId);
            if (callback != null) callback.onResult(user);
        });
    }

    public void getByEmail(String email, UserCallback callback) {
        executor.execute(() -> {
            User user = userDao.getByEmail(email);
            if (callback != null) callback.onResult(user);
        });
    }

    public void login(String email, String password, UserCallback callback) {
        executor.execute(() -> {
            User user = userDao.login(email, password);
            if (callback != null) callback.onResult(user);
        });
    }

    public void emailExists(String email, UserCallback callback) {
        executor.execute(() -> {
            int count = userDao.emailExists(email);
            if (callback != null) callback.onResult(count > 0 ? new User() : null);
        });
    }

    public void deleteById(int userId, Runnable onComplete) {
        executor.execute(() -> {
            userDao.deleteById(userId);
            if (onComplete != null) onComplete.run();
        });
    }
}
