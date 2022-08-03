package evaluation;

import bit.Bit;
import bit.Bitboard;
import log.Debug;

import java.util.ArrayList;

import static bit.Bit.shift;
import static lib.Color.black;
import static lib.Color.white;
import static lib.Direction.*;
import static movegen.Magic.*;

public class EvalPos {
    long[] NonPieces = new long[2];
    ArrayList<Integer>[] Pawn_Pos = new ArrayList[]{new ArrayList(), new ArrayList()};
    ArrayList<Integer>[] King_Pos = new ArrayList[]{new ArrayList(), new ArrayList()};
    long[][] PawnMove = new long[2][4];
    int sumPieces;
    long[] OpenSquares = new long[3];
    long[] PawnAttackedSquares = new long[2];
    long[] PawnMobilityArea = new long[2];
    long[][] PawnShift = new long[2][4];
    long[] PawnBlockStormSq = new long[2];
    long[] PinnedSquares = new long[2];
    long[] PawnCombatSquares = new long[2];
    long[][] SafePawns = new long[2][2];

    EvalPos(Bitboard board) {

        NonPieces[white] = Bit.reverse(board.Pieces[white]);
        NonPieces[black] = Bit.reverse(board.Pieces[black]);


        Pawn_Pos[white] = Bit.getIndex(board.Pawn[white]);
        Pawn_Pos[black] = Bit.getIndex(board.Pawn[black]);
        King_Pos[white] = Bit.getIndex(board.King[white]);
        King_Pos[black] = Bit.getIndex(board.King[black]);

        PawnMove[white][NE] = shift(board.Pawn[white], north_east);
        PawnMove[white][NW] = shift(board.Pawn[white], north_west);
        PawnMove[white][SE] = shift(board.Pawn[white], south_east);
        PawnMove[white][SW] = shift(board.Pawn[white], south_west);

        PawnMove[black][NE] = shift(board.Pawn[black], north_east);
        PawnMove[black][NW] = shift(board.Pawn[black], north_west);
        PawnMove[black][SE] = shift(board.Pawn[black], south_east);
        PawnMove[black][SW] = shift(board.Pawn[black], south_west);

        sumPieces = Pawn_Pos[white].size() + Pawn_Pos[black].size() + King_Pos[white].size() + King_Pos[black].size();

        PawnShift[white][NE] = shift(board.Pawn[white], north_east);
        PawnShift[white][NW] = shift(board.Pawn[white], north_west);
        PawnShift[white][SE] = shift(board.Pawn[white], south_east);
        PawnShift[white][SW] = shift(board.Pawn[white], south_west);

        PawnShift[black][NE] = shift(board.Pawn[black], north_east);
        PawnShift[black][NW] = shift(board.Pawn[black], north_west);
        PawnShift[black][SE] = shift(board.Pawn[black], south_east);
        PawnShift[black][SW] = shift(board.Pawn[black], south_west);

        OpenSquares[white] |= PawnShift[white][SW] & PawnShift[white][NE] &
                board.Empty;
        OpenSquares[white] |= PawnShift[white][SE] & PawnShift[white][NW] &
                board.Empty;

        OpenSquares[black] |= PawnShift[black][NE] & PawnShift[black][SW] &
                board.Empty;
        OpenSquares[black] |= PawnShift[black][NW] & PawnShift[black][SE] &
                board.Empty;

        PawnAttackedSquares[white] |= PawnShift[white][NE] & shift(NonPieces[white], south_west) & NonPieces[white];
        PawnAttackedSquares[white] |= PawnShift[white][NW] & shift(NonPieces[white], south_east) & NonPieces[white];
        PawnAttackedSquares[white] |= PawnShift[white][SE] & shift(NonPieces[white], north_west) & NonPieces[white];
        PawnAttackedSquares[white] |= PawnShift[white][SW] & shift(NonPieces[white], north_east) & NonPieces[white];

        PawnAttackedSquares[black] |= PawnShift[black][NE] & shift(NonPieces[black], south_west) & NonPieces[black];
        PawnAttackedSquares[black] |= PawnShift[black][NW] & shift(NonPieces[black], south_east) & NonPieces[black];
        PawnAttackedSquares[black] |= PawnShift[black][SE] & shift(NonPieces[black], north_west) & NonPieces[black];
        PawnAttackedSquares[black] |= PawnShift[black][SW] & shift(NonPieces[black], north_east) & NonPieces[black];

        PawnMobilityArea[white] = (PawnShift[white][NE] | PawnShift[white][NW]) & board.Empty;
        PawnMobilityArea[black] = (PawnShift[black][NE] | PawnShift[black][NW]) & board.Empty;

        PinnedSquares[white] |= PawnShift[white][NE] & shift(board.Empty, south_west) & board.Empty;
        PinnedSquares[white] |= PawnShift[white][NW] & shift(board.Empty, south_east) & board.Empty;
        PinnedSquares[white] |= PawnShift[white][SE] & shift(board.Empty, north_west) & board.Empty;
        PinnedSquares[white] |= PawnShift[white][SW] & shift(board.Empty, north_east) & board.Empty;

        PinnedSquares[black] |= PawnShift[black][NE] & shift(board.Empty, south_west) & board.Empty;
        PinnedSquares[black] |= PawnShift[black][NW] & shift(board.Empty, south_east) & board.Empty;
        PinnedSquares[black] |= PawnShift[black][SE] & shift(board.Empty, north_west) & board.Empty;
        PinnedSquares[black] |= PawnShift[black][SW] & shift(board.Empty, north_east) & board.Empty;

        PawnCombatSquares[0] = PawnMove[white][NE] & PawnMove[black][SW] & board.Empty;
        PawnCombatSquares[1] = PawnMove[white][NW] & PawnMove[black][SE] & board.Empty;

        PawnBlockStormSq[white] = PawnAttackedSquares[white] & board.Empty | board.Pawn[white];
        PawnBlockStormSq[black] = PawnAttackedSquares[black] & board.Empty | board.Pawn[black];

        //NE
        SafePawns[white][0] |= board.Pawn[white] & 0x3e0080200803fL;
        SafePawns[white][0] |= shift(board.Pieces[white] & 0x3e0080200803fL, north_east) & board.Pawn[white];
        SafePawns[white][0] |= shift(shift(board.Pieces[white], north_east) & board.Pieces[white], north_east) & board.Pawn[white];

        //NW
        SafePawns[white][1] |= board.Pawn[white] & 0x3f0040100401fL;
        SafePawns[white][1] |= shift(board.Pieces[white] & 0x3f0040100401fL, north_west) & board.Pawn[white];
        SafePawns[white][1] |= shift(shift(board.Pieces[white], north_west) & board.Pieces[white], north_west) & board.Pawn[white];

        //SE
        SafePawns[black][1] |= board.Pawn[black] & 0x3e0080200803fL;
        SafePawns[black][1] |= shift(board.Pieces[black] & 0x3e0080200803fL, south_east) & board.Pawn[black];
        SafePawns[black][1] |= shift(shift(board.Pieces[black], south_east) & board.Pieces[black], south_east) & board.Pawn[black];

        //SW
        SafePawns[black][0] |= board.Pawn[black] & 0x3f0040100401fL;
        SafePawns[black][0] |= shift(board.Pieces[black] & 0x3f0040100401fL, south_west) & board.Pawn[black];
        SafePawns[black][0] |= shift(shift(board.Pieces[black], south_west) & board.Pieces[black], south_west) & board.Pawn[black];
    }

    boolean isEndGame() {
        return Pawn_Pos[white].size() + Pawn_Pos[black].size() <= 15;
    }
}
