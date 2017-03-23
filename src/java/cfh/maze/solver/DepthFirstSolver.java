package cfh.maze.solver;

import static java.awt.Color.*;
import static java.util.Objects.*;

import java.awt.Point;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import cfh.maze.Maze;
import cfh.maze.Node;
import cfh.maze.Path;
import cfh.maze.Solver;


public class DepthFirstSolver extends Solver {
    
    public DepthFirstSolver() {
        super("Depth First", "explore depth first");
    }
    
    @Override
    protected Path solve(Maze maze) {
        mazePanel().clearPaths();
        Map<Node, CostNode> found = new HashMap<>();
        Deque<CostNode> open = new LinkedList<>();
        
        open.addLast(new CostNode(maze.entry, null, 0));
        
        while (!open.isEmpty()) {
            CostNode current = open.removeLast();
            found.put(current.node, current);
            if (current.node.equals(maze.exit)) {
                Path path = Path.create(BLUE);
                for (CostNode node = current; node != null; node = node.parent) {
                    path.prepend(node.node.x, node.node.y);
                }
                mazePanel().addPath(path);
                return path;
            }
            for (Node node : current.node.neighbours()) {
                double cost = Point.distance(current.node.x, current.node.y, node.x, node.y) + current.cost;
                CostNode old = found.get(node);
                if (old == null || cost < old.cost) {
                    open.addLast(new CostNode(node, current, cost));
                }
            }
        }
 
        return null;
    }
    
    private static class CostNode {
        final Node node;
        CostNode parent;
        double cost;

        CostNode(Node node, CostNode parent, double cost) {
            this.node = requireNonNull(node);
            this.parent = parent;
            this.cost = cost;
        }
    }
}
