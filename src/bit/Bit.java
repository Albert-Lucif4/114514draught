package bit;

import java.util.ArrayList;

import static lib.Direction.*;

/**
 *
 * @author prn
 */
public class Bit {
    private static final int[] index = {
            -1, 0, 1, 39, 2, 15, 40, 23, 3, 12, 16,
            -1, 41, 19, 24, -1, 4, -1, 13, 10, 17,
            -1, -1, 28, 42, 30, 20, -1, 25, 44, -1,
            47, 5, 32, -1, 38, 14, 22, 11, -1, 18, -1, -1,
            9, -1, 27, 29, -1, 43, 46, 31, 37, 21, -1, -1, 8, 26, 49, 45,
            36, -1, 7, 48, 35, 6, 34, 33
    };

    public static int bitCount(long i) {
        i = i - ((i >>> 1) & 0x5555555555555555L);
        i = (i & 0x3333333333333333L) + ((i >>> 2) & 0x3333333333333333L);
        i = (i + (i >>> 4)) & 0x0f0f0f0f0f0f0f0fL;
        i = i + (i >>> 8);
        i = i + (i >>> 16);
        i = i + (i >>> 32);
        return (int)i & 0x7f;
    }



    public static int Index(long i) {
        return index[(int) (i % 67)];
    }

    public static ArrayList<Integer> getIndex(long bit){
        ArrayList<Integer> result = new ArrayList<>();
        while (bit != 0L){
            result.add(Index(bit & -bit));
            bit = bit & (bit - 1);
        }
        return result;
    }

    public static long shift(long bb, int direction) {
        switch (direction) {
            case north_east:
                return (bb & 0xf03c0f03c00L) >>> 4 | (bb & 0x3e0f83e0f83e0L) >>> 5;
            case north_west:
                return (bb & 0x1f07c1f07c00L) >>> 5 | (bb & 0x3c0f03c0f03c0L) >>> 6;
            case south_east:
                return ((bb & 0xf03c0f03c0fL) << 6 | (bb & 0xf83e0f83e0L) << 5) & 0x3ffffffffffffL;
            case south_west:
                return ((bb & 0x1f07c1f07c1fL) << 5 | (bb & 0xf03c0f03c0L) << 4) & 0x3ffffffffffffL;
            case north:
                return bb >>> 10;
            case south:
                return bb << 10 & 0x3ffffffffffffL;
            case west:
                return bb >> 1 & 0x1ef7bdef7bdefL;
            case east:
                return bb << 1 & 0x3def7bdef7bdeL;
        }
        return 0;
    }

    public static long reverse(long bb){
        return ~bb & 0x3ffffffffffffL;
    }

    public static boolean more_than_one(long bb){
        return (bb & (bb - 1)) != 0;
    }
}
