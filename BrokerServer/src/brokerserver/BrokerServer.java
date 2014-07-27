package brokerserver;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Di
 * 
 * The central hotel booking broker server that bridges between the hotel servers and clients.
 * If receives message from Clients and forwards some of them to the actual hotel Servers;
 * Therefore, it does not maintain its own database.
 */
public class BrokerServer {
    
    public static int BROKERPORT = 9189;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        ServerSocket serverSocket = null;
        
        try {
            serverSocket = new ServerSocket(BROKERPORT);
        } catch(IOException e) {
            System.out.println(e);
            System.exit(1);
        }
        System.out.println("Broker Server Running");
        
        while(true) {
            Socket clientSock = null;
            try {
                clientSock = serverSocket.accept();
            } catch(IOException e) {
                System.out.println(e);
                continue;
            }
            
            new SocketHandler(clientSock).start();
            System.out.println("clientSock Start");
        }
    }
}

class SocketHandler extends Thread {
    
    //Port number and address for all hotel servers
    public static final int SERVERPORT1 = 8189;
    public static final int SERVERPORT2 = 8190;
    public static final int SERVERPORT3 = 8191;
    public static final String SERVERADDRESS = "localhost";
    
    //State for each session
    private final int SELECTCITY = 1;
    private final int SELECTHOTEL = 2;
    private final int CHECKVACANCY = 3;
    private final int MAKEBOOKING = 4;
    private final int QUIT = 5;    
    
    Socket clientSock;
    
    SocketHandler(Socket clientSock) {
	this.clientSock = clientSock;
        System.out.println("SocketHandler Constructed");
    }
    
    @Override
    public void run() {
        try{
            BufferedReader reader =
		new BufferedReader(new InputStreamReader(
				   clientSock.getInputStream()));				   
	    PrintStream printer =
		new PrintStream(clientSock.getOutputStream());       
            
            //Connect all databases on all servers
            HotelConnector hc1 = new HotelConnector(SERVERADDRESS,SERVERPORT1);
            HotelConnector hc2 = new HotelConnector(SERVERADDRESS,SERVERPORT2);
            HotelConnector hc3 = new HotelConnector(SERVERADDRESS,SERVERPORT3);
            
            List<Hotel> hotelList = new ArrayList<>();
            hotelList.add(hc1.createHotelObject());
            hotelList.add(hc2.createHotelObject());
            hotelList.add(hc3.createHotelObject());
            System.out.println("Hotel List: " + hotelList);
            
            //A session starts
            String str = reader.readLine();
            System.out.println(clientSock.getInetAddress() + ": " + str);
            printer.println("Broker says Hello to Client ");            
            
            int state = SELECTCITY;
            String hotelId  = "";
            int checkIn = 0;
            int checkOut = 0;
            int vacancy = 0;            
            
            while(state != QUIT) {
                switch(state) {
                    case SELECTCITY:
                        printer.println("Which city to stay?");
                        printer.println("1. Melbourne");
                        printer.println("2. Perth");
                        printer.println("0. Quit");
                        printer.println("Please enter a number: ");
                        
                        str = reader.readLine().trim();
                        switch (str) {
                            case "0":
                                state = QUIT;
                                break;
                            case "1":
                            case "2":
                                state = SELECTHOTEL;
                                break;
                            default:
                                state = SELECTCITY;
                                str = "";
                                break;
                        }
                        break;
                        
                    case SELECTHOTEL:
                        printer.println("Select a hotel to check vacancy. ");
                        if(str.equals("1")) {
                            printer.println("Hotel ID: " + hotelList.get(0).getHotelId() +
                                    " Name: " + hotelList.get(0).getHotelName() +
                                    " City: " + hotelList.get(0).getCity() +
                                    " Rate: AUD " + hotelList.get(0).getRate());
                            printer.println("Hotel ID: " + hotelList.get(1).getHotelId() +
                                    " Name: " + hotelList.get(1).getHotelName() +
                                    " City: " + hotelList.get(1).getCity() +
                                    " Rate: AUD " + hotelList.get(1).getRate());
                            printer.println("Please enter a hotel ID or '0' to Start Over: ");
                            
                            str = reader.readLine().trim();
                            switch (str) {
                                case "0":
                                    state = SELECTCITY;
                                    break;
                                case "1":
                                case "2":
                                    state = CHECKVACANCY;
                                    break;
                                default:
                                    state = SELECTHOTEL;
                                    str = "";
                                    break;
                            }
                        } else {
                            printer.println("Hotel ID: " + hotelList.get(2).getHotelId() +
                                    " Name: " + hotelList.get(2).getHotelName() +
                                    " City: " + hotelList.get(2).getCity() +
                                    " Rate: " + hotelList.get(2).getRate());
                            printer.println("Please enter a hotel ID or '0' to Start Over: ");
                            
                            str = reader.readLine().trim();
                            switch (str) {
                                case "0":
                                    state = SELECTCITY;
                                    break;
                                case "3":
                                    state = CHECKVACANCY;
                                    break;
                                default:                          
                                    state = SELECTHOTEL;
                                    str = "";
                                    break;
                            }
                        }
                        break;
                        
                    case CHECKVACANCY:      
                        printer.println("Enter check-in day for July 2014 (e.g. 15 for 15th):");
                        checkIn = Integer.parseInt(reader.readLine().trim());
                        while(checkIn < 1 || checkIn > 30) {
                            printer.println("Invalid input!");
                            printer.println("Enter check-in day for July 2014 (e.g. 15 for 15th):");
                            checkIn = Integer.parseInt(reader.readLine().trim());
                        }
                        
                        printer.println("Enter check-out day for July 2014 (e.g. 16 for 16th):");
                        checkOut = Integer.parseInt(reader.readLine().trim());
                        while(checkOut < 2 || checkOut > 31 || checkOut <= checkIn) {
                            printer.println("Invalid input!");
                            printer.println("Enter check-out day for July 2014 (e.g. 16 for 16th):");
                            checkOut = Integer.parseInt(reader.readLine().trim());
                        }
                        
                        hotelId = str;
                        switch (hotelId) {
                            case "1":
                                vacancy = hc1.findVacancy(checkIn, checkOut);
                                break;
                            case "2":
                                vacancy = hc2.findVacancy(checkIn, checkOut);
                                break;
                            case "3":
                                vacancy = hc3.findVacancy(checkIn, checkOut);
                                break;
                            default:
                                state = CHECKVACANCY;
                                str = "";
                                break;
                        }
                        
                        if(vacancy > 0) {
                            do {
                                printer.println("Found an available room.");
                                printer.println("1. Book the Room");
                                printer.println("0. Start Over");
                                printer.println("Please enter a number:");                            
                                str = reader.readLine().trim();
                            } while(!str.trim().equals("0") && !str.trim().equals("1"));
                            
                            switch (str.trim()) {
                                case "0":
                                    state = SELECTCITY;
                                    break;
                                case "1":
                                    state = MAKEBOOKING;
                                    break;
                            }
                        } else {
                            do {
                                printer.println("No room available.");
                                printer.println("1. Check other dates");
                                printer.println("0. Start Over");
                                printer.println("Please enter a number:");                            
                                str = reader.readLine().trim();
                            } while(!str.trim().equals("0") && !str.trim().equals("1"));
                            
                            switch (str) {
                                case "0":
                                    state = SELECTCITY;
                                    break;
                                case "1":
                                    state = CHECKVACANCY;
                                    break;
                            }
                        }                        
                        break;
                        
                    case MAKEBOOKING:
                        Booking booking = new Booking();
                        booking.setHotelId(Integer.parseInt(hotelId));
                        booking.setRoomId(vacancy);
                        booking.setCheckIn(checkIn);
                        booking.setCheckOut(checkOut);
                        
                        printer.println("Please enter your name: ");
                        String name = reader.readLine().trim();
                        booking.setName(name);
                        
                        printer.println("Please enter your email: ");
                        String email = reader.readLine().trim();
                        boolean validEmail = new EmailValidator().validate(email);
                        while(!validEmail) {
                            printer.println("Invalid email format!");
                            printer.println("Please enter your email: ");
                            email = reader.readLine().trim();
                            validEmail = new EmailValidator().validate(email);
                        }
                        booking.setEmail(email);
                        
                        printer.println("Please enter your credit card number (only accept Visa): ");
                        String creditCard = reader.readLine().trim();
                        boolean validVisa = new CreditCardValidator().validate(creditCard);
                        while(!validVisa) {
                            printer.println("Invalid Visa card number!");
                            printer.println("Please enter your credit card number (only accept Visa): ");
                            creditCard = reader.readLine().trim();
                            validVisa = new CreditCardValidator().validate(creditCard);
                        }
                        booking.setCreditCard(creditCard);
                        
                        switch (hotelId) {
                            case "1":
                                try {
                                    hc1.storeBooking(booking);
                                    printer.println("Booking Successful!");
                                } catch(Exception e) {
                                    System.out.println(e);
                                    printer.println("Booking Failed");
                                }
                                break;
                            case "2":
                                try {
                                    hc2.storeBooking(booking);
                                    printer.println("Booking Successful!");
                                } catch(Exception e) {
                                    System.out.println(e);
                                    printer.println("Booking Failed");
                                }
                                break;
                            case "3":
                                try {
                                    hc3.storeBooking(booking);
                                    printer.println("Booking Successful!");
                                } catch(Exception e) {
                                    System.out.println(e);
                                    printer.println("Booking Failed");
                                }
                                break;
                            default:
                        }
                        state = SELECTCITY;
                        break;
                        
                    case QUIT:
                        break;
                        
                    default:                    
                } 
            }
            System.out.println("Client Quit");
            printer.println("BYEBYE");
            clientSock.close();
        }
        catch(IOException e) {
            System.out.println(e);
        }
    }
}

/**
 * 
 * Class for validating email input.
 */
class EmailValidator {
 
    private Pattern pattern;
    private Matcher matcher;

    private static final String EMAIL_PATTERN = 
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public EmailValidator() {
            pattern = Pattern.compile(EMAIL_PATTERN);
    }

    /**
     * Validate input with regular expression
     * @param input
     * @return true valid input, false invalid input
     */
    public boolean validate(final String input) {
            matcher = pattern.matcher(input);
            return matcher.matches();
    }
}

/**
 * 
 * Class for validating credit card input.
 */
class CreditCardValidator {
 
    private Pattern pattern;
    private Matcher matcher;

    private static final String VISA_PATTERN = 
            "^4[0-9]{12}(?:[0-9]{3})?$"; // Visa card regex

    public CreditCardValidator() {
            pattern = Pattern.compile(VISA_PATTERN);
    }

    /**
     * @param input
     * @return true valid input, false invalid input
     */
    public boolean validate(final String input) {
            matcher = pattern.matcher(input);
            return matcher.matches();
    }
}