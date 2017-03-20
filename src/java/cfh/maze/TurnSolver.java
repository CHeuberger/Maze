package cfh.maze;

import static cfh.maze.Direction.*;
import static java.awt.Color.*;

import java.util.ArrayList;
import java.util.List;


public class TurnSolver extends Solver {
    
    private final boolean right;
    private final boolean compress;

    TurnSolver(boolean right) {
        this(right, true);
    }
    
    TurnSolver(boolean right, boolean compress) {
        super(right ? "Right Turn" : "Left Turn", 
                "Solves the maze by going through it and always turning " + (right ? "right" : "left"));
        this.right = right;
        this.compress = compress;
    }

    @Override
    Path solve(Maze maze) {
        List<Node> solution = new ArrayList<>();
        solution.add(maze.entry);
        Path path = Path.create(YELLOW, maze.entry);
        mazePanel().clearPaths();
        mazePanel().addPath(path);
        Direction dir = SOUTH;
        Node current = maze.entry.neighbour(dir);
        solution.add(current);
        path.add(current.x, current.y);
        while (!current.equals(maze.exit) && !current.equals(maze.exit)) {
            if (current.neighbour(right ? dir.right() : dir.left()) != null) {
                dir = right ? dir.right() : dir.left();
            } else if (current.neighbour(dir) != null) {
                // go straight
            } else if (current.neighbour(right ? dir.left() : dir.right()) != null) {
                dir = right ? dir.left() : dir.right();
            } else if (current.neighbour(dir.back()) != null) {
                dir = dir.back();
            } else {
                assert false : "stuck at " + current + " going " + dir;
                break;
            }
            current = current.neighbour(dir);
            int index = solution.indexOf(current);
            if (compress && index != -1) {
                solution.subList(index+1, solution.size()).clear();
            } else {
                solution.add(current);
            }
            path.add(current.x, current.y);
        }
        
        path = Path.create(BLUE, solution.toArray(new Node[solution.size()]));
        mazePanel().addPath(path);
        return current.equals(maze.exit) ? path : null;
    }
}
