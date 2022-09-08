package bf.be.android.hangman.model.dal.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import bf.be.android.hangman.model.dal.DbHelper;
import bf.be.android.hangman.model.dal.entities.Highscore;

public class HighscoreDao {
    public static final String CREATE_QUERY = "CREATE TABLE highscores(id INTEGER PRIMARY KEY AUTOINCREMENT, score INTEGER NOT NULL, date VARCHAR(25) NOT NULL, languageId INTEGER NOT NULL, userId INTEGER NOT NULL, CONSTRAINT fk_languages FOREIGN KEY (languageId) REFERENCES languages(id), CONSTRAINT fk_users FOREIGN KEY (userId) REFERENCES users(id))";
    public static final String UPGRADE_QUERY = "DROP TABLE highscores;";

    private final DbHelper helper;
    private SQLiteDatabase database;

    public HighscoreDao(Context context) {
        helper = new DbHelper(context);
    }

    public SQLiteDatabase openWritable() {
        return this.database = helper.getWritableDatabase();
    }

    public SQLiteDatabase openReadable() {
        return this.database = helper.getReadableDatabase();
    }

    @SuppressLint("Range")
    public Highscore getHighscoreFromCursor(Cursor cursor) {
        Highscore highscore = new Highscore();
        highscore.setId(cursor.getLong(cursor.getColumnIndex("id")));
        highscore.setScore(cursor.getInt(cursor.getColumnIndex("score")));
        highscore.setDate(cursor.getString(cursor.getColumnIndex("date")));
        highscore.setLanguageId(cursor.getInt(cursor.getColumnIndex("languageId")));
        highscore.setUserId(cursor.getInt(cursor.getColumnIndex("userId")));
        return highscore;
    }

    public List<Highscore> findAll() {
        List<Highscore> listOfHighscores = new ArrayList<>();
        Cursor cursor = this.database.query("highscores", null, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                Highscore highscore = getHighscoreFromCursor(cursor);
                listOfHighscores.add(highscore);
            } while (cursor.moveToNext());
        }

        return listOfHighscores;
    }

    public List<Highscore> findById(long id) {
        List<Highscore> listOfHighscores = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM highscores WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                listOfHighscores.add(getHighscoreFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        return listOfHighscores;
    }

    public long insert(Highscore highscore) {
        ContentValues cv = new ContentValues();
        cv.put("score", highscore.getScore());
        cv.put("date", highscore.getDate());
        cv.put("languageId", highscore.getLanguageId());
        cv.put("userId", highscore.getUserId());

        return this.database.insert("highscores", null, cv);
    }

    public int update(long id, Highscore highscore) {
        ContentValues cv = new ContentValues();
        cv.put("score", highscore.getScore());
        cv.put("date", highscore.getDate());
        cv.put("languageId", highscore.getLanguageId());
        cv.put("userId", highscore.getUserId());

        return this.database.update("highscores", cv, "id = ?", new String[]{String.valueOf(id)});
    }

    public int delete(long id) {
        return this.database.delete("highscores", "id = ?", new String[]{id + ""});
    }

    public void close() {
        database.close();
    }
}
