package uk.ac.gla.navt.main;

import org.jnetpcap.packet.PcapPacket;


public class BoundedBuffer {
	
	private static final int d_size = 25;
	private int capacity = 0; 
	private int out = -1;
	private int in = -1;
	private PcapPacket[] BB;
	int size;
	
	public BoundedBuffer(){
		this.BB = new PcapPacket[d_size];
		this.size = d_size;
	}

	public BoundedBuffer(int s){
		this.BB = new PcapPacket[s];
		this.size = s;
		
	}
        
	public synchronized void insert(PcapPacket packet){
		while (capacity == size) {
			try {
				System.out.format("\nBuffer is full - Prod %s waiting\n",
						Thread.currentThread().getName());
				wait();
			} catch (InterruptedException e) { }
		}
		in = (in + 1) % size;
		BB[in] = packet;
		capacity++;
		notifyAll();
	}
        
	public synchronized PcapPacket remove() {
		while (capacity == 0){
			try {
				System.out.format("\nBuffer is empty - Cons %s"
						+ "waiting\n", Thread.currentThread().getName());
				wait();
			} catch (InterruptedException e) { }
		}
		out = (out + 1) % size;
		PcapPacket k = BB[out];
		capacity--;
		notifyAll();
		return k;
	}
}
