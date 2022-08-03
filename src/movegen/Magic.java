package movegen;

import bit.Bit;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import lib.Direction;
import java.util.ArrayList;

import static bit.Bit.shift;
import static lib.Color.black;
import static lib.Color.white;
import static lib.Direction.*;


public class Magic {

    public static void init() {
        load();
    }


    public static final long[] MASK = new long[64];

    static {
        for (int i = 0; i < 64; i++) {
            MASK[i] = 1L << i;
        }
    }

    public static int[] row = new int[]{
            0, 0, 0, 0, 0,
            1, 1, 1, 1, 1,
            2, 2, 2, 2, 2,
            3, 3, 3, 3, 3,
            4, 4, 4, 4, 4,
            5, 5, 5, 5, 5,
            6, 6, 6, 6, 6,
            7, 7, 7, 7, 7,
            8, 8, 8, 8, 8,
            9, 9, 9, 9, 9
    };
    public static int[] column = new int[]{
            1, 3, 5, 7, 9,
            0, 2, 4, 6, 8,
            1, 3, 5, 7, 9,
            0, 2, 4, 6, 8,
            1, 3, 5, 7, 9,
            0, 2, 4, 6, 8,
            1, 3, 5, 7, 9,
            0, 2, 4, 6, 8,
            1, 3, 5, 7, 9,
            0, 2, 4, 6, 8
    };
    public static int[][] index = {
            {-1, 0, -1, 1, -1, 2, -1, 3, -1, 4},
            {5, -1, 6, -1, 7, -1, 8, -1, 9, -1},
            {-1, 10, -1, 11, -1, 12, -1, 13, -1, 14},
            {15, -1, 16, -1, 17, -1, 18, -1, 19, -1},
            {-1, 20, -1, 21, -1, 22, -1, 23, -1, 24},
            {25, -1, 26, -1, 27, -1, 28, -1, 29, -1},
            {-1, 30, -1, 31, -1, 32, -1, 33, -1, 34},
            {35, -1, 36, -1, 37, -1, 38, -1, 39, -1},
            {-1, 40, -1, 41, -1, 42, -1, 43, -1, 44},
            {45, -1, 46, -1, 47, -1, 48, -1, 49, -1}
    };

    public static long[][]      PawnNormalMove          = new long[2][50];
    public static long[]        PawnOccupiedSq          = new long[50];
    public static long[][]      BesideSquares           = new long[2][50];
    public static int[]         WallSquaresBeside       = new int[50];
    public static long[][]      ForwardStormSquares     = new long[2][50];
    public static int[][]       ForwardStormSquaresCount = new int[2][50];
    public static long[][]      ForwardRankThreeFiles   = new long[2][50];
    public static long[][]      DiagonalMask            = new long[2][50];
    public static long[][]      DirectionMask           = new long[4][50];
    public static long[][][]    KingMoveBB              = new long[50][2][1024];
    public static long[][][]    KingAttackBB            = new long[50][2][1024];
    public static long[]        KingMoveMask            = new long[50];
    public static long[][]      Shift_1_Mask            = new long[4][50];
    public static long[][]      Shift_2_Mask            = new long[4][50];
    public static long[][]      Fill_3_Mask             = new long[4][50];
    public static long[][]      BetweenBB               = new long[50][50];
    public static long[][]      AfterBB                 = new long[2][50];
    //Shape of you
    public static long[]        DiamondShape            = new long[50];
    public static long[]        SpinnerShape            = new long[50];
    public static long[]        BoomerangShape          = new long[50];
    public static long[][]      SpaceShipShape          = new long[2][50];

    //direction
    public static int           NE                      = 0;
    public static int           NW                      = 1;
    public static int           SE                      = 2;
    public static int           SW                      = 3;
    public static int           NDIR                    = 4;
    public static long          LegalMask               = 0x3ffffffffffffL;
    public static long          RANK1BB                 = 0x3e00000000000L;
    public static long          RANK2BB                 = RANK1BB >>> 5;
    public static long          RANK3BB                 = RANK1BB >>> 10;
    public static long          RANK4BB                 = RANK1BB >>> 15;
    public static long          RANK5BB                 = RANK1BB >>> 20;
    public static long          RANK6BB                 = RANK1BB >>> 25;
    public static long          RANK7BB                 = RANK1BB >>> 30;
    public static long          RANK8BB                 = RANK1BB >>> 35;
    public static long          RANK9BB                 = RANK1BB >>> 40;
    public static long          RANK10BB                = RANK1BB >>> 45;
    public static long          FILE1BB                 = 0x200802008020L;
    public static long          FILE2BB                 = 0x10040100401L;
    public static long          FILE3BB                 = FILE1BB << 1;
    public static long          FILE4BB                 = FILE2BB << 1;
    public static long          FILE5BB                 = FILE1BB << 2;
    public static long          FILE6BB                 = FILE2BB << 2;
    public static long          FILE7BB                 = FILE1BB << 3;
    public static long          FILE8BB                 = FILE2BB << 3;
    public static long          FILE9BB                 = FILE1BB << 4;
    public static long          FILE10BB                = FILE2BB << 4;
    public static long[]        RANKS                   = {
            RANK1BB, RANK2BB, RANK3BB, RANK4BB, RANK5BB, RANK6BB, RANK7BB, RANK8BB, RANK9BB, RANK10BB
    };
    public static long[]        FILES                   = {
            FILE1BB, FILE2BB, FILE3BB, FILE4BB, FILE5BB, FILE6BB, FILE7BB, FILE8BB, FILE9BB, FILE10BB
    };
    public static long[]        THREATING_SQUARES       = {0x100c030L, 0x300c02000000L};

    public static long getKingMove(int i, long occupied, boolean capture){
        int[] h = hash(i, occupied);
        return capture ? KingAttackBB[i][0][h[0]] | KingAttackBB[i][1][h[1]] : KingMoveBB[i][0][h[0]] | KingMoveBB[i][1][h[1]];
    }

    private static void load() {
        generate_shape();
        generate_mask();
        generate_men_normal_move();
        generate_king_move();
    }

    //Generate bitboard data

    private static void generate_shape(){
        for(int i = 0; i < 50; i++){
            long mask = 0x1L << i;
            long nw = shift(mask, north_west);
            long ne = shift(mask, north_east);
            long sw = shift(mask, south_west);
            long se = shift(mask, south_east);

            if(column[i] == 1){
                BoomerangShape[i] = mask | nw | sw;
            }
            else if(column[i] == 8) {
                BoomerangShape[i] = mask | ne | se;
            }

            if(column[i] > 0 && column[i] < 9 && row[i] < 9 && row[i] > 0) SpinnerShape[i] = mask | sw | se | ne | nw;
            if(column[i] > 1 && column[i] < 8 && row[i] < 8 && row[i] > 1) DiamondShape[i] = SpinnerShape[i] | shift(mask, north) | shift(mask, south) | shift(mask, west) | shift(mask, east);
            if(column[i] < 6){
                SpaceShipShape[white][i] = mask | ne | shift(ne, east) | shift(shift(ne, east), south_east);
                SpaceShipShape[black][i] = mask | se | shift(se, east) | shift(shift(se, east), north_east);
            }
        }
    }


    private static void generate_men_normal_move() {
        for (int i = 0; i < 50; i++) {
            long mask = 0x1L << i;
            PawnNormalMove[0][i] |= shift(MASK[i], north_east) | shift(MASK[i], north_west);
            PawnNormalMove[1][i] |= shift(MASK[i], south_east) | shift(MASK[i], south_west);
            PawnOccupiedSq[i] = PawnNormalMove[0][i] | PawnNormalMove[1][i];
            BesideSquares[0][i] = shift(MASK[i], north_east) | shift(MASK[i], south_west);
            BesideSquares[1][i] = shift(MASK[i], north_west) | shift(MASK[i], south_east);
            WallSquaresBeside[i] = 4 - Bit.bitCount(PawnOccupiedSq[i]);

            long tmp = DirectionMask[NW][i] | MASK[i];
            while (tmp != 0) {
                int index = Bit.Index(tmp & -tmp);
                ForwardStormSquares[white][i] |= DirectionMask[NE][index] | MASK[index];
                tmp = tmp & (tmp - 1);
            }
            tmp = DirectionMask[NE][i] | MASK[i];
            while (tmp != 0) {
                int index = Bit.Index(tmp & -tmp);
                ForwardStormSquares[white][i] |= DirectionMask[NW][index] | MASK[index];
                tmp = tmp & (tmp - 1);
            }
            ForwardStormSquares[white][i] ^= MASK[i];
            ForwardStormSquaresCount[white][i] = Bit.bitCount(ForwardStormSquares[white][i]);

            tmp = DirectionMask[SW][i] | MASK[i];
            while (tmp != 0) {
                int index = Bit.Index(tmp & -tmp);
                ForwardStormSquares[black][i] |= DirectionMask[SE][index] | MASK[index];
                tmp = tmp & (tmp - 1);
            }

            tmp = DirectionMask[SE][i] | MASK[i];
            while (tmp != 0) {
                int index = Bit.Index(tmp & -tmp);
                ForwardStormSquares[black][i] |= DirectionMask[SW][index] | MASK[index];
                tmp = tmp & (tmp - 1);
            }
            ForwardStormSquares[black][i] ^= MASK[i];
            ForwardStormSquaresCount[black][i] = Bit.bitCount(ForwardStormSquares[black][i]);

        }
    }

    private static int[] hash(int index, long occupied) {
        return new int[]{hash(DiagonalMask[0][index] & occupied), hash(DiagonalMask[1][index] & occupied)};
    }

    private static void generate_king_move() {
        int[] h;
        for (int i = 0; i < 50; i++) {
            ArrayList<Long> blockers = get_blockers_list(KingMoveMask[i]);
            for(long blocker: blockers){
                h = hash(i, blocker);
                KingMoveBB[i][0][h[0]] = get_bb_from_blocker(i, blocker, north_east) | get_bb_from_blocker(i, blocker, south_west);
                KingMoveBB[i][1][h[1]] = get_bb_from_blocker(i, blocker, north_west) | get_bb_from_blocker(i, blocker, south_east);
                KingAttackBB[i][0][h[0]] = get_capture_bb_from_blocker(i, blocker, north_east) | get_capture_bb_from_blocker(i, blocker, south_west);
                KingAttackBB[i][1][h[1]] = get_capture_bb_from_blocker(i, blocker, north_west) | get_capture_bb_from_blocker(i, blocker, south_east);
            }
        }
    }

    private static void generate_mask() {
        for (int i = 0; i < 50; i++) {
            long mask = 0x1L << i;
            DirectionMask[NE][i] = get_bb_from_direction(i, Direction.north_east);
            DirectionMask[NW][i] = get_bb_from_direction(i, Direction.north_west);
            DirectionMask[SE][i] = get_bb_from_direction(i, Direction.south_east);
            DirectionMask[SW][i] = get_bb_from_direction(i, Direction.south_west);
            DiagonalMask[0][i] = DirectionMask[NE][i] | DirectionMask[SW][i];
            DiagonalMask[1][i] = DirectionMask[NW][i] | DirectionMask[SE][i];
            KingMoveMask[i] = DiagonalMask[0][i] | DiagonalMask[1][i];

            Shift_1_Mask[NE][i] = shift(mask, north_east);
            Shift_1_Mask[NW][i] = shift(mask, north_west);
            Shift_1_Mask[SE][i] = shift(mask, south_east);
            Shift_1_Mask[SW][i] = shift(mask, south_west);

            Shift_2_Mask[NE][i] = shift(Shift_1_Mask[NE][i], north_east);
            Shift_2_Mask[NW][i] = shift(Shift_1_Mask[NW][i], north_west);
            Shift_2_Mask[SE][i] = shift(Shift_1_Mask[SE][i], south_east);
            Shift_2_Mask[SW][i] = shift(Shift_1_Mask[SW][i], south_west);

            Fill_3_Mask[NE][i] = mask | Shift_1_Mask[NE][i] | Shift_2_Mask[NE][i];
            Fill_3_Mask[NW][i] = mask | Shift_1_Mask[NW][i] | Shift_2_Mask[NW][i];
            Fill_3_Mask[SE][i] = mask | Shift_1_Mask[SE][i] | Shift_2_Mask[SE][i];
            Fill_3_Mask[SW][i] = mask | Shift_1_Mask[SW][i] | Shift_2_Mask[SW][i];

            int count = 1;
            long tmp = shift(mask, Direction.north) |
                    shift(mask, Direction.north_west) |
                    shift(mask, Direction.north_east);
            while (tmp != 0) {
                ForwardRankThreeFiles[white][i] |= tmp;
                tmp = shift(tmp, Direction.north);
                count++;
                if (count == 4) break;
            }

            count = 1;
            tmp = shift(0x1L << i, Direction.south) |
                    shift(0x1L << i, Direction.south_west) |
                    shift(0x1L << i, Direction.south_east);
            while (tmp != 0) {
                ForwardRankThreeFiles[black][i] |= tmp;
                tmp = shift(tmp, Direction.south);
                count++;
                if (count == 4) break;
            }

            AfterBB[white][i] = shift(mask, south_east) | shift(mask, south_west) | shift(mask, south);
            AfterBB[black][i] = shift(mask, north_east) | shift(mask, north_west) | shift(mask, north);

        }

        for(int i = 0; i < 50; i++){
            for(int j = 0; j < 50; j++){
                if((KingMoveMask[i] & MASK[j]) == 0) continue;
                if((DirectionMask[NE][i] & MASK[j]) != 0){
                    BetweenBB[i][j] = get_bb_from_blocker(i, MASK[j], north_east) ^ MASK[j];
                } else if((DirectionMask[NW][i] & MASK[j]) != 0){
                    BetweenBB[i][j] = get_bb_from_blocker(i, MASK[j], north_west) ^ MASK[j];
                } else if((DirectionMask[SE][i] & MASK[j]) != 0){
                    BetweenBB[i][j] = get_bb_from_blocker(i, MASK[j], south_east) ^ MASK[j];
                } else if((DirectionMask[SW][i] & MASK[j]) != 0){
                    BetweenBB[i][j] = get_bb_from_blocker(i, MASK[j], south_west) ^ MASK[j];
                } else throw new ValueException("generate between bitboard error!");
            }
        }
    }

    //Getter functions
    private static long get_capture_bb_from_blocker(int pos, long blocker, int direction) {
        long result = 0;
        switch (direction) {
            case north_east:
                while (northeast(pos) != -1 && (0x1L << northeast(pos) & blocker) == 0) pos = northeast(pos);
                if (northeast(pos) != -1) {
                    result |= 0x1L << northeast(pos);
                    result |= get_bb_from_blocker(northeast(pos), blocker, direction);
                }
                break;
            case north_west:
                while (northwest(pos) != -1 && (0x1L << northwest(pos) & blocker) == 0) pos = northwest(pos);
                if (northwest(pos) != -1) {
                    result |= 0x1L << northwest(pos);
                    result |= get_bb_from_blocker(northwest(pos), blocker, direction);
                }
                break;
            case south_east:
                while (southeast(pos) != -1 && (0x1L << southeast(pos) & blocker) == 0) pos = southeast(pos);
                if (southeast(pos) != -1) {
                    result |= 0x1L << southeast(pos);
                    result |= get_bb_from_blocker(southeast(pos), blocker, direction);
                }
                break;
            case south_west:
                while (southwest(pos) != -1 && (0x1L << southwest(pos) & blocker) == 0) pos = southwest(pos);
                if (southwest(pos) != -1) {
                    result |= 0x1L << southwest(pos);
                    result |= get_bb_from_blocker(southwest(pos), blocker, direction);
                }
                break;
        }

        return result;
    }

    private static long get_bb_from_blocker(int pos, long blocker, int direction) {
        long result = 0;
        switch (direction) {
            case north_east:
                while (northeast(pos) != -1 && (0x1L << northeast(pos) & blocker) == 0) {
                    result |= 0x1L << northeast(pos);
                    pos = northeast(pos);
                }
                break;
            case north_west:
                while (northwest(pos) != -1 && (0x1L << northwest(pos) & blocker) == 0) {
                    result |= 0x1L << northwest(pos);
                    pos = northwest(pos);
                }
                break;
            case south_east:
                while (southeast(pos) != -1 && (0x1L << southeast(pos) & blocker) == 0) {
                    result |= 0x1L << southeast(pos);
                    pos = southeast(pos);
                }
                break;
            case south_west:
                while (southwest(pos) != -1 && (0x1L << southwest(pos) & blocker) == 0) {
                    result |= 0x1L << southwest(pos);
                    pos = southwest(pos);
                }
                break;
        }

        return result;
    }

    private static long get_bb_from_direction(int pos, int direction) {
        return get_bb_from_blocker(pos, 0, direction);
    }

    private static long get_bb_from_array(ArrayList<Integer> arrayList) {
        long result = 0x0L;
        for (int i : arrayList) result |= 0x1L << i;
        return result;
    }

    private static void get_sub_bb(int start, int max, ArrayList<Integer> currentIndex, ArrayList<Integer> indexes, ArrayList<Long> out) {
        if (start >= indexes.size() && max != 0) throw new ValueException("get_sub_bb exception!");
        if (max == 0) out.add(get_bb_from_array(currentIndex));
        else {
            for (int i = start; i < indexes.size() - max + 1; i++) {
                ArrayList<Integer> tmp = new ArrayList<>(currentIndex);
                tmp.add(indexes.get(i));
                get_sub_bb(i + 1, max - 1, tmp, indexes, out);
            }
        }
    }

    private static ArrayList<Long> get_blockers_list(long bb) {
        ArrayList<Integer> indexes = Bit.getIndex(bb);
        ArrayList<Long> blockers = new ArrayList<>();
        for (int i = 0; i <= indexes.size(); i++) get_sub_bb(0, i, new ArrayList<>(), indexes, blockers);
        return blockers;
    }

    //Helper functions

    private boolean is_ok(int position) {
        return 0 < row[position] && row[position] < 11 && 0 < column[position] && column[position] < 11;
    }

    private static int northeast(int position) {
        return row[position] > 0 && column[position] < 9 ? index[row[position] - 1][column[position] + 1] : -1;
    }

    private static int northwest(int position) {
        return row[position] > 0 && column[position] > 0 ? index[row[position] - 1][column[position] - 1] : -1;
    }

    private static int southeast(int position) {
        return row[position] < 9 && column[position] < 9 ? index[row[position] + 1][column[position] + 1] : -1;
    }

    private static int southwest(int position) {
        return row[position] < 9 && column[position] > 0 ? index[row[position] + 1][column[position] - 1] : -1;
    }

    /* 哈希使用旋转位。
     *  */
    private static int hash(long bb) {
        return (int) ((((bb & 0x1f07c1f07c1fL) * 0x210842108421L) & 0x1f0000000000L) >>> 40 |
                     (((bb & 0x3e0f83e0f83e0L) * 0x210842108421L) & 0x3e00000000000L) >>> 40);
    }
}
