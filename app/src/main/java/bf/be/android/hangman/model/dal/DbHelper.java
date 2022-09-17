package bf.be.android.hangman.model.dal;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import bf.be.android.hangman.R;
import bf.be.android.hangman.model.dal.dao.AvatarDao;
import bf.be.android.hangman.model.dal.dao.ExtraDao;
import bf.be.android.hangman.model.dal.dao.EyebrowsDao;
import bf.be.android.hangman.model.dal.dao.EyesDao;
import bf.be.android.hangman.model.dal.dao.HighscoreDao;
import bf.be.android.hangman.model.dal.dao.LanguageDao;
import bf.be.android.hangman.model.dal.dao.MouthDao;
import bf.be.android.hangman.model.dal.dao.UserDao;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "hangman_db";
    public static int DB_VERSION = 1;
    private static Context context;

    public DbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        DbHelper.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(UserDao.CREATE_QUERY);
        sqLiteDatabase.execSQL(MouthDao.CREATE_QUERY);
        sqLiteDatabase.execSQL(LanguageDao.CREATE_QUERY);
        sqLiteDatabase.execSQL(HighscoreDao.CREATE_QUERY);
        sqLiteDatabase.execSQL(EyesDao.CREATE_QUERY);
        sqLiteDatabase.execSQL(EyebrowsDao.CREATE_QUERY);
        sqLiteDatabase.execSQL(ExtraDao.CREATE_QUERY);
        sqLiteDatabase.execSQL(AvatarDao.CREATE_QUERY);

        prepopulateDb(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(UserDao.UPGRADE_QUERY);
        sqLiteDatabase.execSQL(MouthDao.UPGRADE_QUERY);
        sqLiteDatabase.execSQL(LanguageDao.UPGRADE_QUERY);
        sqLiteDatabase.execSQL(HighscoreDao.UPGRADE_QUERY);
        sqLiteDatabase.execSQL(EyesDao.UPGRADE_QUERY);
        sqLiteDatabase.execSQL(EyebrowsDao.UPGRADE_QUERY);
        sqLiteDatabase.execSQL(ExtraDao.UPGRADE_QUERY);
        sqLiteDatabase.execSQL(AvatarDao.UPGRADE_QUERY);
        DB_VERSION = i1;
        onCreate(sqLiteDatabase);
    }

    private void prepopulateDb(SQLiteDatabase sqLiteDatabase) {
        Resources res = DbHelper.context.getResources();

        String[] languagesArray = res.getStringArray(R.array.languages);
        ContentValues languagesValues = new ContentValues();
        for (int i = 0; i < languagesArray.length; i += 2){
            languagesValues.put("name", languagesArray[i]);
            languagesValues.put("src", languagesArray[i + 1]);
            sqLiteDatabase.insert("languages", null, languagesValues);
        }

        String[] eyesArray = res.getStringArray(R.array.eyes);
        ContentValues eyesValues = new ContentValues();
        for (String s : eyesArray) {
            eyesValues.put("src", s);
            sqLiteDatabase.insert("eyes", null, eyesValues);
        }

        String[] extrasArray = res.getStringArray(R.array.extras);
        ContentValues extrasValues = new ContentValues();
        for (String s : extrasArray) {
            extrasValues.put("src", s);
            sqLiteDatabase.insert("extras", null, extrasValues);
        }

        String[] mouthsArray = res.getStringArray(R.array.mouths);
        ContentValues mouthsValues = new ContentValues();
        for (String s : mouthsArray) {
            mouthsValues.put("src", s);
            sqLiteDatabase.insert("mouths", null, mouthsValues);
        }

        String[] eyebrowsArray = res.getStringArray(R.array.eyebrows);
        ContentValues eyebrowsValues = new ContentValues();
        for (String s : eyebrowsArray) {
            eyebrowsValues.put("src", s);
            sqLiteDatabase.insert("eyebrows", null, eyebrowsValues);
        }

        String[] avatarsArray = res.getStringArray(R.array.avatars);
        ContentValues avatarsValues = new ContentValues();
        for (int i = 0; i < avatarsArray.length; i += 9){
            avatarsValues.put("head_shot", avatarsArray[i]);
            avatarsValues.put("head_src", avatarsArray[i + 1]);
            avatarsValues.put("torso_src", avatarsArray[i + 2]);
            avatarsValues.put("left_arm_src", avatarsArray[i + 3]);
            avatarsValues.put("right_arm_src", avatarsArray[i + 4]);
            avatarsValues.put("left_leg_src", avatarsArray[i + 5]);
            avatarsValues.put("right_leg_src", avatarsArray[i + 6]);
            avatarsValues.put("eyesId", 9);
            if (avatarsArray[i + 8].equals("light")) {
                avatarsValues.put("mouthId", 7);
                avatarsValues.put("eyebrowsId", 4);
            } else {
                avatarsValues.put("mouthId", 3);
                avatarsValues.put("eyebrowsId", 3);
            }
            avatarsValues.put("extrasId", avatarsArray[i + 7]);
            avatarsValues.put("complexion", avatarsArray[i + 8]);
            sqLiteDatabase.insert("avatars", null, avatarsValues);
        }
    }
}
