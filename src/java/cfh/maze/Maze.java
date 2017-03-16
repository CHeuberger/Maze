package cfh.maze;

import static java.util.Objects.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class Maze {
    
    final Map<Node, Node> nodes;
    final Node entry;
    final Node exit;
    
    
    Maze(Collection<Node> nodes, Node entry, Node exit) {
        Map<Node, Node> map = new HashMap<>();
        for (Node node : nodes) {
            Node old = map.put(node, node);
            assert old == null : old;
        }
        this.nodes = Collections.unmodifiableMap(map);
        this.entry = requireNonNull(entry);
        this.exit = requireNonNull(exit);
    }
}
