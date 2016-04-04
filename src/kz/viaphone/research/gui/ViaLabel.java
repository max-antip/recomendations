package kz.viaphone.research.gui;

import javax.swing.*;
import java.awt.*;

public class ViaLabel extends JLabel{
    public static final Font ARIAL = new Font("Arial", Font.PLAIN, 14);

    public ViaLabel(String text) {
        super(text);
        setFont(ARIAL);

    }
}
