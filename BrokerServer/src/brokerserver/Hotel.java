package brokerserver;

/**
 *
 * @author Di
 * 
 * Entity class for the Hotel data
 */
public class Hotel {
    
    private int hotelId;
    private String hotelName;
    private String city;
    private int rate;
    
    public Hotel()
    {
    }

    public Hotel(int hotelId, String hotelName, String city, int rate) {
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.city = city;
        this.rate = rate;
    }
    
    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
    
}
