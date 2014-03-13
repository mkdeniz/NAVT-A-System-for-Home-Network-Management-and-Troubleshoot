package uk.ac.gla.navt.main;  

import uk.ac.gla.navt.utilities.Classifier;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;  
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
  
import org.jnetpcap.Pcap;  
import org.jnetpcap.PcapIf;  
import org.jnetpcap.packet.PcapPacket;  
import org.jnetpcap.packet.PcapPacketHandler;  
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jrobin.core.RrdException;
import org.jrobin.core.Util;

public class test {  
  
    protected static HashMap<String,flow> flows = new HashMap<>();
    protected static BoundedBuffer bb = new BoundedBuffer(100);
    
    protected static Queue<Integer> queue = new LinkedList();
    
    public static int n = 0;
    public static int syn = 0;
    public static int fin = 0;
    public static double c = 0.1;
    
    public test() {
        Timer t = new Timer(1000, updateRRD);
        t.start();
    }
    
    ActionListener updateRRD = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int delta = syn-fin;
                double x = fin * (1 -c);
                queue.add(syn-fin);
            }
        };
    
    public static void main(String[] args) throws IOException {  
        //final Classifier c = new Classifier();
        test t = new test();
        //int 
        List<PcapIf> alldevs = new ArrayList<>(); // Will be filled with NICs  
        StringBuilder errbuf = new StringBuilder(); // For any error msgs  
        final Tcp tcp = new Tcp();
        final Ip4 ip = new Ip4();
        /*************************************************************************** 
         * First get a list of devices on this system 
         **************************************************************************/  
        int r = Pcap.findAllDevs(alldevs, errbuf);  
        if (r != Pcap.OK || alldevs.isEmpty()) {  
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
  
        PcapIf device = alldevs.get(2); // We know we have atleast 1 device 
        System.out  
            .printf("\nChoosing '%s' on your behalf:\n",  
                (device.getDescription() != null) ? device.getDescription()  
                    : device.getName());  
  
        /*************************************************************************** 
         * Second we open up the selected device 
         **************************************************************************/  
        int snaplen = 64 * 1024;           // Capture all packets, no trucation  
        int flags = Pcap.MODE_PROMISCUOUS; // capture all packets  
        int timeout = 10*1000;           // 10 seconds in millis  
        Pcap pcap =  
            Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);  
        
        if (pcap == null) {  
            System.err.printf("Error while opening device for capture: "  
                + errbuf.toString());  
            return;  
        } 
  
        /*************************************************************************** 
         * Third we create a packet handler which will receive packets from the 
         * libpcap loop. 
         **************************************************************************/  
        PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() {  
  
            @Override
            public void nextPacket(PcapPacket packet, String user) {
                if (packet.hasHeader(ip)) {
                    byte[] sIP = packet.getHeader(ip).source();
                    String s_IP = org.jnetpcap.packet.format.FormatUtils.ip(sIP);
                    byte[] dIP = packet.getHeader(ip).destination();
                    String d_IP = org.jnetpcap.packet.format.FormatUtils.ip(dIP);
                    if (packet.hasHeader(tcp)) {
                        flow f = new flow(s_IP,d_IP,packet.getHeader(tcp).source(),packet.getHeader(tcp).destination(),packet.getTotalSize());
                        if (tcp.flags_SYN())
                            syn++;
                        if (tcp.flags_FIN())
                            fin++;
                                   
                        flow temp;
                        if (flows.isEmpty()){
                            f.updateByte(packet.getTotalSize());
                            flows.put(f.check(), f);
                            if (!s_IP.equals("10.23.194.252")) f.in();
                            else f.out();
                        }
                        else if (flows.containsKey(f.check())){
                            temp = flows.get(f.check());
                            if (packet.getHeader(tcp).flags_FIN() && s_IP.equals("10.23.194.252")) {
                                 //System.out.println(c.KNNClassify(temp.avg(), temp.varArr()));
                                System.out.println(temp.End());
                                System.out.println(flows.remove(f.check()));
                            }
                            else{
                            if (!s_IP.equals("10.23.194.252")) temp.in();
                            else temp.out();
                            temp.updateByte(packet.getTotalSize());
                            flows.put(f.check(), temp);}
                        }
                        
                        else if (flows.containsKey(f.check2())){
                            temp = flows.get(f.check2());
                            if (packet.getHeader(tcp).flags_FIN() && !s_IP.equals("10.23.194.252")) {
                                System.out.println(temp.End());
                                System.out.println(flows.remove(f.check()));
                            }
                            else{
                            if (!s_IP.equals("10.23.194.252")){
                                temp.in();
                            }
                            else temp.out();
                            temp.updateByte(packet.getTotalSize());
                            flows.put(f.check2(), temp);}
                        }
                                               
                        else {
                            f.updateByte(packet.getTotalSize());
                            flows.put(f.check(), f);
                            if (!s_IP.equals("10.23.194.252")) f.in();
                            else f.out();}
                    }
                }
            }
        };  
  
        /*************************************************************************** 
         * Fourth we enter the loop and tell it to capture 10 packets. The loop 
         * method does a mapping of pcap.datalink() DLT value to JProtocol ID, which 
         * is needed by JScanner. The scanner scans the packet buffer and decodes 
         * the headers. The mapping is done automatically, although a variation on 
         * the loop method exists that allows the programmer to sepecify exactly 
         * which protocol ID to use as the data link type for this pcap interface. 
         **************************************************************************/  
        pcap.loop(0, jpacketHandler, "jNetPcap rocks!");  
  
        /*************************************************************************** 
         * Last thing to do is close the pcap handle 
         **************************************************************************/  
        
        pcap.close();  
    }  
}  