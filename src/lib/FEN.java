package lib;

import bit.Bitboard;

/**
 *保存棋盘的分。
 *棋盘与分数相互转换
 *
 * @author 田所浩二
 */
public class FEN {
    private String fen;
    public FEN(String fen){
        this.fen = fen;
    }

    public Bitboard getBoard(){
        long Kings = 0;
        long WhitePieces = 0;
        long BlackPieces = 0;
        String[] tmp = fen.replaceAll(" ", "").split(":");
        String[] white = tmp[1].substring(1).replaceAll(" ", "").split(",");
        String[] black = tmp[2].substring(1).replaceAll(" ", "").split(",");
        for (String i : white) {
            if (i.charAt(0) == 'K'){
                Kings |= 0x1L << (50 - Integer.valueOf(i.substring(1)));
                WhitePieces |= 0x1L << (50 - Integer.valueOf(i.substring(1)));
            }
            else{
                WhitePieces |= 0x1L << (50 - Integer.valueOf(i));
            }
        }
        for (String i : black) {
            if (i.charAt(0) == 'K'){
                Kings |= 0x1L << (50 - Integer.valueOf(i.substring(1)));
                BlackPieces |= 0x1L << (50 - Integer.valueOf(i.substring(1)));
            }
            else {
                BlackPieces |= 0x1L << (50 - Integer.valueOf(i));
            }
        }

        int colorToMove = Color.white;
        switch (fen.replaceAll(" ", "").charAt(0)) {
            case 'W':
                colorToMove = Color.white;
                break;
            case 'B':
                colorToMove = Color.black;
                break;
        }

        return new Bitboard(WhitePieces, BlackPieces, Kings, colorToMove);
    }

    public String getFen() {
        return fen;
    }

    public static String getFenFromBB(Bitboard board){
        String fen = board.colorToMove == 0 ? "W" : "B";
        StringBuilder w = new StringBuilder(":W");
        StringBuilder b = new StringBuilder(":B");
        for(int i = 0; i < 50; i++){
            long mask = 0x1L << i;
            if((mask & board.WhitePieces) != 0){
                if((mask & board.Kings) != 0) w.append("K");
                w.append(Integer.toString(50 - i)).append(",");
            }else if((mask & board.BlackPieces) != 0){
                if((mask & board.Kings) != 0) b.append("K");
                b.append(Integer.toString(50 - i)).append(",");
            }
        }
        if(w.charAt(w.length() - 1) == ',') w.deleteCharAt(w.length() - 1);
        if(b.charAt(b.length() - 1) == ',') b.deleteCharAt(b.length() - 1);
        fen += w;
        fen += b;
        return fen;
    }
}
