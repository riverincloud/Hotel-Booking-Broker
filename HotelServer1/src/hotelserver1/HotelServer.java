package hotelserver1;

import java.io.*;
import java.net.*;

/**
 *
 * @author Di
 * The hotel server maintain its own database and 
 * communicate with clients through the hotel booking broker server.
 */
public class HotelServer {    
    /**
     *
     */
    public static int HOTELPORT = 8189;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        ServerSocket serverSocket = null;        
        try {
            serverSocket = new ServerSocket(HOTELPORT);
        } catch(IOException e) {
            System.out.println(e);
            System.exit(1);
        }
        System.out.println("Hotel Server 1 Running");
        
        while(true) {
            Socket brokerSock = null;
            try {
                brokerSock = serverSocket.accept();
            } catch(IOException e) {
                System.out.println(e);
                continue;
            }
            
            new SocketHandler(brokerSock).start();   
            System.out.println("brokerSock Start");
        }
    }
}

class SocketHandler extends Thread {

    Socket brokerSock;

    SocketHandler(Socket brokerSock) {
	this.brokerSock = brokerSock;
        System.out.println("SocketHandler Constructed");
    }
    
    @Override
    public void run() {
	try {	    
	    BufferedReader reader =
		new BufferedReader(new InputStreamReader(
				   brokerSock.getInputStream()));				   
	    PrintStream printer =
		new PrintStream(brokerSock.getOutputStream());            
	    String str = reader.readLine();
	    System.out.println(brokerSock.getInetAddress() + ": " + str);
	    
	    boolean done = false;
	    while ( ! done) {
		str = reader.readLine();
		if (str == null) {
		    done = true;
                }
		else {
                    /**
                     * Broker forwarded "hotel"
                     * Reply with hotel data
                     */
                    if(str.trim().equalsIgnoreCase("HOTEL")) {
                        System.out.println("Broker says: " + str);
                        DBConnector dbc = new DBConnector();
                        String allHotels = dbc.findAllHotels();
                        printer.print(allHotels);
                        System.out.println("Server replies: " + str);
                    }
                    /**
                     * Broker forwarded "room"
                     * Reply with room data
                     */
                    if(str.trim().equalsIgnoreCase("ROOM")) {
                        System.out.println("Broker says: " + str);
                        DBConnector dbc = new DBConnector();
                        String allRooms = dbc.findAllRooms();
                        printer.print(allRooms);
                        System.out.println("Server replies: " + str);
                    }
                    /**
                     * Broker forwarded "booking"
                     * Reply with booking data
                     */
                    if(str.trim().equalsIgnoreCase("BOOKING")) {
                        System.out.println("Broker says: " + str);
                        DBConnector dbc = new DBConnector();
                        String allBookings = dbc.findAllBookings();
                        printer.print(allBookings);
                        System.out.println("Server replies: " + str);
                    }
                    /**
                     * Broker forwarded "book"
                     * Make a booking with given data
                     * Update database
                     */
                    if(str.trim().equalsIgnoreCase("BOOK")) {
                        System.out.println("Broker says: " + str);
                        DBConnector dbc = new DBConnector();
                        Thread.sleep(500);
                        String hotelId = reader.readLine();
                        System.out.println("hotelId: " + hotelId);
                        String roomId = reader.readLine();
                        System.out.println("roomId: " + roomId);
                        String checkIn = reader.readLine();
                        System.out.println("checkIn: " + checkIn);
                        String checkOut = reader.readLine();
                        System.out.println("checkOut: " + checkOut);
                        String name = reader.readLine();
                        System.out.println("name: " + name);
                        String email = reader.readLine();
                        System.out.println("email: " + email);
                        String creditCard = reader.readLine();
                        System.out.println("creditCard: " + creditCard);
                        try {
                            dbc.insertBooking(Integer.parseInt(hotelId), Integer.parseInt(roomId), 
                                              Integer.parseInt(checkIn), Integer.parseInt(checkOut), 
                                              name, email, creditCard);
                            System.out.println("New Booking Inserted Into Database");
                            printer.println("Booking Successful");
                        } catch (NumberFormatException e) {
                            System.out.println(e);
                            printer.println("Booking Failed");
                        }
                    }                    
                    /**
                     * Broker forwarded "bye"
                     * End
                     */
                    if(str.trim().equalsIgnoreCase("BYE"))
                    {
                        System.out.println("Broker says: " + str);
                        done = true;
                    }
		}		
	    }
	    try {
                brokerSock.close();
            } catch(IOException e) {
                System.out.println(e);
            }
            System.out.println("brokerSock Close");
            
	} catch(IOException | InterruptedException | NumberFormatException e) {
            System.out.println(e);
        }
    }
}

