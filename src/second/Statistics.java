/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package second;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import navd.core.Speed;
import navd.ui.SplashScreen;
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
    private Dimension d = this.getSize();
    private ChartPanel p;
    JPanel logoPanel;
    GUIGraphPanel graphPanel;
    
    public GUIGraphPanel get(){
        return this.graphPanel;
    }
    
    public Statistics (JFrame f) throws RrdException, IOException {
        super();
        frame = f;
        JPanel j1 = new JPanel();
        JPanel j2 = new JPanel();
        //j1.setLayout(new GridBagLayout());
        //GridBagConstraints c = new GridBagConstraints();
        
        tBox = new JCheckBox("TCP");
        /*c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;*/
        j1.add(tBox);
        
        uBox = new JCheckBox("UDP");
        /*c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;*/
        j1.add(uBox);
        
        dBox = new JCheckBox("DNS");
        /*c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 1;*/
        j1.add(dBox);
        
        iBox = new JCheckBox("ICMP");
        /*c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 1;*/
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
            Date startTime = new Date(endTime.getTime() - 86400 * 10L);
            graphDef.setTimePeriod(startTime, endTime);
            if (tBox.isSelected()){
                graphDef.datasource("tcp", name, "tcp", "AVERAGE");
                graphDef.area("tcp", new Color(0, 0xFF, 0), "TCP");
            }
            if (uBox.isSelected()) {
                graphDef.datasource("udp", name, "udp", "AVERAGE");
                graphDef.area("udp", new Color(0, 0, 0xFF), "UDP");
            }
            if (dBox.isSelected()) {
                graphDef.datasource("dns", name, "dns", "AVERAGE");
                graphDef.area("dns", new Color(0xFF, 0xAA, 0), "DNS");
            }
            if (iBox.isSelected()) {
                graphDef.datasource("icmp", name, "icmp", "AVERAGE");
                graphDef.area("icmp", new Color(0xFF, 0, 0), "ICMP");
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
