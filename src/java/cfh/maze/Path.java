package cfh.maze;

import static java.util.Objects.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.Deque;
import java.util.LinkedList;


public class Path {
    
    static Path create(Color color, Node... nodes) {
        Path path = new Path(requireNonNull(color));
        for (Node node : nodes) {
            path.addPoint(node.x, node.y);
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
        addPoint(x, y);
    }
    
    void addPoint(Point point) {
        points.addLast(new Point(point));
    }
    
    void addPoint(int x, int y) {
        addPoint(new Point(x, y));
    }
    
    void back() {
        if (!points.isEmpty()) {
            points.removeLast();
        }
    }
    
    void paint(Graphics2D gg) {
        if (!points.isEmpty()) {
            gg.setColor(color);
            if (points.size() == 1) {
                Point point = points.getFirst();
                gg.fillRect(point.x, point.y, 1, 1);
            } else {
                Point last = null;
                for (Point point : points) {
                    if (last != null) {
                        gg.draw(new Line2D.Double(last.x+0.5, last.y+0.5, point.x+0.5, point.y+0.5));
                    }
                    last = point;
                }
            }
        }
    }
}
