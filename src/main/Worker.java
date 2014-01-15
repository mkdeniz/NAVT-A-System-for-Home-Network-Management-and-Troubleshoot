package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Arp;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import org.jrobin.core.RrdBackendFactory;
import org.jrobin.core.RrdDb;
import org.jrobin.core.RrdException;
import org.jrobin.core.Sample;
import org.jrobin.core.Util;
import org.jrobin.graph.RrdGraphDef;


public class Worker implements Runnable{

    protected JFrame frame;
    protected RrdGraphDef gDef;
    protected final String rrd = "file.rrd";
    protected final String name = "/home/mkdeniz/tcp.rdd";
    protected Arp arp = new Arp();
    protected Ethernet eth = new Ethernet();
    protected Tcp tcp = new Tcp();
    protected Ip4 ip = new Ip4();
    protected Udp udp = new Udp();
    protected Icmp icmp = new Icmp();
    protected Http http = new Http();
    protected int num;
    protected String str;
    protected JPanel id;
    protected String str2 = "";
    protected JLabel label;
    protected JLabel test = new JLabel();
    protected HashMap<String,Integer> hm;
    RrdDb rrdDb;
    RrdDb rrd1;
    RrdDb rrd2;
    Sample sample;
    Sample sample2;
    Sample sample3;
    long tmp = 0;
    long tmp2 = 0;
    int hcount = 0;    //IN HTTP
    int ohcount = 0;   //OUT HTTP
    long ucount = 0;   //UPLOAD
    long count = 0;    //DOWNLOAD
    int tcount = 0;    //IN TCP
    int otcount = 0;   //OUT TCP
    int icount = 0;    //IN ICMP
    int oicount = 0;   //OUT ICMP
    int udcount = 0;   //IN UDP
    int oudcount = 0;  //OUT UDP
    int dnscount = 0;  //IN DNS
    int odnscount = 0; //OUT DNS
    int counter = 0;
    long totald = 0;
    long totalu= 0;
    int syn = 0;
    int ack = 0;
    
    public Worker(JFrame f, RrdGraphDef g,int n,String s,JPanel ids,JLabel l) throws IOException, RrdException{
        frame = f;
        id = ids;
        id.add(test);
        gDef = g;
        num = n;
        str = s;
        label = l;
        hm = new HashMap<String,Integer>();
        Timer t = new Timer(1000, updateRRD);
        t.start();
        rrdDb = new RrdDb(rrd, RrdBackendFactory.getFactory("MEMORY"));
        rrd1 = new RrdDb(name);
        rrd2 = new RrdDb("/home/mkdeniz/download.rdd");
                
        sample = rrdDb.createSample();
        sample2 = rrd1.createSample();
        sample3 = rrd2.createSample();
        
    }
    
    ActionListener updateRRD = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
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
                    tmp = ((count)/1024)/1;
                    tmp2 = ((ucount)/1024)/1;
                    sample.setTime(Util.getTimestamp());
                    sample.setValue("a", tmp);
                    sample.setValue("b", tmp2);
                    label.setText("D/L:"+tmp+" U/L"+tmp2);
                    sample.update();
                    frame.repaint();
                    Date endTime = new Date();
                    Date startTime = new Date(endTime.getTime() - 300000);
                    gDef.setTimePeriod(startTime, endTime);
                    totald = totald + count;
                    sample3.setTime(Util.getTimestamp());
                    sample3.setValue("download", totald);
                    count = 0;
                    totalu = totalu + ucount;
                    sample3.setValue("upload", totalu);
                    sample3.update();
                    ack = 0;
                    ucount = 0;
                    for (String str : hm.keySet())
                        if (hm.get(str) > 20) id.add(new JLabel("Too Many Activity to host" + " " + str +" "+ new Date().toString())); 
                    hm.clear();
                    if (syn > 10) id.add(new JLabel("Too Many syn" + "Possible SYN FLOOD " + new Date().toString())); 
                    syn = 0;
                    Thread.sleep(500);
                } catch (RrdException | IOException | InterruptedException ex) {
                    Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

    @Override
    public void run() {
        System.out.println(str);
        String[] host = str.split("\\.");
        str2 = str2 + host[0] + "." + host[1] + "." + host[2];
        System.out.println(host.length);
        frame.repaint();
        frame.requestFocus();
        List<PcapIf> alldevs = new ArrayList<>();
        StringBuilder errbuf = new StringBuilder();
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
        PcapIf device = alldevs.get(num);
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
            @Override
            public void nextPacket(PcapPacket packet, String user)  {
                
                if (packet.hasHeader(ip)/* && tcount == 7*/){
                    byte[] sIP = packet.getHeader(ip).source();
                    String s_IP = org.jnetpcap.packet.format.FormatUtils.ip(sIP);
                    byte[] dIP = packet.getHeader(ip).destination();
                    String d_IP = org.jnetpcap.packet.format.FormatUtils.ip(dIP);
                    if (packet.hasHeader(udp)){
                        if (packet.getHeader(udp).destination() == 68 ||packet.getHeader(udp).source() == 68   || packet.getHeader(udp).destination() == 67 || packet.getHeader(udp).source() == 67) {
                        int n = (packet.toHexdump().indexOf("0110:"));
                        String type_dhcp = packet.toHexdump().substring(n+46, n+45+2);
                            switch (type_dhcp) {
                                case "1":
                                    id.add(new JLabel("DHCP Discovery - "+s_IP+" : "+d_IP));
                                    break;
                                case "2":
                                    id.add(new JLabel("DHCP Offer - "+s_IP+" : "+d_IP));
                                    break;
                                case "3":
                                    id.add(new JLabel("DHCP Request - "+s_IP+" : "+d_IP));
                                    break;
                                case "5":
                                    id.add(new JLabel("DHCP Ack - "+s_IP+" : "+d_IP));
                                    break;
                                case "7":
                                    id.add(new JLabel("DHCP Release - "+s_IP+" : "+d_IP));
                                    break;
                            }
                        }
                    }
                    
                    if (!s_IP.equals(str) && !s_IP.startsWith(str2)){
                        if (hm.containsKey(s_IP)) {
                            int n = hm.get(s_IP);
                            n = n + 1;
                            hm.put(s_IP,n);
                        }
                        else {
                            hm.put(s_IP, 0);
                        }
                        count = count + packet.getPacketWirelen();
                        if (packet.hasHeader(tcp)){
                            if(packet.getHeader(tcp).source()== 80 ||packet.getHeader(tcp).destination()==80){
                                
                                hcount++;
                            }
                            else{
                                if (packet.getHeader(tcp).flags_ACK()) ack ++;
                                if (packet.getHeader(tcp).flags_SYN()) syn ++;
                                //else 
                                tcount++;
                            }
                        }
                        else if (packet.hasHeader(udp)){
                            if (packet.getHeader(udp).source()== 53 ||packet.getHeader(udp).destination()==53) {
                                dnscount++;
                            }
                            else
                                udcount++;
                        }
                    }
                    else if (s_IP.equals(str) || s_IP.startsWith(str2)){
                        ucount = ucount + packet.getPacketWirelen();
                        if (packet.hasHeader(tcp)){
                            if(packet.getHeader(tcp).source()== 80 ||packet.getHeader(tcp).destination()==80) ohcount++;
                            else{
                                if (packet.getHeader(tcp).flags_SYN()) syn ++;
                                if (packet.getHeader(tcp).flags_ACK()) ack ++;
                                otcount++;
                            }
                        }
                        else if (packet.hasHeader(udp)){
                            if (packet.getHeader(udp).source()== 53 ||packet.getHeader(udp).destination()==53) {
                                odnscount++;
                            }
                            else
                                oudcount++;
                        }
                    }
                }
            }
        };
        pcap.loop(0, jpacketHandler, "jNetPcap rocks!");
        pcap.close();
        }
    }