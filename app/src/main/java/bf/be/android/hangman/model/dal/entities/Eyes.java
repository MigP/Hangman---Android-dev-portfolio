package bf.be.android.hangman.model.dal.entities;

import androidx.annotation.NonNull;

public class Eyes {
    private long id;
    private String src;
    private int left;
    private int bottom;

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
                ", left='" + left + '\'' +
                ", bottom='" + bottom + '\'' +
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

    public int getLeft() {
        return left;
    }

    public Eyes setLeft(int left) {
        this.left = left;
        return this;
    }

    public int getBottom() {
        return bottom;
    }

    public Eyes setBottom(int bottom) {
        this.bottom = bottom;
        return this;
    }
}
