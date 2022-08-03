package pc;

import java.awt.*;
import java.awt.event.MouseEvent;

public class NewGame extends Button{
    NewGame(int x, int y, BoardScreen parent) {
        super(x, y, parent);
        this.width = 100;
        this.height = 50;
    }

    @Override
    void update(){
        if(state){
            this.parent.g2D.drawImage(Setting.newGamePressing, x, y, width, height, this.parent);
        }else{
            this.parent.g2D.drawImage(Setting.newGame, x, y, width, height, this.parent);
        }
    }

    @Override
    void mouseReleased(MouseEvent e){
        if(inside(e)) {
            this.state = false;
            if (this.parent.playerTurn) {
                this.parent.newGame();
            }
        }else {
            this.state = false;
        }
    }

    @Override
    void mousePressed(MouseEvent e){
        this.state = true;
    }

    @Override
    boolean inside(MouseEvent e){
        return x < e.getX() && e.getX() < x + width && y < e.getY() && e.getY() < y + height;
    }
}
