package pc;

import movegen.Magic;

public class ScreenPos {
    private int row;
    private int column;

    ScreenPos(int row, int column) {
        this.row = row;
        this.column = column;
    }

    boolean valid() {
        return 0 <= row && row < 10 && column < 10 && column >= 0;
    }

    int getRow() {
        return row;
    }

    int index() {
        if (!valid()) return -1;
        return Magic.index[row][column];
    }

    public ScreenPos setColumn(int column) {
        this.column = column;
        return this;
    }

    int getColumn() {
        return column;
    }

    public ScreenPos setRow(int row) {
        this.row = row;
        return this;
    }

    void reset() {
        this.row = -1;
        this.column = -1;
    }


    public String toString() {
        return Integer.toString(this.row) + "," + Integer.toString(this.column);
    }
}
