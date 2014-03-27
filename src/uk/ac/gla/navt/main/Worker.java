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
import javax.swing.JPanel;
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
    protected final String name = "/home/mkdeniz/tcp.rdd";
    protected Flow f;
    protected int num;
    protected String str;
    protected JPanel id;
    protected String str2 = "";
    protected JLabel label;
    protected JLabel test = new JLabel();
    protected RrdDb rrdDb;
    protected RrdDb rrd1;
    protected RrdDb rrd2;
    protected Sample sample;
    protected Sample sample2;
    protected Sample sample3;
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
        Timer t2 = new Timer(10000, removeUDP);
        Timer t3 = new Timer(3600000, refresh);
        t.start();
        t2.start();
        rrdDb = new RrdDb(rrd, RrdBackendFactory.getFactory("MEMORY"));
        sample = rrdDb.createSample();
    }
    
    ActionListener removeUDP = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(String str : uflows.keySet()){
                    if (!uflows.get(str).open)
                        uflows.remove(str);
                }  
                for(String str : flows.keySet()){
                    if (!flows.get(str).open)
                        flows.remove(str);
                }  
            }
        };
    
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
                        int n = (packet.toHexdump().indexOf("0110:"));
                        String type_dhcp = packet.toHexdump().substring(n+46, n+45+2);
                            switch (type_dhcp) {
                                case "1":
                                     tableModel.addRow(new Object[]{"DHCP Discovery",s_IP + " : "+ d_IP});
                                    break;
                                case "2":
                                    tableModel.addRow(new Object[]{"DHCP Offer",s_IP+" : "+d_IP});
                                    break;
                                case "3":
                                    tableModel.addRow(new Object[]{"DHCP Request",s_IP+" : "+d_IP});
                                    break;
                                case "5":
                                    tableModel.addRow(new Object[]{"DHCP Ack",s_IP+" : "+d_IP});
                                    break;
                                case "7":
                                    tableModel.addRow(new Object[]{"DHCP Release - ",s_IP+" : "+d_IP});
                                    break;
                            }
                        }
                    }
                    if (!s_IP.equals(str)){// && !s_IP.startsWith(str2)){
                        count = count + packet.getPacketWirelen();
                        if (packet.hasHeader(udp)){
                            UFlow f = new UFlow(s_IP,d_IP,packet.getHeader(udp).source(),packet.getHeader(udp).destination(),packet.getTotalSize());
                            UFlow temp;
                            if (uflows.containsKey(f.check())){
                                f.destroy();
                                temp = (UFlow) uflows.get(f.check());
                                temp.updateByte(packet.getPacketWirelen());
                                uflows.put(f.check(), temp);
                            }
                            else if (uflows.containsKey(f.check2())){
                                f.destroy();
                                temp = (UFlow) uflows.get(f.check2());
                                    temp.updateByte(packet.getPacketWirelen());
                                    uflows.put(f.check2(), temp);
                            }
                            else {
                                f.updateByte(packet.getTotalSize());
                                uflows.put(f.check(), f);
                            }
                        }
                        else if (packet.hasHeader(tcp)) {
                            Flow f = new Flow(s_IP,d_IP,packet.getHeader(tcp).source(),packet.getHeader(tcp).destination(),packet.getTotalSize());
                            Flow temp;
                            if (flows.containsKey(f.check())){
                                temp = flows.get(f.check());
                                if (packet.getHeader(tcp).flags_FIN() && s_IP.equals(str)) {
                                   temp.End();
                                   flows.remove(f.check());
                                }
                                else{
                                    temp.updateByte(packet.getPacketWirelen());
                                    flows.put(f.check(), temp);
                                }
                            }
                            else if (flows.containsKey(f.check2())){
                                temp = flows.get(f.check2());
                                if (packet.getHeader(tcp).flags_FIN() && !s_IP.equals(str)) {
                                    temp.End();
                                    flows.remove(f.check());
                                }
                                else{
                                    temp.updateByte(packet.getPacketWirelen());
                                    flows.put(f.check2(), temp);
                                }
                            }
                            
                            else {
                                f.updateByte(packet.getTotalSize());
                                flows.put(f.check(), f);
                            }
                        } 
                    }
                    else if (s_IP.equals(str)){
                        ucount = ucount + packet.getPacketWirelen();
                        if (packet.hasHeader(udp)){
                            UFlow f = new UFlow(s_IP,d_IP,packet.getHeader(udp).source(),packet.getHeader(udp).destination(),packet.getTotalSize());
                            UFlow temp;
                            if (uflows.containsKey(f.check())){
                                f.destroy();
                                temp = (UFlow) uflows.get(f.check());
                                temp.updateByte(packet.getPacketWirelen());
                                uflows.put(f.check(), temp);
                            }
                            else if (uflows.containsKey(f.check2())){
                                f.destroy();
                                temp = (UFlow) uflows.get(f.check2());
                                    temp.updateByte(packet.getPacketWirelen());
                                    uflows.put(f.check2(), temp);
                            }
                            else {
                                f.updateByte(packet.getTotalSize());
                                uflows.put(f.check(), f);
                            }
                        }
                        
                        else if (packet.hasHeader(tcp)) {
                            Flow f = new Flow(s_IP,d_IP,packet.getHeader(tcp).source(),packet.getHeader(tcp).destination(),packet.getTotalSize());
                            Flow temp;
                            if (flows.containsKey(f.check())){
                                temp = flows.get(f.check());
                                if (packet.getHeader(tcp).flags_FIN() && s_IP.equals(str)) {
                                    temp.End();
                                    flows.remove(f.check());
                                }
                                else{
                                    temp.updateByte(packet.getPacketWirelen());
                                    flows.put(f.check(), temp);
                                }
                            }
                            else if (flows.containsKey(f.check2())){
                                temp = flows.get(f.check2());
                                if (packet.getHeader(tcp).flags_FIN() && !s_IP.equals(str)) {
                                    temp.End();
                                    flows.remove(f.check());
                                }
                                else{
                                    temp.updateByte(packet.getPacketWirelen());
                                    flows.put(f.check2(), temp);
                                }
                            }
                            
                            else {
                                f.updateByte(packet.getTotalSize());
                                flows.put(f.check(), f);
                            }
                        }
                    }
                }
            }
        };
        pcap.loop(0, jpacketHandler, "jNetPcap rocks!");
        pcap.close();
        }
    }