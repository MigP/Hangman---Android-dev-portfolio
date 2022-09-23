package bf.be.android.hangman.model.dal.entities;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.NonNull;

import bf.be.android.hangman.R;
import bf.be.android.hangman.model.dal.DbHelper;

public class User {
    private final Context context;
    private long id;
    private String username;
    private String password;
    private int highscore;
    private int languageId = 0;
    private int avatarId = 0;
    private int coins = 0;
    private int banknotes = 0;
    private int diamonds = 0;
    private int lives = 5;
    private int score = 0;

    public User(Context context) {
        this.context = context;

        initialiseConstants();
    }

    public User(Context context, String username, String password) {
        this.context = context;
        this.username = username;
        this.password = password;

        initialiseConstants();
    }

    public User(Context context, String username, String password, int highscore, int languageId, int avatarId) {
        this.context = context;
        this.username = username;
        this.password = password;
        this.highscore = highscore;
        this.languageId = languageId;
        this.avatarId = avatarId;

        initialiseConstants();
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", highscore='" + highscore + '\'' +
                ", languageId='" + languageId + '\'' +
                ", avatarId='" + avatarId + '\'' +
                ", coins='" + coins + '\'' +
                ", banknotes='" + banknotes + '\'' +
                ", diamonds='" + diamonds + '\'' +
                ", lives='" + lives + '\'' +
                ", score='" + score + '\'' +
                '}';
    }

    public long getId() {
        return id;
    }

    public User setId(long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public int getHighscore() {
        return highscore;
    }

    public User setHighscore(int highscore) {
        this.highscore = highscore;
        return this;
    }

    public int getLanguageId() {
        return languageId;
    }

    public User setLanguageId(int languageId) {
        this.languageId = languageId;
        return this;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public User setAvatarId(int avatarId) {
        this.avatarId = avatarId;
        return this;
    }

    public int getCoins() {
        return coins;
    }

    public User setCoins(int coins) {
        this.coins = coins;
        return this;
    }

    public int getBanknotes() {
        return banknotes;
    }

    public User setBanknotes(int banknotes) {
        this.banknotes = banknotes;
        return this;
    }

    public int getDiamonds() {
        return diamonds;
    }

    public User setDiamonds(int diamonds) {
        this.diamonds = diamonds;
        return this;
    }

    public int getLives() {
        return lives;
    }

    public User setLives(int lives) {
        this.lives = lives;
        return this;
    }

    public int getScore() {
        return score;
    }

    public User setScore(int score) {
        this.score = score;
        return this;
    }

    private void initialiseConstants() {
        Resources res = this.context.getResources();

        String[] gameConstantsValues = res.getStringArray(R.array.user_resources);
        this.coins = Integer.parseInt(gameConstantsValues[0]);
        this.banknotes = Integer.parseInt(gameConstantsValues[1]);
        this.diamonds = Integer.parseInt(gameConstantsValues[2]);
        this.lives = Integer.parseInt(gameConstantsValues[3]);
        this.score = Integer.parseInt(gameConstantsValues[4]);
    }
}
