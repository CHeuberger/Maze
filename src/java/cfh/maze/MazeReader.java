package cfh.maze;

import static java.awt.Color.*;
import static java.util.Objects.*;
import static cfh.maze.Direction.*;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;


public class MazeReader {

    public static final boolean PATH = true;
    public static final boolean WALL = false;
    
    private final MazePanel mazePanel;
    
    private Map<Node, Node> nodes;
    
    MazeReader(MazePanel mazePanel) {
        this.mazePanel = requireNonNull(mazePanel);
    }
    
    Maze createMaze(BufferedImage image) throws MazeException {
        int width = image.getWidth();
        int height = image.getHeight();
        if (width < 3 || height < 3)
            throw new MazeException("image to small: %d*%d", width, height);
        boolean[][] data = new boolean[height][width];
        
        int emptycount = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x ++) {
                if ((image.getRGB(x, y) & 0x00FFFFFF) != 0) {
                    data[y][x] = MazeReader.PATH;
                    emptycount += 1;
                }
            }
        }
        
        Node entry = findGate(data, 0, "entry");
        Node exit = findGate(data, height-1, "exit");
        
        for (int y = 1; y < height-1; y++) {
            if (data[y][0] == MazeReader.PATH)
                throw new MazeException("missing wall at (%d,0)", y);
            if (data[y][width-1] == MazeReader.PATH)
                throw new MazeException("missing wall at (%d,%d)", y, width-1);
        }
        
        mazePanel.addPath(Path.create(BLUE, entry));
        mazePanel.addPath(Path.create(GREEN, exit));
        
        nodes = new HashMap<>();
        nodes.put(entry, entry);
        nodes.put(exit, exit);
        for (int y = 1; y < height-1; y++) {
            for (int x = 1; x < width-1; x++) {
                if (data[y][x] == MazeReader.PATH) {
                    if (!(data[y][x-1] == MazeReader.PATH && data[y][x+1] == MazeReader.PATH && data[y-1][x] == MazeReader.WALL && data[y+1][x] == MazeReader.WALL)
                        && 
                        !(data[y][x-1] == MazeReader.WALL && data[y][x+1] == MazeReader.WALL && data[y-1][x] == MazeReader.PATH && data[y+1][x] == MazeReader.PATH)
                       ) {
                        Node node = new Node(x, y);
                        nodes.put(node, node);
                        mazePanel.addPath(Path.create(YELLOW, node));
                    }
                }
            }
        }
        
        for (Node node : nodes.values()) {
            if (node.equals(exit))
                continue;
            Node next;
            next = findNextNode(data, node, SOUTH);
            if (next != null) {
                node.setNeighbour(SOUTH, next);
                next.setNeighbour(SOUTH.back(), node);
                mazePanel.addPath(Path.create(CYAN, node, next));
            }
            next = findNextNode(data, node, EAST);
            if (next != null) {
                node.setNeighbour(EAST, next);
                next.setNeighbour(EAST.back(), node);
                mazePanel.addPath(Path.create(CYAN, node, next));
            }
        }
        
        mazePanel.message(
                "Size: %d x %d\n"
                + "Entry: %s\n"
                + "Exit: %s\n"
                + "Empty: %d\n"
                + "Nodes: %d",
                width, height,
                entry,
                exit,
                emptycount,
                nodes.size()
                );
        
        return new Maze(nodes.values(), entry, exit);
    }
    
    private Node findGate(boolean[][] data, int row, String name) throws MazeException {
        Node gate = null;
        for (int x = 1; x < data[row].length-1; x++) {
            if (data[row][x] == MazeReader.PATH) {
                if (gate != null)
                    throw new MazeException("second %s at (%d,%d)", name, x, row);
                gate = new Node(x, row);
            }
        }
        if (gate == null)
            throw new MazeException("no %s", name);
        return gate;
    }
    
    private Node findNextNode(boolean[][] data, Node from, Direction dir) throws MazeException {
        int x = dir.nextX(from.x);
        int y = dir.nextY(from.y);
        if (data[y][x] == MazeReader.WALL)
            return null;
        while (0 <= y && y < data.length && 0 <= x && x < data[y].length) {
            if (data[y][x] == MazeReader.WALL) {
                mazePanel.addPath(new Path(RED, x, y));
                throw new MazeException("unexpected wall at (%d,%d) going %s", x, y, dir);
            }
            Node node = nodes.get(new Node(x, y));
            if (node != null)
                return node;
            x = dir.nextX(x);
            y = dir.nextY(y);
        }
        throw new MazeException("unexpected exit at (%d,%d) going %s", x, y, dir);
    }
}
