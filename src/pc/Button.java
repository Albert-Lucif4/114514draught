package pc;

import java.awt.*;
import java.awt.event.MouseEvent;

public class Button {
    int x;
    int y;
    int width;
    int height;
    boolean state;
    BoardScreen parent;
    Button(int x, int y, BoardScreen parent){
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.state = false;
    }

    void mouseReleased(MouseEvent e){

    }

    void mousePressed(MouseEvent e){

    }

    void update(){

    }

    boolean inside(MouseEvent e){
        return false;
    }


}
