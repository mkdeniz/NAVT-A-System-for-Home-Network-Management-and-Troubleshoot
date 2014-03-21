package uk.ac.gla.navt.main;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
 
public class ForkWorker extends SwingWorker<String,String> {
  
  private JTextArea output; // Where to redirect STDERR & STDOUT to
  private ProcessBuilder builder;
  
  public ForkWorker(JTextArea output, ProcessBuilder builder) {
    this.output=output;
    this.builder= builder;
  }
  
  protected void process(java.util.List<String> chunks) {
    // Done on the event thread
    Iterator<String> it = chunks.iterator();
    while (it.hasNext()) {
      output.append(it.next());
    }
  }
  
  public String doInBackground() {
    Process process;
    try {
      process = builder.start();
      InputStream res = process.getInputStream();
      byte[] buffer = new byte[100];
      int len;
      while ( (len=res.read(buffer,0,buffer.length))!=-1) {
        publish(new String(buffer,0,len));
        if (isCancelled()) {
          process.destroy();
          return "";
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return "";  // Don't care
  }
  
  protected void done() {
    // Done on the swing event thread
    output.append("\nALL DONE");  
  }
}