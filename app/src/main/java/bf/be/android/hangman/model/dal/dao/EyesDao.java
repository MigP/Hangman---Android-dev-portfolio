package bf.be.android.hangman.model.dal.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import bf.be.android.hangman.model.dal.DbHelper;
import bf.be.android.hangman.model.dal.entities.Eyes;

public class EyesDao {
    public static final String CREATE_QUERY = "CREATE TABLE eyes(id INTEGER PRIMARY KEY AUTOINCREMENT, src VARCHAR(20) NOT NULL UNIQUE, left INTEGER NOT NULL, bottom INTEGER NOT NULL)";
    public static final String UPGRADE_QUERY = "DROP TABLE eyes;";

    private final DbHelper helper;
    private SQLiteDatabase database;

    public EyesDao(Context context) {
        helper = new DbHelper(context);
    }

    public SQLiteDatabase openWritable() {
        return this.database = helper.getWritableDatabase();
    }

    public SQLiteDatabase openReadable() {
        return this.database = helper.getReadableDatabase();
    }

    @SuppressLint("Range")
    public Eyes getEyesFromCursor(Cursor cursor) {
        Eyes eyes = new Eyes();
        eyes.setId(cursor.getLong(cursor.getColumnIndex("id")));
        eyes.setSrc(cursor.getString(cursor.getColumnIndex("src")));
        eyes.setLeft(cursor.getInt(cursor.getColumnIndex("left")));
        eyes.setBottom(cursor.getInt(cursor.getColumnIndex("bottom")));
        return eyes;
    }

    public List<Eyes> findAll() {
        List<Eyes> listOfEyes = new ArrayList<>();
        Cursor cursor = this.database.query("eyes", null, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                Eyes eyes = getEyesFromCursor(cursor);
                listOfEyes.add(eyes);
            } while (cursor.moveToNext());
        }

        return listOfEyes;
    }

    public List<Eyes> findById(long id) {
        List<Eyes> listOfEyes = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM eyes WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                listOfEyes.add(getEyesFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        return listOfEyes;
    }

    public long insert(Eyes eyes) {
        ContentValues cv = new ContentValues();
        cv.put("src", eyes.getSrc());
        cv.put("left", eyes.getLeft());
        cv.put("bottom", eyes.getBottom());

        return this.database.insert("eyes", null, cv);
    }

    public int update(long id, Eyes eyes) {
        ContentValues cv = new ContentValues();
        cv.put("src", eyes.getSrc());
        cv.put("left", eyes.getLeft());
        cv.put("bottom", eyes.getBottom());

        return this.database.update("eyes", cv, "id = ?", new String[]{String.valueOf(id)});
    }

    public int delete(long id) {
        return this.database.delete("eyes", "id = ?", new String[]{id + ""});
    }

    public void close() {
        database.close();
    }
}
