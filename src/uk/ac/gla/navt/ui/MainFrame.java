package uk.ac.gla.navt.ui;

/**
* Main frame of the system that runs all the services
* 
* @Author Mehmet Kemal Deniz
* @Date 27/03/2014
*/

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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import uk.ac.gla.navt.process.Worker;
import org.jrobin.core.RrdException;
import org.jrobin.graph.RrdGraph;
import org.jrobin.graph.RrdGraphDef;

public class MainFrame extends JFrame {
    
    protected GUIGraphPanel graphPanel;
    protected String rrd = "file.rrd";
    protected DNSpanel dPanel;
    protected JFrame frame;
    protected JLabel clock;
    protected JPanel statusPanel;
    protected JLabel statusLabel;
    
    /**
     * 
     * A method to create status bar down on the user interface
     * 
     */
    private void prepareStatus() {
        clock = new JLabel(new Date().toString());
        statusPanel = new JPanel();
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
    
    /**
     *
     * Default Constructor
     * 
     * @param n Choice of NIC
     * @param s IP of NIC
     * 
     */
    public MainFrame(int n, String s){
        super(s);
        try {
            frame = this;
            //Preparing Menus
            JMenuBar greenMenuBar = new JMenuBar();
            setJMenuBar(greenMenuBar);
            Menu menu = new Menu();
            greenMenuBar.add(menu);
            prepareStatus();
            
            //Preparing databases and graphs
            System.out.print("System is preparing databases:");
            Database d = new Database(rrd);
            d.prepareRRDB();
            d.prepareRRDC();
            
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
            System.out.println(" Done");
            
            System.out.print("System is preparing panels:");
            //Creating Events Tab and Panel
            JPanel Events = new JPanel(new GridLayout(1,0));
            JTable table = new JTable();
            JScrollPane pane = new JScrollPane(table);
            pane.setSize(200, 350 );
            DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Event","Source"},0);
            table.setModel(tableModel);
            Events.add(pane);
            pane.setSize(350,450);
            Events.setSize(350,450);
            table.setSize(350,450);
            
            //Creating Tabs
            JTabbedPane tabs  = new JTabbedPane();
            JPanel Home = new JPanel(new BorderLayout());
            Classification Class = new Classification(this);
            dPanel = new DNSpanel();
            String Result = "" + dPanel.getResults();
            Home.add((new JLabel( Result)));
            this.getContentPane().add(tabs); // outer tabbed pane
            
            tabs.addTab("Overview", Home);
            tabs.addTab("DNS", dPanel);
            tabs.addTab("Bandwith", graphPanel);
            tabs.addTab("Events", Events);
            tabs.addTab("Classification", Class);
            tabs.setMnemonicAt(0, KeyEvent.VK_1);
            Home.add(new Overview(this, Result), BorderLayout.CENTER);
            
            //Main Frame features
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setSize(450,350);
            this.setLocationByPlatform(true);
            this.setVisible(true);
            System.out.println(" Done");
            
            System.out.print("System is preparing analyser:");
            Worker w = new Worker(this,gDef,n,s,tableModel,statusLabel);
            System.out.println(" Done");
            
            //Run the worker
            w.run();
        } catch (IOException | RrdException | InterruptedException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
}

    



