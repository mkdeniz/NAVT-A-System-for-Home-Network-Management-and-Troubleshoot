
package uk.ac.gla.navt.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
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
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import org.jrobin.core.RrdException;
import org.jrobin.graph.ChartPanel;
import org.jrobin.graph.RrdGraph;
import org.jrobin.graph.RrdGraphDef;

/**
 *
 * @author root
 */
public class Statistics extends JPanel implements ActionListener {
   
    protected JLabel Overall;
    protected String name = "/home/mkdeniz/tcp.rdd";
    protected JButton submit;
    protected JCheckBox tBox,uBox,dBox,iBox,hBox;
    protected GUIGraphPanel graphPanel;
    
    public Statistics (JFrame f,String result) throws RrdException, IOException {
        super(new GridLayout(1,1));
        JPanel j1 =  new JPanel(new GridLayout(3,3));
        j1.setSize(f.getWidth()/2, 100);
        j1.setBorder(new TitledBorder("Overview"));
        
        JLabel DNS;
        if (Integer.parseInt(result) > 0){
            ImageIcon img3 = new ImageIcon("/home/mkdeniz/Dropbox/University/Year4/Project/Images/green.png");
            DNS = new JLabel("DNS");
            DNS.setIcon(img3);
        }
        else {
            ImageIcon img3 = new ImageIcon("/home/mkdeniz/Dropbox/University/Year4/Project/Images/red.png");
            DNS = new JLabel("DNS");
            DNS.setIcon(img3);
        }
        
        JLabel Overall;
        if (Integer.parseInt(result) > 0){
            ImageIcon img3 = new ImageIcon("/home/mkdeniz/Dropbox/University/Year4/Project/Images/green.png");
            Overall = new JLabel("No Problems Detected");
            Overall.setIcon(img3);
        }
        else {
            ImageIcon img3 = new ImageIcon("/home/mkdeniz/Dropbox/University/Year4/Project/Images/red.png");
            Overall = new JLabel("Problem Detected");
            Overall.setIcon(img3);
        }
        
        j1.add(DNS);
        j1.add(Overall);
        
        add(j1);
        this.setSize(300, 300);
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

