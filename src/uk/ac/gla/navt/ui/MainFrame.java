package uk.ac.gla.navt.ui;

import uk.ac.gla.navt.utilities.Database;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import uk.ac.gla.navt.main.Worker;
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
    protected JPanel jpOutTab = new JPanel();
    protected JFrame frame;
    protected JLabel clock;
    protected JLabel statusLabel;
    private final DNSpanel dPanel;
    
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
        
        JPanel IDS = new JPanel(new GridLayout(1,0));
        JTable table = new JTable();
        JScrollPane pane = new JScrollPane(table);
        pane.setSize(200, 350 );
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"column1","column2"},0);
        
        table.setModel(tableModel);
        IDS.add(pane);
        pane.setSize(350,450);
        IDS.setSize(350,450);
        table.setSize(350,450);
        
        
        //Creating Tabs
        JPanel jpControlTab = new JPanel(new BorderLayout());
        dPanel = new DNSpanel();
        String Result = "" + dPanel.getResults();
        jpControlTab.add((new JLabel( Result)));
        this.getContentPane().add(jtp); // outer tabbed pane
        jtp.addTab("Control", jpControlTab);
        jtp.addTab("DNS", dPanel);
        jtp.addTab("Bandwith", graphPanel);
        jtp.addTab("Notification",IDS);
        jtp.addTab("Traceroute", new ForkDemo());
        jtp.setMnemonicAt(0, KeyEvent.VK_1);
        Thread.sleep(1000);
        jpControlTab.add(new Statistics(this,Result), BorderLayout.CENTER);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(450,350);
        
        this.setLocationByPlatform(true);
        this.setVisible(true);
        
        Worker w = new Worker(this,gDef,n,s,tableModel,statusLabel);
        w.run();
    }
}

    


