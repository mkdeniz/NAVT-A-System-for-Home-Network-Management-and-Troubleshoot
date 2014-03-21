package uk.ac.gla.navt.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import uk.ac.gla.navt.utilities.dnsTest;
import java.awt.GridLayout;
import javax.swing.ImageIcon;
import javax.swing.border.TitledBorder;

/**
 *
 * @author root
 */
public class DNSpanel extends JPanel implements ActionListener  {
    
    
    private int result = 0;
    private JPanel j1;
    private String t;
    private String t1;
    private JPanel j;
    private MonitorWorker w;
    private JButton submit;
    JLabel a;
    
    public DNSpanel() throws InterruptedException {
        super(new GridLayout(1,2));
        j1 =  new JPanel(new GridLayout(4,1));
        j1.setSize(300, 300);
        JPanel j2 = new JPanel();
        j2.setSize(20,100);
        j1.setBorder(new TitledBorder("Tests"));
        submit = new JButton("Retest");
        j2.add(submit);
        add(j1);
        add(j2);
        w = new MonitorWorker(j1);
        w.run();
        submit.addActionListener(this);
    }
    
    public int getResults(){
        return result;
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        //a.setVisible(true);
        //this.setVisible(false);
        j1.removeAll();
        this.repaint();
        //a.setVisible(false);
        //this.setVisible(true);
        w.run();
    }
    
    class MonitorWorker implements Runnable { 
        private JPanel f;
        
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
                    l = new JLabel("\nGoogle DNS");//: " + t2[2] + " in " + time[2]);
                    l.setIcon(img1);
                }
                else {
                    ImageIcon img1 = new ImageIcon("/home/mkdeniz/Dropbox/University/Year4/Project/Images/green.png");
                    l = new JLabel("\nGoogle DNS");// + recv + " Received, " + t2[2] + " in " + time[2]);
                    l.setIcon(img1);
                    suc++;
                    
                }
                j1.add(l);
            }
            l.setVisible(true);
            t = dnsTest.runTest("208.67.222.222","3");
            if (t != null) {
                t2 = t.split(",");
                String[] result = t2[1].toString().split("");
                String[] time = t2[t2.length-1].toString().split(" ");
                int recv = Integer.parseInt(result[2]);
                if (recv == 0){
                    ImageIcon img= new ImageIcon("/home/mkdeniz/Dropbox/University/Year4/Project/Images/red.png");
                    l1 = new JLabel("\nOpenDNS Test");//: " + t2[2] + " in " + time[2]);
                    l1.setIcon(img);
                }
                else {
                    ImageIcon img= new ImageIcon("/home/mkdeniz/Dropbox/University/Year4/Project/Images/green.png");
                    l1 = new JLabel("\nOpenDNS Test");//: " + recv + " Received, " + t2[2] + " in " + time[2]);
                    l1.setIcon(img);
                    suc++;
                }
                j1.add(l1);
                
            }
            
            if (suc > 0) {
                result = suc;    
                t1 = dnsTest.runTest("www.google.com","3");
                if (t1 != null) {
                    t2 = t1.split(",");
                    l2 = new JLabel(t2[2]);
                    ImageIcon img3 = new ImageIcon("/home/mkdeniz/Dropbox/University/Year4/Project/Images/green.png");
                    //JLabel j = new JLabel(t1);
                    //j.setIcon(img3);
                    //j1.add(j);
                    l3 = new JLabel("Internet Connection");
                    l3.setIcon(img3);
                }
            
                else {
                    ImageIcon img3 = new ImageIcon("/home/mkdeniz/Dropbox/University/Year4/Project/Images/red.png");
                    
                    l3 = new JLabel("Wrong DNS");
                    l3.setIcon(img3);
                }
            }
            else {
                ImageIcon img3 = new ImageIcon("/home/mkdeniz/Dropbox/University/Year4/Project/Images/red.png");
                    
                l3 = new JLabel("You have no Internet Connection. Contact your ISP");
                l3.setIcon(img3);
                
             }
             j1.add(l3);
       }
    }  
    
}
