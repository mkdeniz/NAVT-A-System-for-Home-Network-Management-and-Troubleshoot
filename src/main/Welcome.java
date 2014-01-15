/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import navd.ui.SplashScreen;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jrobin.core.RrdBackendFactory;
import org.jrobin.core.RrdDb;
import org.jrobin.core.RrdDef;
import org.jrobin.core.RrdException;

/**
 *
 * @author root
 */
public class Welcome extends JDialog implements ActionListener{
    
    private static final String rrd = "file.rrd";
    private JComboBox petList;
    private JButton button;
    private JTextField field;
    int n;
    String st;
    private List<PcapIf> alldevs;
    
    
    
    
    
    
    
    
    
    public Welcome(){
        super();
        alldevs = new ArrayList<>(); // Will be filled with NICs  
        StringBuilder errbuf = new StringBuilder(); // For any error msgs  
        
        int r = Pcap.findAllDevs(alldevs, errbuf);  
        if (alldevs.isEmpty()) {  
            System.err.printf("Can't read list of devices, error is %s", errbuf.toString());  
            return;  
        }
        
        Vector<String> petStrings = new Vector<String>();
        
        int i = 0;  
        for (PcapIf device : alldevs) {  
            String description =  
                (device.getDescription() != null) ? device.getDescription()  
                    : "No description available";  
            petStrings.add(device.getName());
            //System.out.printf("#%d: %s [%s]\n", i++, device.getName(), description); 
        }  
        
        petList = new JComboBox((Vector) petStrings);
        button = new JButton("GO");
        field = new JTextField();
        this.add(petList, BorderLayout.PAGE_START);
        this.add(button, BorderLayout.PAGE_END);
        this.add(field, BorderLayout.CENTER);
        this.setVisible(true);
        this.setSize(250, 100);
        button.addActionListener(this);
     
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        st = field.getText();
        n = petList.getSelectedIndex();
        String[] host = st.split("\\.");
        if (st.length()>2) {
            this.dispose();
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {

                        SplashScreen s = new SplashScreen();
                        Thread.sleep(500);
                        s.dispose();
                        MainFrame mainFrame = new MainFrame(n,st);
                    } catch (    RrdException | IOException | InterruptedException ex) {
                        Logger.getLogger(Welcome.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            thread.start();
        }
    }
    
    public static void main(String[] args) {
        new Welcome();
    }
                
}
    

