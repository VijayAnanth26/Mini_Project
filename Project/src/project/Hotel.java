package project;

import java.util.ArrayList;
import java.util.List;

public class Hotel {
    private ArrayList<HotelRoom> rooms; // Use the HotelRoom subclass here

    public Hotel() {
        rooms = new ArrayList<>();
    }

    public void addRoom(HotelRoom room) { // Accept HotelRoom objects
        rooms.add(room);
    }

    public List<HotelRoom> getAvailableRooms(String roomType) {
        List<HotelRoom> availableRooms = new ArrayList<>();
        for (HotelRoom room : rooms) {
            if (room.getRoomType().equalsIgnoreCase(roomType) && room.isAvailable()) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }
}
