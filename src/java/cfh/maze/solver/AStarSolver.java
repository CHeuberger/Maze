package cfh.maze.solver;

import static java.awt.Color.*;
import static java.util.Objects.*;

import java.awt.Point;
import java.util.PriorityQueue;

import cfh.maze.Maze;
import cfh.maze.Node;
import cfh.maze.Path;
import cfh.maze.Solver;


public class AStarSolver extends Solver {
    
    public AStarSolver() {
        super("A *", "A Star search");
    }
    
    @Override
    protected Path solve(Maze maze) {
        PriorityQueue<CostNode> open = new PriorityQueue<>();
        mazePanel().clearPaths();
 
        open.add(new CostNode(maze.entry, null, 0, maze.exit));
        
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
                    open.add(new CostNode(node, current, cost, maze.exit));
                }
            }
        }
        
        return null;
    }
    
    private static class CostNode implements Comparable<CostNode> {
        final Node node;
        CostNode parent;
        double cost;
        final double prev;

        CostNode(Node node, CostNode parent, double cost, Node exit) {
            this.node = requireNonNull(node);
            this.parent = parent;
            this.cost = cost;
            this.prev = Point.distance(node.x, node.y, exit.x, exit.y);
        }
        
        @Override
        public int compareTo(CostNode other) {
            return other == null ? 1 : Double.compare(this.cost+this.prev, other.cost+other.prev);
        }
    }
}
