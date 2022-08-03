package test;

class TestFEN {
    String fen;
    String id;
    String bestMove;
    TestFEN(String epd){
        String[] tmp = epd.split(" ");
        fen = tmp[0];
        bestMove = tmp[1];
        id = tmp[2];
    }

}
