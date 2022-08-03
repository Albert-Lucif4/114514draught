package movegen;

import bit.Bit;
import bit.Bitboard;
import engine.Constants;
import static bit.Bit.more_than_one;
import static bit.Bit.shift;
import static evaluation.EvalConstants.Rank;
import static lib.Direction.*;
import static lib.Piece.king;
import static lib.Piece.pawn;
import static movegen.Magic.*;

public class MoveGenUtil {

    private final long[]    moves                   = new long[3000];
    private final int[]     scores                  = new int[3000];
    private final int[]     captureCount            = new int[3000];
    private final int[]     kingCapturedCount       = new int[3000];
    private final long[][][] COUNTER_MOVES = new long[2][2][50];
    //wp, bp, k, key
    public final long[][]   boardCache              = new long[Constants.MAX_PLIES * 2][4];
    public final int[]      nextToGen               = new int[Constants.MAX_PLIES * 2];
    public final int[]      nextToMove              = new int[Constants.MAX_PLIES * 2];
    private final long[]    KILLER_MOVE_1           = new long[Constants.MAX_PLIES * 2];
    private final long[]    KILLER_MOVE_2           = new long[Constants.MAX_PLIES * 2];
    private final long[]     BEST_MOVE_CACHE         = new long[Constants.MAX_PLIES * 2];
    public int              currentPly              = 0;

    public long getBestMove(final int ply){
        return BEST_MOVE_CACHE[ply];
    }

    public void addBestMove(final long bestMove, final int ply){
        BEST_MOVE_CACHE[ply] = bestMove;
    }

    public long getMove(int pos){
        if(pos >= 2000){
            for(int i = 0; i < Constants.MAX_PLIES * 2; i++){
                System.out.println(nextToGen[i] + ", " + nextToMove[i]);
            }
        }
        return moves[pos];
    }

    public int getScore(int pos){
        return scores[pos];
    }

    public int getCaptureCount(int pos){
        return captureCount[pos];
    }

    public int getKingCapturedCount(int pos){
        return kingCapturedCount[pos];
    }

    public void setBoardCache(long wp, long bp, long k, long zk){
        boardCache[currentPly][0] = wp;
        boardCache[currentPly][1] = bp;
        boardCache[currentPly][2] = k;
        boardCache[currentPly][3] = zk;
    }

    public void startPly() {
        nextToGen[currentPly + 1] = nextToGen[currentPly];
        nextToMove[currentPly + 1] = nextToGen[currentPly];
        currentPly++;
    }

    public void endPly() {
        currentPly--;
    }

    public int next() {
        return nextToMove[currentPly]++;
    }

    public int getNextScore() {
        return scores[nextToMove[currentPly]];
    }

    public long previous() {
        return moves[nextToMove[currentPly] - 1];
    }

    public boolean hasNext() {
        return nextToGen[currentPly] != nextToMove[currentPly];
    }

    public void addCounterMove(final int color, final long parentMove, final long counterMove) {
        COUNTER_MOVES[color][MoveUtil.getPiece(parentMove)][MoveUtil.getEndIndex(parentMove)] = counterMove;
    }

    public long getCounter(final int color, final long parentMove) {
        return COUNTER_MOVES[color][MoveUtil.getPiece(parentMove)][MoveUtil.getEndIndex(parentMove)];
    }

    public void generateMoves(Bitboard board) {
        boolean capture = canTake(board, board.colorToMove);
        if(capture) addCapture(board, board.colorToMove);
        else addMove(board, board.colorToMove);
    }

    public void generateMoves(Bitboard board, int color){
        boolean capture = canTake(board, color);
        if(capture) addCapture(board, color);
        else addMove(board, color);
    }

    public void sort(){
        //quick sort
        final int left = nextToMove[currentPly];
        for (int i = left, j = i; i < nextToGen[currentPly] - 1; j = ++i) {
            final int ai = scores[i + 1];
            final long mi = moves[i + 1];
            final int capi = captureCount[i + 1];
            final int kcapi = kingCapturedCount[i + 1];
            while (ai > scores[j]) {
                moves[j + 1] = moves[j];
                scores[j + 1] = scores[j];
                captureCount[j + 1] = captureCount[j];
                kingCapturedCount[j + 1] = kingCapturedCount[j];

                if (j-- == left) {
                    break;
                }
            }
            moves[j + 1] = mi;
            scores[j + 1] = ai;
            captureCount[j + 1] = capi;
            kingCapturedCount[j + 1] = kcapi;
        }
    }

    public void setScores() {
        for (int i = nextToMove[currentPly]; i < nextToGen[currentPly]; i++) {
            if(!MoveUtil.isLegalMove(moves[i])) continue;
            int piece = MoveUtil.getPiece(moves[i]);
            scores[i] = (kingCapturedCount[i] * 2 + captureCount[i]) * 100;
            if(piece == pawn) scores[i] += relative_rank(Rank[MoveUtil.getEndIndex(moves[i])], MoveUtil.getColor(moves[i])) * 50;
        }
    }

    public long getKiller1(final int ply) {
        return KILLER_MOVE_1[ply];
    }

    public long getKiller2(final int ply) {
        return KILLER_MOVE_2[ply];
    }

    public void addMove(final long move) {
        moves[nextToGen[currentPly]++] = move;
    }

    public void addKillerMove(final long move, final int ply) {
        if (KILLER_MOVE_1[ply] != move) {
            KILLER_MOVE_2[ply] = KILLER_MOVE_1[ply];
            KILLER_MOVE_1[ply] = move;
        }
    }

    private void addManMove(long men, int color, long occupied) {
        long from, to, move;
        while (men != 0) {
            from = men & -men;
            move = Magic.PawnNormalMove[color][Bit.Index(from)] & Bit.reverse(occupied);
            while (move != 0){
                to = move & -move;
                moves[nextToGen[currentPly]++] = MoveUtil.createMove(from, Bit.Index(to), pawn, color, 0);
                move = move & (move -1);
            }
            men = men & (men - 1);
        }
    }

    private void addKingMove(long kings, int color, long occupied) {
        long from, to, move;
        while (kings != 0){
            from = kings & -kings;
            move = Magic.getKingMove(Bit.Index(from), occupied, false);
            while (move != 0){
                to = move & -move;
                moves[nextToGen[currentPly]++] = MoveUtil.createMove(from, Bit.Index(to), king, color, 0);
                move = move & (move - 1);
            }
            kings = kings & (kings - 1);
        }
    }

    private void addCaptureMove(long removed, long end, int piece, int color){
        int removeCount = Bit.bitCount(removed) - 1;
        if(nextToGen[currentPly] > nextToMove[currentPly]){
            if(removeCount > captureCount[nextToMove[currentPly]]) {
                nextToGen[currentPly] = nextToMove[currentPly];
            }else if(removeCount < captureCount[nextToMove[currentPly]]){
                return;
            }
        }
        captureCount[nextToGen[currentPly]] = removeCount;
        moves[nextToGen[currentPly]++] = MoveUtil.createMove(removed, Bit.Index(end), piece, color, 1);
    }

    private void addManCapture(long enemy, long empty , long end, long removed, int color) {
        int i = Bit.Index(end);
        boolean cap = false;
        for(int dir = NE; dir <= SW; dir++){
            if((Shift_1_Mask[dir][i] & enemy) != 0 && (Shift_2_Mask[dir][i] & empty) != 0){
                addManCapture(enemy ^ Shift_1_Mask[dir][i],
                        empty ^ Fill_3_Mask[dir][i],
                        Shift_2_Mask[dir][i],
                        removed | Shift_1_Mask[dir][i], color);
                cap = true;
            }
        }
        if(!cap && more_than_one(removed)){
            addCaptureMove(removed, end, pawn, color);
        }

    }

    private void addKingCapture(long enemy, long empty, long end, long removed, int color, int comefrom) {

        int i = Bit.Index(end);
        long occupied = Bit.reverse(empty);
        boolean cap = false;
        long move = Magic.getKingMove(i, occupied, true);
        long capPiece, dirMove, pop;

        if(move != 0) {
            for (int dir = NE; dir <= SW; dir++) {
                if (Dir[dir] + Dir[comefrom] == 0) continue;
                capPiece = DirectionMask[dir][i] & move & enemy;
                if (capPiece != 0) {
                    dirMove = (DirectionMask[dir][i] & move) ^ capPiece;
                    cap = dirMove != 0;
                    while (dirMove != 0) {
                        pop = dirMove & -dirMove;
                        addKingCapture(enemy ^ capPiece,
                                empty ^ (end | pop | capPiece),
                                pop,
                                removed | capPiece,
                                color, dir);
                        dirMove = dirMove & (dirMove - 1);
                    }
                }
            }
        }

        if(!cap && more_than_one(removed)) {
            addCaptureMove(removed, end, king, color);
        }
    }

    private void addCapture(Bitboard board, int color) {
        long kings = board.King[color];
        long men   = board.Pawn[color];
        long pop;
        int Them = 1 - color;

        //make king capture
        while (kings != 0) {
            pop = kings & -kings;
            addKingCapture(board.Pieces[Them], board.Empty, pop, pop, color, NDIR);
            kings = kings & (kings - 1);
        }
        while (men != 0) {
            pop = men & -men;
            addManCapture(board.Pieces[Them], board.Empty, pop, pop, color);
            men = men & (men - 1);
        }
        for(int i = nextToMove[currentPly]; i < nextToGen[currentPly]; i++){
            kingCapturedCount[i] = Bit.bitCount(MoveUtil.getRemoveMap(moves[i]) & board.Pieces[Them] & board.King[Them]);
        }


        long remove;
        int end;
        for(int i = nextToMove[currentPly]; i < nextToGen[currentPly]; i++){
            if(!MoveUtil.isLegalMove(moves[i])) continue;
            remove = MoveUtil.getRemoveMap(moves[i]);
            end    = MoveUtil.getEndIndex(moves[i]);
            for(int j = i + 1; j < nextToGen[currentPly]; j++){
                if(!MoveUtil.isLegalMove(moves[j])) continue;
                if(MoveUtil.getRemoveMap(moves[j]) ==  remove && MoveUtil.getEndIndex(moves[j]) == end){
                    moves[j] = MoveUtil.removeLegal(moves[j]);
                }
            }
        }
    }

    private void addMove(Bitboard board, int color) {
        addKingMove(board.King[color], color, board.Occupied);
        addManMove(board.Pawn[color], color, board.Occupied);
    }

    public static boolean canTake(Bitboard board, int color) {
        //get cache
        long key = board.colorToMove == color ? board.zobristKey : board.zobristKey ^ board.zkTurn;
        byte cap = CaptureDetectionCache.getFlag(key);
        if (cap == 0) return false;
        else if (cap == 1) return true;
        //add cache
        CaptureDetectionCache.addValue(key, true);

        //caculate
        //xét khả năng ăn quân của tốt
        int Them = 1 - color;
        long occupied_NE = shift(board.Pieces[color], north_east) & board.Pieces[Them];
        if ((shift(occupied_NE, north_east) & board.Empty) != 0) return true;
        long occupied_NW = shift(board.Pieces[color], north_west) & board.Pieces[Them];
        if ((shift(occupied_NW, north_west) & board.Empty) != 0) return true;
        long occupied_SE = shift(board.Pieces[color], south_east) & board.Pieces[Them];
        if ((shift(occupied_SE, south_east) & board.Empty) != 0) return true;
        long occupied_SW = shift(board.Pieces[color], south_west) & board.Pieces[Them];
        if ((shift(occupied_SW, south_west) & board.Empty) != 0) return true;
        long king_attack;
        int index;
        for (long i = board.King[color]; i != 0; i = i & (i - 1)) {
            index = Bit.Index(i & -i);
            king_attack = Magic.getKingMove(index, board.Occupied, true);
            for(int dir = NE; dir <= SW; dir++) {
                if ((king_attack & DirectionMask[dir][index] & board.Pieces[Them]) != 0 && more_than_one(king_attack & DirectionMask[dir][index])) return true;
            }
        }

        //add cache
        CaptureDetectionCache.addValue(key, false);
        return false;
    }

    public int relative_rank(int s, int color) {
        return color == 0 ? s : 9 - s;
    }
}
