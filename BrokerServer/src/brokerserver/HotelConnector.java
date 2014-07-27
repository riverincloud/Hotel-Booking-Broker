package brokerserver;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author Di
 * 
 * The HotelConnector class connects the Broker Server with 3 Hotel Servers.
 * Retrieve and manipulate data received from the Servers.
 */
public class HotelConnector {
    
    private int hotelPort;
    InetAddress address = null;
    Socket hotelSock = null;
    BufferedReader reader = null;
    PrintStream printer = null;
    
    /**
     * Constructor
     * Connect server with the port number and address
     * @param strAddress 
     * @param portNum
     */
    public HotelConnector(String strAddress, int portNum) {
        try {
            address = InetAddress.getByName(strAddress);
        } catch(UnknownHostException e) {
            System.out.println(e);
        }
        this.hotelPort = portNum;        
        System.out.println("HotelConnector Constructed");
    }
    
    /**
     * Create a Hotel object with each hotel's data retrieved from servers
     * @return 
     */
    public Hotel createHotelObject() {
        System.out.println("Calling createHotelObject()");
        Hotel hotel = new Hotel();        
        
        connectHotelServer();
        
        try {
            printer.println("HOTEL");
            Thread.sleep(500);
            while(reader.ready()) {
                String result = reader.readLine();
                String str[] = result.split(" ");
                hotel = new Hotel(Integer.parseInt(str[0]),str[1],str[2],Integer.parseInt(str[3]));
            }
        } catch(IOException | InterruptedException | NumberFormatException e) {
            System.out.println(e);
        } 
        System.out.println("Hotel Object: " + hotel);
        
        try {
            hotelSock.close();
        } catch(IOException e) {
            System.out.println(e);
        }
        System.out.println("hotelSock Close");
        
        return hotel;
    }
    
    /**
     * Create a Room object with each room's data retrieved from servers
     * @return a List of Room objects
     */
    public List<Room> createRoomList() {        
        System.out.println("Calling createRoomList()");
        List<Room> roomList = new ArrayList<>();
        
        connectHotelServer();
        
        try {
            printer.println("ROOM");
            Thread.sleep(500);
            while(reader.ready()) {
                String result = reader.readLine();
                String str[] = result.split(" ");
                Room room = new Room(Integer.parseInt(str[0]),Integer.parseInt(str[1]));
                roomList.add(room);
            }
        } catch(IOException | InterruptedException | NumberFormatException e) {
            System.out.println(e);
        }       
        System.out.println("Room List: " + roomList);
        
        try {
            hotelSock.close();
        } catch(IOException e) {
            System.out.println(e);
        }
        
        return roomList;
    }    
    
    /**
     * Create a Booking object with each booking's data retrieved from servers
     * @return a List of Booking objects
     */
    public List<Booking> createBookingList() {
        System.out.println("Calling createBookingList()");
        List<Booking> bookingList = new ArrayList<>();
        
        connectHotelServer();
        
        try {
            printer.println("BOOKING");            
            Thread.sleep(500);
            while(reader.ready()) {
                String result = reader.readLine();
                String str[] = result.split(" ");
                Booking booking = new Booking(Integer.parseInt(str[0]),Integer.parseInt(str[1]),
                                              Integer.parseInt(str[2]),Integer.parseInt(str[3]),
                                              Integer.parseInt(str[4]),str[5],str[6],str[7]);
                bookingList.add(booking);
            }   
        } catch(IOException | InterruptedException | NumberFormatException e) {
            System.out.println(e);
        }      
        System.out.println("Booking List: " + bookingList);

        try {
            hotelSock.close();
        } catch(IOException e) {
            System.out.println(e);
        }
        
        return bookingList;
    }
    
    /**
     * Store a new Booking object
     * @param booking 
     */
    public void storeBooking(Booking booking){
        System.out.println("Calling createBookingList()");
        
        connectHotelServer();
        
        try {
            printer.println("BOOK");
            //Send booking's info to server
            printer.println(booking.getHotelId());
            printer.println(booking.getRoomId());
            printer.println(booking.getCheckIn());
            printer.println(booking.getCheckOut());
            printer.println(booking.getName());
            printer.println(booking.getEmail());
            printer.println(booking.getCreditCard());
            Thread.sleep(500);
            System.out.println(reader.readLine());         
        } catch(IOException | InterruptedException e) {
            System.out.println(e);
        }       
        
        try {
            hotelSock.close();
        } catch(IOException e) {
            System.out.println(e);
        }
    }
    
    /*
    * Check vacancy with given check-in and out date
    * Return 0 if no vacancy
    * Otherwise return the roomID of the first available room
    */
    public int findVacancy(int checkIn, int checkOut) {
        int vacancy = 0;
        List<Room> roomList = this.createRoomList();
        //Create a boolean array for all rooms, representing availability
        boolean roomAvai[] = new boolean[roomList.size()];
        for(int i = 0; i < roomAvai.length; i++) {
            roomAvai[i] = true;
        }
        List<Booking> bookingList = this.createBookingList();
        //Retrieve all Bookings from servers
        //Compare every Booking with given period
        //Set a room's availability to false if booked during the period
        for(Booking b : bookingList)
        {
            int in = b.getCheckIn();
            int out = b.getCheckOut();
            
            if((checkIn >= in) && (checkIn <= out))
                roomAvai[b.getRoomId()-1] = false;
            if((checkOut >= in) && (checkOut <= out))
                roomAvai[b.getRoomId()-1] = false;          
        }
        
        for(int i = 0; i < roomAvai.length; i++) {
            if(roomAvai[i] == true) {
                vacancy = i+1;
                System.out.println("Vacant room ID: " + vacancy);
                break;
            }
        }
        
        return vacancy;
    }    
    
    private void connectHotelServer() {
        try {
            hotelSock = new Socket(address, hotelPort);
        } catch(IOException e) {
            System.out.println(e);
        }
        System.out.println("hotelSock Start");        
        
        try {
            reader = new BufferedReader(new InputStreamReader(hotelSock.getInputStream()));
            printer = new PrintStream(hotelSock.getOutputStream());
        } catch(IOException e) {
           System.out.println(e); 
        }    
        printer.println("Broker says Hello to Server!");
    }
    
}
