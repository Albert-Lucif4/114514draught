package engine;

import lib.Util;
import search.TimeUtil;

public class Constants {

    public static final int     MAX_PLIES 				    = 64;
    public static long  qNodes                              = 0;
    public static long  sNodes                              = 0;
    public static long  ttNodes                             = 0;
    public static long  nullpnNodes                         = 0;
    public static long  razoringNodes                       = 0;
    public static long  killerMoveNodes                     = 0;
    public static long  killerMoveAcceptedNodes             = 0;
    public static long  lmpNodes                            = 0;
    public static long futilityNodes                        = 0;
    public static long lmrMissNodes                         = 0;
    public static long pvsMissNodes                         = 0;
    public static long lmrHitNodes                          = 0;
    public static long pvsHitNodes                          = 0;
    public static long mateNodes                            = 0;
    public static long pvNodes                              = 0;
    public static long nullMoveCutOffNodes                  = 0;
    public static long cacheMoveCutOffNodes                 = 0;
    public static long killerMoveCutOffNodes                = 0;
    public static long counterMoveCutOffNodes               = 0;
    public static long normalCutOff                         = 0;
    public static long failHighNodes                        = 0;
    public static long evalCacheHits                        = 0;
    public static long evalCacheMiss                        = 0;
    public static long capdetectCacheHits                   = 0;
    public static long capdetectCacheMiss                   = 0;
    public static long maxDepth                             = 0;
    public static long maxPad                               = 0;
    public static long maxWindowDepth                       = 0;
    public static long AWPResearchCount                     = 0;
    public static long ThinkingTime                         = 0;
    public static boolean ENABLE_STATIC_NULL_MOVE           = true;
    public static boolean ENABLE_RAZORING                   = true;
    public static boolean ENABLE_LMP                        = true;
    public static boolean ENABLE_FUTILITY_PRUNING           = true;
    public static boolean ENABLE_LMR                        = true;
    public static boolean ENABLE_PVS                        = true;
    public static final boolean ENABLE_NULL_MOVE            = false;
    public static final boolean COUNT_NODES                 = true;
    public static final boolean ENABLE_COUNT                = true;
    public static final boolean ENABLE_EVAL_CACHE           = true;
    public static final boolean ENABLE_CAP_CACHE            = true;
    public static final boolean ENABLE_ASPIRATION           = true;
    public static final boolean ENABLE_KILLER_MOVE          = false;
    public static final boolean ENABLE_IID                  = false;
    public static final boolean ENABLE_COUNTER              = false;
    public static final boolean ENABLE_TIME_COUNTER         = false;

    /* send detail to terminal */
    public static void showDetail(){
        System.out.println("Best move:                   " + Util.bestMoveStr);
        System.out.println("Time:                        " + TimeUtil.getTime());
        System.out.println("Score:                       " + Util.bestScore);
        System.out.println("Max Ply:                     " + maxDepth);
        System.out.println("Max depth:                   " + maxWindowDepth);
        System.out.println("Max Extension:               " + maxPad);
        System.out.println("AWP Research:                " + AWPResearchCount);
        System.out.println("qNodes:                      " + qNodes);
        System.out.println("sNodes:                      " + sNodes);
        System.out.println("ttNodes:                     " + ttNodes);
        System.out.println("Null move prunning Nodes:    " + nullpnNodes);
        System.out.println("Null move cut-off:           " + nullMoveCutOffNodes);
        System.out.println("Razoring Nodes:              " + razoringNodes);
        System.out.println("Killer move Nodes:           " + killerMoveNodes);
        System.out.println("Killer move accepted Nodes:  " + killerMoveAcceptedNodes);
        System.out.println("LMP Nodes:                   " + lmpNodes);
        System.out.println("Futility Nodes:              " + futilityNodes);
        System.out.println("LMR Miss Nodes:              " + lmrMissNodes);
        System.out.println("PVS Miss Nodes:              " + pvsMissNodes);
        System.out.println("LMR Hit Nodes:               " + lmrHitNodes);
        System.out.println("PVS Hit Nodes:               " + pvsHitNodes);
        System.out.println("Mate Nodes:                  " + mateNodes);
        System.out.println("Cache Cut off Nodes:         " + cacheMoveCutOffNodes);
        System.out.println("Killer move Cut off Nodes:   " + killerMoveCutOffNodes);
        System.out.println("Counter move Cut off Nodes:  " + counterMoveCutOffNodes);
        System.out.println("Normal Cut off Nodes:        " + normalCutOff);
        System.out.println("Eval Cache Hit:              " + evalCacheHits);
        System.out.println("Eval Cache Miss:             " + evalCacheMiss);
        System.out.println("Cap Cache Hit:               " + capdetectCacheHits);
        System.out.println("Cap Cache Miss:              " + capdetectCacheMiss);
        System.out.println("PV Nodes:                    " + pvNodes);
        System.out.println("Fail High Nodes:             " + failHighNodes);
    }

    public static void reset(){
        qNodes                              = 0;
        sNodes                              = 0;
        ttNodes                             = 0;
        nullpnNodes                         = 0;
        razoringNodes                       = 0;
        killerMoveNodes                     = 0;
        killerMoveAcceptedNodes             = 0;
        lmpNodes                            = 0;
        futilityNodes                        = 0;
        lmrMissNodes                         = 0;
        pvsMissNodes                         = 0;
        lmrHitNodes                         = 0;
        pvsHitNodes                          = 0;
        mateNodes                            = 0;
        pvNodes                              = 0;
        nullMoveCutOffNodes                  = 0;
        cacheMoveCutOffNodes                 = 0;
        killerMoveCutOffNodes                = 0;
        counterMoveCutOffNodes               = 0;
        normalCutOff                         = 0;
        failHighNodes                        = 0;
        evalCacheHits                        = 0;
        evalCacheMiss                        = 0;
        maxDepth                             = 0;
        maxWindowDepth                       = 0;
        maxPad                               = 0;
        AWPResearchCount                     = 0;
        ThinkingTime                         = 0;
    }

}
