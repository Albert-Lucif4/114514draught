package log;

import bit.Bitboard;
import movegen.Magic;

/**
 *
 * @author prn
 */
public class Debug {
    public static void Log(long bb) {
        String str_tmp = convertBBtoString(bb);
        System.out.print("\n +---+---+---+---+---+---+---+---+---+---+\n |");
        for (int i = 0; i < str_tmp.length(); i++) {
            if (str_tmp.charAt(i) == '\n') System.out.print("\n +---+---+---+---+---+---+---+---+---+---+\n |");
            else if (str_tmp.charAt(i) == '1') {
                System.out.print(" X |");
            } else if (str_tmp.charAt(i) == '0') {
                System.out.print("///|");
            } else {
                System.out.print("   |");
            }
        }
        System.out.println("\n");
    }

    public static void Log(Bitboard board, Object message){
        System.out.print(message);
        Log(board);
    }


    public static void Log(Bitboard board) {
        long wm = (board.WhitePieces & board.Kings) ^ board.WhitePieces;
        long wk = board.WhitePieces & board.Kings;
        long bm = (board.BlackPieces & board.Kings) ^ board.BlackPieces;
        long bk = board.BlackPieces & board.Kings;
        System.out.print("\n +---+---+---+---+---+---+---+---+---+---+\n |");
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                if((i + j) % 2 == 0) System.out.print("   |");
                else{
                    if((0x1L << Magic.index[i][j] & wm) != 0) System.out.print(" M |");
                    else if ((0x1L << Magic.index[i][j] & wk) != 0) System.out.print(" K |");
                    else if ((0x1L << Magic.index[i][j] & bm) != 0) System.out.print(" m |");
                    else if ((0x1L << Magic.index[i][j] & bk) != 0) System.out.print(" k |");
                    else System.out.print("///|");
                }
            }
            if(i == 9) System.out.print("\n +---+---+---+---+---+---+---+---+---+---+\n");
            else
                System.out.print("\n +---+---+---+---+---+---+---+---+---+---+\n |");
        }
    }

    public static void Log(long bb, int x) {
        String str_tmp = convertBBtoString(bb);
        System.out.print("\n +---+---+---+---+---+---+---+---+---+---+\n |");
        int number = 0;
        for (int i = 0; i < str_tmp.length(); i++) {
            if (str_tmp.charAt(i) == '\n') System.out.print("\n +---+---+---+---+---+---+---+---+---+---+\n |");
            else if (str_tmp.charAt(i) == '1') {
                System.out.print(" X |");
                number++;
            } else if (str_tmp.charAt(i) == '0') {
                if (number == x) System.out.print(" o |");
                else
                    System.out.print("///|");
                number++;
            } else {
                System.out.print("   |");
            }
        }
        System.out.println("\n");
    }

    public static void Log(long bb, int[] move) {
        String str_tmp = convertBBtoString(bb);
        System.out.print("\n +---+---+---+---+---+---+---+---+---+---+\n |");
        int number = 0;
        for (int i = 0; i < str_tmp.length(); i++) {
            if (str_tmp.charAt(i) == '\n') System.out.print("\n +---+---+---+---+---+---+---+---+---+---+\n |");
            else if (str_tmp.charAt(i) == '1') {
                if(number == move[0]) System.out.print(" x |");
                else System.out.print(" X |");
                number++;
            } else if (str_tmp.charAt(i) == '0') {
                if (number == move[1]) System.out.print(" o |");
                else if (number == move[2]) System.out.print(" $ |");
                else if(number == move[0]) System.out.print(" x |");
                else
                    System.out.print("///|");
                number++;
            } else {
                System.out.print("   |");
            }
        }
        System.out.println("\n");
    }

    public static void Log(int[] move) {
        String str_tmp = convertBBtoString(0);
        System.out.print("\n +---+---+---+---+---+---+---+---+---+---+\n |");
        int number = 0;
        for (int i = 0; i < str_tmp.length(); i++) {
            if (str_tmp.charAt(i) == '\n') System.out.print("\n +---+---+---+---+---+---+---+---+---+---+\n |");
            else if (str_tmp.charAt(i) == '1') {
                System.out.print(" X |");
                number++;
            } else if (str_tmp.charAt(i) == '0') {
                if (number == move[0]) System.out.print(" o |");
                else if(number == move[1]) System.out.print(" X |");
                else if(number == move[2]) System.out.print(" $ |");
                else
                    System.out.print("///|");
                number++;
            } else {
                System.out.print("   |");
            }
        }
        System.out.println("\n");
    }

    public static void Log(long bb, String label) {
        System.out.println(label);
        Log(bb);
    }

    private static String convertBBtoString(long bb) {
        StringBuilder tmp = new StringBuilder(Long.toBinaryString(bb & Magic.LegalMask));
        int length = tmp.length();
        for (int i = 0; i < 50 - length; i++) tmp.insert(0, "0");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                StringBuilder str_tmp = new StringBuilder();
                for (int j = 0; j < 5; j++) {
                    str_tmp.append("-").append(tmp.charAt(i * 5 + j));
                }
                result.insert(0, str_tmp.reverse().append("\n").toString());
            } else {
                StringBuilder str_tmp = new StringBuilder();
                for (int j = 0; j < 5; j++) {
                    str_tmp.append(tmp.charAt(i * 5 + j)).append("-");
                }
                result.insert(0, str_tmp.reverse().append("\n").toString());
            }
        }
        return result.toString();
    }
}
