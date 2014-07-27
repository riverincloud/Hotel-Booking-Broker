package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 *
 * @author Di
 * Multiple instances of the Client can communicate to three hotel servers concurrently.
 */
public class Client {
    
    public static final int CLIENTPORT = 9189;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        InetAddress address = null;        
        try {
            address = InetAddress.getLocalHost();
        } catch(UnknownHostException e) {
            System.out.println(e);
        }
        
        Socket clientSock = null;
        try {
            clientSock = new Socket(address, CLIENTPORT);
        } catch(IOException e) {
            System.out.println(e);
        }
        
        InputStream in = null;
        PrintStream printer = null;        
        try {
            in = clientSock.getInputStream();
            printer = new PrintStream(clientSock.getOutputStream());
        } catch(IOException e) {
            System.out.println(e);
        }        
        System.out.println("Client Running");
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        printer.println("Client says Hello to Broker!");
        
        Scanner scanner = new Scanner(System.in);
        String message = "";
        while(!message.trim().equalsIgnoreCase("BYE")) {
            String reply = null;
            try {
                //Wait until message comes
                while(!reader.ready()) {
                    System.out.println("Loading...");
                    Thread.sleep(1000);                  
                }
                while(reader.ready()) {
                    reply = reader.readLine();
                    if(reply.trim().equalsIgnoreCase("BYEBYE")) {
                        clientSock.close();
                    }
                    System.out.println(reply);
                }
            } catch(IOException | InterruptedException e) {
                System.out.println(e);
            }
            message = scanner.nextLine();
            printer.println(message);
        }
        
        try {
            clientSock.close();
        } catch(IOException e) {
            System.out.println(e);
        }
        System.out.println("Client Closed");        
    }
    
}