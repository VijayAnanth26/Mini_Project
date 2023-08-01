package project;

// HotelRoom class that extends Room to represent individual hotel rooms
public class HotelRoom extends Room {
    public HotelRoom(int roomNumber, String roomType, boolean isAvailable, double price) {
        super(roomNumber, roomType, isAvailable, price);
    }

    // Implementing the abstract method to calculate total cost for a hotel room
    @Override
    public double calculateTotalCost(int numberOfDays) {
        return getPrice() * numberOfDays;
    }

    // Add other methods specific to HotelRoom if needed
    // For example, displayRoomDetails() can be overridden here to display additional details specific to hotel rooms.
}
