/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import org.jrobin.core.RrdException;
import org.jrobin.graph.ChartPanel;
import org.jrobin.graph.RrdGraph;
import org.jrobin.graph.RrdGraphDef;

/**
 *
 * @author root
 */
public class Statistics extends JPanel implements ActionListener {
    
    private final String name = "/home/mkdeniz/tcp.rdd";
    private JButton submit;
    private JCheckBox tBox,uBox,dBox,iBox,hBox;
    private TimeSpinner t1,t2;
    //private RrdGraph graph;
    private JFrame frame;
    JPanel logoPanel = new JPanel();
    JLabel label = new JLabel(); 
    private JPanel panel;
    private Dimension d = this.getSize();
    private ChartPanel p;
    GUIGraphPanel graphPanel;
    
    public GUIGraphPanel get(){
        return this.graphPanel;
    }
    
    public Statistics (JFrame f) throws RrdException, IOException {
        super();
        frame = f;
        panel = this;
        JPanel j1 = new JPanel();
        JPanel j2 = new JPanel();
        j1.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        tBox = new JCheckBox("TCP");
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        j1.add(tBox);
        
        uBox = new JCheckBox("UDP");
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        j1.add(uBox);
        
        dBox = new JCheckBox("DNS");
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 1;
        j1.add(dBox);
        
        iBox = new JCheckBox("ICMP");
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 1;
        j1.add(iBox);
        
        hBox = new JCheckBox("HTTP");
        j1.add(hBox);
        
        t1 = new TimeSpinner();/*
        c.gridx = 2;
        c.gridy = 0;*/
        j1.add(t1);
        t2 = new TimeSpinner();
        /*c.gridx = 2;
        c.gridy = 1;*/
        j1.add(t2);
        
        submit = new JButton("Submit");
        /*c.weightx = 0.0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 2;*/
        j1.add(submit);
        
        add(j1);
        submit.addActionListener(this);
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        try {
            RrdGraphDef graphDef = new RrdGraphDef();
            Date endTime = new Date();
            Date startTime = new Date(endTime.getTime() - 864000 * 10L);
            graphDef.setTimePeriod(startTime, endTime);
            if (tBox.isSelected()){
                graphDef.datasource("itcp", name, "itcp", "AVERAGE");
                graphDef.area("itcp", new Color(0, 0xFF, 0), "in-TCP");
                graphDef.datasource("otcp", name, "otcp", "AVERAGE");
                graphDef.area("otcp", new Color(0, 0xCA, 0), "out-TCP");
            }
            if (uBox.isSelected()) {
                graphDef.datasource("iudp", name, "iudp", "AVERAGE");
                graphDef.area("iudp", Color.YELLOW, "in-UDP");
                graphDef.datasource("oudp", name, "oudp", "AVERAGE");
                graphDef.area("oudp", Color.RED, "out-UDP");
            }
            if (dBox.isSelected()) {
                graphDef.datasource("idns", name, "idns", "AVERAGE");
                graphDef.area("idns", Color.BLUE, "in-DNS");
                graphDef.datasource("odns", name, "odns", "AVERAGE");
                graphDef.area("odns", Color.PINK, "out-DNS");
            }
            if (iBox.isSelected()) {
                graphDef.datasource("icmp", name, "icmp", "AVERAGE");
                graphDef.area("icmp", Color.BLACK, "ICMP");
            }
            if (hBox.isSelected()) {
                graphDef.datasource("ihttp", name, "ihttp", "AVERAGE");
                graphDef.area("ihttp", Color.CYAN, "in-HTTP");
                graphDef.datasource("ohttp", name, "ohttp", "AVERAGE");
                graphDef.area("ohttp", Color.GREEN, "out-HTTP");
            }
            RrdGraph graph = new RrdGraph(graphDef);
            
            graph.saveAsGIF("/home/mkdeniz/1.gif");
            graphPanel = new GUIGraphPanel(graph);
            final JDialog j = new JDialog();
            j.add(graphPanel);
            j.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    Dimension d = j.getContentPane().getSize();
                    graphPanel.setGraphDimension(d);
                }
            });
            j.setSize(350, 350);
            j.setVisible(true);
        } catch (RrdException | IOException ex) {
            Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
