/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package second;

import second.GUIGraphPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerModel;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import navd.core.CheckBox;
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
    private JCheckBox tBox,uBox,dBox,iBox;    
    private Boolean tcp=false,udp =false, dns = false,icmp = false;  
    private TimeSpinner t1,t2;
    private RrdGraph graph;
    private JFrame frame;
    private Dimension d = this.getSize();
    private ChartPanel p;
    JPanel logoPanel;
    GUIGraphPanel graphPanel;
    private selectGraph g;
    
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
            g = new selectGraph();
        } catch (RrdException ex) {
            Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
