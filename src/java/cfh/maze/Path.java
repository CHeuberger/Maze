package cfh.maze;

import static java.util.Objects.*;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;


public class Path {
    
    static Path create(Color color, Node... nodes) {
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
    
    Color getColor() {
        return color;
    }
    
    void add(Point point) {
        points.addLast(new Point(point));
    }
    
    void add(int x, int y) {
        add(new Point(x, y));
    }
    
    List<Point> points() {
        return new ArrayList<Point>(points);
    }
    
    void back() {
        if (!points.isEmpty()) {
            points.removeLast();
        }
    }
}
