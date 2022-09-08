package bf.be.android.hangman.model.dal.entities;

import androidx.annotation.NonNull;

public class Eyebrows {
    private long id;
    private String src;

    public Eyebrows() {
    }

    public Eyebrows(String src) {
        this.src = src;
    }

    @NonNull
    @Override
    public String toString() {
        return "Eyebrows{" +
                "id='" + id + '\'' +
                ", src='" + src + '\'' +
                '}';
    }

    public long getId() {
        return id;
    }

    public Eyebrows setId(long id) {
        this.id = id;
        return this;
    }

    public String getSrc() {
        return src;
    }

    public Eyebrows setSrc(String src) {
        this.src = src;
        return this;
    }
}
