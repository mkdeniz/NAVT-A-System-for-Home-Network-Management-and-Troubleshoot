package uk.ac.gla.navt.process;

 /**
  * A Worker Thread to provide low level packet capturing and flow creation.
  * 
  * @Author Mehmet Kemal Deniz
  * @Date 27/03/2014
  * 
  */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
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
    
    /*Temporal Storage Maps*/
    protected static ConcurrentHashMap<String,UFlow> uflows;
    protected static ConcurrentHashMap<String,Flow> flows;
    
    /*Network Protocols*/
    protected Ip4 ip = new Ip4();
    protected Tcp tcp = new Tcp();
    protected Udp udp = new Udp();
    
    /*Database variables*/
    protected RrdGraphDef gDef;
    protected Sample sample;
    protected RrdDb rrdDb;
    
    /*Java Swing Components*/
    protected DefaultTableModel tableModel;
    protected JLabel statusBar;
    protected JFrame frame;
    protected int choice;
    protected String IP;
    
    /*Values to hold download and upload bandwitdh*/
    protected AtomicLong download = new AtomicLong();
    protected AtomicLong upload = new AtomicLong();
   
    
    /**
     * Worker Constructor
     * 
     * @param f Copy of main screen frame
     * @param g RRD graph definition
     * @param c interface choice
     * @param i IP address of machine
     * @param m model of the swing table component
     * @param s status bar label
     * 
     * @throws java.io.IOException
     * @throws org.jrobin.core.RrdException
     * 
     */
    public Worker(JFrame f, RrdGraphDef g, int c, String i, DefaultTableModel m, 
            JLabel s) throws IOException, RrdException{
        rrdDb = new RrdDb("file.rrd", RrdBackendFactory.getFactory("MEMORY"));
        Timer t1 = new Timer(3600000, refresh);
        Timer t2 = new Timer(1100, updateRRD);
        uflows = new ConcurrentHashMap<>();
        flows = new ConcurrentHashMap<>();
        sample = rrdDb.createSample();
        //host = i.split("\\.");
        tableModel = m;
        statusBar = s;
        choice = c;
        frame = f;
        gDef = g;
        IP = i;
        
        t1.start();
        t2.start();

    }
    
    /**
     * A method to clear temporal storage
     * 
     */
    ActionListener refresh = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uflows.clear();
                flows.clear();
            }
        };
    
    /**
     * A method to update bandwidth
     * utilisation
     * 
     */
    ActionListener updateRRD = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //Capture current values and reset them
                    double tmp = ((download.get())/1024)/1.1;
                    double tmp2 = ((upload.get())/1024)/1.1;
                    download.set(0);
                    upload.set(0);
                    
                    //Create the sample of current values and update graph
                    sample.setTime(Util.getTimestamp());
                    sample.setValue("a", tmp);
                    sample.setValue("b", tmp2);
                    statusBar.setText("D/L:"+tmp+" U/L"+tmp2);
                    sample.update();
                    Date endTime = new Date();
                    Date startTime = new Date(endTime.getTime() - 300000);
                    gDef.setTimePeriod(startTime, endTime);
                    
                    //Refresh the panel
                    frame.repaint();
                } catch (IOException | RrdException ex) {
                    Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, 
                            null, ex);
                }
            }
        };

   /**
     * A method to process UDP packets
     * 
     * @param s_IP source IP address
     * @param d_IP destination IP address
     * @param s_P source Port
     * @param d_P destination Port
     * @param b packet size
     * 
     */
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
    
    /**
     * A method to process TCP packets
     * They are matched with the existing 
     * flows or if not matched new flow is
     * created.
     * 
     * @param s_IP source IP address
     * @param d_IP destination IP address
     * @param s_P source Port
     * @param d_P destination Port
     * @param b packet size
     * @param flag fin packet of TCP packet
     * 
     */
    public void processTCP(String s_IP, String d_IP, int s_P, int d_P, long b, 
            boolean flag) {
        Flow tF = new Flow(s_IP, d_IP, s_P, d_P, b);
        Flow temp;
        if (flows.containsKey(tF.check())){
            temp = flows.get(tF.check());
            if (flag && s_IP.equals(IP)) {
               temp.End();
               flows.remove(tF.check());
            }
            else{
                temp.updateByte(b);
                flows.put(tF.check(), temp);
            }
        }
        else if (flows.containsKey(tF.check2())){
            temp = flows.get(tF.check2());
            if (flag && !s_IP.equals(IP)) {
                temp.End();
                flows.remove(tF.check());
            }
            else{
                temp.updateByte(b);
                flows.put(tF.check2(), temp);
            }
        }

        else {
            tF.updateByte(b);
            flows.put(tF.check(), tF);
        }
    }
    
    /**
     * A method to process DHCP protocol
     * and update events according to the
     * DHCP events decoded.
     * 
     * @param s_IP source IP address
     * @param d_IP destination IP address
     * @param packet IP packet captured
     * @param model Swing table model
     * 
     */
    public void processDHCP(String s_IP, String d_IP, PcapPacket packet, 
            DefaultTableModel model){
        int n = (packet.toHexdump().indexOf("0110:"));
        String type_dhcp = packet.toHexdump().substring(n+46, n+45+2);
            switch (type_dhcp) {
                case "1":
                     model.addRow(new Object[]{"DHCP Discovery",s_IP + " : "
                             + d_IP});
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
        
        frame.repaint();
        frame.requestFocus();
        
        List<PcapIf> alldevs = new ArrayList<>();
        StringBuilder errbuf = new StringBuilder();
        Pcap.findAllDevs(alldevs, errbuf);
        
        PcapIf device = alldevs.get(choice);
       
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
                long size = packet.getPacketWirelen();
                if (packet.hasHeader(ip)) {
                    
                    //Get the destination and source IP addesses of the packet
                    byte[] sIP = packet.getHeader(ip).source();
                    String s_IP = org.jnetpcap.packet.format.FormatUtils.ip(sIP);
                    byte[] dIP = packet.getHeader(ip).destination();
                    String d_IP = org.jnetpcap.packet.format.FormatUtils.ip(dIP);
                    
                    //Process DHCP packet.
                    if (packet.hasHeader(udp)){
                        //DHCP uses ports: 68 and 67
                        if (packet.getHeader(udp).destination() == 68 || 
                                packet.getHeader(udp).source() == 68   || 
                                packet.getHeader(udp).destination() == 67 || 
                                packet.getHeader(udp).source() == 67) {
                            processDHCP(s_IP, d_IP, packet,tableModel);
                        }
                    }
                    //Incoming packet
                    if (!s_IP.equals(IP)){
                        download.set(download.get() + size); //Update Download Value
                        
                        //Process flows according to the protocol of packet
                        if (packet.hasHeader(udp)){
                            processUDP(s_IP, d_IP, packet.getHeader(udp).source(), 
                                    packet.getHeader(udp).destination(), size);
                        }
                        else if (packet.hasHeader(tcp)) {
                            boolean flag = packet.getHeader(tcp).flags_FIN();
                            processTCP(s_IP, d_IP, packet.getHeader(tcp).source(), 
                                    packet.getHeader(tcp).destination(), size, flag);
                        } 
                    }
                    //Outgoing packet
                    else if (s_IP.equals(IP)){
                        upload.set(upload.get() + size); //Update Upload Value
                        
                        //Process flows according to the protocol of packet
                        if (packet.hasHeader(udp)){
                            processUDP(s_IP, d_IP, packet.getHeader(udp).source(), 
                                    packet.getHeader(udp).destination(), size);
                        }
                        else if (packet.hasHeader(tcp)) {
                            boolean flag = packet.getHeader(tcp).flags_FIN();
                            processTCP(s_IP, d_IP, packet.getHeader(tcp).source(), 
                                    packet.getHeader(tcp).destination(), size, flag);
                        } 
                    }
                }
            }
        };
        pcap.loop(0, jpacketHandler, "Packet Captured");
        pcap.close();
        }
    }