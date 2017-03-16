package cfh.maze;

import static java.util.Objects.*;


public abstract class Solver {

    private MazePanel mazePanel;
    final String name;
    final String tooltip;
    
    
    protected Solver(String name, String tooltip) {
        this.name = requireNonNull(name);
        this.tooltip = requireNonNull(tooltip);
    }
    
    void mazePanel(MazePanel mazePanel) {
        assert this.mazePanel == null;
        this.mazePanel = requireNonNull(mazePanel);
    }
    
    protected MazePanel mazePanel() {
        return mazePanel;
    }
    
    final boolean solve(Maze maze) {
        assert mazePanel != null;
        
        boolean solved;
        if (maze == null) {
            mazePanel.message("first load a maze");
            solved = false;
        } else {
            long time = System.nanoTime();
            solved = solve0(maze);
            time = System.nanoTime() - time;
            mazePanel.message("%s in %.1f ms", solved ? "solved" : "not solved", time / 1e6);
        }
        return solved;
    }
    
    protected abstract boolean solve0(Maze maze);
}
