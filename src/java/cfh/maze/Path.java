package cfh.maze;

import static java.util.Objects.*;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;


public class Path {
    
    public static Path create(Color color, Node... nodes) {
        Path path = new Path(requireNonNull(color));
        for (Node node : nodes) {
            path.add(node.x, node.y);
        }
        return path;
    }
    
    private Color color;
    private final Deque<Point> points = new LinkedList<>();
    
    Path(Color color) {
        assert color != null;
        this.color = color;
    }
    
    Path(Color color, int x, int y) {
        this(color);
        add(x, y);
    }
    
    public Color getColor() {
        return color;
    }
    
    public void add(Point point) {
        points.addLast(new Point(point));
    }
    
    public void add(int x, int y) {
        add(new Point(x, y));
    }
    
    public void prepend(int x, int y) {
        points.addFirst(new Point(x, y));
    }
    
    public List<Point> points() {
        return new ArrayList<Point>(points);
    }
    
    public void back() {
        if (!points.isEmpty()) {
            points.removeLast();
        }
    }
}
