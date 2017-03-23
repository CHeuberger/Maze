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
    
    protected abstract Path solve(Maze maze);
}
