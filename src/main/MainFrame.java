package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import Utilities.RRD;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import navd.ui.SplashScreen;
import org.jrobin.core.RrdException;
import org.jrobin.core.Util;
import org.jrobin.graph.RrdGraph;
import org.jrobin.graph.RrdGraphDef;

/**
 *
 * @author root
 */
public class MainFrame extends JFrame {
    
    public JTabbedPane jtp  = new JTabbedPane();
    protected GUIGraphPanel graphPanel;
    protected String rrd = "file.rrd";
    protected JPanel jpControlTab = new JPanel(new BorderLayout());
    protected JPanel jpOutTab = new JPanel();
    protected JPanel jpOutTab1 = new JPanel();
    protected JPanel j1 = new JPanel(new BorderLayout());
    public JFrame f;
    JLabel clock;
    
    public MainFrame(int n,String s) throws RrdException, IOException, InterruptedException {
        super(s);
        JMenuBar greenMenuBar = new JMenuBar();
        setJMenuBar(greenMenuBar);
        f = this;
        //System.out.print(n);
        RrdGraphDef gDef = new RrdGraphDef();
        Date endTime = new Date();
        Date startTime = new Date(endTime.getTime() - 300000);
        gDef.setTimePeriod(startTime, endTime);
        gDef.setImageBorder(Color.WHITE, 0);
        gDef.setTitle("Bandiwth");
        gDef.setVerticalLabel("Speed");
        gDef.setTimeAxisLabel("Time");
        gDef.datasource("a", rrd, "a", "AVERAGE", "MEMORY");
        gDef.datasource("b", rrd, "b", "AVERAGE", "MEMORY");
        gDef.line("a", Color.RED, "Real");
        gDef.line("b", Color.GREEN, "Real");
        gDef.gprint("a", "MAX", "max = @2 kb/s@l");
        gDef.gprint("b", "MAX", "max = @2 kb/s@l");
        gDef.time("@l@lTime period: @t", "MMM dd, yyyy    HH:mm:ss", startTime);
        gDef.time("to  @t@c", "HH:mm:ss");
        RrdGraph graph = new RrdGraph(gDef);
        
        clock = new JLabel(new Date().toString());
        JPanel statusPanel = new JPanel();
statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
this.add(statusPanel, BorderLayout.SOUTH);
statusPanel.setPreferredSize(new Dimension(this.getWidth(), 16));
statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
JLabel statusLabel = new JLabel("status");
statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
statusPanel.add(statusLabel);
clock.setHorizontalAlignment(SwingConstants.RIGHT);
statusPanel.add(clock);
        
    

        ActionListener updateClockAction = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                clock.setText(new Date().toString()); 
            }
        };

        Menu menu = new Menu("A Menu");
        greenMenuBar.add(menu);
        
        Timer t = new Timer(1000, updateClockAction);
t.start();
        
        RRD.prepareRRD();
        
        graphPanel = new GUIGraphPanel(graph);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension d = f.getContentPane().getSize();
                d.setSize(f.getWidth(), f.getHeight()-70-15);
                graphPanel.setGraphDimension(d);
            }
        });
        
        JPanel IDS = new JPanel();
        JPanel jp = new JPanel();
        jp.add(graphPanel);
        jp.setBounds(100, 100, 500, 400);
        
        this.getContentPane().add(jtp); // outer tabbed pane
        jtp.addTab("Control", jpControlTab);
        jtp.addTab("DNS", new DNSpanel());
        jtp.addTab("Bandwith",null, graphPanel);
        jtp.addTab("Bandwith",IDS);
        jtp.setMnemonicAt(0, KeyEvent.VK_1);
        Worker w = new Worker(this,gDef,n,s,IDS);
        Statistics Stat = new Statistics(this);
        jpControlTab.add(Stat, BorderLayout.CENTER);
        //graphPanel.setVisible(false);
        //Display the window.00
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //this.pack();
        this.setSize(400,300);
        this.setVisible(true);
        
        
        setVisible(true);
        
        w.run();
        
    }
    
    public static void main(String[] args) throws UnknownHostException, IOException, RrdException, InterruptedException {
        
        //MainFrame mainFrame = new MainFrame();
        /*int timeout=1000;
        for (int i=1;i<254;i++){
        String host="192.168.1" + "." + i;
        if (InetAddress.getByName(host).isReachable(timeout)){
        System.out.println(InetAddress.getByName(host).getHostName() + " is reachable");
        System.out.println(dnsTest.runTest(host, "3"));
        System.out.println(arpTest.runTest(host));
        }
        else
        System.out.print(nMapTest.runTest(host));
        }System.out.print("End");*/
                
            
    }
}

    



