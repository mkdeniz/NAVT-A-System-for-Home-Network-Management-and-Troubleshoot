package navd.core;

import navd.ui.GUIGraphPanel;
import navd.ui.SplashScreen;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jrobin.core.*;
import org.jrobin.graph.RrdGraphDef;
import org.jrobin.graph.RrdGraph;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.jnetpcap.protocol.tcpip.Udp;

/**
 *
 * @author mkdeniz
 */
public class Speed {
    private static final String name = "/home/mkdeniz/tcp.rdd";
    private static JFrame frame;
    private static GUIGraphPanel graphPanel;
    private static RrdGraph graph;
    private static RrdGraphDef gDef;
    private static final String rrd = "file.rrd";
    private static final long START = Util.getTimestamp();
    private static Date end = new Date(START);
    static int t12 = 0;
    JButton redButton;
    
    static double cal(int c) {
        return (c/1)/1024;
    }
    protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }
    
    public static void doGraph() throws RrdException, IOException {
        RrdGraphDef graphDef = new RrdGraphDef();
        Date endTime = new Date();
        Date startTime = new Date(endTime.getTime() - 86400 * 10L);
        graphDef.setTimePeriod(startTime, endTime);
        graphDef.datasource("inoctets", name, "input", "AVERAGE");
        graphDef.datasource("outoctets", name, "output", "AVERAGE");
        graphDef.datasource("tcp", name, "tcp", "AVERAGE");
        graphDef.datasource("udp", name, "udp", "AVERAGE");
        graphDef.area("tcp", new Color(0, 0xFF, 0), "In traffic");
        graphDef.area("udp", new Color(0, 0, 0xFF), "Out traffic");
        RrdGraph graph1 = new RrdGraph(graphDef);
        graph1.saveAsGIF("/home/mkdeniz/1.gif");
    }
    
    private void prepareFrame() throws RrdException, IOException {
        gDef = new RrdGraphDef();
        Date endTime = new Date();
        Date startTime = new Date(endTime.getTime() - 600000);
        gDef.setTimePeriod(startTime, endTime);
        //gDef.setTimePeriod(START - 10, START);
        gDef.setImageBorder(Color.WHITE, 0);
        gDef.setTitle("Bandiwth");
        gDef.setVerticalLabel("Speed");
        gDef.setTimeAxisLabel("Time");
        gDef.datasource("a", rrd, "a", "AVERAGE", "MEMORY");
        gDef.datasource( "avg", "a", "AVERAGE");
        gDef.line("a", Color.decode("0xb6e4"), "Real");
        gDef.line("avg", Color.RED,  "Average@l");
        gDef.gprint("a", "MIN", "min = @2 kb/s@l");
        gDef.gprint("a", "MAX", "max = @2 kb/s@l");
        gDef.gprint("a", "AVERAGE", "avg = @2 kb/s");
        gDef.time("@l@lTime period: @t", "MMM dd, yyyy    HH:mm:ss", START);
        gDef.time("to  @t@c", "HH:mm:ss");

        // create graph
        graph = new RrdGraph(gDef);
        
        // create swing components
        graphPanel = new GUIGraphPanel(graph);
        frame = new JFrame("Monitor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.getContentPane().add(graphPanel);
        frame.pack();
       
        JTabbedPane tabbedPane = new JTabbedPane();
        JComponent panel1 = makeTextPanel("Panel #1");
        tabbedPane.addTab("Tab 1", null,graphPanel,
                "Does nothing");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        
        JComponent panel2 = makeTextPanel("Panel #2");
        tabbedPane.addTab("Overview", null, panel2,
                "Does twice as much nothing");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
        
        frame.add(tabbedPane, BorderLayout.CENTER);
        
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension d = frame.getContentPane().getSize();
                graphPanel.setGraphDimension(d);
            }
        });
        frame.setBounds(100, 100, 500, 400);
        frame.setVisible(true);
    }
    
    public Speed(int interval) throws IOException, RrdException {
        this.prepareFrame();
        List<PcapIf> alldevs = new ArrayList<>(); // Will be filled with NICs  
        StringBuilder errbuf = new StringBuilder(); // For any error msgs  
        
        int r = Pcap.findAllDevs(alldevs, errbuf);  
        if (alldevs.isEmpty()) {  
            System.err.printf("Can't read list of devices, error is %s", errbuf  
                .toString());  
            return;  
        }  
  
        System.out.println("Network devices found:");  
  
        int i = 0;  
        for (PcapIf device : alldevs) {  
            String description =  
                (device.getDescription() != null) ? device.getDescription()  
                    : "No description available";  
            System.out.printf("#%d: %s [%s]\n", i++, device.getName(), description);  
        }  
  
        PcapIf device = alldevs.get(4); // We know we have atleast 1 device  
        System.out  
            .printf("\nChoosing '%s' on your behalf:\n",  
                (device.getDescription() != null) ? device.getDescription()  
                    : device.getName()); 
        
        int snaplen = 64 * 1024;           // Capture all packets, no trucation  
        int flags = Pcap.MODE_PROMISCUOUS; // capture all packets  
        int timeout = 50 * 1000;           // 10 seconds in millis  
        Pcap pcap =  
            Pcap.openLive(device.getName(), snaplen, flags, 0, errbuf);  
  
        if (pcap == null) {  
            System.err.printf("Error while opening device for capture: "  
                + errbuf.toString());  
            return;  
        }  
  
        /*************************************************************************** 
         * Third we create a packet handler which will receive packets from the 
         * libpcap loop. 
         **************************************************************************/  
        PcapPacketHandler<String> jpacketHandler;  
        jpacketHandler = new PcapPacketHandler<String>() {  
        Tcp tcp = new Tcp();
        Ip4 ip = new Ip4();
        Udp udp = new Udp();
        Icmp icmp = new Icmp();
        int count = 0;
        double c1 = 0;
        double c = 0;
        double diff = 0;
        long t0 = 0;
        long seconds = 0;
        Date t1 = new Date();
        long t = t1.getTime()/1000;
        RrdDb rrdDb = new RrdDb(rrd, RrdBackendFactory.getFactory("MEMORY"));
        RrdDb rrd1 = new RrdDb(name);
        Sample sample = rrdDb.createSample();
        Sample sample2 = rrd1.createSample();
        int ucount = 0;
        int tcount = 0;
        int udcount = 0;
        int dnscount = 0;
        
        
            
        @Override
        public void nextPacket(PcapPacket packet, String user)  {
            if (packet.hasHeader(ip)){
                byte[] sIP = packet.getHeader(ip).source();
                String s_IP = org.jnetpcap.packet.format.FormatUtils.ip(sIP);
                if (!s_IP.equals("10.23.194.252"))
                    count = count + packet.getPacketWirelen();
                else
                    ucount = ucount + packet.getPacketWirelen();
                if (packet.hasHeader(tcp)) tcount++;
                else if (packet.hasHeader(udp)){
                    if (packet.getHeader(udp).source()== 53 ||packet.getHeader(udp).destination()==53) {
                        dnscount++;
                        udcount++;
                    }
                    else
                        udcount++;
                }
                else if (packet.hasHeader(icmp));
            }
            
            t0 = new Date().getTime()/1000;
            seconds = t0 - t;
            if ( seconds > 1) {
                try {
                    sample2.setTime(Util.getTimestamp());
                    sample2.setValue("tcp", tcount);
                    sample2.setValue("udp", udcount);
                    sample2.setValue("dns", dnscount);
                    sample2.update();
                    tcount = 0;
                    udcount = 0;
                    dnscount = 0;
                    sample.setTime(Util.getTimestamp());
                    sample.setValue("a", cal(count));
                    sample.update();
                    Date endTime = new Date();
                    Date startTime = new Date(endTime.getTime() - 600000);
                    gDef.setTimePeriod(startTime, endTime);
                    count = 0;
                    ucount = 0;
                    t1 = new Date();
                    t = new Date().getTime()/1000;
                } catch (RrdException | IOException ex) {
                    Logger.getLogger(Speed.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            frame.repaint();
        }     
        
        };
        
        pcap.loop(0, jpacketHandler, "jNetPcap rocks!");
        pcap.close();
        }
    
    
        public static void prepareRRD() throws RrdException, IOException{
            RrdDef def = new RrdDef(name, 2);
            def.addDatasource("input", "GAUGE", 600, 0, Double.NaN);
            def.addDatasource("output", "GAUGE", 600, 0, Double.NaN);
            def.addDatasource("tcp", "GAUGE", 600, 0, Double.NaN);
            def.addDatasource("udp", "GAUGE", 600, 0, Double.NaN);
            def.addDatasource("dns", "GAUGE", 600, 0, Double.NaN);
            def.addArchive("AVERAGE", 0.5, 1, 600);
            def.addArchive("AVERAGE", 0.5, 6, 700);
            def.addArchive("AVERAGE", 0.5, 24, 797);
            def.addArchive("AVERAGE", 0.5, 288, 775);
            def.addArchive("MAX", 0.5, 1, 600);
            def.addArchive("MAX", 0.5, 6, 700);
            def.addArchive("MAX", 0.5, 24, 797);
            def.addArchive("MAX", 0.5, 288, 775);
            RrdDb rrd1 = new RrdDb(def);
            rrd1.close();
        }
    
    
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws org.jrobin.core.RrdException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws IOException, RrdException, InterruptedException {
        SplashScreen s = new SplashScreen();
        s.setVisible(true);
        Thread.sleep(5000);
        s.dispose();
        prepareRRD();
        String t = dnsTest.runTest("8.8.8.8");
        System.out.print(t);
        t = dnsTest.runTest("google.com");
        System.out.print(t);
        Speed speed = new Speed(15);
          
    }  
        
}
