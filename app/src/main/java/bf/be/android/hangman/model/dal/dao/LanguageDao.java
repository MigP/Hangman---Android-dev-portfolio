package bf.be.android.hangman.model.dal.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import bf.be.android.hangman.model.dal.DbHelper;
import bf.be.android.hangman.model.dal.entities.Language;
import bf.be.android.hangman.model.dal.entities.User;

public class LanguageDao {
    public static final String CREATE_QUERY = "CREATE TABLE languages(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name VARCHAR(10) NOT NULL UNIQUE)";
    public static final String UPGRADE_QUERY = "DROP TABLE languages;";

    private final DbHelper helper;
    private SQLiteDatabase database;

    public LanguageDao(Context context) {
        helper = new DbHelper(context);
    }

    public SQLiteDatabase openWritable() {
        return this.database = helper.getWritableDatabase();
    }

    public SQLiteDatabase openReadable() {
        return this.database = helper.getReadableDatabase();
    }

    @SuppressLint("Range")
    public Language getLanguageFromCursor(Cursor cursor) {
        Language language = new Language();
        language.setId(cursor.getLong(cursor.getColumnIndex("id")));
        language.setName(cursor.getString(cursor.getColumnIndex("name")));
        return language;
    }

    public List<Language> findAll() {
        List<Language> listOfLanguages = new ArrayList<>();
        Cursor cursor = this.database.query("languages", null, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                Language language = getLanguageFromCursor(cursor);
                listOfLanguages.add(language);
            } while (cursor.moveToNext());
        }

        return listOfLanguages;
    }

    public List<Language> findById(long id) {
        List<Language> listOfLanguages = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM languages WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                listOfLanguages.add(getLanguageFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        return listOfLanguages;
    }

    public long insert(Language language) {
        ContentValues cv = new ContentValues();
        cv.put("name", language.getName());

        return this.database.insert("languages", null, cv);
    }

    public int update(long id, Language language) {
        ContentValues cv = new ContentValues();
        cv.put("name", language.getName());

        return this.database.update("languages", cv, "id = ?", new String[]{String.valueOf(id)});
    }

    public int delete(long id) {
        return this.database.delete("languages", "id = ?", new String[]{id + ""});
    }

    public void close() {
        database.close();
    }
}

