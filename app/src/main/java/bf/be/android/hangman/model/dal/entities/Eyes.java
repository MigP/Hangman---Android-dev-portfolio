package bf.be.android.hangman.model.dal.entities;

import androidx.annotation.NonNull;

public class Eyes {
    private long id;
    private String src;

    public Eyes() {
    }

    public Eyes(String src) {
        this.src = src;
    }

    @NonNull
    @Override
    public String toString() {
        return "Eyes{" +
                "id='" + id + '\'' +
                ", src='" + src + '\'' +
                '}';
    }

    public long getId() {
        return id;
    }


    public Eyes setId(long id) {
        this.id = id;
        return this;
    }

    public String getSrc() {
        return src;
    }

    public Eyes setSrc(String src) {
        this.src = src;
        return this;
    }
}
