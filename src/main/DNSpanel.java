/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import Utilities.dnsTest;
import java.awt.GridLayout;
import javax.swing.ImageIcon;
import javax.swing.border.TitledBorder;

/**
 *
 * @author root
 */
public class DNSpanel extends JPanel implements ActionListener  {
    
    private String t;
    private String t1;
    private JPanel j;
    private MonitorWorker w;
    private JButton submit;
    JLabel a;
    
    public DNSpanel() throws InterruptedException {
        super();
        
        submit = new JButton("Retest");
        add(submit);
        a = new JLabel("Getting test");
        add(a);
        a.setVisible(true);
        Thread.sleep(400);
        w = new MonitorWorker(this);
        w.run();
        this.a.setVisible(false);
        submit.addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        a.setVisible(true);
        this.setVisible(false);
        this.repaint();
        a.setVisible(false);
        this.setVisible(true);
        w.run();
    }
    
    class MonitorWorker implements Runnable { 
        private JPanel f;
        JPanel j1 =  new JPanel(new GridLayout(4,1));
        
        JLabel l = new JLabel();
        JLabel l1 = new JLabel();
        JLabel l2 = new JLabel();
        JLabel l3 = new JLabel();
        
        public MonitorWorker(JPanel j){
            this.f = j;
        }

        @Override
        public void run() {
            
            
            l.setVisible(false);
            j1.setVisible(false);
            l1.setVisible(false);
            l2.setVisible(false);
            l3.setVisible(false);
            int suc = 0;
            String[] t2;
            t = dnsTest.runTest("8.8.8.8","3");
            if (t != null) {
                t2 = t.split(",");
                String[] result = t2[1].toString().split("");
                String[] time = t2[t2.length-1].toString().split(" ");
                int recv = Integer.parseInt(result[2]);
                if (recv == 0){
                    ImageIcon img1= new ImageIcon("/home/mkdeniz/Dropbox/University/Year4/Project/Images/red.png");
                    l = new JLabel("\nGoogle DNS Test: " + t2[2] + " in " + time[2]);
                    l.setIcon(img1);
                }
                else {
                    ImageIcon img1 = new ImageIcon("/home/mkdeniz/Dropbox/University/Year4/Project/Images/green.png");
                    l = new JLabel("\nGoogle DNS Test: " + recv + " Received, " + t2[2] + " in " + time[2]);
                    l.setIcon(img1);
                    suc++;
                    
                }
                j1.add(l);
            }
          
            t = dnsTest.runTest("8.8.8.8","3");
            if (t != null) {
                t2 = t.split(",");
                String[] result = t2[1].toString().split("");
                String[] time = t2[t2.length-1].toString().split(" ");
                int recv = Integer.parseInt(result[2]);
                if (recv == 0){
                    ImageIcon img= new ImageIcon("/home/mkdeniz/Dropbox/University/Year4/Project/Images/red.png");
                    l1 = new JLabel("\nOpenDNS Test: " + t2[2] + " in " + time[2]);
                    l1.setIcon(img);
                }
                else {
                    ImageIcon img= new ImageIcon("/home/mkdeniz/Dropbox/University/Year4/Project/Images/green.png");
                    l1 = new JLabel("\nOpenDNS Test: " + recv + " Received, " + t2[2] + " in " + time[2]);
                    l1.setIcon(img);
                    suc++;
                }
                j1.add(l1);
                
            }
            
            if (suc > 0) {
                t1 = dnsTest.runTest("www.google.com","3");
                if (t1 != null) {
                    t2 = t1.split(",");
                    l2 = new JLabel(t2[2]);
                    ImageIcon img3 = new ImageIcon("/home/mkdeniz/Dropbox/University/Year4/Project/Images/green.png");
                    JLabel j = new JLabel(t1);
                    j.setIcon(img3);
                    j1.add(j);
                    l3 = new JLabel("Your Internet Connection and DNS seems fine.");
                    l3.setIcon(img3);
                }
            
                else {
                    ImageIcon img3 = new ImageIcon("/home/mkdeniz/Dropbox/University/Year4/Project/Images/red.png");
                    
                    l3 = new JLabel("Your DNS Settings are wrong Connection.");
                    l3.setIcon(img3);
                }
            }
            else {
                ImageIcon img3 = new ImageIcon("/home/mkdeniz/Dropbox/University/Year4/Project/Images/red.png");
                    
                l3 = new JLabel("You have no Internet Connection. Contact your ISP");
                l3.setIcon(img3);
                
             }
             j1.add(l3);
             j1.setVisible(true);
             l.setVisible(true);
             l1.setVisible(true);
             l2.setVisible(true);
             l3.setVisible(true);
             this.f.add(j1);
       }
    }  
    
}
