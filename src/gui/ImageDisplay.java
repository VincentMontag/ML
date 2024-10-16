package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageDisplay extends JPanel {

    private BufferedImage image;

    public ImageDisplay(BufferedImage image) {
        System.out.println(image.getWidth() + ", " + image.getHeight());
        this.image = image;
        JFrame frame = new JFrame("BufferedImage Display");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setSize(image.getWidth(), image.getHeight());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.image != null)
            g.drawImage(image, 0, 0, this);
    }

}
