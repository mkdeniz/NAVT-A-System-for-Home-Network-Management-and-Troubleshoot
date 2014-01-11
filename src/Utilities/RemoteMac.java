package Utilities;

 import java.io.BufferedReader;
 import java.io.InputStream;
 import java.io.InputStreamReader;

public class RemoteMac {

public static String command;

public static void viewMac(String ip) {
    command = "/bin/arp -a " + ip;
    String process = null;
    String mac[] = new String[5];
    String rmac[] = new String[10];
    try {
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(command);
        InputStream inputstream = proc.getInputStream();
        InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
        String line;
        int i = 0;
        while ((line = bufferedreader.readLine()) != null) {
            mac[i] = line;

            i++;
        }

        rmac = mac[3].split("    ");
        System.out.println(rmac[2]);
    } catch (Exception e) {
        System.out.println("mac cant find");
    }

}
 }