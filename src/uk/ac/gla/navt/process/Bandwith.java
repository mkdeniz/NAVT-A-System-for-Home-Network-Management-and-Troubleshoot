/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.gla.navt.process;

import uk.ac.gla.navt.ui.GUIGraphPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jrobin.core.RrdException;
import org.jrobin.core.Util;
import org.jrobin.graph.RrdGraph;
import org.jrobin.graph.RrdGraphDef;

/**
 *
 * @author root
 */
public class Bandwith extends JPanel implements ActionListener {
    
    private final String name = "/home/mkdeniz/tcp.rdd";
    private static final String rrd = "file.rrd";
    private JButton submit;
    private JCheckBox tBox,uBox,dBox,iBox;    
    private Boolean tcp,udp,dns,icmp;
    private static final long START = Util.getTimestamp();
    
    public Bandwith (final JFrame f) throws RrdException {
        super();
        RrdGraphDef gDef = new RrdGraphDef();
        Date endTime = new Date();
        Date startTime = new Date(endTime.getTime() - 600000);
        gDef.setTimePeriod(startTime, endTime);
        //gDef.setTimePeriod(START - 10, START);
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
        final GUIGraphPanel graphPanel = new GUIGraphPanel(graph);
        this.add(graphPanel);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension d = f.getContentPane().getSize();
                graphPanel.setGraphDimension(d);
            }
        });
        this.setSize(f.getContentPane().getSize());
        f.pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
