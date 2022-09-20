package bf.be.android.hangman.model.dal.entities;

import androidx.annotation.NonNull;

public class Highscore {
    private long id;
    private int score;
    private String date;
    private int languageId;
    private int userId;
    private int avatarId;

    public Highscore() {
    }

    public Highscore(int score, String date, int languageId, int userId, int avatarId) {
        this.score = score;
        this.date = date;
        this.languageId = languageId;
        this.userId = userId;
        this.avatarId = avatarId;
    }

    @NonNull
    @Override
    public String toString() {
        return "Highscore{" +
                "id='" + id + '\'' +
                ", score='" + score + '\'' +
                ", date='" + date + '\'' +
                ", languageId='" + languageId + '\'' +
                ", userId='" + userId + '\'' +
                ", avatarId='" + avatarId + '\'' +
                '}';
    }

    public long getId() {
        return id;
    }

    public Highscore setId(long id) {
        this.id = id;
        return this;
    }

    public int getScore() {
        return score;
    }

    public Highscore setScore(int score) {
        this.score = score;
        return this;
    }

    public String getDate() {
        return date;
    }

    public Highscore setDate(String date) {
        this.date = date;
        return this;
    }

    public int getLanguageId() {
        return languageId;
    }

    public Highscore setLanguageId(int languageId) {
        this.languageId = languageId;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Highscore setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public Highscore setAvatarId(int avatarId) {
        this.avatarId = avatarId;
        return this;
    }
}
