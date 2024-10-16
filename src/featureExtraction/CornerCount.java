package featureExtraction;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import gui.ShapeDisplay;
import main.Color;
import main.FeatureExtractor;
import main.Point;
import main.Util;

public class CornerCount implements FeatureExtractor {

    private static final int STEP_SIZE = 1;

    private static final int POINT_DIFF = 8;

    private static final double GRADIENT_TOLERANCE = 1;

    private ShapeDisplay shapeDisplay;

    private List<Point> points = new ArrayList<>();

    private List<Boolean> bendStepResults = new ArrayList<>();

    public CornerCount() {
        this.shapeDisplay = new ShapeDisplay(this.points);
    }

    @Override
    public int extractFeature(BufferedImage image) {
        detect(
                0, (framePos) -> framePos < image.getWidth(), STEP_SIZE,
                0, (innerPos) -> innerPos < image.getHeight(), 1,
                (framePos, innerPos) -> new Point(framePos, innerPos), 0, image);
        return evaluateBendStepResults();
    }

    private void detect(
            int frameStart, Function<Integer, Boolean> frameStep, int frameIncrement,
            int innerStart, Function<Integer, Boolean> innerStep, int innerIncrement,
            BiFunction<Integer, Integer, Point> getPos, int exec, BufferedImage image) {

        boolean atLeastOnePointDetected = false;

        frameFor: for (int framePos = frameStart; frameStep.apply(framePos); framePos += frameIncrement) {
            innerFor: for (int innerPos = innerStart; innerStep.apply(innerPos); innerPos += innerIncrement) {

                Point p = getPos.apply(framePos, innerPos);
                boolean pointDetected = evaluate(p, image, exec);

                if (pointDetected) {
                    atLeastOnePointDetected = true;
                    break innerFor;
                }

                if (atLeastOnePointDetected && !innerStep.apply(innerPos + 1)) {
                    break frameFor;
                }
            }
        }

        Point latest = points.get(points.size() - 1);

        exec++;
        if (exec == 1)
            detect(
                    latest.y, (framePos) -> framePos < image.getHeight(), STEP_SIZE,
                    latest.x, (innerPos) -> innerPos > 0, -1,
                    (framePos, innerPos) -> new Point(innerPos, framePos), exec, image);
        else if (exec == 2)
            detect(
                    latest.x, (framePos) -> framePos > 0, -STEP_SIZE,
                    latest.y, (innerPos) -> innerPos > 0, -1,
                    (framePos, innerPos) -> new Point(framePos, innerPos), exec, image);
        else if (exec == 3)
            detect(
                    latest.y, (framePos) -> framePos > 0, -STEP_SIZE,
                    latest.x, (innerPos) -> innerPos < image.getWidth(), 1,
                    (framePos, innerPos) -> new Point(innerPos, framePos), exec, image);

    }

    private boolean evaluate(Point p, BufferedImage image, int rotation) {
        int color = image.getRGB(p.x, p.y);

        // If color is sth else than white (unknown), the first pixel of the sign has
        // been detected
        Color detectedColor = Util.getColorOfPixel(color);
        if (!detectedColor.equals(Color.UNKNOWN)) {
            points.add(p);
            this.bendStepResults.add(isBend());

            if (this.shapeDisplay != null) {
                try {
                    //Thread.sleep(50);
                    this.shapeDisplay.repaint();
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }

            return true;
        }

        // Only unknown pixel detected
        return false;
    }

    private boolean isBend() {
        if (this.points.size() < 3 * POINT_DIFF)
            return false;

        Point p0 = this.points.get(this.points.size() - 1 - 2 * POINT_DIFF);
        Point p1 = this.points.get(this.points.size() - 1 - POINT_DIFF);
        Point p2 = this.points.get(this.points.size() - 1);

        double gradient0 = Math.atan2(p1.y - p0.y, p1.x - p0.x);
        double gradient1 = Math.atan2(p2.y - p1.y, p2.x - p1.x);

        if (Math.abs(gradient0 - gradient1) > GRADIENT_TOLERANCE) {
            return true;
        }
        return false;
    }

    private int evaluateBendStepResults() {
        this.shapeDisplay.setBends(this.bendStepResults);
        this.shapeDisplay.repaint();
        int bendCount = 0;
        boolean incremented = false;
        for (boolean result : this.bendStepResults) {
            if (result) {
                if (!incremented)
                    bendCount++;
                incremented = true;
            } else {
                incremented = false;
            }
        }
        return bendCount;
    }

}
