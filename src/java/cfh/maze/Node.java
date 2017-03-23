package cfh.maze;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class Node {
    
    public final int x;
    public final int y;
    
    private Node[] neighbours = new Node[Direction.size()];
    
    
    Node(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    void setNeighbour(Direction dir, Node node) {
        neighbours[dir.ordinal()] = node;
    }
    
    public Node neighbour(Direction dir) {
        return neighbours[dir.ordinal()];
    }
    
    public Collection<Node> neighbours() {
        return Arrays.stream(neighbours).filter(n -> n != null).collect(Collectors.toList());
    }
    
    @Override
    public int hashCode() {
        return 39 * x + 17 * y;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node other = (Node) obj;
            return other.x == this.x && other.y == this.y;
        }
        return super.equals(obj);
    }
    
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
