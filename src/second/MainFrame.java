/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package second;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import navd.ui.SplashScreen;
import org.jrobin.core.RrdException;
import org.jrobin.graph.ChartPanel;
import org.jrobin.graph.RrdGraph;
import org.jrobin.graph.RrdGraphDef;

/**
 *
 * @author root
 */
public class MainFrame extends JFrame {
    
    private JTabbedPane jtp  = new JTabbedPane();
    private JPanel jpControl = new JPanel(new BorderLayout());
    private JPanel jpControlTab = new JPanel(new BorderLayout());
    private JPanel jpOutTab = new JPanel();
    private JPanel jpOutTab1 = new JPanel();
    JPanel j1 = new JPanel(new BorderLayout());
    
    public MainFrame() throws RrdException, IOException, InterruptedException {
        super();
        JMenuBar greenMenuBar = new JMenuBar();
        setJMenuBar(greenMenuBar);
        
        Menu menu = new Menu("A Menu");
        greenMenuBar.add(menu);
        
        
        this.getContentPane().add(jtp); // outer tabbed pane
        jtp.addTab("Control", jpControlTab);
        jtp.addTab("DNS", new DNSpanel());
        jtp.addTab("Bandwith", new Bandwith(this));
        Statistics Stat = new Statistics(this);
        jpControlTab.add(Stat);
        //jpOutTab.add(Stat, BorderLayout.CENTER); 
        
        //Display the window.00
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setBounds(100, 100, 500, 400);
        this.setVisible(true);
        setVisible(true);
        
        
    }
    
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
                try {
                    SplashScreen s = new SplashScreen();
                    s.setVisible(true);
        Thread.sleep(5000);
        s.dispose();
                    new MainFrame();
                } catch (RrdException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
    }

    



