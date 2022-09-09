package bf.be.android.hangman.model.dal.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import bf.be.android.hangman.model.dal.DbHelper;
import bf.be.android.hangman.model.dal.entities.Eyebrows;

public class EyebrowsDao {
    public static final String CREATE_QUERY = "CREATE TABLE eyebrows(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "src VARCHAR(20) NOT NULL UNIQUE, " +
            "left INTEGER NOT NULL, " +
            "bottom INTEGER NOT NULL)";
    public static final String UPGRADE_QUERY = "DROP TABLE eyebrows;";

    private final DbHelper helper;
    private SQLiteDatabase database;

    public EyebrowsDao(Context context) {
        helper = new DbHelper(context);
    }

    public SQLiteDatabase openWritable() {
        return this.database = helper.getWritableDatabase();
    }

    public SQLiteDatabase openReadable() {
        return this.database = helper.getReadableDatabase();
    }

    @SuppressLint("Range")
    public Eyebrows getEyebrowsFromCursor(Cursor cursor) {
        Eyebrows eyebrows = new Eyebrows();
        eyebrows.setId(cursor.getLong(cursor.getColumnIndex("id")));
        eyebrows.setSrc(cursor.getString(cursor.getColumnIndex("src")));
        eyebrows.setLeft(cursor.getInt(cursor.getColumnIndex("left")));
        eyebrows.setBottom(cursor.getInt(cursor.getColumnIndex("bottom")));
        return eyebrows;
    }

    public List<Eyebrows> findAll() {
        List<Eyebrows> listOfEyebrows = new ArrayList<>();
        Cursor cursor = this.database.query("eyebrows", null, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                Eyebrows eyebrows = getEyebrowsFromCursor(cursor);
                listOfEyebrows.add(eyebrows);
            } while (cursor.moveToNext());
        }

        return listOfEyebrows;
    }

    public List<Eyebrows> findById(long id) {
        List<Eyebrows> listOfEyebrows = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM eyebrows WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                listOfEyebrows.add(getEyebrowsFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        return listOfEyebrows;
    }

    public long insert(Eyebrows eyebrows) {
        ContentValues cv = new ContentValues();
        cv.put("src", eyebrows.getSrc());
        cv.put("left", eyebrows.getLeft());
        cv.put("bottom", eyebrows.getBottom());

        return this.database.insert("eyebrows", null, cv);
    }

    public int update(long id, Eyebrows eyebrows) {
        ContentValues cv = new ContentValues();
        cv.put("src", eyebrows.getSrc());
        cv.put("left", eyebrows.getLeft());
        cv.put("bottom", eyebrows.getBottom());

        return this.database.update("eyebrows", cv, "id = ?", new String[]{String.valueOf(id)});
    }

    public int delete(long id) {
        return this.database.delete("eyebrows", "id = ?", new String[]{id + ""});
    }

    public void close() {
        database.close();
    }
}
