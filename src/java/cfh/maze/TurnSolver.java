package cfh.maze;

import static cfh.maze.Direction.*;
import static java.awt.Color.*;

import java.util.ArrayList;
import java.util.List;


public class TurnSolver extends Solver {
    
    private final boolean right;

    TurnSolver(boolean right) {
        super(right ? "Right Turn" : "Left Turn", 
                "Solves the maze by going through it and always turning " + (right ? "right" : "left"));
        this.right = right;
    }

    @Override
    protected boolean solve0(Maze maze) {
        List<Node> solution = new ArrayList<>();
        solution.add(maze.entry);
        Path path = Path.create(YELLOW, maze.entry);
        mazePanel().clearPaths();
        mazePanel().addPath(path);
        Direction dir = SOUTH;
        Node current = maze.entry.neighbour(dir);
        solution.add(current);
        path.addPoint(current.x, current.y);
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
            if (index == -1) {
                solution.add(current);
            } else {
                solution.subList(index+1, solution.size()).clear();
            }
            path.addPoint(current.x, current.y);
        }
        
        path = Path.create(BLUE, solution.toArray(new Node[0]));
        mazePanel().addPath(path);
        return current.equals(maze.exit);
    }
}
