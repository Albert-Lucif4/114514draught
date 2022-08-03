package pc;

public class Deviation {
    private int xDeviation;
    private int yDeviation;

    Deviation(int xDeviation, int yDeviation) {
        this.xDeviation = xDeviation;
        this.yDeviation = yDeviation;
    }

    int getX() {
        return this.xDeviation;
    }

    int getY() {
        return this.yDeviation;
    }
}
