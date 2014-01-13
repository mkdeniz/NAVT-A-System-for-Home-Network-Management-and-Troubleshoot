package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Arp;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Radius;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import org.jrobin.core.RrdBackendFactory;
import org.jrobin.core.RrdDb;
import org.jrobin.core.RrdException;
import org.jrobin.core.Sample;
import org.jrobin.core.Util;
import org.jrobin.graph.RrdGraphDef;


public class Worker implements Runnable{

    private JFrame frame;
    private RrdGraphDef gDef;
    private final String rrd = "file.rrd";
    private final String name = "/home/mkdeniz/tcp.rdd";
    private Arp arp = new Arp();
    private Ethernet eth = new Ethernet();
    private Tcp tcp = new Tcp();
    private Ip4 ip = new Ip4();
    private Udp udp = new Udp();
    private Icmp icmp = new Icmp();
    private Http http = new Http();
    private int num;
    private String str;
    private JPanel id;
    String str2 = "";
    HashMap<String,Integer> hm;
    //Error er = new Error();
    
    public Worker(JFrame f, RrdGraphDef g,int n,String s,JPanel ids){
        frame = f;
        id = ids;
        gDef = g;
        num = n;
        str = s;
        hm =new HashMap<String,Integer>();
    }

    @Override
    public void run() {
        try {
            System.out.println(str);
            String[] host = str.split("\\.");
            str2 = str2 + host[0] + "." + host[1] + "." + host[2];
            System.out.println(host.length);
            frame.repaint();
            frame.requestFocus();
            List<PcapIf> alldevs = new ArrayList<>(); // Will be filled with NICs
            StringBuilder errbuf = new StringBuilder(); // For any error msgs
            
            int r = Pcap.findAllDevs(alldevs, errbuf);
            if (alldevs.isEmpty()) {
                System.err.printf("Can't read list of devices, error is %s", errbuf.toString());
                return;
            }
            
            int i = 0;
            for (PcapIf device : alldevs) {
                String description =
                        (device.getDescription() != null) ? device.getDescription()
                        : "No description available";
                System.out.printf("#%d: %s [%s]\n", i++, device.getName(), description);
            }
            PcapIf device = alldevs.get(num); // We know we have atleast 1 device
            System.out.printf("\nChoosing '%s' on your behalf:\n",
                            (device.getDescription() != null) ? device.getDescription()
                                    : device.getName());
            
            int snaplen = 64 * 1024;
            int flags = Pcap.MODE_PROMISCUOUS; 
            Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags, 0, errbuf);
            
            if (pcap == null) {
                System.err.printf("Error while opening device for capture: "
                        + errbuf.toString());
                return;
            }
            
            PcapPacketHandler<String> jpacketHandler;
            jpacketHandler = new PcapPacketHandler<String>() {
                double c1 = 0;
                double c = 0;
                double diff = 0;
                long t0 = 0;
                long seconds = 0;
                long t = new Date().getTime()/1000;
                RrdDb rrdDb = new RrdDb(rrd, RrdBackendFactory.getFactory("MEMORY"));
                RrdDb rrd1 = new RrdDb(name);
                Sample sample = rrdDb.createSample();
                Sample sample2 = rrd1.createSample();
                int hcount = 0;   //IN HTTP
                int ohcount = 0;  //OUT HTTP
                int ucount = 0;   //UPLOAD
                int count = 0;    //DOWNLOAD
                int tcount = 0;   //IN TCP
                int otcount = 0;  //OUT TCP
                int icount = 0;   //IN ICMP
                int oicount = 0;  //OUT ICMP
                int udcount = 0;  //IN UDP
                int oudcount = 0; //OUT UDP
                int dnscount = 0; //IN DNS
                int odnscount = 0;//OUT DNS
                int counter = 0;
                
                @Override
                public void nextPacket(PcapPacket packet, String user)  {
                    
                    if (packet.hasHeader(ip)/* && tcount == 7*/){
                        System.out.print(FormatUtils.mac(packet.getHeader(eth).source())+"-");
                    System.out.println(FormatUtils.mac(packet.getHeader(eth).destination()));
                        byte[] sIP = packet.getHeader(ip).source();
                        String s_IP = org.jnetpcap.packet.format.FormatUtils.ip(sIP);
                        byte[] dIP = packet.getHeader(ip).destination();
                        String d_IP = org.jnetpcap.packet.format.FormatUtils.ip(dIP);
                        if (!s_IP.equals(str) && !s_IP.startsWith(str2)){
                            if (hm.containsKey(s_IP)) {
                                
                                //System.out.print("");
                                //System.out.print("YES");
                                int n = hm.get(s_IP);
                                n = n + 1;
                                hm.put(s_IP,n);
                            }
                            else {
                                //System.out.print("NO");
                                hm.put(s_IP, 0);
                            }
                            count = count + packet.getPacketWirelen();
                            if (packet.hasHeader(tcp)){
                                if(packet.getHeader(tcp).source()== 80 ||packet.getHeader(tcp).destination()==80) hcount++;
                                else tcount++;
                            }
                            else if (packet.hasHeader(udp)){
                                if (packet.getHeader(udp).source()== 53 ||packet.getHeader(udp).destination()==53) {
                                    dnscount++;
                                }
                                else
                                    udcount++;
                            }
                        }
                        else if (s_IP.equals(str) || s_IP.startsWith(str.substring(0, 8))){
                            /*System.out.print(FormatUtils.mac(packet.getHeader(eth).source())+"-");
                            System.out.println(FormatUtils.mac(packet.getHeader(eth).destination()));*/
                            System.out.print(FormatUtils.mac(packet.getHeader(eth).source())+"-");
                            System.out.println(FormatUtils.mac(packet.getHeader(eth).destination()));
                            ucount = ucount + packet.getPacketWirelen();
                            if (packet.hasHeader(tcp)){
                                if(packet.getHeader(tcp).source()== 80 ||packet.getHeader(tcp).destination()==80) ohcount++;
                                else otcount++;
                            }
                            else if (packet.hasHeader(udp)){
                                if (packet.getHeader(udp).source()== 53 ||packet.getHeader(udp).destination()==53) {
                                    odnscount++;
                                }
                                else
                                    oudcount++;
                            }
                        }
                        
                        /*System.out.println("============CURRENT REMOTES==============");
                        for (String s : hm.keySet())
                            System.out.println(s+":"+hm.get(s));
                        System.out.println("============END==============");*/
                    }
                   
                    if (icount > 5){
                            id.add(new JLabel("ICMP Attack"));
                        }
                    
                    t0 = new Date().getTime()/1000;
                    seconds = t0 - t;
                    if ( seconds > 1) {
                        if (counter == 3600) hm.clear();
                        counter++;
                        try {
                            sample2.setTime(Util.getTimestamp());
                            sample2.setValue("itcp", tcount);
                            sample2.setValue("otcp", otcount);
                            sample2.setValue("iudp", udcount);
                            sample2.setValue("oudp", oudcount);
                            sample2.setValue("idns", dnscount);
                            sample2.setValue("odns", odnscount);
                            sample2.setValue("iicmp", icount);
                            sample2.setValue("oicmp", oicount);
                            sample2.setValue("ihttp", hcount);
                            sample2.setValue("ohttp", ohcount);
                            sample2.update();
                            tcount = 0;
                            otcount = 0;
                            udcount = 0;
                            oudcount = 0;
                            dnscount = 0;
                            odnscount = 0;
                            icount = 0;
                            oicount = 0;
                            hcount = 0;
                            ohcount = 0;
                            sample.setTime(Util.getTimestamp());
                            sample.setValue("a", ((count)/1024)/2);
                            sample.setValue("b", ((ucount)/1024)/2);
                            sample.update();
                            Date endTime = new Date();
                            Date startTime = new Date(endTime.getTime() - 300000);
                            gDef.setTimePeriod(startTime, endTime);
                            count = 0;
                            ucount = 0;
                            t = new Date().getTime()/1000;
                        } catch (RrdException | IOException ex) {
                            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                    }
                    frame.repaint();
                }
                
            };
            
            pcap.loop(0, jpacketHandler, "jNetPcap rocks!");
            pcap.close();
        } catch (IOException | RrdException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    }