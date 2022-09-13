package bf.be.android.hangman.model.dal.entities;

import androidx.annotation.NonNull;

public class User {
    private long id;
    private String username;
    private String password;
    private Integer languageId;
    private Integer avatarId;
    private Integer highscoreId;
    private Integer coins;
    private Integer banknotes;
    private Integer diamonds;
    private Integer lives;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.languageId = 0;
        this.avatarId = 0;
        this.highscoreId = 0;
        this.coins = 0;
        this.banknotes = 0;
        this.diamonds = 0;
        this.lives = 3;
    }

    public User(String username, String password, Integer languageId, Integer avatarId, Integer highscoreId, Integer coins, Integer banknotes, Integer diamonds, Integer lives) {
        this.username = username;
        this.password = password;
        this.languageId = languageId;
        this.avatarId = avatarId;
        this.highscoreId = highscoreId;
        this.coins = coins;
        this.banknotes = banknotes;
        this.diamonds = diamonds;
        this.lives = lives;
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
}
