package bf.be.android.hangman.model.dal.entities;

import androidx.annotation.NonNull;

public class Language {
    private long id;
    private String name;

    public Language() {
    }

    public Language(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return "Language{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
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
}