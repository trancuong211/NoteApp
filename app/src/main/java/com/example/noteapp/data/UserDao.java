package com.example.noteapp.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.noteapp.model.User;
import java.util.List;

@Dao
public interface UserDao {
    @Insert
    long insert(User user);

    @Update
    void update(User user);

    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    User getById(int userId);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getByEmail(String email);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    User login(String email, String password);

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    int emailExists(String email);

    @Query("DELETE FROM users WHERE id = :userId")
    void deleteById(int userId);
}
