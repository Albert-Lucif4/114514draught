import bit.Bitboard;
import engine.Constants;
import lib.FEN;
import lib.Util;
import log.Debug;
import movegen.MoveUtil;
import pc.MainScreen;
import search.*;

public class Main {

    static long nodes = 0;


    static void play(Bitboard board) {
//        int depth = board.colorToMove == 0 ? 11 : 10;
        TimeUtil.setThinkingTime(2000);
        Search.search(board, 64);
        if (!MoveUtil.isLegalMove(Util.bestMove)) return;
        board.doMove(Util.bestMove);
        Debug.Log(board);
        System.out.println(FEN.getFenFromBB(board));
        System.out.println("SCORE: " + Util.bestScore + ". MAX DEPTH: " + Constants.maxWindowDepth + ". MAX PLY: " + Constants.maxDepth + ". TIME: " + Constants.ThinkingTime + ". RESEARCH: " + Constants.AWPResearchCount);
        play(board);
    }

    public static void main(String[] args) {
        new MainScreen();
//        System.out.println("p".toUpperCase());


    }
}
