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
    
    public Overview (JFrame f,String result){
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
}

