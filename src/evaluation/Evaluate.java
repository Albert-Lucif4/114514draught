package evaluation;

import bit.Bit;
import bit.Bitboard;
import log.Debug;
import movegen.Magic;
import static evaluation.EvalConstants.*;
import static lib.Color.*;
import static movegen.Magic.*;

public class Evaluate {

    public static int getScore(Bitboard board){

        final int score = EvalCache.getScore(board.zobristKey);
        if (score != Integer.MIN_VALUE) {
            return score;
        }

        return evaluate(board);
    }

    private static int evaluate(Bitboard board) {
        int color = board.colorToMove;
        EvalPos Pos = new EvalPos(board);
        Score score = evaluate(Pos, board);
        int pad = color == white ? 20 : -20;

        int rs =  (score.EgScore() * (80 - Pos.sumPieces) + score.MgScore() * Pos.sumPieces + pad) / 80;
        int s = board.colorToMove == white ? rs : -rs;
        EvalCache.addValue(board.zobristKey, s);
        return s;
    }

    private static Score evaluate(EvalPos Pos, Bitboard board) {
        Score score = new Score();

        score.add(pst(white, Pos));
        score.minus(pst(black, Pos));

        score.add(material(white, Pos));
        score.minus(material(black, Pos));

        score.add(balance(white, Pos));
        score.minus(balance(black, Pos));

        score.add(mobility(white, Pos, board));
        score.minus(mobility(black, Pos, board));

   /*     score.add(Imbalance(Pos, board, white));
        score.minus(Imbalance(Pos, board, black));

        score.add(KingEval(Pos, board, white));
        score.minus(KingEval(Pos, board, black));

        score.add(Pieces(Pos, board, white));
        score.minus(Pieces(Pos, board, black));

        score.add(Threats(Pos, board, white));
        score.minus(Threats(Pos, board, black));
     */
        return score;
    }

    private static Score Imbalance(EvalPos Pos, Bitboard board, int Us) {
        Score score = new Score();
        int us = Pos.Pawn_Pos[Us].size() + Pos.King_Pos[Us].size();

        //Piece values
        score.add(PawnValue[mg], PawnValue[eg], Pos.Pawn_Pos[Us].size());
        score.add(KingValue[mg], KingValue[eg], Pos.King_Pos[Us].size());
        score.add(PawnRaito[mg], PawnRaito[eg], (float) (Pos.Pawn_Pos[Us].size() / Pos.sumPieces));
        score.add(KingRaito[mg], KingRaito[eg], (float) (Pos.King_Pos[Us].size() / Pos.sumPieces));


        score.minus(BalancePieces[mg], BalancePieces[eg], Math.abs((float) (Bit.bitCount(board.Pieces[Us] & RightSide) - us / 2)));
        score.minus(BalancePieces[mg], BalancePieces[eg], Math.abs((float) (Bit.bitCount(board.Pieces[Us] & CenterLeft) - us / 2)));
        score.minus(BalancePieces[mg], BalancePieces[eg], Math.abs((float) (Bit.bitCount(board.Pieces[Us] & CenterRight) - us / 2)));
        score.minus(BalancePieces[mg], BalancePieces[eg], Math.abs((float) (Bit.bitCount(board.Pieces[Us] & LongFlank) - us / 2)));

        return score;
    }

    private static Score KingEval(EvalPos Pos, Bitboard board, int Us){
        int Them = 1 - Us;
        Score score = new Score();
        score.add(KingOnLongDiagonal[mg], KingOnLongDiagonal[eg], Bit.bitCount(board.King[Us] & LongDiagonal));

        for (int i : Pos.King_Pos[Us]) {
            //King mobility
            long attackers = Magic.getKingMove(i, board.Occupied, false);
            Pos.PawnAttackedSquares[Us] |= attackers;
            Pos.PawnBlockStormSq[Us] |= attackers;
            score.add(KingSeeLongDiagonal[mg], KingSeeLongDiagonal[eg], Bit.bitCount(attackers & LongDiagonal));

            int attackers_count = Bit.bitCount(attackers);
            score.add(KingMobility[mg][attackers_count], KingMobility[eg][attackers_count]);
            int threats_by_pawn_line = Bit.bitCount(Magic.DirectionMask[NE][i] & board.Pawn[Them]) > 2 ? 1 : 0;
            threats_by_pawn_line += Bit.bitCount(Magic.DirectionMask[NW][i] & board.Pawn[Them]) > 2 ? 1 : 0;
            threats_by_pawn_line += Bit.bitCount(Magic.DirectionMask[SE][i] & board.Pawn[Them]) > 2 ? 1 : 0;
            threats_by_pawn_line += Bit.bitCount(Magic.DirectionMask[SW][i] & board.Pawn[Them]) > 2 ? 1 : 0;
            score.add(ThreatByPawnLine[mg], ThreatByPawnLine[eg], threats_by_pawn_line);
        }

        return score;
    }

    private static Score Pieces(EvalPos Pos, Bitboard board, int Us) {
        Score score = new Score();
        int Them = 1 - Us;
        int[] forwards = Us == 0 ? new int[]{NE, NW} : new int[]{SW, SE};

        if((board.Pawn[Us] & 0x800000000004L) != 0){
            score.add(GoldenStoneBonus[mg], GoldenStoneBonus[eg]);
        }

        for (int i : Pos.Pawn_Pos[Us]) {
            int rrank = relative_rank(Rank[i], Us);

            //wall threat
            if((MASK[i] & THREATING_SQUARES[Us]) != 0){
                score.add(ThreatOnHightWall[mg], ThreatOnHightWall[eg], rrank);
            }

            //pinned
            if(
                (
                (Shift_1_Mask[forwards[0]][i] & (Pos.PinnedSquares[Them] | Pos.PawnCombatSquares[0] | board.Pieces[Us])) |
                (Shift_1_Mask[forwards[1]][i] & (Pos.PinnedSquares[Them] | Pos.PawnCombatSquares[1] | board.Pieces[Us]))
                ) == PawnNormalMove[Us][i]){
                score.minus(Pinned[mg], Pinned[eg]);
            }

            //Trapped
            if((PawnNormalMove[Us][i] & board.Pieces[Us]) == PawnNormalMove[Us][i]){
                score.minus(TrappedPawn[mg], TrappedPawn[eg]);
            }

            //lonely
            if((AfterBB[Us][i] & board.Pawn[Us]) == 0){
                score.minus(Lonely[mg], Lonely[eg]);
            }
            //pawn material
            score.add(PAWN_MATERIAL[Us][mg][i], PAWN_MATERIAL[Us][eg][i]);
            //Passed pawn
            score.add(PawnPassedRank[mg][rrank], PawnPassedRank[eg][rrank]);
            //Supporters
            int supporters = Bit.bitCount(Magic.PawnOccupiedSq[i] & board.Pawn[Us]) + Magic.WallSquaresBeside[i];
            score.add(PawnSafety[mg][supporters], PawnSafety[eg][supporters]);
            //Threat
            if ((MASK[i] & Pos.PawnAttackedSquares[Them]) != 0) {
                score.add(ThreatByPawn[mg][supporters], ThreatByPawn[eg][supporters]);
            }
            //Pawn storm
            int block_count = Bit.bitCount(board.Pieces[Them] & Magic.ForwardStormSquares[Us][i]);
            if(Pos.King_Pos[Them].size() == 0) {
                int blocked = Magic.ForwardStormSquaresCount[Us][i] - block_count;
                if(blocked == 1) score.add(137, 149);
                else if(blocked == 0) score.add(231, 244);
                else score.add(BlockPawnStorm[mg][rrank], BlockPawnStorm[eg][rrank], blocked);
            }

        }

        //pawn on wall
        score.add(PawnOnWall[mg][0], PawnOnWall[eg][0], Bit.bitCount(board.Pawn[Us] & FileWall));
        score.add(PawnOnWall[mg][1], PawnOnWall[eg][1], Bit.bitCount(board.Pawn[Us] & RankWall));

        return score;
    }

    private static Score Threats(EvalPos Pos, Bitboard board, int Us) {
        Score score = new Score();
        //Open squares
        score.add(-30, -40, Bit.bitCount(Pos.OpenSquares[Us] & board.Empty));
        //Open squares are occupied by enemy pieces
        score.add(OpenSquare[mg], OpenSquare[eg], Bit.bitCount(Pos.OpenSquares[1 - Us] & board.Pieces[Us]));
        //Pawn mobility
        score.add(PawnMobility[mg], PawnMobility[eg], Bit.bitCount(Pos.PawnMobilityArea[Us]));
        //King size
        score.add(KingSupporters[mg], KingSupporters[eg], Pos.Pawn_Pos[Us].size() * Pos.King_Pos[Us].size());
        //Center occupation
        score.add(CenterOccupation[mg], CenterOccupation[eg], Bit.bitCount(board.Pieces[Us] & CenterSquares));

        if(Pos.King_Pos[1- Us].size() == 0 && Pos.King_Pos[Us].size() > 0){
            score.add(400, 500);
        }

        for(long f: FILES){
            if((board.Pieces[Us] & f) == 0) score.minus(EmptyFile[mg], EmptyFile[eg]);
        }

        score.add(SafePawnBonus[mg], SafePawnBonus[eg], Bit.bitCount(Pos.SafePawns[Us][0] | Pos.SafePawns[Us][1]));

        return score;
    }

    private static Score pst(int Us, EvalPos pos){
        Score score = new Score();
        for(int index : pos.Pawn_Pos[Us]){
            score.add(PAWN_MATERIAL[Us][mg][index], PAWN_MATERIAL[Us][eg][index]);
        }

        for(int index : pos.Pawn_Pos[Us]){
            score.add(KING_MATERIAL[Us][mg][index], KING_MATERIAL[Us][eg][index]);
        }

        return score;
    }

    private static Score material(int Us, EvalPos pos){
        Score score = new Score();
        //Piece values
        score.add(PawnValue[mg], PawnValue[eg], pos.Pawn_Pos[Us].size());
        score.add(KingValue[mg], KingValue[eg], pos.King_Pos[Us].size());

        score.add(300, 200, pos.King_Pos[Us].size() > 0 ? 1 : 0);
        score.add(100, 100, Math.max(pos.King_Pos[Us].size() - 1, 0));
        return score;
    }

    private static Score balance(int Us, EvalPos pos){
        Score score = new Score();
        int file_sum = 0;
        for(int index : pos.Pawn_Pos[Us]){
            file_sum +=  File[index] * 2 - 9;
        }

        score.minus(5, 3, Math.abs(file_sum));

        return score;
    }

    private static Score mobility(int Us, EvalPos pos, Bitboard board){
        Score score = new Score();
        for(int i: pos.Pawn_Pos[Us]){
            int supporters = Bit.bitCount(Magic.PawnOccupiedSq[i] & board.Pawn[Us]) + Magic.WallSquaresBeside[i];
            score.add(PawnSafety[mg][supporters], PawnSafety[eg][supporters]);
        }
        for (int i : pos.King_Pos[Us]) {
            //King mobility
            long attackers = Magic.getKingMove(i, board.Occupied, false);
            int attackers_count = Bit.bitCount(attackers);
            score.add(KingMobility[mg][attackers_count], KingMobility[eg][attackers_count]);
        }

        return score;
    }


    private static int relative_rank(int s, int color) {
        return color == 0 ? s : 9 - s;
    }

    private static long lowestBit(int c, long b) {
        return c == 0 ? Long.highestOneBit(b) : Long.lowestOneBit(b);
    }

    private static long highestBit(int c, long b) {
        return c == 0 ? Long.lowestOneBit(b) : Long.highestOneBit(b);
    }
}
