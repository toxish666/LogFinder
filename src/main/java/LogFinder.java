import com.aaa.gui.MainFrame;

import javax.swing.*;
import java.awt.*;

public class LogFinder {

    public static void main(String[] args){

        EventQueue.invokeLater(()->{
            JFrame mainFrame = new MainFrame();
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setVisible(true);
        });
    }
}
