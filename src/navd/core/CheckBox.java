package navd.core;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JButton;
import org.jrobin.core.RrdException;
import org.jrobin.graph.RrdGraph;
import org.jrobin.graph.RrdGraphDef;


public class CheckBox extends javax.swing.JPanel implements ActionListener {

    private static String name = "/home/mkdeniz/tcp.rdd";
    private static Boolean tcp,udp = false;
    private JButton btnButton;
    private JCheckBox chkCheckbox,chkCheckbox2;              

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.add(new CheckBox());
        frame.pack();
        frame.setVisible(true);
    }
    

    public CheckBox() {
        btnButton = new JButton();
        chkCheckbox = new JCheckBox();
        chkCheckbox2 = new JCheckBox();
        btnButton.setText("Continue");

        chkCheckbox.setText("TCP?");
        chkCheckbox2.setText("UDP?");
        

        add(chkCheckbox2);
        add(chkCheckbox);
        add(btnButton);btnButton.addActionListener(this);
        
    }
    
    
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnButton)
        {
            System.out.print("kml");
            tcp = chkCheckbox.isSelected();
            udp = chkCheckbox2.isSelected();
            //if (tcp || udp) {
            try {
                doGraph();
            } catch (    RrdException | IOException ex) {
                Logger.getLogger(CheckBox.class.getName()).log(Level.SEVERE, null, ex);
            }//}
        }
        
        
    }
    
    public static void doGraph() throws RrdException, IOException {
        RrdGraphDef graphDef = new RrdGraphDef();
        Date endTime = new Date();
        Date startTime = new Date(endTime.getTime() - 86400 * 10L);
        graphDef.setTimePeriod(startTime, endTime);
        if (tcp == true){
            graphDef.datasource("tcp", name, "tcp", "AVERAGE");
            graphDef.area("tcp", new Color(0, 0xFF, 0), "TCP");
        }
        if (udp == true) {
            graphDef.datasource("udp", name, "udp", "AVERAGE");
            graphDef.area("udp", new Color(0, 0, 0xFF), "UDP");
        }
        graphDef.datasource("dns", name, "dns", "AVERAGE");
        graphDef.area("dns", new Color(0xFF, 0, 0), "DNS");
        
        RrdGraph graph = new RrdGraph(graphDef);
        graph.saveAsGIF("/home/mkdeniz/1.gif");
    }
}