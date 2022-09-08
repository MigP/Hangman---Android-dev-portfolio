package bf.be.android.hangman.model.dal.entities;

import androidx.annotation.NonNull;

public class Extra {
    private long id;
    private String src;

    public Extra() {
    }

    public Extra(String src) {
        this.src = src;
    }

    @NonNull
    @Override
    public String toString() {
        return "Extras{" +
                "id='" + id + '\'' +
                ", src='" + src + '\'' +
                '}';
    }

    public long getId() {
        return id;
    }


    public Extra setId(long id) {
        this.id = id;
        return this;
    }

    public String getSrc() {
        return src;
    }

    public Extra setSrc(String src) {
        this.src = src;
        return this;
    }
}
