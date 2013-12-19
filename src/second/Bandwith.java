/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package second;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 *
 * @author root
 */
public class Bandwith extends JPanel implements ActionListener {
    
    private final String name = "/home/mkdeniz/tcp.rdd";
    private JButton submit;
    private JCheckBox tBox,uBox,dBox,iBox;    
    private Boolean tcp,udp,dns,icmp;  
    private TimeSpinner t1,t2;
    
    public Bandwith () {
        super();
        this.setSize(300, 300);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        tBox = new JCheckBox("TCP");
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        add(tBox,c);
        
        uBox = new JCheckBox("UDP");
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        add(uBox,c);
        
        dBox = new JCheckBox("DNS");
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 1;
        add(dBox,c);
        
        iBox = new JCheckBox("ICMP");
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 1;
        add(iBox,c);
        
        submit = new JButton("Submit");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 2;
        add(submit, c);
    
        t1 = new TimeSpinner();
        c.gridx = 2;
        c.gridy = 0;
        add(t1,c);
        t2 = new TimeSpinner();
        c.gridx = 2;
        c.gridy = 1;
        add(t2,c);
        submit.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
