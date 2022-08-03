package pc;

import javax.swing.*;
import java.awt.*;

public class Setting {
    static String path = System.getProperty("user.dir");
    static Image newGame = (new JPanel()).getToolkit().getImage(path + "\\Textures\\开始游戏.jpg");
    static Image newGamePressing = (new JPanel()).getToolkit().getImage(path + "\\Textures\\开始游戏.jpg");
    static Image color = (new JPanel()).getToolkit().getImage(path + "/Textures/换手.jpg");
    static Image colorPressing = (new JPanel()).getToolkit().getImage(path + "/Textures/换手.jpg");


}
