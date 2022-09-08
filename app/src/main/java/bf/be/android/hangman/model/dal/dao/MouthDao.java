package bf.be.android.hangman.model.dal.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import bf.be.android.hangman.model.dal.DbHelper;
import bf.be.android.hangman.model.dal.entities.Extra;
import bf.be.android.hangman.model.dal.entities.Mouth;

public class MouthDao {
    public static final String CREATE_QUERY = "CREATE TABLE mouths(id INTEGER PRIMARY KEY AUTOINCREMENT, src VARCHAR(20) NOT NULL UNIQUE)";
    public static final String UPGRADE_QUERY = "DROP TABLE mouths;";

    private final DbHelper helper;
    private SQLiteDatabase database;

    public MouthDao(Context context) {
        helper = new DbHelper(context);
    }

    public SQLiteDatabase openWritable() {
        return this.database = helper.getWritableDatabase();
    }

    public SQLiteDatabase openReadable() {
        return this.database = helper.getReadableDatabase();
    }

    @SuppressLint("Range")
    public Mouth getMouthsFromCursor(Cursor cursor) {
        Mouth mouth = new Mouth();
        mouth.setId(cursor.getLong(cursor.getColumnIndex("id")));
        mouth.setSrc(cursor.getString(cursor.getColumnIndex("src")));
        return mouth;
    }

    public List<Mouth> findAll() {
        List<Mouth> listOfMouths = new ArrayList<>();
        Cursor cursor = this.database.query("mouths", null, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                Mouth mouth = getMouthsFromCursor(cursor);
                listOfMouths.add(mouth);
            } while (cursor.moveToNext());
        }

        return listOfMouths;
    }

    public List<Mouth> findById(long id) {
        List<Mouth> listOfMouths = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM mouths WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                listOfMouths.add(getMouthsFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        return listOfMouths;
    }

    public long insert(Mouth mouth) {
        ContentValues cv = new ContentValues();
        cv.put("src", mouth.getSrc());

        return this.database.insert("extras", null, cv);
    }

    public int update(long id, Mouth mouth) {
        ContentValues cv = new ContentValues();
        cv.put("src", mouth.getSrc());

        return this.database.update("mouths", cv, "id = ?", new String[]{String.valueOf(id)});
    }

    public int delete(long id) {
        return this.database.delete("mouths", "id = ?", new String[]{id + ""});
    }

    public void close() {
        database.close();
    }
}
