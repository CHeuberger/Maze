package cfh.maze;

import static java.awt.Color.*;
import static java.util.Objects.*;

import java.awt.Point;
import java.util.PriorityQueue;


public class BreathFirstSolver extends Solver {
    
    BreathFirstSolver() {
        super("Breadth First", "explore neighboors first");
    }
    
    @Override
    Path solve(Maze maze) {
        PriorityQueue<CostNode> open = new PriorityQueue<>();
        
        open.add(new CostNode(maze.entry, null, 0));
        
        while (!open.isEmpty()) {
            CostNode current = open.remove();
            if (current.node.equals(maze.exit)) {
                Path path = Path.create(BLUE);
                for (CostNode node = current; node != null; node = node.parent) {
                    path.prepend(node.node.x, node.node.y);
                }
                mazePanel().addPath(path);
                return path;
            }
            for (Node node : current.node.neighbours()) {
                mazePanel().addPath(Path.create(YELLOW, current.node, node));
                double cost = Point.distance(current.node.x, current.node.y, node.x, node.y) + current.cost;
                CostNode old = open.stream().filter(cn -> cn.node.equals(node)).findAny().orElse(null);
                if (old != null) {
                    if (cost < old.cost) {
                        old.parent = current;
                        old.cost = cost;
                    }
                } else {
                    open.add(new CostNode(node, current, cost));
                }
            }
        }
        
        return null;
    }
    
    private static class CostNode implements Comparable<CostNode> {
        final Node node;
        CostNode parent;
        double cost;

        CostNode(Node node, CostNode parent, double cost) {
            this.node = requireNonNull(node);
            this.parent = parent;
            this.cost = cost;
        }
        
        @Override
        public int compareTo(CostNode other) {
            return other == null ? 1 : Double.compare(this.cost, other.cost);
        }
    }
}
