package uk.ac.gla.navt.ui;

import java.awt.Color;
import java.io.IOException;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFrame;
import org.jrobin.core.RrdException;
import org.jrobin.core.Util;
import org.jrobin.graph.RrdGraph;
import org.jrobin.graph.RrdGraphDef;

/**
 *
 * @author root
 */
public class Analyse {
    private static final String name = "/home/mkdeniz/tcp.rdd";
    private static final String rrd = "file.rrd";
    private static final long START = Util.getTimestamp();
    private static Date end = new Date(START);
    static int t12 = 0;
    
    private RrdGraph prepareFrame() throws RrdException, IOException {
        RrdGraphDef gDef = new RrdGraphDef();
        gDef.setTimePeriod(START - 10, START);
        gDef.setImageBorder(Color.WHITE, 0);
        gDef.setTitle("Bandiwth");
        gDef.setVerticalLabel("Speed");
        gDef.setTimeAxisLabel("Time");
        gDef.datasource("a", rrd, "a", "AVERAGE", "MEMORY");
        gDef.datasource( "avg", "a", "AVERAGE");
        gDef.line("a", Color.decode("0xb6e4"), "Real");
        gDef.line("avg", Color.RED,  "Average@l");
        gDef.gprint("a", "MIN", "min = @2 kb/s@l");
        gDef.gprint("a", "MAX", "max = @2 kb/s@l");
        gDef.gprint("a", "AVERAGE", "avg = @2 kb/s");
        gDef.time("@l@lTime period: @t", "MMM dd, yyyy    HH:mm:ss", START);
        gDef.time("to  @t@c", "HH:mm:ss");
        RrdGraph graph = new RrdGraph(gDef);
        return graph;
    
    }
    public JFrame createAndShowGUI() throws RrdException, IOException {
        GUIGraphPanel graphPanel = new GUIGraphPanel(this.prepareFrame());
        JFrame frame = new JFrame("Monitor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(graphPanel);
        frame.pack();
        frame.setBounds(100, 100, 500, 400);
        frame.setVisible(true);
        return frame;
    }
}
