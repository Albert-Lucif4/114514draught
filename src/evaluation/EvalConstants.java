package evaluation;

import java.util.Random;
import static lib.Color.black;
import static lib.Color.white;

public class EvalConstants {
    static int mg = 0;
    static int eg = 1;

    static long RightSide = 0x31cc731cc731cL;
    static long CenterLeft = 0x1c6719c6719c6L;
    static long CenterRight = 0x18e6398e6398eL;
    static long FileWall = 0x2318c6318c631L;
    static long RankWall = 0x3e0000000001fL;
    static long CenterSquares = 0x198660000L;
    static long LongDiagonal = 0x239d398672e71L;
    static long RightShortFlank = 0x2188621886218L;
    static long LeftShortFlank = 0x611846118461L;
    static long LongFlank = 0x1866198661986L;


    public static int[] Rank = {
            9, 9, 9, 9, 9,
            8, 8, 8, 8, 8,
            7, 7, 7, 7, 7,
            6, 6, 6, 6, 6,
            5, 5, 5, 5, 5,
            4, 4, 4, 4, 4,
            3, 3, 3, 3, 3,
            2, 2, 2, 2, 2,
            1, 1, 1, 1, 1,
            0, 0, 0, 0, 0
    };

    static int[] File = {
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

    static int[][][] PAWN_MATERIAL = {
            { // WHITE
                    { // MG
                            0, 0, 0, 0, 0,
                            90, 80, 80, 80, 80,
                            20, 0, 0, 0, 80,
                            70, 0, 0, 0, 20,
                            20, 30, 30, 0, 60,
                            50, 0, 30, 30, 20,
                            10, 5, 5, 5, 10,
                            0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0,
                            0, 0, 50, 0, 0
                    },
                    { // EG
                            200, 200, 200, 200, 200,
                            56, 68, 80, 74, 62,
                            55, 67, 73, 61, 49,
                            42, 54, 66, 60, 48,
                            41, 53, 59, 47, 35,
                            28, 40, 52, 46, 34,
                            27, 39, 45, 33, 21,
                            14, 26, 38, 32, 20,
                            13, 25, 31, 19, 7,
                            0, 12, 24, 18, 6
                    }
            },
            { // BLACK
                    { // MG
                            0, 0, 50, 0, 0,
                            0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0,
                            10, 5, 5, 5, 10,
                            20, 30, 30, 0, 50,
                            60, 0, 30, 30, 20,
                            20, 0, 0, 0, 70,
                            80, 0, 0, 0, 20,
                            80, 80, 80, 80, 90,
                            0, 0, 0, 0, 0,
                    },
                    { // EG
                            6, 18, 24, 12, 0,
                            7, 19, 31, 25, 13,
                            20, 32, 38, 26, 14,
                            21, 33, 45, 39, 27,
                            34, 46, 52, 40, 28,
                            35, 47, 59, 53, 41,
                            48, 60, 66, 54, 42,
                            49, 61, 73, 67, 55,
                            62, 74, 80, 68, 56,
                            200, 200, 200, 200, 200
                    }
            }
    };


    static int[][][] KING_MATERIAL = {
            { // WHITE
                    { // MG
                            5, 5, 5, 5, 5,
                            5, 10, 20, 5, 5,
                            10, 10, 30, 10, 10,
                            10, 30, 40, 30, 10,
                            10, 50, 50, 20, 10,
                            0, 30, 50, 40, 5,
                            10, 40, 40, 5, 0,
                            0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0
                    },
                    { // EG
                            5, 5, 5, 5, 5,
                            5, 10, 20, 5, 5,
                            10, 10, 30, 10, 10,
                            10, 30, 40, 30, 10,
                            10, 50, 50, 20, 10,
                            0, 30, 50, 40, 5,
                            10, 40, 40, 5, 0,
                            0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0
                    }
            },
            { // BLACK
                    { // MG
                            0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0,
                            0, 5, 40, 40, 10,
                            5, 40, 50, 30, 0,
                            10, 20, 50, 50, 10,
                            10, 30, 40, 30, 10,
                            10, 10, 30, 10, 10,
                            5, 5, 20, 10, 5,
                            5, 5, 5, 5, 5,
                    },
                    { // EG
                            0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0,
                            0, 0, 0, 0, 0,
                            0, 5, 40, 40, 10,
                            5, 40, 50, 30, 0,
                            10, 20, 50, 50, 10,
                            10, 30, 40, 30, 10,
                            10, 10, 30, 10, 10,
                            5, 5, 20, 10, 5,
                            5, 5, 5, 5, 5,
                    }
            }
    };

    //PawnValue[mg/eg]
    static int[] PawnValue = {500, 510};
    //KingValue[mg/eg]
    static int[] KingValue = {1710, 1770};
    //PawnRaito[mg/eg]
    static int[] PawnRaito = {1500, 1700};
    //KingRaito[mg/eg]
    static int[] KingRaito = {2000, 2500};
    //PawnPassedRank
    static int[][] PawnPassedRank = {
            {0, 2, 4, 7, 16, 38, 51, 95, 920},
            {0, 13, 15, 20, 110, 190, 237, 277, 1000}
    };

    //PawnSafety[number of assistances] <0 -> 4>
    static int[][] PawnSafety = {
            {-80, -10, 27, 41, 75},
            {0, 5, 10, -15, -32}
    };

    //PawnOnWall[file_wall/rank_wall]
    static int[][] PawnOnWall = {
            {15, 17},
            {-15, -17}
    };


    // ThreatByPawnLine[pawn/king]
    static int[] ThreatByPawnLine = {-40, -50};

    //OpenSquare[mg/eg]
    static int[] OpenSquare = {55, 80};

    //BlockPawnStorn[mg/eg]
    static int[][] BlockPawnStorm = {
            {
                    0, 0, 0, 2, 3, 7, 14, 20, 40, 100
            },
            {
                    0, 0, 0, 2, 3, 8, 17, 24, 45, 130
            }
    };

    //KingMobility[mg/eg][number of moves]
    static int[][] KingMobility = {
            {
                    -457, -297, -233, -121, -95, 2, 5, 8, 12, 17, 23, 30, 39, 49, 60, 65, 73, 80
            },
            {
                    -357, -231, -153, -121, 0, 2, 5, 5, 5, 7, 13, 20, 29, 29, 30, 35, 43, 50
            }

    };

    //ThreatByPawn[mg/eg][number of supporters]
    static int[][] ThreatByPawn = {
            {
                    -90, -40, -5, 0
            },
            {
                    -50, -30, 0, 0
            }
    };

    //PawnMobility[mg/eg]
    static int[] PawnMobility = {29, 25};

    //BalancePieces[mg/eg]
    static int[] BalancePieces = {
            31, 35
    };

    //KingSupporters[mg/eg]
    static int[] KingSupporters = {
            35, 40
    };

    //PiecesDanger[mg/eg]
    static int[][] PiecesDanger = {
            { // in turn
                    448, 483
            },
            {//not in turn
                    77, 319
            }
    };

    static int[] ThreatByABunch = {
            7, 10
    };

    static int[] CenterOccupation = {
            25, 21
    };

    static int[] GoldenStoneBonus = {
            50, 40
    };

    static int[] KingOnLongDiagonal = {
            32, 86
    };

    static int[] KingSeeLongDiagonal = {
            2, 5
    };

    static int[][] ShapeBonus = {
            //Diamond, Spinner, Boomerang, SpaceShip
            {
                22, 15, 78, 18
            },
            {
                30, 20, 60, 25
            }
    };

    static int[] EmptyFile = {
            47, 45
    };

    static int[] Pinned = {
            23, 97
    };

    static int[] Lonely = {
            125, 25
    };

    static int[] ThreatOnHightWall = {
            25, 30
    };

    static int[] TrappedPawn = {
            0, 67
    };

    static int[] SafePawnBonus = {
            23, 0
    };

    public static void randomWeight(){
        Random rd = new Random();
        for(int i = 0; i < 50; i++){
            PAWN_MATERIAL[white][mg][i] = (rd.nextInt(20) + 4) * 5;
            PAWN_MATERIAL[black][mg][i] = (rd.nextInt(20) + 4) * 5;
        }
    }
}
