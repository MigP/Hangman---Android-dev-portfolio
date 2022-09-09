package bf.be.android.hangman.model.dal.entities;

import androidx.annotation.NonNull;

public class Extra {
    private long id;
    private String src;
    private int left;
    private int bottom;

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
                ", left='" + left + '\'' +
                ", bottom='" + bottom + '\'' +
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

    public int getLeft() {
        return left;
    }

    public Extra setLeft(int left) {
        this.left = left;
        return this;
    }

    public int getBottom() {
        return bottom;
    }

    public Extra setBottom(int bottom) {
        this.bottom = bottom;
        return this;
    }
}
