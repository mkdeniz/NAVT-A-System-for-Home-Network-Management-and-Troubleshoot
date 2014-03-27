
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

/**
 *
 * @author root
 */
public class Classification extends JPanel implements ActionListener {
    
    protected String name = "classification.rrd";
    protected JButton submit;
    protected JCheckBox tBox,uBox,dBox,iBox,hBox;
    protected JFrame frame;
    protected JPanel panel;
    protected Dimension d = this.getSize();
    protected ChartPanel p;
    protected GUIGraphPanel graphPanel;
    
    public Classification (JFrame f) throws RrdException, IOException {
        super(new GridLayout(1,2));
        JPanel j1 =  new JPanel(new GridLayout(2,3));
        j1.setSize(f.getWidth()/2, 100);
        JPanel j2 = new JPanel();
        j1.setBorder(new TitledBorder("Options"));
        
        tBox = new JCheckBox("WEB");
        j1.add(tBox);
        
        uBox = new JCheckBox("BULK");
        j1.add(uBox);
        
        dBox = new JCheckBox("SERVICES");
        j1.add(dBox);
        
        iBox = new JCheckBox("P2P");
        j1.add(iBox);
        
        hBox = new JCheckBox("OTHER");
        j1.add(hBox);
        
        submit = new JButton("Submit");
        j2.add(submit);
        
        add(j1);
        add(j2);
        submit.addActionListener(this);
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
                graphDef.datasource("WEB", name, "WEB", "AVERAGE");
                graphDef.area("WEB", new Color(0, 0xFF, 0), "WEB");
            }
            if (uBox.isSelected()) {
                graphDef.datasource("BULK", name, "BULK", "AVERAGE");
                graphDef.area("BULK", Color.YELLOW, "BULK");
            }
            if (dBox.isSelected()) {
                graphDef.datasource("SERVICE", name, "SERVICE", "AVERAGE");
                graphDef.area("SERVICE", Color.BLUE, "SERVICE");
            }
            if (iBox.isSelected()) {
                graphDef.datasource("P2P", name, "P2P", "AVERAGE");
                graphDef.area("P2P", Color.BLACK, "P2P");
            }
            if (hBox.isSelected()) {
                graphDef.datasource("ihttp", name, "ihttp", "AVERAGE");
                graphDef.area("ihttp", Color.CYAN, "in-HTTP");
                graphDef.datasource("ohttp", name, "ohttp", "AVERAGE");
                graphDef.area("ohttp", Color.GREEN, "out-HTTP");
            }
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
        } catch (RrdException ex) {
            Logger.getLogger(Classification.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Classification.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}