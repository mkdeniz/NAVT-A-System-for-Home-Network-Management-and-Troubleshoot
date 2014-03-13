package uk.ac.gla.navt.main;


import java.util.ArrayList;
import java.util.Date;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author root
 */
public class flow {
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
    
    
    public flow(String s, String d, int sp, int dp, long t){
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
    
    public double varArr() {
        double sum = 0;
        double v;
        int l = arr.size();
        for (int i = 3; i < l; i++){
            v = arr.get(i)*0.001;
            sum = sum + Math.pow((this.avg()-v), 2.0);
        }
        return Math.sqrt((sum/(l-3)));
    }
    
    public double varArr2() {
        double m = 0;
        double v;
        int l = arr2.size();
        for (int i = 3; i < l; i++){
            v = arr2.get(i)*0.1024;
            m = m + Math.pow((this.avg2()-v), 2.0);
        }
        return (m/(l-3));
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
    
    public double avg() {
        double sum = 0;
        int l = arr.size();
        for (int i = 3; i < l; i++) {
            double v = arr.get(i)*0.001;
            sum = sum + v;
        }
        return (sum/(l-3));
            
    }
    
    public double avg2() {
        double sum = 0;
        int l = arr2.size();
        for (int i = 3; i < l; i++) {
            double v = arr2.get(i);
            sum = sum + v;
        }
        return (sum/(l-3));
            
    }
    
    public double getDur(){
        double intv = this.end.getTime() - this.start.getTime();
        return intv*0.001;
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
            long intv = tmp.getTime() - this.end.getTime();
            double intV = tmp.getTime() - this.end.getTime();
            if (this.min_int > intv && intv>0.000000000000000000000000000000000){
                this.min_int = (intv*0.001);
            }
            this.end = tmp;
            arr.add(intV);
            double a = b;
            arr2.add(a);
            this.traffic = this.traffic + a;
        }
    } 
    
    public void in(){
        in++;
    }
    public void out() {
        out++;
    }
    
    public String End() {
        this.end = new Date();
        this.open = false;
        double t = this.traffic/1024;
        String s = "";
        s = s + "Flow: " + this.s_ip + ":" + this.s_port + " - " + this.d_ip + ":" + this.d_port +// + " - " + this.start +  " - " + this.end + "\n" +
                "\nInterarrival Time: "+this.min_int+" : "+ this.avg() +" : "+ this.varArr() +
                "\nPackets: " + this.in + " : " + this.out + " : " + this.avg2()/1024 + " : " + this.varArr2() +
                "\nTotal Duration and Traffic: " + this.getDur() + " : " + t + " : " + t/this.noPack +
                "\n"+this.s_port+","+this.d_port+","+this.avg()+","+this.varArr();
        return s+"\n";
    }
    
    
            
}
