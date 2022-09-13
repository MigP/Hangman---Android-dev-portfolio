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


public class ExtraDao {
    public static final String CREATE_QUERY = "CREATE TABLE extras(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "src VARCHAR(20) NOT NULL UNIQUE)";

    public static final String UPGRADE_QUERY = "DROP TABLE extras;";

    private final DbHelper helper;
    private SQLiteDatabase database;

    public ExtraDao(Context context) {
        helper = new DbHelper(context);
    }

    public SQLiteDatabase openWritable() {
        return this.database = helper.getWritableDatabase();
    }

    public SQLiteDatabase openReadable() {
        return this.database = helper.getReadableDatabase();
    }

    @SuppressLint("Range")
    public Extra getExtrasFromCursor(Cursor cursor) {
        Extra extra = new Extra();
        extra.setId(cursor.getLong(cursor.getColumnIndex("id")));
        extra.setSrc(cursor.getString(cursor.getColumnIndex("src")));
        return extra;
    }

    public List<Extra> findAll() {
        List<Extra> listOfExtras = new ArrayList<>();
        Cursor cursor = this.database.query("extras", null, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                Extra extra = getExtrasFromCursor(cursor);
                listOfExtras.add(extra);
            } while (cursor.moveToNext());
        }

        return listOfExtras;
    }

    public List<Extra> findById(long id) {
        List<Extra> listOfExtras = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM extras WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                listOfExtras.add(getExtrasFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        return listOfExtras;
    }

    public long insert(Extra extra) {
        ContentValues cv = new ContentValues();
        cv.put("src", extra.getSrc());

        return this.database.insert("extras", null, cv);
    }

    public long update(long id, Extra extra) {
        ContentValues cv = new ContentValues();
        cv.put("src", extra.getSrc());

        return this.database.update("extras", cv, "id = ?", new String[]{String.valueOf(id)});
    }

    public long delete(long id) {
        return this.database.delete("extras", "id = ?", new String[]{id + ""});
    }

    public void close() {
        database.close();
    }
}
