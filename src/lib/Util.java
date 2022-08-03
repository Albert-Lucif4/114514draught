package lib;

import engine.Config;
import engine.SearchResultUtil;

/**
 *保存移动点的结果
 *在搜索中保存奖励的默认值
 *
 *
 */
public class Util {

    public static final short SHORT_MIN                         = -32767;
    public static final short SHORT_MAX                         = 32767;
    public static final int   SCORE_MATE_BOUND                  = 30000;
    public static final int     SCORE_MUST_MOVE                 = 40000;
    public static final int     PHASE_TT                        = 1;
    public static final int   PHASE_KILLER_1                    = 2;
    public static final int   PHASE_KILLER_2                    = 3;
    public static final int PHASE_COUNTER       = 4;
    public static final int PHASE_QUIET                         =  5;
    public static final int ASPIRATION_WINDOW_DELTA             = 50;
    public static final int[] STATIC_NULLMOVE_MARGIN            = Config.nullMargins[Config.nullIndex];
    public static final int[] RAZORING_MARGIN                   = Config.razoringMargins[Config.razorIndex];
    public static final int[] FUTILITY_MARGIN                   = Config.futilityMargins[Config.futilityIndex];
    public static final int[] MATERIAL 					        = {453, 1470};
    public static final int SCORE_DRAW 						    = 0;
    public static long bestMove                                 = 0;
    public static String bestMoveStr                            = null;
    public static int bestScore                                 = SHORT_MIN;


    /* 重置结果 */
    public static void reset(){
        bestScore = SHORT_MIN;
        bestMoveStr = null;
        bestMove = 0;
    }

    public static void getFromSearchResult(){
        bestMoveStr = SearchResultUtil.currentBestMoveStr;
        bestMove = SearchResultUtil.currentBestMove;
        bestScore = SearchResultUtil.currentBestScore;
    }

}
