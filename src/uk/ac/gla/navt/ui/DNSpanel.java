package uk.ac.gla.navt.ui;

/**
* A panel to provide DNS tests.
* 
* @Author Mehmet Kemal Deniz
* @Date 27/03/2014
*/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import uk.ac.gla.navt.utilities.dnsTest;
import java.awt.GridLayout;
import javax.swing.ImageIcon;
import javax.swing.border.TitledBorder;

public class DNSpanel extends JPanel implements ActionListener  {
    protected JPanel sidePanel;
    protected MonitorWorker w;
    protected int result = 0;
    protected JPanel Results;
    protected JButton submit;
    
    public DNSpanel() throws InterruptedException {
        super(new GridLayout(1,2));
        
        Results =  new JPanel(new GridLayout(4,1));
        Results.setBorder(new TitledBorder("Tests"));
        Results.setSize(300, 300);
        
        submit = new JButton("Retest");
        submit.setSize(10, 10);
        submit.addActionListener(this);
    
        sidePanel = new JPanel();
        sidePanel .setSize(20,100);
        sidePanel.add(submit);
        
        add(Results);
        
        w = new MonitorWorker();
        w.run();
    }
    
    public int getResults(){
        return result;
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        Results.removeAll();
        this.repaint();
        w.run();
    }
    
    class MonitorWorker implements Runnable {
        protected JLabel l = new JLabel();
        protected JLabel l1 = new JLabel();
        protected JLabel l2 = new JLabel();
        protected JLabel l3 = new JLabel();
        
        public MonitorWorker(){}
        

        @Override
        public void run() {
            l.setVisible(false);
            l1.setVisible(false);
            l2.setVisible(false);
            l3.setVisible(false);
            int suc = 0;
            String[] t2;
            String t,t1;
            t = dnsTest.runTest("8.8.8.8","3");
            if (t != null) {
                t2 = t.split(",");
                String[] result = t2[1].toString().split("");
                int recv = Integer.parseInt(result[2]);
                if (recv == 0){
                    ImageIcon img1= new ImageIcon("/home/mkdeniz/Dropbox/University/Year4/Project/Images/red.png");
                    l = new JLabel("\nGoogle DNS");
                    l.setIcon(img1);
                }
                else {
                    ImageIcon img1 = new ImageIcon("/home/mkdeniz/Dropbox/University/Year4/Project/Images/green.png");
                    l = new JLabel("\nGoogle DNS");l.setIcon(img1);
                    suc++;
                    
                }
                Results.add(l);
            }
            l.setVisible(true);
            t = dnsTest.runTest("208.67.222.222","3");
            if (t != null) {
                t2 = t.split(",");
                String[] result = t2[1].toString().split("");
                int recv = Integer.parseInt(result[2]);
                if (recv == 0){
                    ImageIcon img= new ImageIcon("/home/mkdeniz/Dropbox/University/Year4/Project/Images/red.png");
                    l1 = new JLabel("\nOpenDNS Test");
                    l1.setIcon(img);
                }
                else {
                    ImageIcon img= new ImageIcon("/home/mkdeniz/Dropbox/University/Year4/Project/Images/green.png");
                    l1 = new JLabel("\nOpenDNS Test");l1.setIcon(img);
                    suc++;
                }
                Results.add(l1);
                
            }
            
            if (suc > 0) {
                result = suc;    
                t1 = dnsTest.runTest("www.google.com","3");
                if (t1 != null) {
                    t2 = t1.split(",");
                    l2 = new JLabel(t2[2]);
                    ImageIcon img3 = new ImageIcon("/home/mkdeniz/Dropbox/University/Year4/Project/Images/green.png");
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
                l3 = new JLabel("You have no Internet Connection\n. Contact your ISP");
                l3.setIcon(img3); 
                ImageIcon img= new ImageIcon("/home/mkdeniz/Dropbox/University/Year4/Project/Images/red.png");
                l1 = new JLabel("\nOpenDNS Test");
                l1.setIcon(img);
                ImageIcon img1 = new ImageIcon("/home/mkdeniz/Dropbox/University/Year4/Project/Images/red.png");
                l = new JLabel("\nGoogle DNS");l.setIcon(img1);
                Results.add(l1);
                Results.add(l);
            }
            
            Results.add(l3);
            Results.add(submit);
        }
    }  
}
