package bf.be.android.hangman.model.dal.entities;

import androidx.annotation.NonNull;

public class Mouth {
    private long id;
    private String src;

    public Mouth() {
    }

    public Mouth(String src) {
        this.src = src;
    }

    @NonNull
    @Override
    public String toString() {
        return "Mouth{" +
                "id='" + id + '\'' +
                ", src='" + src + '\'' +
                '}';
    }

    public long getId() {
        return id;
    }


    public Mouth setId(long id) {
        this.id = id;
        return this;
    }

    public String getSrc() {
        return src;
    }

    public Mouth setSrc(String src) {
        this.src = src;
        return this;
    }
}
