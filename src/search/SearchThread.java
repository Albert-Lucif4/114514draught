package search;


import bit.Bitboard;
import engine.Constants;
import engine.Engine;
import engine.SearchResultUtil;
import evaluation.EvalCache;
import lib.Util;
import movegen.CaptureDetectionCache;
import movegen.ConvertMove;
import movegen.MoveGenUtil;

public class SearchThread {
    private int maxDepth;
    private Bitboard board;

    public SearchThread(Bitboard board, int maxDepth) {
        this.maxDepth = maxDepth;
        this.board = board;
    }



    /* 开始搜索 */
    public void run() {
        /* reset values */
        TimeUtil.start();
        EvalCache.clearValues();
        CaptureDetectionCache.clearValues();

        /* init */
        int depth = 0;
        int alpha = Util.SHORT_MIN;
        int beta = Util.SHORT_MAX;
        int score = Util.SHORT_MIN;

        /* start searching with aspiration window */
        Loop: while (depth < this.maxDepth) {
            /* check time is left */
            if (TimeUtil.isTimeLeft()){
                break;
            }

            /* extend depth by 1 */
            depth++;
            if(Constants.ENABLE_COUNT) Constants.maxWindowDepth = Math.max(Constants.maxWindowDepth, depth);
            int delta = Util.ASPIRATION_WINDOW_DELTA;
            boolean lower = false;

            while (true) {
                if (TimeUtil.isTimeLeft()){
                    Util.getFromSearchResult();
                    Engine.sendInfo();
                    break;
                }

                /* init hash tables */
                MoveGenUtil moveGen = new MoveGenUtil();
                TTUtil tt = new TTUtil();
                EvalCache.clearValues();
                CaptureDetectionCache.clearValues();

                /* do search */
                score = Search.search(board, moveGen, tt, 0, depth, alpha, beta, 0);
                Util.bestScore = score;
                Engine.sendInfo();

                /* Mate found */
                if(Math.abs(score) > Util.SCORE_MATE_BOUND && Math.abs(score) <= Util.SHORT_MAX){
                    break Loop;
                }

                /* check time is left */
                if (TimeUtil.isTimeLeft()){
                    if(Math.abs(score) == Util.SCORE_MUST_MOVE) {
                        Util.getFromSearchResult();
                        Engine.sendInfo();
                        break;
                    }
                }

                /* change asporation window delta */
                if (score <= alpha && alpha != Util.SHORT_MIN) {
                    Engine.sendScoreInfo(TTUtil.FLAG_LOWER);
                    /* extend window delta to the left and then do research*/
                    if (score < -10) {
                        alpha = Util.SHORT_MIN;
                        beta = Util.SHORT_MAX;
                    } else {
                        alpha = Math.max(alpha - delta, Util.SHORT_MIN);
                    }
                    delta *= 2;
                    if(Constants.ENABLE_COUNT) Constants.AWPResearchCount++;
                } else {
                    if (score >= beta && beta != Util.SHORT_MAX) {
                        Engine.sendScoreInfo(TTUtil.FLAG_UPPER);
                        /* extend window delta to the right and then do research */
                        if (score > 10) {
                            alpha = Util.SHORT_MIN;
                            beta = Util.SHORT_MAX;
                        } else {
                            beta = Math.min(beta + delta, Util.SHORT_MAX);
                        }
                        delta *= 2;
                        if (Constants.ENABLE_COUNT) Constants.AWPResearchCount++;
                        SearchResultUtil.setBestMove();

                    } else {
                        Engine.sendScoreInfo(TTUtil.FLAG_EXACT);
                        /* continue searching at next depth after change alpha and beta*/
                        if (Constants.ENABLE_ASPIRATION && depth > 5) {
                            if (Math.abs(score) > 10) {
                                alpha = Util.SHORT_MIN;
                                beta = Util.SHORT_MAX;
                            } else {
                                delta = (delta + Util.ASPIRATION_WINDOW_DELTA) / 2;
                                alpha = Math.max(score - delta, Util.SHORT_MIN);
                                beta = Math.min(score + delta, Util.SHORT_MAX);
                            }
                        }
                        SearchResultUtil.setBestMove();
                        break;
                    }
                }
            }
        }
        TimeUtil.end();
    }
}