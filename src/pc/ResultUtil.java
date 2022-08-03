package pc;

public class ResultUtil {
    public static String currentBestMove = "";
    public static int currentBestScore;
    public static long maxPly;
    public static long currentDepth;
    public static double speed;
    public static int scoreState;
    public static long research;
    public static long timeSearch;
    public static long nodes;

    public static void reset(){
        currentBestMove = "";
        currentBestScore = 0;
        maxPly = 0;
        currentDepth = 0;
        speed = 0;
        scoreState = 0;
        research = 0;
        timeSearch = 0;
        nodes = 0;
    }

}
