package pc;

import bit.Bit;
import bit.Bitboard;
import engine.Constants;
import lib.Util;
import movegen.ConvertMove;
import movegen.Magic;
import movegen.MoveGenUtil;
import movegen.MoveUtil;
import search.Search;
import search.TTUtil;
import search.TimeUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import static lib.Color.black;
import static lib.Color.white;

public class BoardScreen  extends JPanel implements ActionListener, KeyListener, MouseListener, MouseWheelListener {
    Graphics2D g2D;
    private ArrayList<Image> images;
    private int squareSize;
    private Deviation deviation;
    //Set up values
    private Bitboard caculationBoard;
    private Bitboard drawingBoard;
    private ArrayList<Move> suggestMoves;
    private ScreenPos clickPos;
    boolean playerTurn;
    private int thinkingTime;
    private boolean end;
    private Font detail_font = new Font("Monospaced", Font.BOLD, 12);
    private ArrayList<String> history;
    private ButtonGroup buttonGroup;
    private String last_move;


    BoardScreen(Bitboard board) {
        Timer timer = new Timer(10, this);
        timer.start();
        caculationBoard = board;
        drawingBoard = clone(board);
        this.SetupValue();
        this.SetupScreen();
    }

    public void paint(Graphics g) {
        super.paint(g);
        this.g2D = (Graphics2D) g;
//        g2D.setColor(new Color());
        if (end) {
            caculationBoard = new Bitboard();
            drawingBoard = clone(caculationBoard);
            if (!playerTurn) {
                Runnable runnable = this::think;
                Thread thread = new Thread(runnable);
                thread.start();
            }
        }
        this.drawBoard();
        //this.showDetail();
        this.drawPieces();
        this.buttonGroup.update();
    }

    private void SetupValue() {
        this.deviation = new Deviation(0, 50);
        clickPos = new ScreenPos(-1, -1);
        suggestMoves = new ArrayList<>();
        this.images = new ArrayList<>();
        this.squareSize = 75;
        end = false;
        playerTurn = true;
        thinkingTime = 1000;
        last_move = "";
        this.loadImage();
        this.buttonGroup = new ButtonGroup();
        buttonGroup.add(new NewGame(0, 0, this));
        buttonGroup.add(new ColorButton(110, 0, this));
    }

    private void SetupScreen() {
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseWheelListener(this);
        this.setSize(1650, 1700);
        this.setFocusable(true);
        this.requestFocusInWindow();
        this.setVisible(true);
    }

    void newGame(){
        caculationBoard = new Bitboard();
        drawingBoard = new Bitboard();
        clickPos.reset();
        suggestMoves = new ArrayList<>();
        end = false;
        playerTurn = true;
        last_move = "";
        history = new ArrayList<>();
    }

    private void loadImage() {
        String path = System.getProperty("user.dir");
        this.images.add(this.getToolkit().getImage(path + "/Textures/whitechess.png")); //0
        this.images.add(this.getToolkit().getImage(path + "/Textures/king_white.png")); // 1
        this.images.add(this.getToolkit().getImage(path + "/Textures/blackchess.jpeg")); // 2
        this.images.add(this.getToolkit().getImage(path + "/Textures/Play/black_king.png")); // 3
        this.images.add(this.getToolkit().getImage(path + "/Textures/BlackMan.png")); // 5

    }

    private void drawBoard() {
        this.g2D.setColor(new Color(255, 255, 255, 255));
        this.g2D.fillRect(deviation.getX() + 20, deviation.getY() + 20, 15 * squareSize, 15 * squareSize);

        for (int row = 0; row < 10; row++) {
            for (int column = 0; column < 10; column++) {
                if ((row + column) % 2 == 0)
                    this.g2D.setColor(new Color(217,144,96));
                else

                    this.g2D.setColor(new Color(175,97,79));
                this.g2D.fillRect(
                        column * this.squareSize + this.deviation.getX(),
                        row * this.squareSize + this.deviation.getY(),
                        this.squareSize, this.squareSize
                );

            }
        }




        //draw last move
        if(!last_move.equals("")){
            String[] splits = last_move.split("-");
            for (String s : splits) {
                int i = 50 - Integer.parseInt(s);
                int column = Magic.column[i];
                int row = Magic.row[i];
                g2D.setColor(new Color(118, 27, 35, 217));
                g2D.fillRect(
                        column * squareSize + deviation.getX(),
                        row * squareSize + deviation.getY(), squareSize, squareSize
                );
            }
        }
    }

    private void drawPieces() {
        for (int index = 0; index < 50; index++) {
            int row = Magic.row[index];
            int column = Magic.column[index];
            switch (getPieces(drawingBoard, index)) {
                case 1:
                    g2D.drawImage(images.get(0), column * squareSize + deviation.getX(),
                            row * squareSize + deviation.getY(), squareSize, squareSize, this);
                    break;
                case 2:
                    g2D.drawImage(images.get(1), column * squareSize + deviation.getX(),
                            row * squareSize + deviation.getY(), squareSize, squareSize, this);
                    break;
                case -1:
                    g2D.drawImage(images.get(2), column * squareSize + deviation.getX(),
                            row * squareSize + deviation.getY(), squareSize, squareSize, this);
                    break;
                case -2:
                    g2D.drawImage(images.get(3), column * squareSize + deviation.getX(),
                            row * squareSize + deviation.getY(), squareSize, squareSize, this);
                    break;
            }
        }

    }

    private void updateMousePos(int x, int y) {
        if (x < deviation.getX() || y < deviation.getY()) {
            this.clickPos = new ScreenPos(-1, -1);
        } else {
            this.clickPos = new ScreenPos((y - deviation.getY()) / squareSize, (x - deviation.getX()) / squareSize);
        }
    }

    public void fuckbrain(){
        Runnable runnable = this::think;
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void think() {
        if(end) return;
        playerTurn = false;
        ResultUtil.reset();
        TimeUtil.setThinkingTime(this.thinkingTime);
//        System.out.println(caculationBoard.toString());
        Search.search(caculationBoard, Constants.MAX_PLIES);

        if(ResultUtil.currentBestMove == null || ResultUtil.currentBestMove.equals("")){
            end = true;
        }

        if(!end){
            caculationBoard.doMove(Util.bestMove);
            drawingBoard = clone(caculationBoard);
            playerTurn = true;
            last_move = ResultUtil.currentBestMove;
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 10) {
            end = true;
            caculationBoard.colorToMove = 1 - caculationBoard.colorToMove;
            playerTurn = caculationBoard.colorToMove == white;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.buttonGroup.mousePressed(e);
        if (e.getButton() == 1 && playerTurn && !end) {
            this.updateMousePos(e.getX(), e.getY());
            if (!this.clickPos.valid()) {
                //点击退出
                this.clickPos.reset();
                suggestMoves = new ArrayList<>();
            } else if (suggestMoves.size() > 0) {
                //Đã có suggest trước đó
                //Check xem có phải ô di chuyển quân ko
                boolean moved = false;
                for (Move m : suggestMoves) {
                    if (m.end == clickPos.index()) {
                        //玩家移动
                        last_move = ConvertMove.getMove(caculationBoard, m.raw);
                        caculationBoard.doMove(m.raw);
                        drawingBoard = clone(caculationBoard);
                        playerTurn = false;
                        moved = true;
                        suggestMoves = new ArrayList<>();
                        clickPos.reset();
                        Runnable runnable = this::think;
                        Thread thread = new Thread(runnable);
                        thread.start();
                    }
                }
                if (!moved) addNewMoveList();
            } else {
                addNewMoveList();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.buttonGroup.mouseReleased(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
    }

    private int getPieces(Bitboard board, int index) {
        long mask = 0x1L << index;
        int result = 0;
        if ((board.Kings & mask) != 0) result = 2;
        else if (((board.WhitePieces | board.BlackPieces) & mask) != 0) result = 1;
        if ((board.WhitePieces & mask) == 0) result = -result;
        return result;
    }

    private void addNewMoveList() {
        suggestMoves = new ArrayList<>();
        if ((getPieces(caculationBoard, clickPos.index()) > 0 && caculationBoard.colorToMove == white) ||
                (getPieces(caculationBoard, clickPos.index()) < 0 && caculationBoard.colorToMove == black)) {
            suggestMoves = new ArrayList<>();
            MoveGenUtil moveGen = new MoveGenUtil();
            moveGen.startPly();
            moveGen.generateMoves(caculationBoard);
            while(moveGen.hasNext()){
                int next = moveGen.next();
                long move = moveGen.getMove(next);
                if(!MoveUtil.isLegalMove(move)) continue;
                int start = Bit.Index(MoveUtil.getRemoveMap(move) & caculationBoard.Pieces[caculationBoard.colorToMove]);
                if(start == clickPos.index()) suggestMoves.add(new Move(start, MoveUtil.getEndIndex(move), MoveUtil.getRemoveMap(move), move));
            }
        }
    }

    private Bitboard clone(Bitboard board) {
        return new Bitboard(board.WhitePieces, board.BlackPieces, board.Kings, board.colorToMove);
    }

    private void drawString(Graphics g, String text, int x, int y) {
        for (String line : text.split("\n"))
            g.drawString(line, x, y += g.getFontMetrics().getHeight());
    }
}
