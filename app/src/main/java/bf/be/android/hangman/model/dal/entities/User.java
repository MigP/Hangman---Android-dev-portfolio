package bf.be.android.hangman.model.dal.entities;

import androidx.annotation.NonNull;

public class User {
    private long id;
    private String username;
    private String password;
    private Integer languageId = 0;
    private Integer avatarId = 0;
    private Integer highscoreId = 0;
    private Integer coins = 0;
    private Integer banknotes = 0;
    private Integer diamonds = 0;
    private Integer lives = 5;
    private Integer score = 0;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, Integer languageId, Integer avatarId, Integer highscoreId) {
        this.username = username;
        this.password = password;
        this.languageId = languageId;
        this.avatarId = avatarId;
        this.highscoreId = highscoreId;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", languageId='" + languageId + '\'' +
                ", avatarId='" + avatarId + '\'' +
                ", highscoreId='" + highscoreId + '\'' +
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

    public Integer getLanguageId() {
        return languageId;
    }

    public User setLanguageId(Integer languageId) {
        this.languageId = languageId;
        return this;
    }

    public Integer getAvatarId() {
        return avatarId;
    }

    public User setAvatarId(Integer avatarId) {
        this.avatarId = avatarId;
        return this;
    }

    public Integer getHighscoreId() {
        return highscoreId;
    }

    public User setHighscoreId(Integer highscoreId) {
        this.highscoreId = highscoreId;
        return this;
    }

    public Integer getCoins() {
        return coins;
    }

    public User setCoins(Integer coins) {
        this.coins = coins;
        return this;
    }

    public Integer getBanknotes() {
        return banknotes;
    }

    public User setBanknotes(Integer banknotes) {
        this.banknotes = banknotes;
        return this;
    }

    public Integer getDiamonds() {
        return diamonds;
    }

    public User setDiamonds(Integer diamonds) {
        this.diamonds = diamonds;
        return this;
    }

    public Integer getLives() {
        return lives;
    }

    public User setLives(Integer lives) {
        this.lives = lives;
        return this;
    }

    public Integer getScore() {
        return score;
    }

    public User setScore(Integer score) {
        this.score = score;
        return this;
    }
}
