package cfh.maze.solver;

import static cfh.maze.Direction.*;
import static java.awt.Color.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import cfh.maze.Direction;
import cfh.maze.Maze;
import cfh.maze.Node;
import cfh.maze.Path;
import cfh.maze.Solver;


public class TurnSolver extends Solver {
    
    private final boolean compress;
    
    private Function<Direction, Direction> turnStart;
    private Function<Direction, Direction> turnNext;

    public TurnSolver(boolean right) {
        this(right, true);
    }
    
    TurnSolver(boolean right, boolean compress) {
        super(right ? "Right Turn" : "Left Turn", 
                "Solves the maze by always turning " + (right ? "right" : "left"));
        this.compress = compress;
        turnStart = right ? Direction::right : Direction::left;
        turnNext = right ? Direction::left : Direction::right;
    }

    @Override
    protected Path solve(Maze maze) {
        List<Node> solution = new ArrayList<>();
        solution.add(maze.entry);
        Path path = Path.create(YELLOW, maze.entry);
        mazePanel().clearPaths();
        mazePanel().addPath(path);
        Direction dir = SOUTH;
        Node current = maze.entry.neighbour(dir);
        solution.add(current);
        path.add(current.x, current.y);
        
        search:
        while (!current.equals(maze.exit) && !current.equals(maze.exit)) {
            Direction test = turnStart.apply(dir);
            for (int i = 0; i < 4; i++) {
                if (current.neighbour(test) != null) {
                    dir = test;
                    current = current.neighbour(dir);
                    int index = solution.indexOf(current);
                    if (compress && index != -1) {
                        solution.subList(index+1, solution.size()).clear();
                    } else {
                        solution.add(current);
                    }
                    path.add(current.x, current.y);
                    continue search;
                }
                test = turnNext.apply(test);
            }
            assert false : "stuck at " + current + " going " + dir;
            break;
        }
        
        path = Path.create(BLUE, solution.toArray(new Node[solution.size()]));
        mazePanel().addPath(path);
        return current.equals(maze.exit) ? path : null;
    }
}
