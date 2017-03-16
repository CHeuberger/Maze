package cfh.maze;

import static cfh.maze.Direction.*;
import static java.awt.Color.*;

import java.util.ArrayList;
import java.util.List;


public class LeftTurnSolver extends Solver {

    LeftTurnSolver(MazePanel panel) {
        super(panel, "Left Turn", "Solves the maze by going through it and always turning left");
    }

    @Override
    protected boolean solve0(Maze maze) {
        List<Node> solution = new ArrayList<>();
        solution.add(maze.entry);
        Path path = Path.create(YELLOW, maze.entry);
        mazePanel.addPath(path);
        Direction dir = SOUTH;
        Node current = maze.entry.neighbour(dir);
        solution.add(current);
        path.addPoint(current.x, current.y);
        while (!current.equals(maze.exit) && !current.equals(maze.exit)) {
            if (current.neighbour(dir.left()) != null) {
                dir = dir.left();
            } else if (current.neighbour(dir) != null) {
                // go straight
            } else if (current.neighbour(dir.right()) != null) {
                dir = dir.right();
            } else if (current.neighbour(dir.back()) != null) {
                dir = dir.back();
            } else {
                assert false : "stuck at " + current + " going " + dir;
                break;
            }
            current = current.neighbour(dir);
            solution.add(current);
            path.addPoint(current.x, current.y);
        }
        
        return current.equals(maze.exit);
    }
}
