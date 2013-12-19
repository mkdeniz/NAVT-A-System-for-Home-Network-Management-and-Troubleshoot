/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package second;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import navd.core.dnsTest;
import org.jrobin.core.RrdException;

/**
 *
 * @author root
 */
public class panel extends JPanel implements ActionListener  {
    
    private boolean ab = false;
    private String t;
    private String t1;
    private JPanel j;
    MonitorWorker w;
    JButton submit;
    JLabel a;
    
    public panel() throws InterruptedException {
        super();
        submit = new JButton("Submit");
        add(submit);
        a = new JLabel("Getting test");
        add(a);
        w = new MonitorWorker(this);
        w.run();
        /*c.weightx = 0.0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 2;*/
        
        submit.addActionListener(this);
        
    }
    
        public void actionPerformed(ActionEvent e){
            this.setVisible(false);
            ab = true;
            this.repaint();
            a.setVisible(false);
            this.setVisible(true);
            w.run();
            
    }
    
    class MonitorWorker implements Runnable { 
        private JPanel f;
        JLabel l = new JLabel();
        JLabel l2 = new JLabel();
        
        public MonitorWorker(JPanel j){
            this.f = j;
        }

        @Override
        public void run() {
            l.setVisible(false);
            l2.setVisible(false);
            String[] t2;
            t = dnsTest.runTest("8.8.8.8");
            t2 = t.split(",");
            l = new JLabel(t2[2]);
            this.f.add(l);
            t1 = dnsTest.runTest("google.com");
            t2 = t1.split(",");
            l2 = new JLabel(t2[2]);
            this.f.add(l2);
            l.setVisible(true);
            l2.setVisible(true);
        }
    }  
    
}
