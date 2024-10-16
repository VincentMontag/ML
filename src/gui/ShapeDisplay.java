package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import main.Point;

public class ShapeDisplay extends JPanel {

    private static final int SIZE = 5;

    private static final double SCALE = 10;

    private List<Point> points;

    private List<Boolean> bends;

    public ShapeDisplay(List<Point> points) {
        this.points = points;
        setPreferredSize(new Dimension(1000, 1000));

        JFrame frame = new JFrame("Point drawing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void setBends(List<Boolean> bends) {
        this.bends = bends;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            for (int i = 0; i < points.size(); i++) {
                Point point = points.get(i);
                if (bends != null && bends.get(i))
                    g.setColor(Color.GREEN);
                else
                    g.setColor(Color.RED);
                g.fillOval((int) (point.x * SCALE), (int) (point.y * SCALE), SIZE, SIZE);

            }
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

}