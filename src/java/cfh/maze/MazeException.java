package cfh.maze;

@SuppressWarnings("serial")
public class MazeException extends Exception {

    public MazeException() {
        super();
    }

    public MazeException(Throwable cause, String format, Object... args) {
        super(String.format(format, args), cause);
    }

    
    public MazeException(String format, Object... args) {
        super(String.format(format, args));
    }

    public MazeException(Throwable cause) {
        super(cause);
    }
}
