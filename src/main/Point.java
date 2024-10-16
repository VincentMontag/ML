package main;

public class Point {

    public final int x;
    public final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Point(" + this.x + ", " + this.y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Point p) {
            return p.x == this.x && p.y == this.y;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return (x << 16) + y;
    }
}
