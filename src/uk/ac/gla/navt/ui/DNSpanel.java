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
import uk.ac.gla.navt.utilities.DNS;
import java.awt.GridLayout;
import javax.swing.ImageIcon;
import javax.swing.border.TitledBorder;

public class DNSpanel extends JPanel implements ActionListener  {
    
    protected JPanel sidePanel;
    protected MonitorWorker w;
    protected int result = 0;
    protected JPanel Results;
    protected JButton submit;
    
    /**
     * 
     * Default Constructor
     * 
     * @throws InterruptedException
     */
    public DNSpanel() throws InterruptedException {
        super(new GridLayout(1,2));
        
        //Result panel creation
        Results =  new JPanel(new GridLayout(4,1));
        Results.setBorder(new TitledBorder("Tests"));
        Results.setSize(300, 300);
        
        //Test button creation
        submit = new JButton("Retest");
        submit.setSize(10, 10);
        submit.addActionListener(this);
    
        //Button panel
        sidePanel = new JPanel();
        sidePanel .setSize(20,100);
        sidePanel.add(submit);
        
        //Add Panel
        add(Results);
        
        //Create and run DNS test Thread 
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
        
        protected String green = "images/green.png";
        protected String red = "images/red.png";
        
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
            
            //First Test to google DNS
            t = DNS.runTest("8.8.8.8","3");
            if (t != null) {
                t2 = t.split(",");
                String[] result = t2[1].toString().split("");
                int recv = Integer.parseInt(result[2]);
                
                //Problem
                if (recv == 0){
                    ImageIcon img1 = new ImageIcon(red);
                    l = new JLabel("\nGoogle DNS");
                    l.setIcon(img1);
                }
                
                //Success
                else {
                    ImageIcon img1 = new ImageIcon(green);
                    l = new JLabel("\nGoogle DNS");
                    l.setIcon(img1);
                    suc++;
                    
                }
                Results.add(l);
            }
            l.setVisible(true);
            
            //Second Test to openDNS
            t = DNS.runTest("208.67.222.222","3");
            if (t != null) {
                t2 = t.split(",");
                String[] result = t2[1].toString().split("");
                int recv = Integer.parseInt(result[2]);
                
                //If an error occurs
                if (recv == 0){
                    ImageIcon img = new ImageIcon(red);
                    l1 = new JLabel("\nOpenDNS Test");
                    l1.setIcon(img);
                }
                
                //Else successfull
                else {
                    ImageIcon img = new ImageIcon(green);
                    l1 = new JLabel("\nOpenDNS Test");
                    l1.setIcon(img);
                    suc++;
                }
                Results.add(l1);  
            }
            l1.setVisible(true);
            
            //Third Test to current DNS
            if (suc > 0) {
                result = suc;    
                t1 = DNS.runTest("www.google.com","3");
                //No Errors
                if (t1 != null) {
                    t2 = t1.split(",");
                    l2 = new JLabel(t2[2]);
                    ImageIcon img3 = new ImageIcon(green);
                    l3 = new JLabel("Internet Connection");
                    l3.setIcon(img3);
                }
                
                //ERROR
                else {
                    ImageIcon img3 = new ImageIcon(red);
                    l3 = new JLabel("Wrong DNS");
                    l3.setIcon(img3);
                }
            }
            
            //No Internet
            else {
                ImageIcon img3 = new ImageIcon(red);
                l3 = new JLabel("You have no Internet Connection. Contact your ISP");
                l3.setIcon(img3);
            }
            
            Results.add(l3);
            Results.add(submit);
        }
    }  
}
