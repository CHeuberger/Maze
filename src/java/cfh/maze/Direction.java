package cfh.maze;

public enum Direction {

    NORTH,
    EAST,
    SOUTH,
    WEST,
    ;
    
    public static int size() {
        return values().length;
    }
    
    public Direction back() {
        switch (this) {
            case NORTH: return SOUTH;
            case EAST:  return WEST;
            case SOUTH: return NORTH;
            case WEST:  return EAST;
            default:    throw new RuntimeException("unimplemented: " + this);
        }
    }
    
    public Direction left() {
        switch (this) {
            case NORTH: return WEST;
            case EAST:  return NORTH;
            case SOUTH: return EAST;
            case WEST:  return SOUTH;
            default:    throw new RuntimeException("unimplemented: " + this);
        }
    }
    
    public Direction right() {
        switch (this) {
            case NORTH: return EAST;
            case EAST:  return SOUTH;
            case SOUTH: return WEST;
            case WEST:  return NORTH;
            default:    throw new RuntimeException("unimplemented: " + this);
        }
    }
    
    public int nextX(int x) {
        switch (this) {
            case NORTH: return x;
            case EAST:  return x+1;
            case SOUTH: return x;
            case WEST:  return x-1;
            default:    throw new RuntimeException("unimplemented: " + this);
        }
    }
    
    public int nextY(int y) {
        switch (this) {
            case NORTH: return y-1;
            case EAST:  return y;
            case SOUTH: return y+1;
            case WEST:  return y;
            default:    throw new RuntimeException("unimplemented: " + this);
        }
    }
}
