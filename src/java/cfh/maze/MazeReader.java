package cfh.maze;

import static java.awt.Color.*;
import static cfh.maze.Direction.*;
import static cfh.maze.Maze.*;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;


public class MazeReader {

    private final MazePanel mazePanel;
    private final int width;
    private final int height;
    private final boolean[][] data;
    
    private Map<Node, Node> nodes;
    
    
    MazeReader(MazePanel mazePanel) throws MazeException {
        this.mazePanel = mazePanel;
        
        BufferedImage image = mazePanel.getImage();
        width = image.getWidth();
        height = image.getHeight();
        if (width < 3 || height < 3)
            throw new MazeException("image to small: %d*%d", width, height);
        data = new boolean[height][width];
        
        int emptycount = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x ++) {
                if ((image.getRGB(x, y) & 0x00FFFFFF) != 0) {
                    data[y][x] = PATH;
                    emptycount += 1;
                }
            }
        }
        
        Node entry = findGate("entry", 0);
        Node exit = findGate("exit", height-1);
        
        for (int y = 1; y < height-1; y++) {
            if (data[y][0] == PATH)
                throw new MazeException("missing wall at (%d,0)", y);
            if (data[y][width-1] == PATH)
                throw new MazeException("missing wall at (%d,%d)", y, width-1);
        }
        
        mazePanel.addPath(Path.create(BLUE, entry));
        mazePanel.addPath(Path.create(GREEN, exit));
        
        nodes = new HashMap<>();
        nodes.put(entry, entry);
        nodes.put(exit, exit);
        for (int y = 1; y < height-1; y++) {
            for (int x = 1; x < width-1; x++) {
                if (data[y][x] == PATH) {
                    if (!(data[y][x-1] == PATH && data[y][x+1] == PATH && data[y-1][x] == WALL && data[y+1][x] == WALL)
                        && 
                        !(data[y][x-1] == WALL && data[y][x+1] == WALL && data[y-1][x] == PATH && data[y+1][x] == PATH)
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
            next = findNextNode(node, SOUTH);
            if (next != null) {
                node.setNeighbour(SOUTH, next);
                next.setNeighbour(SOUTH.back(), node);
                mazePanel.addPath(Path.create(CYAN, node, next));
            }
            next = findNextNode(node, EAST);
            if (next != null) {
                node.setNeighbour(EAST, next);
                next.setNeighbour(EAST.back(), node);
                mazePanel.addPath(Path.create(CYAN, node, next));
            }
        }
        
        String message = String.format(
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
        JOptionPane.showMessageDialog(mazePanel, message);
    }
    
    private Node findGate(String name, int row) throws MazeException {
        Node gate = null;
        for (int x = 1; x < width-1; x++) {
            if (data[row][x] == PATH) {
                if (gate != null)
                    throw new MazeException("second %s at (%d,%d)", name, x, row);
                gate = new Node(x, row);
            }
        }
        if (gate == null)
            throw new MazeException("no %s", name);
        return gate;
    }
    
    private Node findNextNode(Node from, Direction dir) throws MazeException {
        int x = dir.nextX(from.x);
        int y = dir.nextY(from.y);
        if (data[y][x] == WALL)
            return null;
        while (0 <= x && x < width && 0 <= y && y < height) {
            if (data[y][x] == WALL) {
                mazePanel.addPath(new Path(RED, x, y));  // TODO {TEST}
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
