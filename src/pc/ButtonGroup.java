package pc;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ButtonGroup {
    ArrayList<Button> buttons;

    ButtonGroup(){
        buttons = new ArrayList<>();
    }

    void add(Button button){
        buttons.add(button);
    }

    void mouseReleased(MouseEvent e){
        for(Button button: buttons){
            button.mouseReleased(e);
        }
    }

    void mousePressed(MouseEvent e){
        for(Button button: buttons){
            if(button.inside(e)) {
                button.mousePressed(e);
            }
        }
    }

    void update(){
        for(Button button: buttons){
            button.update();
        }
    }
}
