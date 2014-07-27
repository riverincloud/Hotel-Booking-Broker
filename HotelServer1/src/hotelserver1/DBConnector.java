package hotelserver1;

import java.sql.*;

/**
 *
 * @author Di
 * 
 * The DBConnector Class connects the Server and its database.
 * It performs basic queries to manipulate the records in the database.
 */
public class DBConnector {
    
    private Connection connection = null;
    private final String url = "jdbc:derby://localhost:1527/DBHotel1";
    private final String driver = "org.apache.derby.jdbc.ClientDriver";
    private final String username = "app";
    private final String password = "app";    
    
    public DBConnector() {      
        System.out.println("DBConnector Constructed");
    };
    
    /**
     * Read all hotels from database
     * Return as a string
     * @return 
     */
    public String findAllHotels() {        
        System.out.println("Calling findAllHotels()");
        String strAllHotels = "";
        try {
            Class.forName(driver).newInstance();
            connection = DriverManager.getConnection(url, username, password);
            String query = "SELECT * FROM HOTEL";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();  
            
            while(rs.next()) {
                strAllHotels += rs.getInt("HOTELID") + " " + rs.getString("HOTELNAME") + " " + 
                                rs.getString("CITY") + " " + rs.getInt("RATE") + "\n";
            }
            connection.close();
            
        } catch(ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e) {
            System.out.println(e);
        }
        System.out.println("All Hotels: " + strAllHotels);
        return strAllHotels;    
    }
           
    /**
     * Read all rooms from database
     * Return as a string
     * @return
     */
    public String findAllRooms() {
        System.out.println("Calling findAllRooms()");
        String strAllRooms = "";        
        try {
            Class.forName(driver).newInstance();
            connection = DriverManager.getConnection(url, username, password);
            String query = "SELECT * FROM ROOM";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();  
            
            while(rs.next()) {
                strAllRooms += rs.getInt("ROOMID") +" "+ rs.getInt("HOTELID") + " " + "\n";
            }
            connection.close();
            
        } catch(ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e) {
            System.out.println(e);
        }
        System.out.println("All Rooms: " + strAllRooms);
        return strAllRooms;
    } 
    
    /**
     * Read all bookings from database
     * Return as a string
     * @return 
     */
    public String findAllBookings() {
        System.out.println("Calling findAllBookings()");
        String strAllBookings = "";        
        try {
            Class.forName(driver).newInstance();
            connection = DriverManager.getConnection(url, username, password);
            String query = "SELECT * FROM BOOKING";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();  
            
            while(rs.next()) {
                strAllBookings += rs.getInt("BOOKINGID") + " " + rs.getInt("HOTELID") + " " + 
                                  rs.getInt("ROOMID") + " " + rs.getInt("CHECKIN") + " " + 
                                  rs.getInt("CHECKOUT") + " " + rs.getString("NAME") + " " + 
                                  rs.getString("EMAIL") + " " + rs.getString("CREDITCARD") +"\n";
            }            
            connection.close();
            
        } catch(ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e) {
            System.out.println(e);
        }      
        System.out.println("All Bookings: " + strAllBookings);
        return strAllBookings;
    }
    
    /**
     * Insert a new booking into database
     * @param hotelId
     * @param roomId
     * @param checkIn
     * @param checkOut
     * @param name
     * @param email
     * @param creditCard
     */
    public void insertBooking(int hotelId, int roomId, int checkIn, int checkOut, 
                              String name, String email, String creditCard) {
        System.out.println("Calling insertBooking()");
        try {            
            Class.forName(driver).newInstance();
            connection = DriverManager.getConnection(url, username, password);
            String query = "SELECT COUNT(BOOKINGID) AS BOOKINGNUM FROM BOOKING";
            Statement st = connection.createStatement();            
            ResultSet rs = st.executeQuery(query);
            rs.next();
            int bookingId = rs.getInt("BOOKINGNUM") + 1;
            System.out.println("New bookingId: " + bookingId);
            
            String insertQuery = "INSERT INTO BOOKING "
                    + "(BOOKINGID, HOTELID, ROOMID, CHECKIN, CHECKOUT, NAME, EMAIL, CREDITCARD)" + 
                    "VALUES" + "(?,?,?,?,?,?,?,?)";
            PreparedStatement ps = connection.prepareStatement(insertQuery);
            ps.setInt(1, bookingId);
            ps.setInt(2, hotelId);
            ps.setInt(3, roomId);
            ps.setInt(4, checkIn);
            ps.setInt(5, checkOut);
            ps.setString(6, name);
            ps.setString(7, email);
            ps.setString(8, creditCard);            
            ps.executeUpdate();
        } catch(ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException e) {
            System.out.println(e);
        }
    }  
}
