package cfh.maze;

import static java.awt.event.InputEvent.ALT_DOWN_MASK;
import static java.awt.event.InputEvent.ALT_GRAPH_DOWN_MASK;
import static java.awt.event.InputEvent.BUTTON3_DOWN_MASK;
import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.InputEvent.META_DOWN_MASK;
import static java.awt.event.InputEvent.SHIFT_DOWN_MASK;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JOptionPane;


@SuppressWarnings("serial")
public class MazePanel extends Component {

    private static final int MODS = ALT_DOWN_MASK | ALT_GRAPH_DOWN_MASK | CTRL_DOWN_MASK | SHIFT_DOWN_MASK | META_DOWN_MASK;

    private BufferedImage image = null;
    
    private final List<Path> paths = new ArrayList<>();

    private double x = 0;
    private double y = 0;
    private double zoom = 1;
    
    
    MazePanel() {
        MouseAdapter scrollListener = new MouseAdapter() {
            private Point point = null;
            @Override
            public void mousePressed(MouseEvent ev) {
                if (isScrollButton(ev)) {
                    point = ev.getPoint();
                    point.x -= x;
                    point.y -= y;
                }
            }
            @Override
            public void mouseReleased(MouseEvent ev) {
                point = null;
            }
            @Override
            public void mouseDragged(MouseEvent ev) {
                if (point != null && isScrollButton(ev)) {
                    x = ev.getX() - point.x;
                    y = ev.getY() - point.y;
                    repaint();
                }
            }
            @Override
            public void mouseClicked(MouseEvent ev) {
                if (isScrollResetButton(ev)) {
                    if (image == null) {
                        zoom = 1.0;
                        x = 0.0;
                        y = 0.0;
                    } else {
                        resetZoom();
                    }
                    repaint();
                }
            }
            @Override
            public void mouseWheelMoved(MouseWheelEvent ev) {
                if ((ev.getModifiersEx() & MODS) == 0) {
                    double fx = (ev.getX() - x) / zoom;
                    double fy = (ev.getY() - y) / zoom;
                    zoom *= Math.pow(0.79, ev.getPreciseWheelRotation());
                    if (zoom < 0.1) zoom = 0.1;
                    if (zoom > 50.0) zoom = 50.0;
                    if (0.99 < zoom && zoom < 1.01) zoom = 1.0;
                    x = ev.getX() - fx * zoom;
                    y = ev.getY() - fy * zoom;
                    repaint();
                } else if (isCtrl(ev)) {
                    x -= 10 * ev.getUnitsToScroll();
                    repaint();
                } else if (isShift(ev)) {
                    y -= 10 * ev.getUnitsToScroll();
                    repaint();
                }
            }
        };
        addMouseListener(scrollListener);
        addMouseMotionListener(scrollListener);
        addMouseWheelListener(scrollListener);
    }
    
    void setImage(BufferedImage image) {
        this.image = image;
        paths.clear();
        resetZoom();
    }
    
    public BufferedImage getImage() {
        return image;
    }
    
    void addPath(Path path) {
        paths.add(path);
        repaint();
    }
    
    boolean removePath(Path path) {
        boolean removed = paths.remove(path);
        repaint();
        return removed;
    }
    
    void clearPaths() {
        paths.clear();
        repaint();
    }
    
    void message(String format, Object... args) {
        JOptionPane.showMessageDialog(this, String.format(format, args));

    }

    private void resetZoom() {
        if (image != null) {
            int w = image.getWidth(this);
            int h = image.getHeight(this);
            if (w > 0 && h > 0) {
                double zx = getWidth() / w;
                double zy = getHeight() / h;
                if (zx < zy) {
                    zoom = zx;
                } else {
                    zoom = zy;
                }
                x = (getWidth() - w * zoom) / 2;
                y = (getHeight() - h * zoom) / 2;
            }
        }
        repaint();
    }
    
    @Override
    public void paint(Graphics g) {
        Graphics2D gg = (Graphics2D) g.create();
        try {
            gg.drawString(String.format(Locale.ROOT, "%1.1f,%1.1f  %d", x, y, (int) (100 * zoom)), 4, getHeight()-4);
            
            if (image != null) {
                gg.translate(x, y);
                gg.scale(zoom, zoom);
//                gg.drawImage(image, 0, 0, this);
                
                if (zoom > 2) {
                    gg.setStroke(new BasicStroke(2 / (float) zoom));
                }
                for (Path path : paths) {
                    List<Point> points = path.points();
                    if (!points.isEmpty()) {
                        gg.setColor(path.getColor());
                        if (points.size() == 1) {
                            Point point = points.get(0);
                            gg.fillRect(point.x, point.y, 1, 1);
                        } else {
                            Point last = null;
                            for (Point point : points) {
                                if (last != null) {
                                    gg.draw(new Line2D.Double(last.x+0.5, last.y+0.5, point.x+0.5, point.y+0.5));
                                }
                                last = point;
                            }
                        }
                    }
               }
            }
        } finally {
            gg.dispose();
        }
    }

    private boolean isScrollButton(MouseEvent ev) {
        return isPlain(ev) && isButton3(ev);
    }
    
    private boolean isScrollResetButton(MouseEvent ev) {
        return ev.getClickCount() == 2 && isButton3(ev);
    }

    private static boolean isButton3(MouseEvent ev) {
        return (ev.getButton() == MouseEvent.BUTTON3 || (ev.getModifiersEx() & BUTTON3_DOWN_MASK) != 0);
    }
    
    private static boolean isPlain(MouseEvent ev) {
        return (ev.getModifiersEx() & MODS) == 0;
    }
    
    private static boolean isShift(MouseEvent ev) {
        return (ev.getModifiersEx() & MODS) == SHIFT_DOWN_MASK;
    }
    
    private static boolean isCtrl(MouseEvent ev) {
        return (ev.getModifiersEx() & MODS) == CTRL_DOWN_MASK;
    }
}
