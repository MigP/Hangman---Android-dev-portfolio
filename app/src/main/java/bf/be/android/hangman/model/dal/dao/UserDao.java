package bf.be.android.hangman.model.dal.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import bf.be.android.hangman.model.dal.DbHelper;
import bf.be.android.hangman.model.dal.entities.User;

public class UserDao {
    public static final String CREATE_QUERY = "CREATE TABLE users(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "username VARCHAR(25) NOT NULL UNIQUE, " +
            "password VARCHAR(25) NOT NULL, " +
            "highscore INTEGER NOT NULL DEFAULT 0, " +
            "languageId INTEGER NOT NULL DEFAULT 0, " +
            "avatarId INTEGER NOT NULL DEFAULT 0, " +
            "CONSTRAINT fk_languages FOREIGN KEY (languageId) REFERENCES languages(id), " +
            "CONSTRAINT fk_avatars FOREIGN KEY (avatarId) REFERENCES avatars(id))";

    public static final String UPGRADE_QUERY = "DROP TABLE users;";

    private final DbHelper helper;
    private SQLiteDatabase database;
    private final Context context;

    public UserDao(Context context) {
        this.context = context;
        helper = new DbHelper(context);
    }

    public SQLiteDatabase openWritable() {
        return this.database = helper.getWritableDatabase();
    }

    public SQLiteDatabase openReadable() {
        return this.database = helper.getReadableDatabase();
    }

    @SuppressLint("Range")
    public User getUserFromCursor(Cursor cursor) {
        User user = new User(context);
        user.setId(cursor.getLong(cursor.getColumnIndex("id")));
        user.setUsername(cursor.getString(cursor.getColumnIndex("username")));
        user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
        user.setHighscore(cursor.getInt(cursor.getColumnIndex("highscore")));
        user.setLanguageId(cursor.getInt(cursor.getColumnIndex("languageId")));
        user.setAvatarId(cursor.getInt(cursor.getColumnIndex("avatarId")));
        return user;
    }

    public List<User> findAll() {
        List<User> listOfUsers = new ArrayList<>();
        Cursor cursor = this.database.query("users", null, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                User user = getUserFromCursor(cursor);
                listOfUsers.add(user);
            } while (cursor.moveToNext());
        }

        return listOfUsers;
    }

    public List<User> findById(long id) {
        List<User> listOfUsers = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM users WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                listOfUsers.add(getUserFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        return listOfUsers;
    }

    public List<User> findByUsername(String username) {
        List<User> listOfUsers = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                listOfUsers.add(getUserFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        return listOfUsers;
    }

    public List<User> findByUsernameAndPassword(String username, String password) {
        List<User> listOfUsers = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?", new String[]{username, password});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                listOfUsers.add(getUserFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        return listOfUsers;
    }

    public long insert(User user) {
        ContentValues cv = new ContentValues();
        cv.put("username", user.getUsername());
        cv.put("password", user.getPassword());
        cv.put("highscore", user.getHighscore());
        cv.put("languageId", user.getLanguageId());
        cv.put("avatarId", user.getAvatarId());

        return this.database.insert("users", null, cv);
    }

    public long update(long id, User user) {
        ContentValues cv = new ContentValues();
        cv.put("username", user.getUsername());
        cv.put("password", user.getPassword());
        cv.put("highscore", user.getHighscore());
        cv.put("languageId", user.getLanguageId());
        cv.put("avatarId", user.getAvatarId());

        return this.database.update("users", cv, "id = ?", new String[]{String.valueOf(id)});
    }

    public long delete(long id) {
        return this.database.delete("users", "id = ?", new String[]{id + ""});
    }

    public void close() {
        database.close();
    }
}
