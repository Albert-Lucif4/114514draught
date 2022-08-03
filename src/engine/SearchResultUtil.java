package engine;

import lib.Util;

public class SearchResultUtil {

    public static long currentBestMove;
    public static String currentBestMoveStr;
    public static int currentBestScore;

    public static void setBestMove(){
        currentBestMove = Util.bestMove;
        currentBestMoveStr = Util.bestMoveStr;
        currentBestScore = Util.bestScore;
    }
}
