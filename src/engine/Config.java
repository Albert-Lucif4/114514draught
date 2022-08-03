package engine;

public class Config {
    public static int[][] nullMargins = {
            {0, 400, 800, 1300, 1700, 2400, 3000}, // depth 9, ply 34
            {0, 400, 600, 1000, 1300, 1900, 2500}, // depth 10, ply 35
            {0, 200, 400, 600, 1000, 1300, 1600}, // depth 10, ply 30
    };

    public static int nullIndex = 0;

    public static int[][] razoringMargins = {
            {0, 500, 1000, 1500},
            {0, 800, 1000, 1500 },
            {0, 400, 800, 1300}
    };

    public static int razorIndex = 0;

    public static int[][] futilityMargins = {
            { 0, 400, 570, 990, 1500, 2000, 2500 }
    };

    public static int futilityIndex = 0;
}
