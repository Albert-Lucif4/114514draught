package bit;

import lib.FEN;
import lib.Piece;
import movegen.Magic;
import movegen.MoveGenUtil;
import movegen.MoveUtil;

import java.security.SecureRandom;

import static lib.Color.*;
import static lib.Piece.king;
import static lib.Piece.pawn;
import static movegen.Magic.MASK;

public class Bitboard {

    public long WhitePieces;
    public long BlackPieces;
    public long Kings;
    public long Occupied;
    public long Empty;
    public long[] King = new long[2];
    public long[] Pawn = new long[2];
    public long[] Pieces = new long[2];
    public long zobristKey;
    public int colorToMove = white;

    //zobrist key
    public long zkTurn;
    //color, piece, index
    public long[][][] zkPieceValues = new long[2][2][64];

    {
        SecureRandom rd = new SecureRandom();
        zkTurn = rd.nextLong();
        for (int i = 0; i < 64; i++) {
            zkPieceValues[0][0][i] = rd.nextLong();
            zkPieceValues[0][1][i] = rd.nextLong();
            zkPieceValues[1][0][i] = rd.nextLong();
            zkPieceValues[1][1][i] = rd.nextLong();
        }
    }

    public Bitboard(long WP, long BP, long K) {
        this.WhitePieces = WP;
        this.BlackPieces = BP;
        this.Kings = K;
        setupValues();
        caculateZobristkey();
    }

    public Bitboard(long WP, long BP, long K, int colorToMove) {
        this.WhitePieces = WP;
        this.BlackPieces = BP;
        this.Kings = K;
        this.colorToMove = colorToMove;
        setupValues();
        caculateZobristkey();
    }

    public Bitboard() {
        this.WhitePieces = 0x3ffffc0000000L;
        this.BlackPieces = 0xfffffL;
        this.Kings = 0;
        setupValues();
        caculateZobristkey();
    }

    public Bitboard(char[] board) {

        char[] board_offset = new char[50];
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                board_offset[i * 5] = board[i * 10 + 1];
                board_offset[i * 5 + 1] = board[i * 10 + 3];
                board_offset[i * 5 + 2] = board[i * 10 + 5];
                board_offset[i * 5 + 3] = board[i * 10 + 7];
                board_offset[i * 5 + 4] = board[i * 10 + 9];
            } else {
                board_offset[i * 5] = board[i * 10];
                board_offset[i * 5 + 1] = board[i * 10 + 2];
                board_offset[i * 5 + 2] = board[i * 10 + 4];
                board_offset[i * 5 + 3] = board[i * 10 + 6];
                board_offset[i * 5 + 4] = board[i * 10 + 8];
            }
        }
        long[] tmp = get_bb_from_board(board_offset);
        WhitePieces = tmp[0];
        BlackPieces = tmp[1];
        Kings = tmp[3];
        setupValues();
        caculateZobristkey();
    }

    public Bitboard(char[] board, int colorToMove) {

        char[] board_offset = new char[50];
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                board_offset[i * 5] = board[i * 10 + 1];
                board_offset[i * 5 + 1] = board[i * 10 + 3];
                board_offset[i * 5 + 2] = board[i * 10 + 5];
                board_offset[i * 5 + 3] = board[i * 10 + 7];
                board_offset[i * 5 + 4] = board[i * 10 + 9];
            } else {
                board_offset[i * 5] = board[i * 10];
                board_offset[i * 5 + 1] = board[i * 10 + 2];
                board_offset[i * 5 + 2] = board[i * 10 + 4];
                board_offset[i * 5 + 3] = board[i * 10 + 6];
                board_offset[i * 5 + 4] = board[i * 10 + 8];
            }
        }
        long[] tmp = get_bb_from_board(board_offset);
        WhitePieces = tmp[0];
        BlackPieces = tmp[1];
        Kings = tmp[3];
        setupValues();
        this.colorToMove = colorToMove;
        caculateZobristkey();
    }

    private long[] get_bb_from_board(char[] board) {
        long[] result = new long[4];
        for (int i = 0; i < 50; i++) {
            switch (board[i]) {
                case 'M':
                    result[0] |= 0x1L << i;
                    result[2] |= 0x1L << i;
                    break;
                case 'K':
                    result[0] |= 0x1L << i;
                    result[3] |= 0x1L << i;
                    break;
                case 'm':
                    result[1] |= 0x1L << i;
                    result[2] |= 0x1L << i;
                    break;
                case 'k':
                    result[1] |= 0x1L << i;
                    result[3] |= 0x1L << i;
                    break;
            }
        }
        return result;
    }

    private void setupValues() {
        King[white] = WhitePieces & Kings;
        King[black] = BlackPieces & Kings;
        Pawn[white] = WhitePieces ^ King[white];
        Pawn[black] = BlackPieces ^ King[black];
        Occupied = WhitePieces | BlackPieces;
        Pieces[white] = WhitePieces;
        Pieces[black] = BlackPieces;
        Empty = Bit.reverse(Occupied);
    }

    public void doMove(long move) {
        doMove(move, null);
    }

    public void doMove(long move, MoveGenUtil gen) {
        //luu cache
        if (gen != null) gen.setBoardCache(WhitePieces, BlackPieces, Kings, zobristKey);

        long removed = MoveUtil.getRemoveMap(move);
        int end = MoveUtil.getEndIndex(move);
        long nonRemoved = Bit.reverse(removed);
        int piece = MoveUtil.getPiece(move);
        int color = MoveUtil.getColor(move);
        long wkRemoved = WhitePieces & Kings & removed;
        long bkRemoved = BlackPieces & Kings & removed;
        long wmRemoved = WhitePieces & Bit.reverse(Kings) & removed;
        long bmRemoved = BlackPieces & Bit.reverse(Kings) & removed;
        while (wkRemoved != 0) {
            zobristKey ^= zkPieceValues[white][king][Bit.Index(wkRemoved & -wkRemoved)];
            wkRemoved = wkRemoved & (wkRemoved - 1);
        }
        while (bkRemoved != 0) {
            zobristKey ^= zkPieceValues[black][king][Bit.Index(bkRemoved & -bkRemoved)];
            bkRemoved = bkRemoved & (bkRemoved - 1);
        }
        while (wmRemoved != 0) {
            zobristKey ^= zkPieceValues[white][pawn][Bit.Index(wmRemoved & -wmRemoved)];
            wmRemoved = wmRemoved & (wmRemoved - 1);
        }
        while (bmRemoved != 0) {
            zobristKey ^= zkPieceValues[black][pawn][Bit.Index(bmRemoved & -bmRemoved)];
            bmRemoved = bmRemoved & (bmRemoved - 1);
        }

        zobristKey ^= zkTurn;
        WhitePieces &= nonRemoved;
        BlackPieces &= nonRemoved;
        Kings &= nonRemoved;
        colorToMove = 1 - colorToMove;

        switch (piece) {
            case pawn:
                if (color == white) {
                    if ((MASK[end] & 0x1fL) != 0) {
                        zobristKey ^= zkPieceValues[white][king][end];
                        Kings |= MASK[end];
                    } else {
                        zobristKey ^= zkPieceValues[white][pawn][end];
                    }
                } else {
                    if ((MASK[end] & 0x3e00000000000L) != 0) {
                        zobristKey ^= zkPieceValues[black][king][end];
                        Kings |= MASK[end];
                    } else {
                        zobristKey ^= zkPieceValues[black][pawn][end];
                    }
                }
                break;
            case king:
                Kings |= MASK[end];
                zobristKey ^= zkPieceValues[color][king][end];
                break;
        }

        switch (color) {
            case white:
                WhitePieces |= MASK[end];
                break;
            case black:
                BlackPieces |= MASK[end];
                break;
        }


        setupValues();
    }

    public void undoMove(long move, MoveGenUtil gen) {
        WhitePieces = gen.boardCache[gen.currentPly][0];
        BlackPieces = gen.boardCache[gen.currentPly][1];
        Kings = gen.boardCache[gen.currentPly][2];
        zobristKey = gen.boardCache[gen.currentPly][3];
        colorToMove = 1 - colorToMove;
        setupValues();
    }

    public void doNullMove() {
        zobristKey ^= zkTurn;
        colorToMove = 1 - colorToMove;
    }

    public void undoNullMove() {
        zobristKey ^= zkTurn;
        colorToMove = 1 - colorToMove;
    }

    private void caculateZobristkey() {
        for (int i = 0; i < 50; i++) {
            if ((MASK[i] & WhitePieces) != 0) {
                if ((MASK[i] & Kings) != 0) zobristKey ^= zkPieceValues[white][king][i];
                else zobristKey ^= zkPieceValues[white][pawn][i];
            } else if ((MASK[i] & BlackPieces) != 0) {
                if ((MASK[i] & Kings) != 0) zobristKey ^= zkPieceValues[black][king][i];
                else zobristKey ^= zkPieceValues[black][pawn][i];
            }
        }

        if (colorToMove == white) zobristKey ^= zkTurn;
    }

    public boolean hasPawns() {
        return Bit.bitCount(Pawn[white]) > 4 && Bit.bitCount(Pawn[black]) > 4;
    }

    public boolean isLegal(long move) {
        if (!MoveUtil.isLegalMove(move) || MoveUtil.isCapture(move)) return false;
        int piece = MoveUtil.getPiece(move);
        int sourceIndex = MoveUtil.getEndIndex(move);
        int toIndex = Bit.Index(MoveUtil.getRemoveMap(move));
        if ((MASK[sourceIndex] & Pieces[colorToMove]) == 0) return false;
        if ((MASK[toIndex] & Empty) != 0) return false;
        if (piece == pawn) {
            return (MASK[toIndex] & Magic.PawnOccupiedSq[sourceIndex]) != 0;
        } else {
            return (Magic.BetweenBB[sourceIndex][toIndex] & Empty) == Magic.BetweenBB[sourceIndex][toIndex];
        }
    }

    public boolean hasBigArmy() {
        return Bit.bitCount(this.Occupied) > 10;
    }

    public String toString() {
        return FEN.getFenFromBB(this);
    }
}
