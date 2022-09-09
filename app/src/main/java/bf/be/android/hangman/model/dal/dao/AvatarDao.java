package bf.be.android.hangman.model.dal.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import bf.be.android.hangman.model.dal.DbHelper;
import bf.be.android.hangman.model.dal.entities.Avatar;

public class AvatarDao {
    public static final String CREATE_QUERY = "CREATE TABLE avatars(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "head_shot VARCHAR(20) NOT NULL UNIQUE, " +
            "head_src VARCHAR(20) NOT NULL UNIQUE, " +
            "head_left INTEGER NOT NULL, " +
            "head_bottom INTEGER NOT NULL, " +
            "torso_src VARCHAR(20) NOT NULL UNIQUE, " +
            "torso_left INTEGER NOT NULL, " +
            "torso_bottom INTEGER NOT NULL, " +
            "left_arm_src VARCHAR(20) NOT NULL UNIQUE, " +
            "left_arm_left INTEGER NOT NULL, " +
            "left_arm_bottom INTEGER NOT NULL, " +
            "right_arm_src VARCHAR(20) NOT NULL UNIQUE, " +
            "right_arm_left INTEGER NOT NULL, " +
            "right_arm_bottom INTEGER NOT NULL, " +
            "left_leg_src VARCHAR(20) NOT NULL UNIQUE, " +
            "left_leg_left INTEGER NOT NULL, " +
            "left_leg_bottom INTEGER NOT NULL, " +
            "right_leg_src VARCHAR(20) NOT NULL UNIQUE, " +
            "right_leg_left INTEGER NOT NULL, " +
            "right_leg_bottom INTEGER NOT NULL, " +
            "eyesId INTEGER NOT NULL DEFAULT 9, " +
            "mouthId INTEGER NOT NULL, " +
            "eyebrowsId INTEGER NOT NULL, " +
            "extrasId INTEGER, " +
            "complexion VARCHAR(5) NOT NULL, " +
            "CONSTRAINT fk_eyes FOREIGN KEY (eyesId) REFERENCES eyes(id), " +
            "CONSTRAINT fk_mouth FOREIGN KEY (mouthId) REFERENCES mouths(id))";
    public static final String UPGRADE_QUERY = "DROP TABLE avatars;";

    private final DbHelper helper;
    private SQLiteDatabase database;

    public AvatarDao(Context context) {
        helper = new DbHelper(context);
    }

    public SQLiteDatabase openWritable() {
        return this.database = helper.getWritableDatabase();
    }

    public SQLiteDatabase openReadable() {
        return this.database = helper.getReadableDatabase();
    }

    @SuppressLint("Range")
    public Avatar getAvatarFromCursor(Cursor cursor) {
        Avatar avatar = new Avatar();
        avatar.setId(cursor.getLong(cursor.getColumnIndex("id")));
        avatar.setHeadShot(cursor.getString(cursor.getColumnIndex("head_shot")));
        avatar.setHeadSrc(cursor.getString(cursor.getColumnIndex("head_src")));
        avatar.setHeadLeft(cursor.getInt(cursor.getColumnIndex("head_left")));
        avatar.setHeadBottom(cursor.getInt(cursor.getColumnIndex("head_bottom")));

        avatar.setTorsoSrc(cursor.getString(cursor.getColumnIndex("torso_src")));
        avatar.setTorsoLeft(cursor.getInt(cursor.getColumnIndex("torso_left")));
        avatar.setTorsoBottom(cursor.getInt(cursor.getColumnIndex("torso_bottom")));

        avatar.setLeftArmSrc(cursor.getString(cursor.getColumnIndex("left_arm_src")));
        avatar.setLeftArmLeft(cursor.getInt(cursor.getColumnIndex("left_arm_left")));
        avatar.setLeftArmBottom(cursor.getInt(cursor.getColumnIndex("left_arm_bottom")));

        avatar.setRightArmSrc(cursor.getString(cursor.getColumnIndex("right_arm_src")));
        avatar.setRightArmLeft(cursor.getInt(cursor.getColumnIndex("right_arm_left")));
        avatar.setRightArmBottom(cursor.getInt(cursor.getColumnIndex("right_arm_bottom")));

        avatar.setLeftLegSrc(cursor.getString(cursor.getColumnIndex("left_leg_src")));
        avatar.setLeftLegLeft(cursor.getInt(cursor.getColumnIndex("left_leg_left")));
        avatar.setLeftLegBottom(cursor.getInt(cursor.getColumnIndex("left_leg_bottom")));

        avatar.setRightLegSrc(cursor.getString(cursor.getColumnIndex("right_leg_src")));
        avatar.setRightLegLeft(cursor.getInt(cursor.getColumnIndex("right_leg_left")));
        avatar.setRightLegBottom(cursor.getInt(cursor.getColumnIndex("right_leg_bottom")));

        avatar.setEyesId(cursor.getInt(cursor.getColumnIndex("eyesId")));
        avatar.setMouthId(cursor.getInt(cursor.getColumnIndex("mouthId")));
        avatar.setEyebrowsId(cursor.getInt(cursor.getColumnIndex("eyebrowsId")));
        avatar.setExtrasId(cursor.getInt(cursor.getColumnIndex("extrasId")));
        avatar.setComplexion(cursor.getString(cursor.getColumnIndex("complexion")));
        return avatar;
    }

    public List<Avatar> findAll() {
        List<Avatar> listOfAvatars = new ArrayList<>();
        Cursor cursor = this.database.query("avatars", null, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                Avatar avatar = getAvatarFromCursor(cursor);
                listOfAvatars.add(avatar);
            } while (cursor.moveToNext());
        }

        return listOfAvatars;
    }

    public List<Avatar> findById(long id) {
        List<Avatar> listOfAvatars = new ArrayList<>();
        Cursor cursor = this.database.rawQuery("SELECT * FROM avatars WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                listOfAvatars.add(getAvatarFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        return listOfAvatars;
    }

    public long insert(Avatar avatar) {
        ContentValues cv = new ContentValues();
        cv.put("head_shot", avatar.getHeadShot());
        cv.put("head_src", avatar.getHeadSrc());
        cv.put("head_left", avatar.getHeadLeft());
        cv.put("head_bottom", avatar.getHeadBottom());

        cv.put("torso_src", avatar.getTorsoSrc());
        cv.put("torso_left", avatar.getTorsoLeft());
        cv.put("torso_bottom", avatar.getTorsoBottom());

        cv.put("left_arm_src", avatar.getLeftArmSrc());
        cv.put("left_arm_left", avatar.getLeftArmLeft());
        cv.put("left_arm_bottom", avatar.getLeftArmBottom());

        cv.put("right_arm_src", avatar.getRightArmSrc());
        cv.put("right_arm_left", avatar.getRightArmLeft());
        cv.put("right_arm_bottom", avatar.getRightArmBottom());

        cv.put("left_leg_src", avatar.getLeftLegSrc());
        cv.put("left_leg_left", avatar.getLeftLegLeft());
        cv.put("left_leg_bottom", avatar.getLeftLegBottom());

        cv.put("right_leg_src", avatar.getRightLegSrc());
        cv.put("right_leg_left", avatar.getRightLegLeft());
        cv.put("right_leg_bottom", avatar.getRightLegBottom());

        cv.put("eyesId", avatar.getEyesId());
        cv.put("mouthId", avatar.getMouthId());
        cv.put("eyebrowsId", avatar.getEyebrowsId());
        cv.put("extrasId", avatar.getExtrasId());
        cv.put("complexion", avatar.getComplexion());

        return this.database.insert("avatars", null, cv);
    }

    public int update(long id, Avatar avatar) {
        ContentValues cv = new ContentValues();
        cv.put("head_shot", avatar.getHeadShot());
        cv.put("head_src", avatar.getHeadSrc());
        cv.put("head_left", avatar.getHeadLeft());
        cv.put("head_bottom", avatar.getHeadBottom());

        cv.put("torso_src", avatar.getTorsoSrc());
        cv.put("torso_left", avatar.getTorsoLeft());
        cv.put("torso_bottom", avatar.getTorsoBottom());

        cv.put("left_arm_src", avatar.getLeftArmSrc());
        cv.put("left_arm_left", avatar.getLeftArmLeft());
        cv.put("left_arm_bottom", avatar.getLeftArmBottom());

        cv.put("right_arm_src", avatar.getRightArmSrc());
        cv.put("right_arm_left", avatar.getRightArmLeft());
        cv.put("right_arm_bottom", avatar.getRightArmBottom());

        cv.put("left_leg_src", avatar.getLeftLegSrc());
        cv.put("left_leg_left", avatar.getLeftLegLeft());
        cv.put("left_leg_bottom", avatar.getLeftLegBottom());

        cv.put("right_leg_src", avatar.getRightLegSrc());
        cv.put("right_leg_left", avatar.getRightLegLeft());
        cv.put("right_leg_bottom", avatar.getRightLegBottom());

        cv.put("eyesId", avatar.getEyesId());
        cv.put("mouthId", avatar.getMouthId());
        cv.put("eyebrowsId", avatar.getEyebrowsId());
        cv.put("extrasId", avatar.getExtrasId());
        cv.put("complexion", avatar.getComplexion());

        return this.database.update("avatars", cv, "id = ?", new String[]{String.valueOf(id)});
    }

    public int delete(long id) {
        return this.database.delete("avatars", "id = ?", new String[]{id + ""});
    }

    public void close() {
        database.close();
    }
}
