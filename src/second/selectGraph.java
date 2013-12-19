/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package second;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import org.jrobin.core.RrdException;
import org.jrobin.graph.RrdGraph;
import org.jrobin.graph.RrdGraphDef;

/**
 *
 * @author root
 */
public class selectGraph extends JFrame{
    private String name = "/home/mkdeniz/tcp.rdd";
    private RrdGraph graph;
    private JDialog f;
    private GUIGraphPanel g;
    private JDialog frame;
    private GUIGraphPanel graphPanel;
    
    public selectGraph() throws RrdException, IOException{
        super();
        RrdGraphDef graphDef = new RrdGraphDef();
        Date endTime = new Date();
        Date startTime = new Date(endTime.getTime() - 86400);
        graphDef.setTimePeriod(startTime, endTime);
        //if (tcp == true){
        graphDef.datasource("tcp", name, "tcp", "AVERAGE");
        graphDef.area("tcp", new Color(0, 0xFF, 0), "TCP");
        //}
        //if (udp == true) {
        graphDef.datasource("udp", name, "udp", "AVERAGE");
        graphDef.area("udp", new Color(0, 0, 0xFF), "UDP");
        //}
        graphDef.datasource("dns", name, "dns", "AVERAGE");
        graphDef.area("dns", new Color(0xFF, 0, 0), "DNS");
        
        RrdGraph graph = new RrdGraph(graphDef);

        // create swing components
        graphPanel = new GUIGraphPanel(graph);
        this.getContentPane().add(graph.getChartPanel());
        this.pack();
        /*frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                Dimension d = frame.getContentPane().getSize();
                graphPanel.setGraphDimension(d);
            }
        });*/
        this.setBounds(100, 100, 500, 400);
        this.setVisible(true);
}
}
