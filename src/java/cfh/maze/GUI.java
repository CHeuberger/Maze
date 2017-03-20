package cfh.maze;

import static javax.swing.JOptionPane.*;

import static cfh.maze.Direction.*;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import cfh.Dot;


public class GUI {
    
    private static final List<Solver> SOLVERS = Collections.unmodifiableList(Arrays.asList(
            new TurnSolver(false),
            new TurnSolver(true)
            ));

    public static void main(String[] args) {
        new GUI();
    }
    
    
    private static final String PREF_DIR = "directory";
    
    private final Preferences prefs = Preferences.userNodeForPackage(getClass());
    
    private JFrame frame;
    private MazePanel mazePanel;
    private Maze maze = null;
    private Path path = null;

    private String name = null;
    
    private GUI() {
        SwingUtilities.invokeLater(this::initGUI);
    }
    
    private void initGUI() {
        mazePanel = new MazePanel();
        
        JMenuItem open = new JMenuItem("Open");
        open.addActionListener(this::doOpen);
        
        JMenuItem quit = new JMenuItem("Quit");
        quit.addActionListener(this::doQuit);
        
        JMenu file = new JMenu("File");
        file.add(open);
        file.addSeparator();
        file.add(quit);
        
        JMenu solve = new JMenu("Solve");
        for (Solver solver : SOLVERS) {
            solver.mazePanel(mazePanel);
            JMenuItem item = new JMenuItem(solver.name);
            item.setToolTipText(solver.tooltip);
            item.addActionListener(ev -> doSolve(solver, ev));
            solve.add(item);
        }
        
        JMenuItem tree = new JMenuItem("Tree");
        tree.addActionListener(this::doTreeGraph);
        
        JMenu dot = new JMenu("Graphviz");
        dot.add(tree);
        
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(file);
        menuBar.add(solve);
        menuBar.add(dot);
        
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setJMenuBar(menuBar);
        frame.add(mazePanel, BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void doOpen(ActionEvent ev) {
        String dir = prefs.get(PREF_DIR, ".");
        JFileChooser chooser = new JFileChooser(dir);
        if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION)
            return;
        
        dir = chooser.getCurrentDirectory().getAbsolutePath();
        prefs.put(PREF_DIR, dir);
        
        File file = chooser.getSelectedFile();
        BufferedImage image;
        try {
            image = ImageIO.read(file);
            if (image == null) {
                showMessageDialog(frame, new Object[] { "unable to read image", file }, "Error reading image", ERROR_MESSAGE);
                return;
            }
        } catch (IOException ex) {
            showException(ex);
            return;
        }
        name = file.getName();
        
        mazePanel.setImage(image);
        MazeReader reader = new MazeReader(mazePanel);
        try {
            maze = reader.createMaze(image);
            path = null;
        } catch (MazeException ex) {
            showException(ex);
            return;
        }
    }

    private void doSolve(Solver solver, ActionEvent ev) {
        long time = System.nanoTime();
        path = solver.solve(maze);
        time = System.nanoTime() - time;
        if (path != null) {
            double dist = 0;
            Point last = null;
            for (Point point : path.points()) {
                if (last != null) {
                    dist += point.distance(last);
                }
                last = point;
            }
            mazePanel.message("solved in %.1f ms, distance: %.0f ", time / 1e6, dist);
        } else {
            mazePanel.message("not solved in %.1f ms", time / 1e6);
        }
    }
    
    private void doTreeGraph(ActionEvent ev) {
        StringBuilder dot = new StringBuilder();
        dot.append("graph {\n");
        dot.append("  ").append(fmtNode(maze.entry)).append(" [ shape=invhouse ];\n");
        dot.append("  ").append(fmtNode(maze.exit)).append(" [ shape=house ];\n");
        for (Node node : maze.nodes.values()) {
            for (Direction dir : Arrays.asList(EAST, SOUTH)) {
                Node next = node.neighbour(dir);
                if (next != null) {
                    dot.append("  ").append(fmtNode(node)).append(" -- ").append(fmtNode(next));
                    if (path != null) {
                        boolean found = false;
                        for (Point point : path.points()) {
                            if ((point.x == node.x && point.y == node.y) || (point.x == next.x && point.y == next.y)) {
                                if (found) {  // second time
                                    dot.append(" [style=bold,color=red]");
                                }
                                found = ! found;
                            }
                        }
                    }
                    dot.append(";\n");
                }
            }
        }
        dot.append("}\n");
        byte[] bytes = dot.toString().getBytes(StandardCharsets.UTF_8);
        InputStream in = new ByteArrayInputStream(bytes);
        
        try {
            java.nio.file.Path svgPath = Files.createTempFile(name, ".svg");
            String filename = svgPath.getFileName().toString();
            java.nio.file.Path dotPath = svgPath.resolveSibling(filename.substring(0, filename.length()-4) + ".dot");
            Files.write(dotPath, bytes);
            System.out.println(dotPath);
            BufferedImage image = Dot.toImage(in, "png");
            JLabel label = new JLabel(new ImageIcon(image));
            JDialog dialog = new JDialog(frame, true);
            dialog.setResizable(true);
            dialog.setUndecorated(false);
            dialog.add(new JScrollPane(label));
            dialog.setSize(frame.getSize());
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
        } catch (Throwable ex) {
            System.err.printf("==========  DOT  ==========\n"
                    + "%s\n"
                    + "===========================\n",
                    dot);
            showException(ex);
            return;
        }
    }
    
    private void doQuit(ActionEvent ev) {
        // TODO confirm
        frame.dispose();
    }
    
    private String fmtNode(Node node) {
        return "\"(" + node.x + "-" + node.y + ")\"";
    }
    
    private void showException(Throwable ex) {
        ex.printStackTrace();
        showMessageDialog(frame, ex.getMessage(), ex.getClass().getName(), ERROR_MESSAGE);
    }
}
