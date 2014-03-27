package uk.ac.gla.navt.ui;

/**
* A panel to provide Overview of the system.
* 
* @Author Mehmet Kemal Deniz
* @Date 27/03/2014
*/

import java.awt.GridLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
   
public class Overview extends JPanel{
   
    protected JLabel Overall;
    protected JLabel DNS;
    
    /**
     *
     * Default Constructor
     * 
     * @param f main frame
     * @param r result of DNS tests
     * 
     * */
    public Overview (JFrame f, String r){
        super(new GridLayout(1,1));
        JPanel j1 =  new JPanel(new GridLayout(3,3));
        j1.setSize(f.getWidth()/2, 100);
        j1.setBorder(new TitledBorder("Overview"));
        
        DNS = new JLabel("DNS");
        //Check for problems
        if (Integer.parseInt(r) > 0){
            ImageIcon img3 = new ImageIcon("images/green.png");
            DNS.setIcon(img3);
        }
        
        else {
            ImageIcon img3 = new ImageIcon("images/red.png");
            DNS.setIcon(img3);
        }
        
        //Check for problems
        if (Integer.parseInt(r) > 0){
            ImageIcon img3 = new ImageIcon("images/green.png");
            Overall = new JLabel("No Problems Detected");
            Overall.setIcon(img3);
        }
        
        else {
            ImageIcon img3 = new ImageIcon("images/red.png");
            Overall = new JLabel("Problem Detected");
            Overall.setIcon(img3);
        }
        
        j1.add(DNS);
        j1.add(Overall);
        
        add(j1);
        setSize(300, 300);
    }
}

