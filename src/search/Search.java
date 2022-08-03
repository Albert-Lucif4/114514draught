package search;

import bit.Bitboard;
import engine.Constants;
import engine.Engine;
import evaluation.Evaluate;
import lib.FEN;
import lib.Util;
import movegen.ConvertMove;
import movegen.MoveGenUtil;
import movegen.MoveUtil;
import static evaluation.EvalConstants.Rank;
import static lib.Piece.pawn;
import static lib.Util.*;

public class Search {
    public static long wp, bp, k;

    /* 找到最优移动点 */
    static int search(final Bitboard board, final MoveGenUtil moveGen, final TTUtil tt, int ply, int depth, int alpha, int beta, int nullMoveCounter) {

        if(Constants.ENABLE_TIME_COUNTER && TimeUtil.isTimeLeft()){
            return Util.SCORE_MUST_MOVE;
        }

        if (Constants.ENABLE_COUNT) {
            Constants.sNodes++;
            Constants.maxDepth = Math.max(Constants.maxDepth, ply);
        }

        boolean canCapture = MoveGenUtil.canTake(board, board.colorToMove);

        int extension = canCapture ? 1 : 0;

        depth += extension;
        if (depth <= 0 || ply >= Constants.MAX_PLIES) {
            return Quiescence.search(board, moveGen, alpha, beta);
        }


        /* 配对距离修剪 */
        alpha = Math.max(alpha, Util.SHORT_MIN + ply);
        beta = Math.min(beta, Util.SHORT_MAX - ply - 1);
        if (alpha >= beta) {
            return alpha;
        }

        /* 转置表 */
        int ttValue = tt.getValue(board.zobristKey);
        int score = tt.getScore(ttValue, ply);
        if (ttValue != 0 && ply != 0) {
            if (tt.getDepth(ttValue) >= (depth)) {
                if (Constants.ENABLE_COUNT) Constants.ttNodes++;
                switch (tt.getFlag(ttValue)) {
                    case TTUtil.FLAG_EXACT:
                        return score;
                    case TTUtil.FLAG_LOWER:
                        if (score >= beta) {
                            return score;
                        }
                        break;
                    case TTUtil.FLAG_UPPER:
                        if (score <= alpha) {
                            return score;
                        }
                }
            }
        }

        int eval = Util.SHORT_MIN;
        final boolean isPv = beta - alpha != 1;
        if (Constants.ENABLE_COUNT) {
            Constants.pvNodes += isPv ? 1 : 0;
        }

        if (!isPv && !canCapture) {
            eval = Evaluate.getScore(board);
            /* use tt value as eval */
            if (ttValue != 0) {
                if (tt.getFlag(ttValue) == TTUtil.FLAG_EXACT || tt.getFlag(ttValue) == TTUtil.FLAG_UPPER && score < eval
                        || tt.getFlag(ttValue) == TTUtil.FLAG_LOWER && score > eval) {
                    eval = score;
                }
            }

            /* static null move pruning */
            if (Constants.ENABLE_STATIC_NULL_MOVE && depth < STATIC_NULLMOVE_MARGIN.length) {
                if (eval - STATIC_NULLMOVE_MARGIN[depth] >= beta) {
                    if (Constants.ENABLE_COUNT) Constants.nullpnNodes++;
                    return eval;
                }
            }

            /* razoring */
            if (Constants.ENABLE_RAZORING && depth < RAZORING_MARGIN.length && Math.abs(alpha) < Util.SCORE_MATE_BOUND) {
                if (eval + RAZORING_MARGIN[depth] < alpha) {
                    final int q = Quiescence.search(board, moveGen, alpha - RAZORING_MARGIN[depth], alpha - RAZORING_MARGIN[depth] + 1);
                    if (q + RAZORING_MARGIN[depth] <= alpha) {
                        if (Constants.ENABLE_COUNT) Constants.razoringNodes++;
                        return q;
                    }
                }
            }

            /* null-move */
            if (Constants.ENABLE_NULL_MOVE && nullMoveCounter < 2 && board.hasPawns()) {
                board.doNullMove();
                final int reduction = 2 + depth / 3;
                score = depth - reduction <= 0 ? -Quiescence.search(board, moveGen, -beta, -beta + 1)
                        : -search(board, moveGen, tt, ply + 1, depth - reduction, -beta, -beta + 1, nullMoveCounter + 1);
                board.undoNullMove();
                if (score >= beta) {
                    if (Constants.ENABLE_COUNT) Constants.nullMoveCutOffNodes++;
                    return score;
                }
            }

            /* ProbCut */




//
        }

        final long parentMove = ply == 0 ? 0 : moveGen.previous();
        final int alphaOrig = alpha;
        int bestScore = Util.SHORT_MIN;
        long bestMove = 0;
        long killer1Move = 0;
        long killer2Move = 0;
        int movesPerformed = 0;
        int promotionCount = 0;
        long counterMove = 0;
        long cacheMove = 0;
        moveGen.startPly();

        int phase = PHASE_TT;

        while (phase <= PHASE_QUIET) {
            switch (phase) {
                case PHASE_TT:
                    if (Constants.ENABLE_IID) {
                        /* IID */
                        cacheMove = tt.getMove(board.zobristKey);

                        if (board.isLegal(cacheMove)) {
                            moveGen.addMove(cacheMove);
                        } else {
                            cacheMove = 0;
                        }
                    }
                    break;
                case PHASE_KILLER_1:
                    if (Constants.ENABLE_KILLER_MOVE) {
                        killer1Move = moveGen.getKiller1(ply);
                        if (board.isLegal(killer1Move) && killer1Move != cacheMove) {
                            if (Constants.ENABLE_COUNT) Constants.killerMoveAcceptedNodes++;
                            moveGen.addMove(killer1Move);
                        } else {
                            killer1Move = 0;
                        }
                    }
                    break;
                case PHASE_KILLER_2:
                    if (Constants.ENABLE_KILLER_MOVE) {
                        killer2Move = moveGen.getKiller2(ply);
                        if (board.isLegal(killer2Move) && killer2Move != cacheMove && killer2Move != killer1Move) {
                            if (Constants.ENABLE_COUNT) Constants.killerMoveAcceptedNodes++;
                            moveGen.addMove(killer2Move);
                        } else {
                            killer2Move = 0;
                        }
                    }
                    break;
                case PHASE_COUNTER:
                    if (Constants.ENABLE_COUNTER) {
                        counterMove = moveGen.getCounter(board.colorToMove, parentMove);
                        if (counterMove != 0 && counterMove != cacheMove && counterMove != killer1Move
                                && counterMove != killer2Move && board.isLegal(counterMove)) {
                            moveGen.addMove(counterMove);
                        } else {
                            counterMove = 0;
                        }
                    }
                    break;
                case PHASE_QUIET:
                    moveGen.generateMoves(board);
                    moveGen.setScores();
                    moveGen.sort();
            }
            while (moveGen.hasNext()) {
                final int next = moveGen.next();
                final long move = moveGen.getMove(next);
                if (!MoveUtil.isLegalMove(move)) continue;
                if (phase == PHASE_QUIET) {
                    if (Constants.ENABLE_COUNTER && move == counterMove) continue;
                    if (Constants.ENABLE_IID && move == cacheMove) continue;
                    if (Constants.ENABLE_KILLER_MOVE && (move == killer1Move || move == killer2Move)) continue;
                }

                final int rank = MoveUtil.getPiece(move) == pawn ?
                        moveGen.relative_rank(Rank[MoveUtil.getEndIndex(move)], board.colorToMove) :
                        0;


                if (!isPv && !canCapture && movesPerformed > 0 && rank < 8) {

                    /* 延迟修建 */
                    if (Constants.ENABLE_LMP && depth <= 4 && movesPerformed >= depth * 2 + 3) {
                        if (Constants.ENABLE_COUNT) Constants.lmpNodes++;
                        continue;
                    }

                    /* 无效修建 */
                    if (Constants.ENABLE_FUTILITY_PRUNING && !MoveUtil.isPromotion(move) && rank != 8) {
                        if (depth < FUTILITY_MARGIN.length) {
                            if (eval == Util.SHORT_MIN) {
                                eval = Evaluate.getScore(board);
                            }
                            if (eval + FUTILITY_MARGIN[depth] <= alpha) {
                                if (Constants.ENABLE_COUNT) Constants.futilityNodes++;
                                continue;
                            }
                        }
                    }
                }

                board.doMove(move, moveGen);
                movesPerformed++;

                int pad = 0;

                score = alpha + 1;
                if(moveGen.getCaptureCount(next) > 3){
                    pad += 1;
                } else if (MoveUtil.isPromotion(move)) {
                    promotionCount++;
                    pad += promotionCount < 2 ? 1 : 0;
                }


                if (Constants.ENABLE_LMR && rank < 7 && movesPerformed > 2 && depth > 3 && ply > 0 && !canCapture
                        && !MoveUtil.isPromotion(move) && !MoveGenUtil.canTake(board, board.colorToMove)
                        && !MoveGenUtil.canTake(board, 1 - board.colorToMove)) {
                    /* LMR */
                    if (Constants.ENABLE_COUNT) Constants.lmrHitNodes++;
                    final int reduction = rank < 3 && movesPerformed > 6 ? Math.min(depth - 1, 2 + depth / 6) : 2;
                    score = -search(board, moveGen, tt, ply + 1, depth - reduction, -alpha - 1, -alpha, 0);
                    if (score > alpha) {
                        if (Constants.ENABLE_COUNT) Constants.lmrMissNodes++;
                        score = -search(board, moveGen, tt, ply + 1, depth - 1, -alpha - 1, -alpha, 0);
                    }
                } else if (Constants.ENABLE_PVS && movesPerformed > 1) {
                    /* PVS */
                    if (Constants.ENABLE_COUNT) Constants.pvsHitNodes++;
                    score = -search(board, moveGen, tt, ply + 1, depth + pad - 1, -alpha - 1, -alpha, 0);
                }
                if (score > alpha) {
                    if (Constants.ENABLE_COUNT) Constants.pvsMissNodes++;
                    score = -search(board, moveGen, tt, ply + 1, depth + pad - 1, -beta, -alpha, 0);
                }

                board.undoMove(move, moveGen);

                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                    alpha = Math.max(alpha, score);
                }
                if (alpha >= beta) {
                    if (Constants.ENABLE_COUNT){
                        Constants.failHighNodes++;
                        if(phase == PHASE_TT) Constants.cacheMoveCutOffNodes++;
                        else if(phase == PHASE_KILLER_1 || phase == PHASE_KILLER_2) Constants.killerMoveCutOffNodes++;
                        else if(phase == PHASE_COUNTER) Constants.counterMoveCutOffNodes++;
                        else if(movesPerformed == 1) Constants.normalCutOff++;

                    }
                    /* 杀手锏 */
                    if (!canCapture) {
                        if (Constants.ENABLE_KILLER_MOVE) moveGen.addKillerMove(move, ply);
                        if (Constants.ENABLE_COUNTER) moveGen.addCounterMove(board.colorToMove, parentMove, move);
                    }
                    phase += 10;
                    break;
                }
            }
            phase++;
        }
        moveGen.endPly();

        /* 平局 */
        if (movesPerformed == 0) {
            if (Constants.ENABLE_COUNT) Constants.mateNodes++;
            return Util.SHORT_MIN + ply;
        }


        // set tt-flag
        int flag = TTUtil.FLAG_EXACT;
        if (bestScore >= beta) {
            flag = TTUtil.FLAG_LOWER;
        } else if (bestScore <= alphaOrig) {
            flag = TTUtil.FLAG_UPPER;
        }

        tt.addValue(board.zobristKey, bestScore, flag, ply, bestMove);
        moveGen.addBestMove(bestMove, ply);
        if (ply == 0) {
            Util.bestMove = bestMove;
            Util.bestMoveStr = ConvertMove.getMove(board, bestMove);
            Util.bestScore = score;

        }
        return bestScore;
    }

    /*未完全搜索 */
    public static void search(Bitboard board, int maxDepth) {

        Util.reset();
        Constants.reset();

        /* 停止搜索 */
        MoveGenUtil moveGen = new MoveGenUtil();
        long legalMove = 0;
        int legalMoveCount = 0;

        /* 开始搜索 */
        /* 计算根节点的合法移动 */
        moveGen.startPly();
        moveGen.generateMoves(board);
        while (moveGen.hasNext()) {
            int next = moveGen.next();
            long move = moveGen.getMove(next);
            if (MoveUtil.isLegalMove(move)) {
                legalMoveCount++;
                legalMove = move;
            }
            if (legalMoveCount > 1) break;
        }
        moveGen.endPly();

        /* 检查必要移动 */
        if (legalMoveCount == 1) {
            Util.bestMove = legalMove;
            Util.bestScore = SCORE_MUST_MOVE;
            Util.bestMoveStr = ConvertMove.getMove(board, legalMove);
            Engine.sendInfo();
        } else if (legalMoveCount == 0) {
            Util.bestMove = 0;
            Util.bestMoveStr = null;
            Util.bestScore = Integer.MIN_VALUE;
            Engine.sendInfo();
        } else {
            /* 如果不是必要移动进行全搜索 */
            (new SearchThread(board, maxDepth)).run();
        }
    }

    /* 估值得分索索 */
    public static void search(String f, int maxDepth) {
        FEN fen = new FEN(f);
        Bitboard board = fen.getBoard();
        search(board, maxDepth);
    }
}
