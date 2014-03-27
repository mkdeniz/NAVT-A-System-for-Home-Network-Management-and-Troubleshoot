package uk.ac.gla.navt.main;

import java.util.ArrayList;
import java.util.Date;

public class Flow {
    ArrayList<Double> arr;
    ArrayList<Double> arr2;
    double min_int;
    double max_int;
    String s_ip;
    String d_ip;
    int s_port;
    int d_port;
    double traffic;
    Date start;
    Date end;
    double noPack;
    boolean open;
    int in;
    int out;
    
    public Flow(String s, String d, int sp, int dp, long t){
        this.open = true;
        this.arr = new ArrayList<>();
        this.arr2 = new ArrayList<>();
        in = 0;
        out = 0;
        noPack = 0;
        s_ip = s;
        d_ip = d;
        s_port = sp;
        d_port = dp;
        traffic = t; 
        start = new Date();
        end = new Date();
        min_int = Integer.MAX_VALUE;
        max_int= Integer.MIN_VALUE;
    }
    
    public double varTime() {
        double sum = 0;
        double v;
        int l = arr.size();
        double mean = this.meanTime();
        for (int i = 3; i < l; i++){
            v = arr.get(i);
            sum = sum + Math.pow((mean-v), 2.0);
        }
        return Math.sqrt((sum/(l-3)))*0.001;
    }
    
    public double varPacket() {
        double m = 0;
        double v;
        int l = arr2.size();
        double mean = this.meanPacket();
        for (int i = 3; i < l; i++){
            v = arr2.get(i);
            m = m + Math.pow((mean-v), 2.0);
        }
        return (m/(l-3))/1024;
    }
    
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
    
    public double meanTime() {
        double sum = 0;
        int l = arr.size();
        for (int i = 3; i < l; i++) {
            double v = arr.get(i);
            sum = sum + v;
        }
        return (sum/(l-3));
            
    }
    
    public double meanPacket() {
        double sum = 0;
        int l = arr2.size();
        for (int i = 3; i < l; i++) {
            double v = arr2.get(i);
            sum = sum + v;
        }
        return (sum/(l-3));       
    }
    
    @Override
    public String toString() {
        return "\n";
    }
    
    public ArrayList<Double> getList() {
        return this.arr;
    }
    
    public void updateByte(long b) {
        if (this.open) {
            this.noPack++;
            Date tmp = new Date();
            double intV = tmp.getTime() - this.end.getTime();
            this.end = tmp;
            arr.add(intV);
            arr2.add((double)b);
            this.traffic = this.traffic + b;
        }
    }
    
    public String End() {
        this.end = new Date();
        this.open = false;
        double t = this.traffic/1024;
        String s = "";
        return "" + this.s_port+"," + this.d_port + "," + this.meanTime()*0.001 +","+ this.varTime() +","+ this.meanPacket()/1024 + "," + this.varPacket() + "\n";}
         
}
