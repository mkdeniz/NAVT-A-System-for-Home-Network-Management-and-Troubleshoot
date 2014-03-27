package uk.ac.gla.navt.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextField;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

/**
 *
 * @author root
 */
public class Welcome extends JDialog implements ActionListener{
    
    protected static final String rrd = "file.rrd";
    protected JComboBox List;
    protected JButton button;
    protected JTextField field;
    protected int n;
    protected String st;
    protected List<PcapIf> alldevs;
    protected Vector<String> deviceList;
    protected StringBuilder errbuf;
    
    
    public Welcome(){
        super();
        alldevs = new ArrayList<>();
        errbuf = new StringBuilder();
        Pcap.findAllDevs(alldevs, errbuf); 
        
        deviceList = new Vector<>();
        for (PcapIf device : alldevs) {
            deviceList.add(device.getName());
        }  
        List = new JComboBox((Vector) deviceList);
        
        button = new JButton("GO");
        button.addActionListener(this);
        
        field = new JTextField();
        
        add(List, BorderLayout.PAGE_START);
        add(button, BorderLayout.PAGE_END);
        add(field, BorderLayout.CENTER);
        setVisible(true);
        setSize(250, 100);
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        st = field.getText();
        n = List.getSelectedIndex();
        String[] host;
        host = st.split("\\.");
        if (st.length() > 2) {
            this.dispose();
            Thread thread = new Thread() {
                @Override
                public void run() {
                    MainFrame mainFrame = new MainFrame(n,st);
                }
            };
            thread.start();
        }
    }
    
    public static void main(String[] args) {
        Welcome w = new Welcome();
    }
                
}
    

