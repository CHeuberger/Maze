package cfh.maze;

import static javax.swing.JOptionPane.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;


public class GUI {
    
    private static final List<Class<? extends Solver>> SOLVERS = Collections.unmodifiableList(Arrays.asList(
            LeftTurnSolver.class
            ));

    public static void main(String[] args) {
        new GUI();
    }
    
    
    private static final String PREF_DIR = "directory";
    
    private final Preferences prefs = Preferences.userNodeForPackage(getClass());
    
    private JFrame frame;
    private MazePanel mazePanel;
    private Maze maze = null;

    
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
        for (Class<? extends Solver> solverClass : SOLVERS) {
            Constructor<? extends Solver> constructor;
            Solver solver;
            try {
                constructor = solverClass.getDeclaredConstructor(MazePanel.class);
                solver = constructor.newInstance(mazePanel);
            } catch (ReflectiveOperationException ex) {
                throw new RuntimeException(ex);
            }
            JMenuItem item = new JMenuItem(solver.name);
            item.setToolTipText(solver.tooltip);
            item.addActionListener(ev -> solver.solve(maze));
            solve.add(item);
        }
        
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(file);
        menuBar.add(solve);
        
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
            showMessageDialog(frame, ex.getMessage(), ex.getClass().getName(), ERROR_MESSAGE);
            return;
        }
        
        mazePanel.setImage(image);
        MazeReader reader = new MazeReader(mazePanel);
        try {
            maze = reader.createMaze(image);
        } catch (MazeException ex) {
            showMessageDialog(frame, ex.getMessage(), ex.getClass().getName(), ERROR_MESSAGE);
        }
    }
    
    private void doQuit(ActionEvent ev) {
        // TODO confirm
        frame.dispose();
    }
}
