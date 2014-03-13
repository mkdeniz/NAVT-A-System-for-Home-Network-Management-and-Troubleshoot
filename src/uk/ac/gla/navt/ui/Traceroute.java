/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.gla.navt.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author root
 */
public class Traceroute extends JPanel implements ActionListener  
{

    private String t;
    private String t1;
    private JPanel j;
    private DNSpanel.MonitorWorker w;
    private JButton submit;
    JLabel a;
    
    public Traceroute() throws InterruptedException {
        super();
        submit = new JButton("Retest");
        add(submit);
        submit.addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
