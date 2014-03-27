package uk.ac.gla.navt.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Ip4;
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
    protected Flow f;
    protected int num;
    protected String str;
    protected String str2 = "";
    protected JLabel label;
    protected RrdDb rrdDb;
    protected Sample sample;
    protected long count;
    protected long ucount;
    protected Ip4 ip;
    protected Tcp tcp;
    protected Udp udp;
    protected DefaultTableModel tableModel;
    protected static ConcurrentHashMap<String,Flow> flows;
    protected static ConcurrentHashMap<String,UFlow> uflows;
    
    public Worker(JFrame f, RrdGraphDef g, int n, String s,DefaultTableModel model, JLabel l) throws IOException, RrdException{
        flows = new ConcurrentHashMap<>();
        uflows = new ConcurrentHashMap<>();
        ip = new Ip4();
        tcp = new Tcp();
        udp = new Udp();
        frame = f;
        tableModel = model;
        gDef = g;
        num = n;
        str = s;
        label = l;
        Timer t = new Timer(1100, updateRRD);
        Timer t2 = new Timer(3600000, refresh);
        t.start();
        rrdDb = new RrdDb(rrd, RrdBackendFactory.getFactory("MEMORY"));
        sample = rrdDb.createSample();
    }
    
    ActionListener refresh = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uflows.clear();
                flows.clear();
            }
        };
    
    ActionListener updateRRD = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    long tmp = ((count)/1024)/1;
                    long tmp2 = ((ucount)/1024)/1;
                    sample.setTime(Util.getTimestamp());
                    sample.setValue("a", tmp);
                    sample.setValue("b", tmp2);
                    label.setText("D/L:"+tmp+" U/L"+tmp2);
                    sample.update();
                    Date endTime = new Date();
                    Date startTime = new Date(endTime.getTime() - 300000);
                    gDef.setTimePeriod(startTime, endTime);
                    frame.repaint();
                    count = 0;
                    ucount = 0;
                    
                } catch (IOException | RrdException ex) {
                    Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

    public void processUDP(String s_IP, String d_IP, int s_P, int d_P, long b) {
        UFlow uf = new UFlow(s_IP, d_IP, s_P, d_P, b);
        UFlow temp;
        if (uflows.containsKey(uf.check())){
            temp = uflows.get(uf.check());
            temp.updateByte(b);
            uflows.put(uf.check(), temp);
            uf.destroy();
        }
        
        else if (uflows.containsKey(uf.check2())){
            temp = uflows.get(uf.check2());
            temp.updateByte(b);
            uflows.put(uf.check2(), temp);
            uf.destroy();
        }
        
        else {
            uf.updateByte(b);
            uflows.put(uf.check(), uf);
        }
    }
    
    public void processTCP(String s_IP, String d_IP, int s_P, int d_P, long b, boolean flag) {
        Flow tf = new Flow(s_IP, d_IP, s_P, d_P, b);
        Flow temp;
        if (flows.containsKey(tf.check())){
            temp = flows.get(tf.check());
            if (flag && s_IP.equals(str)) {
               temp.End();
               flows.remove(tf.check());
            }
            else{
                temp.updateByte(b);
                flows.put(tf.check(), temp);
            }
        }
        else if (flows.containsKey(tf.check2())){
            temp = flows.get(tf.check2());
            if (flag && !s_IP.equals(str)) {
                temp.End();
                flows.remove(tf.check());
            }
            else{
                temp.updateByte(b);
                flows.put(tf.check2(), temp);
            }
        }

        else {
            tf.updateByte(b);
            flows.put(tf.check(), tf);
        }
    }
    
    @SuppressWarnings("empty-statement")
    public void processDHCP(String s_IP, String d_IP, PcapPacket packet, DefaultTableModel model){
        int n = (packet.toHexdump().indexOf("0110:"));
        String type_dhcp = packet.toHexdump().substring(n+46, n+45+2);
            switch (type_dhcp) {
                case "1":
                     model.addRow(new Object[]{"DHCP Discovery",s_IP + " : "+ d_IP});
                    break;
                case "2":
                    model.addRow(new Object[]{"DHCP Offer",s_IP+" : "+d_IP});
                    break;
                case "3":
                    model.addRow(new Object[]{"DHCP Request",s_IP+" : "+d_IP});
                    break;
                case "5":
                    model.addRow(new Object[]{"DHCP Ack",s_IP+" : "+d_IP});
                    break;
                case "7":
                    model.addRow(new Object[]{"DHCP Release - ",s_IP+" : "+d_IP});
                    break;
            }
    }
    
    @Override
    public void run() {
        String[] host = str.split("\\.");
        str2 = str2 + host[0] + "." + host[1] + "." + host[2];
        frame.repaint();
        frame.requestFocus();
        List<PcapIf> alldevs = new ArrayList<>();
        StringBuilder errbuf = new StringBuilder();
        int r = Pcap.findAllDevs(alldevs, errbuf);
        if (alldevs.isEmpty()) {
            System.err.printf("Can't read list of devices, error is %s", errbuf.toString());
            return;
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
        }        PcapPacketHandler<String> jpacketHandler;
        jpacketHandler = new PcapPacketHandler<String>() {
            @Override
            public void nextPacket(PcapPacket packet, String user)  {
                if (packet.hasHeader(ip)) {
                    byte[] sIP = packet.getHeader(ip).source();
                    String s_IP = org.jnetpcap.packet.format.FormatUtils.ip(sIP);
                    byte[] dIP = packet.getHeader(ip).destination();
                    String d_IP = org.jnetpcap.packet.format.FormatUtils.ip(dIP);
                    if (packet.hasHeader(udp)){
                        if (packet.getHeader(udp).destination() == 68 ||packet.getHeader(udp).source() == 68   || packet.getHeader(udp).destination() == 67 || packet.getHeader(udp).source() == 67) {
                            processDHCP(s_IP, d_IP, packet,tableModel);
                        }
                    }
                    if (!s_IP.equals(str)){
                        count = count + packet.getPacketWirelen();
                        if (packet.hasHeader(udp)){
                            processUDP(s_IP, d_IP, packet.getHeader(udp).source(), packet.getHeader(udp).destination(), packet.getPacketWirelen());
                        }
                        else if (packet.hasHeader(tcp)) {
                            processTCP(s_IP, d_IP, packet.getHeader(tcp).source(), packet.getHeader(tcp).destination(), packet.getPacketWirelen(), packet.getHeader(tcp).flags_FIN());
                        } 
                    }
                    else if (s_IP.equals(str)){
                        ucount = ucount + packet.getPacketWirelen();
                        if (packet.hasHeader(udp)){
                            processUDP(s_IP, d_IP, packet.getHeader(udp).source(), packet.getHeader(udp).destination(), packet.getPacketWirelen());
                        }
                        else if (packet.hasHeader(tcp)) {
                            processTCP(s_IP, d_IP, packet.getHeader(tcp).source(), packet.getHeader(tcp).destination(), packet.getPacketWirelen(), packet.getHeader(tcp).flags_FIN());
                        } 
                    }
                }
            }
        };
        pcap.loop(0, jpacketHandler, "jNetPcap rocks!");
        pcap.close();
        }
    }
