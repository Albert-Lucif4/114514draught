package movegen;

import bit.Bit;
import bit.Bitboard;
import static bit.Bit.more_than_one;
import static lib.Direction.Dir;
import static lib.Piece.king;
import static lib.Piece.pawn;
import static movegen.Magic.*;
import static movegen.Magic.DirectionMask;
import static movegen.Magic.NDIR;

public class ConvertMove {
    public final long[]     moves                       = new long[1000];
    public final String[]   strings                     = new String[1000];
    private final int[]     captureCount                = new int[1000];
    private int             nextToGen                   = 0;
    private int             nextToMove                  = 0;

    private void addCaptureMove(long removed, long end, int piece, int color, String move){
        int removeCount = Bit.bitCount(removed) - 1;
        if(nextToGen > nextToMove){
            if(removeCount > captureCount[nextToMove]) {
                nextToGen = nextToMove;
            }else if(removeCount < captureCount[nextToMove]){
                return;
            }
        }
        captureCount[nextToGen] = removeCount;
        strings[nextToGen] = move;
        moves[nextToGen++] = MoveUtil.createMove(removed, Bit.Index(end), piece, color, 1);
    }

    private void addManCapture(long enemy, long empty , long end, long removed, int color, String moves) {
        int i = Bit.Index(end);
        boolean cap = false;
        for(int dir = NE; dir <= SW; dir++){
            if((Shift_1_Mask[dir][i] & enemy) != 0 && (Shift_2_Mask[dir][i] & empty) != 0){
                addManCapture(enemy ^ Shift_1_Mask[dir][i],
                        empty ^ Fill_3_Mask[dir][i],
                        Shift_2_Mask[dir][i],
                        removed | Shift_1_Mask[dir][i], color, moves + "-" + Integer.toString(50 - Bit.Index(Shift_2_Mask[dir][i])));
                cap = true;
            }
        }
        if(!cap && more_than_one(removed)){
            addCaptureMove(removed, end, pawn, color, moves);

        }

    }

    private void addKingCapture(long enemy, long empty, long end, long removed, int color, int comefrom, String moves) {

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
                                color, dir, moves + "-" + Integer.toString(50 - Bit.Index(pop)));
                        dirMove = dirMove & (dirMove - 1);
                    }
                }
            }
        }

        if(!cap && more_than_one(removed)) {
            addCaptureMove(removed, end, king, color, moves);
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
            addKingCapture(board.Pieces[Them], board.Empty, pop, pop, color, NDIR, Integer.toString(50 - Bit.Index(pop)));
            kings = kings & (kings - 1);
        }
        while (men != 0) {
            pop = men & -men;
            addManCapture(board.Pieces[Them], board.Empty, pop, pop, color, Integer.toString(50 - Bit.Index(pop)));
            men = men & (men - 1);
        }

        long remove;
        int end;
        for(int i = nextToMove; i < nextToGen; i++){
            if(!MoveUtil.isLegalMove(moves[i])) continue;
            remove = MoveUtil.getRemoveMap(moves[i]);
            end    = MoveUtil.getEndIndex(moves[i]);
            for(int j = i + 1; j < nextToGen; j++){
                if(!MoveUtil.isLegalMove(moves[j])) continue;
                if(MoveUtil.getRemoveMap(moves[j]) ==  remove && MoveUtil.getEndIndex(moves[j]) == end){
                    moves[j] = MoveUtil.removeLegal(moves[j]);
                }
            }
        }
    }

    public boolean hasNext(){
        return nextToGen != nextToMove;
    }

    public int next(){
        return nextToMove++;
    }

    /**
     * 获取代表移动的字符串
     *
     * @param
     * @param move 移动
     * @return 字符串
     */
    public static String getMove(Bitboard board, long move){
        long removed = MoveUtil.getRemoveMap(move);
        int end = MoveUtil.getEndIndex(move);
        int start = Bit.Index(removed & board.Pieces[board.colorToMove]);
        //普通移动
        if(Bit.bitCount(removed) == 1) return Integer.toString(50 - start) + "-" + Integer.toString(50 - end);
        //capture
        ConvertMove convert = new ConvertMove();
        convert.addCapture(board, board.colorToMove);
        while(convert.hasNext()){
            int next = convert.next();
            final long m = convert.moves[next];
            final String str = convert.strings[next];
            if(!MoveUtil.isLegalMove(m)) continue;
            if(move == m) return str;
        }
        return "null";
    }

}
