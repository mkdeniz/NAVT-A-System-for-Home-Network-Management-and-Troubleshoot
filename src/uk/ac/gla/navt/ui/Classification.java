package uk.ac.gla.navt.ui;

/**
* A Panel to provide classification results.
* 
* @Author Mehmet Kemal Deniz
* @Date 27/03/2014
*/

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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import org.jrobin.core.RrdException;
import org.jrobin.graph.ChartPanel;
import org.jrobin.graph.RrdGraph;
import org.jrobin.graph.RrdGraphDef;

public class Classification extends JPanel implements ActionListener {
    
    protected String name = "classification.rrd";
    protected JCheckBox wBox,bBox,sBox,pBox,oBox;
    protected Dimension d = this.getSize();
    protected GUIGraphPanel graphPanel;
    protected JButton submit;
    protected JFrame frame;
    protected JPanel panel;
    protected ChartPanel p;
    
    
    /**
     * 
     * Default Constructor
     * 
     * @param f main frame
     * 
     * @throws RrdException
     * @throws IOException
     * 
     */
    public Classification (JFrame f) throws RrdException, IOException {
        super(new GridLayout(1,2));
        JPanel j1 =  new JPanel(new GridLayout(2,3));
        j1.setBorder(new TitledBorder("Options"));
        j1.setSize(f.getWidth()/2, 100);
        JPanel j2 = new JPanel();
        
        //Check Boxes
        wBox = new JCheckBox("WEB");
        sBox = new JCheckBox("SERVICES");
        pBox = new JCheckBox("P2P");
        bBox = new JCheckBox("BULK");
        oBox = new JCheckBox("OTHER");
        j1.add(bBox);
        j1.add(wBox);
        j1.add(sBox);
        j1.add(pBox);
        j1.add(oBox);
        
        //Submit Button
        submit = new JButton("Submit");
        j2.add(submit);
        
        //Add button and checkBoxes to panel
        add(j1);
        add(j2);
        submit.addActionListener(this);
        this.setSize(300, 300);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        try {
            //Get the time interval of 5 minutes
            Date endTime = new Date();
            Date startTime = new Date(endTime.getTime() - 300000);
            
            //Create graph according to the checkboxes
            RrdGraphDef graphDef = new RrdGraphDef();
            graphDef.setTimePeriod(startTime, endTime);
            if (wBox.isSelected()){
                graphDef.datasource("WEB", name, "WEB", "AVERAGE");
                graphDef.area("WEB", new Color(0, 0xFF, 0), "WEB");
            }
            if (bBox.isSelected()) {
                graphDef.datasource("BULK", name, "BULK", "AVERAGE");
                graphDef.area("BULK", Color.YELLOW, "BULK");
            }
            if (sBox.isSelected()) {
                graphDef.datasource("SERVICE", name, "SERVICE", "AVERAGE");
                graphDef.area("SERVICE", Color.BLUE, "SERVICE");
            }
            if (pBox.isSelected()) {
                graphDef.datasource("P2P", name, "P2P", "AVERAGE");
                graphDef.area("P2P", Color.BLACK, "P2P");
            }
            if (oBox.isSelected()) {
                graphDef.datasource("OTHER", name, "OTHER", "AVERAGE");
                graphDef.area("OTHER", Color.BLACK, "OTHER");
            }
            
            //Create graph pop-screen 
            RrdGraph graph = new RrdGraph(graphDef);
            graph.saveAsGIF("Classification.gif");
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
            Logger.getLogger(Classification.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
