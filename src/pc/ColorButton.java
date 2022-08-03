package pc;

import java.awt.event.MouseEvent;

public class ColorButton extends Button{
    ColorButton(int x, int y, BoardScreen parent) {
        super(x, y, parent);
        this.width = 50;
        this.height = 50;
    }

    @Override
    void update(){
        if(state){
            this.parent.g2D.drawImage(Setting.colorPressing, x, y, width, height, this.parent);
        }else{
            this.parent.g2D.drawImage(Setting.color, x, y, width, height, this.parent);
        }
    }

    @Override
    void mouseReleased(MouseEvent e){
        if(inside(e)) {
            this.state = false;
            if (this.parent.playerTurn) {
                this.parent.fuckbrain();
            }
        }else {
            state = false;
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
