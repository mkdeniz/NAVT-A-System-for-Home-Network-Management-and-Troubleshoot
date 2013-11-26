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
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
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
    private String graphTitle;
    static int t12 = 0;
    JButton redButton;
    private String graphXLabel;
    private String graphYLabel;
    
    static double cal(int c, Date d) {
        double s = (c/1);
        s = s;
        return s/1024;
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
        gDef.setTimePeriod(START - 10, START);
        gDef.setImageBorder(Color.WHITE, 0);
        gDef.setTitle(graphTitle);
        gDef.setVerticalLabel(graphYLabel);
        gDef.setTimeAxisLabel(graphXLabel);
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
        frame.getContentPane().add(graphPanel);
        frame.pack();
        
            
        
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
    
    public Speed(int interval, String graphTitle, String graphYLabel, String graphXLabel) throws IOException, RrdException {
        this.graphTitle = graphTitle;
        this.graphXLabel = graphXLabel;
        this.graphYLabel = graphYLabel;
        this.prepareFrame();
        List<PcapIf> alldevs = new ArrayList<>(); // Will be filled with NICs  
        StringBuilder errbuf = new StringBuilder(); // For any error msgs  
  
        /*************************************************************************** 
         * First get a list of devices on this system 
         **************************************************************************/  
        int r = Pcap.findAllDevs(alldevs, errbuf);  
        if (r == Pcap.NOT_OK || alldevs.isEmpty()) {  
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
  
        /*************************************************************************** 
         * Second we open up the selected device 
         **************************************************************************/  
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
        Icmp icmp = new Icmp();
        int count = 0;
        double c1 = 0;
        double c = 0;
        double diff = 0;
        long t0 = 0;
        long seconds = 0;
        Date t1 = new Date();
        long t = t1.getTime()/1000;
        long time;
        RrdDb rrdDb = new RrdDb(rrd, RrdBackendFactory.getFactory("MEMORY"));
        RrdDb rrd1 = new RrdDb(name);
        Sample sample = rrdDb.createSample();
        Sample sample2 = rrd1.createSample();
        int ucount = 0;
        int tcount = 0;
        int udcount = 0;
        int dnscount = 0;
        
        
            
        @Override
        @SuppressWarnings("empty-statement")
        public void nextPacket(PcapPacket packet, String user)  {
            //long timestamp = Util.getTimestamp();
            
            

            if (packet.hasHeader(ip)){
                
                byte[] sIP = packet.getHeader(ip).source();
                String s_IP = org.jnetpcap.packet.format.FormatUtils.ip(sIP);
                if (!s_IP.equals("10.23.194.252")){
                    count = count + packet.getPacketWirelen();}
                if (packet.hasHeader(tcp)){
                    tcount++;
                }
                else if (packet.hasHeader(new Udp())){
                    if (packet.getHeader(new Udp()).source()== 53 ||packet.getHeader(new Udp()).destination()==53)
                        dnscount++;
                    else
                        udcount++;
                }
                
                else
                    ucount = ucount + packet.getPacketWirelen();
            }
            else if (packet.hasHeader(icmp))
                    System.out.print("kemal");
            
            
            
            t0 = new Date().getTime()/1000;
            seconds = t0 - t;
            if ( seconds > 1) {
                try {
                    sample2.setTime(Util.getTimestamp());
                    sample2.setValue("tcp", tcount); // or: sample.setValue(1, outputValue);
                    sample2.setValue("udp", udcount);
                    sample2.setValue("dns", dnscount);
                    sample2.update();// or: sample.setValue(1, outputValue);
                    tcount = 0;
                    udcount = 0;
                    dnscount = 0;
                    time = Util.getTimestamp();
                    double d = cal(count, t1);
                    sample.setTime(time);
                    sample.setValue("a", d);
                    sample.update();
                    gDef.setTimePeriod(START - 10, time);
                    end.setTime(time);
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
        SplashScreen s=new SplashScreen();
        s.setVisible(true);
        Thread.sleep(5000);
        s.dispose();
        prepareRRD();
        Speed speed = new Speed(15, "Current Usage", "Speed", "time");
          
    }  
        
}
