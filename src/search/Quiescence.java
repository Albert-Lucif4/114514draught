package search;

import bit.Bitboard;
import engine.Constants;
import evaluation.Evaluate;
import lib.Util;
import movegen.MoveGenUtil;
import movegen.MoveUtil;
import static lib.Piece.king;
import static lib.Piece.pawn;

public class Quiescence {
    private static final int FUTILITY_MARGIN = 600;


    public static int search(final Bitboard board, final MoveGenUtil moveGen, int alpha, final int beta) {

        if (Constants.ENABLE_COUNT) {
            Constants.qNodes++;
        }


        int score = Evaluate.getScore(board);
        if (score >= beta || !MoveGenUtil.canTake(board, board.colorToMove)) {
            return score;
        }


        alpha = Math.max(alpha, score);

        moveGen.startPly();
        moveGen.generateMoves(board);

        while (moveGen.hasNext()) {
            final int next = moveGen.next();
            final long move = moveGen.getMove(next);
            if (!MoveUtil.isLegalMove(move)) {
                continue;
            }
            final int kingCaptured = moveGen.getKingCapturedCount(next);
            final int pawnCaptured = moveGen.getCaptureCount(next) - kingCaptured;
            if (MoveUtil.isPromotion(move)) {
                continue;
            } else if (score + FUTILITY_MARGIN + Util.MATERIAL[pawn] * pawnCaptured + Util.MATERIAL[king] * kingCaptured  < alpha) {
                continue;
            }

            board.doMove(move, moveGen);
            score = -search(board, moveGen, -beta, -alpha);

            board.undoMove(move, moveGen);

            if (score >= beta) {
                moveGen.endPly();
                return score;
            }
            alpha = Math.max(alpha, score);
        }

        moveGen.endPly();
        return alpha;
    }
}
