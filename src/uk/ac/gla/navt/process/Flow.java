package uk.ac.gla.navt.process;


/**
 * A Flow object class to allow flow creation and statistical value calculation
 * such as variance and mean of the flow features.
 * 
 * @Author Mehmet Kemal Deniz
 * @Date 27/03/2014

*/


import java.util.ArrayList;
import java.util.Date;

public class Flow {
    //List to hold flow values
    ArrayList<Double> arrivalTimes;
    ArrayList<Double> packetSizes;
   
    double traffic; //Total traffic in both directions
    boolean open;   //To check wheter flow ended or not
    
    //Source and Destination IP address and Port numbers
    String s_ip;    
    String d_ip;
    int s_port;
    int d_port;
    
    //Start and End Time of the Flow
    Date start;
    Date end;
    
    /**
     * Default constructor
     *
     * @param s Source IP
     * @param d Destination IP
     * @param sp Source Port
     * @param dp Destination Port
     * @param t size of packet
     */
    public Flow(String s, String d, int sp, int dp, long t){
        arrivalTimes = new ArrayList<>();
        packetSizes = new ArrayList<>();
        start = new Date();
        end = new Date();
        s_port = sp;
        d_port = dp;
        traffic = t; 
        open = true;
        s_ip = s;
        d_ip = d;
    }
    
    /**
     * A method to calculate variance of inter-arrival times of packets
     * 
     * @return variance 
     * 
     * */
    public double varTime() {
        double sum = 0;
        int l = arrivalTimes.size();
        double mean = this.meanTime();
        for (int i = 3; i < l; i++){
            sum = sum + Math.pow((mean-arrivalTimes.get(i)), 2.0);
        }
        return Math.sqrt((sum/(l-3)))*0.001;
    }
    
    /**
     * A method to calculate variance of sizes of packets
     * 
     * @return variance 
     * 
     * */
    public double varPacket() {
        double m = 0;
        int l = packetSizes.size();
        double mean = this.meanPacket();
        for (int i = 3; i < l; i++){
            m = m + Math.pow((mean-packetSizes.get(i)), 2.0);
        }
        return (m/(l-3))*0.1024;
    }
    
    /**
     * A method to calculate mean of inter-arrival times of packets
     * 
     * @return variance 
     * 
     * */
    public double meanTime() {
        double sum = 0;
        int l = arrivalTimes.size();
        for (int i = 3; i < l; i++) {
            sum = sum + arrivalTimes.get(i);
        }
        return (sum/(l-3));
    }
    
    /**
     * A method to calculate mean of sizes of packets
     * 
     * @return variance 
     * 
     * */
    public double meanPacket() {
        double sum = 0;
        int l = packetSizes.size();
        for (int i = 3; i < l; i++) {
            sum = sum + packetSizes.get(i);
        }
        return (sum/(l-3));       
    }
    
    @Override
    public String toString() {
        return "" + s_port + "," + d_port + "," + meanTime()*0.001 + "," +
                varTime() + "," + meanPacket()/1024 + "," + varPacket();
    }
    
    /**
     * A method to update flow
     * 
     * @param b total bytes in the packet
     * 
     * */
    public void updateByte(long b) {
        if (this.open) {
            Date tmp = new Date();
            double intV = tmp.getTime() - this.end.getTime();
            end = tmp;
            arrivalTimes.add(intV);
            packetSizes.add((double)b);
            traffic = this.traffic + b;
        }
    }
    
    /**
     * A method to end flow
     * 
     * @return Overview of the flow
     * 
     * */
    public String End() {
        end = new Date();
        open = false;
        return toString();
    }
    
    /**
     * A method to get the rate of traffic flow
     * 
     * @return rate KB/s
     * 
     * */
    public double getRate() {
        double tmp = this.traffic;
        double dur = (end.getTime() - start.getTime())/1000; //seconds
        if (dur == 0 || tmp == 0) return 0;
        return tmp/dur; 
    }
    
    public String check() {
        String s = "";
        s = s + this.s_ip + " : " + this.s_port + " : " + this.d_ip + " : " + this.d_port;
        return s;
    }
    
    public String check2() {
        String s = "";
        s = s + this.d_ip + " : " + this.d_port + " : " + this.s_ip + " : " + this.s_port;
        return s;
    }
         
}
