package uk.ac.gla.navt.ui;

import uk.ac.gla.navt.utilities.Database;
import uk.ac.gla.navt.ui.Menu;
import uk.ac.gla.navt.ui.GUIGraphPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import uk.ac.gla.navt.main.Worker;
import uk.ac.gla.navt.ui.DNSpanel;
import uk.ac.gla.navt.ui.Traceroute;
import org.jrobin.core.RrdException;
import org.jrobin.graph.RrdGraph;
import org.jrobin.graph.RrdGraphDef;

/**
 *
 * @author root
 */
public class MainFrame extends JFrame {
    
    protected JTabbedPane jtp  = new JTabbedPane();
    protected GUIGraphPanel graphPanel;
    protected String rrd = "file.rrd";
    protected JPanel jpControlTab = new JPanel(new BorderLayout());
    protected JPanel jpOutTab = new JPanel();
    protected JFrame frame;
    protected JLabel clock;
    protected JLabel statusLabel;
    
    private void prepareStatus() {
        clock = new JLabel(new Date().toString());
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setPreferredSize(new Dimension(this.getWidth(), 16));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusLabel = new JLabel("status");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);
        statusPanel.add(new JLabel("                              "));
        clock.setHorizontalAlignment(SwingConstants.RIGHT);
        statusPanel.add(clock);
        ActionListener updateClockAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clock.setText(new Date().toString()); 
            }
        };
        Timer t = new Timer(1000, updateClockAction);
        t.start();
    }
    
    public MainFrame(int n,String s) throws RrdException, IOException, InterruptedException {
        super(s);
        frame = this;
        JMenuBar greenMenuBar = new JMenuBar();
        setJMenuBar(greenMenuBar);
        Menu menu = new Menu("A Menu");
        greenMenuBar.add(menu);
        prepareStatus();
        
        Database d = new Database(rrd);
        d.prepareRrd();
        RrdGraphDef gDef = d.prepareBandwith();
        RrdGraph graph = new RrdGraph(gDef);
        
        graphPanel = new GUIGraphPanel(graph);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension d = frame.getContentPane().getSize();
                d.setSize(frame.getWidth(), frame.getHeight()-60);
                graphPanel.setGraphDimension(d);
            }
        });
        
        JPanel IDS = new JPanel();
        
        //Creating Tabs
        this.getContentPane().add(jtp); // outer tabbed pane
        jtp.addTab("Control", jpControlTab);
        jtp.addTab("DNS", new DNSpanel());
        jtp.addTab("Bandwith", graphPanel);
        jtp.addTab("Notification",IDS);
        jtp.addTab("Traceroute", new Traceroute());
        jtp.setMnemonicAt(0, KeyEvent.VK_1);
        jpControlTab.add(new Statistics(this), BorderLayout.CENTER);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(450,350);
        this.setVisible(true);
        
        Worker w = new Worker(this,gDef,n,s,IDS,statusLabel);
        w.run();
    }
}

    



