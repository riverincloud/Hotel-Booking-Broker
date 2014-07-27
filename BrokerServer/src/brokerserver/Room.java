package brokerserver;

/**
 *
 * @author Di
 * 
 * Entity class for the Room data
 */
public class Room {
    
    private int roomId;
    private int hotelId;
    
    public Room(){
    }

    public Room(int roomId, int hotelId) {
        this.roomId = roomId;
        this.hotelId = hotelId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }
    
}
