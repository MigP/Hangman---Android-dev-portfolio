package bf.be.android.hangman.model.dal.entities;

import androidx.annotation.NonNull;

public class Language {
    private long id;
    private String name;
    private String src;

    public Language() {
    }

    public Language(String name, String src) {
        this.name = name;
        this.src = src;
    }

    @NonNull
    @Override
    public String toString() {
        return "Language{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", src='" + src + '\'' +
                '}';
    }

    public long getId() {
        return id;
    }

    public Language setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Language setName(String name) {
        this.name = name;
        return this;
    }

    public String getSrc() {
        return src;
    }

    public Language setSrc(String src) {
        this.src = src;
        return this;
    }
}