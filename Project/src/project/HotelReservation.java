package project;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class HotelReservation {
    private static DatabaseConnection dbConnection;
    private static List<Room> rooms;
    private static List<Reservation> reservations;
    private static int nextReservationId;

    public static void main(String[] args) {
        dbConnection = new DatabaseConnection();
        rooms = dbConnection.getAllRooms(); // Initialize rooms from the database
        reservations = dbConnection.getAllReservations();
        nextReservationId = reservations.isEmpty() ? 1 : reservations.get(reservations.size() - 1).getReservationId() + 1;
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("\n=== Hotel Reservation System ===");
            System.out.println("1. Search for Available Rooms");
            System.out.println("2. Make a Reservation");
            System.out.println("3. Cancel a Reservation");
            System.out.println("4. Display All Reservations");
            System.out.println("5. Exit");
            System.out.print("Enter your choice (1-5): ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    searchAvailableRooms();
                    break;
                case 2:
                    makeReservation();
                    break;
                case 3:
                    cancelReservation();
                    break;
                case 4:
                    displayAllReservations();
                    break;
                case 5:
                    System.out.println("Thank you for using the Hotel Reservation System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 5);

        scanner.close();
    }

    private static void searchAvailableRooms() {
        System.out.println("\n=== Available Rooms ===");
        for (Room room : rooms) {
            if (room.isAvailable()) {
                System.out.println(room.getRoomNumber() + " - " + room.getRoomType() + " - $" + room.getPrice());
            }
        }
    }

    private static void makeReservation() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter customer name: ");
        String customerName = scanner.nextLine();

        System.out.println("\n=== Available Rooms ===");
        for (Room room : rooms) {
            if (room.isAvailable()) {
                System.out.println(room.getRoomNumber() + " - " + room.getRoomType() + " - $" + room.getPrice());
            }
        }

        System.out.print("Enter room number for reservation: ");
        int roomNumber = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        System.out.print("Enter check-in date (YYYY-MM-DD): ");
        String checkInDateStr = scanner.nextLine();
        System.out.print("Enter check-out date (YYYY-MM-DD): ");
        String checkOutDateStr = scanner.nextLine();

        // Convert input date strings to LocalDate
        LocalDate checkInDate = LocalDate.parse(checkInDateStr);
        LocalDate checkOutDate = LocalDate.parse(checkOutDateStr);

        // Find the selected room in the list of rooms
        Room selectedRoom = rooms.stream().filter(room -> room.getRoomNumber() == roomNumber).findFirst().orElse(null);

        if (selectedRoom == null || !selectedRoom.isAvailable()) {
            System.out.println("Invalid room selection. Please try again.");
            return;
        }

        // Create the reservation and add it to the list
        Reservation reservation = new Reservation(nextReservationId++, customerName, roomNumber, checkInDate, checkOutDate);
        reservations.add(reservation);
        selectedRoom.setAvailable(false);

        // Save the reservation to the database
        dbConnection.insertReservation(reservation);

        System.out.println("Reservation successfully made!");
    }

    private static void cancelReservation() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter reservation ID to cancel: ");
        int reservationId = scanner.nextInt();

        // Find the reservation in the list of reservations
        Reservation selectedReservation = reservations.stream()
                .filter(reservation -> reservation.getReservationId() == reservationId)
                .findFirst()
                .orElse(null);

        if (selectedReservation == null) {
            System.out.println("Reservation not found. Please enter a valid reservation ID.");
            return;
        }

        // Find the corresponding room and mark it as available
        rooms.stream()
                .filter(room -> room.getRoomNumber() == selectedReservation.getRoomNumber())
                .findFirst()
                .ifPresent(room -> room.setAvailable(true));

        // Remove the reservation from the list
        reservations.remove(selectedReservation);

        // Save the cancellation to the database
        dbConnection.cancelReservation(reservationId);

        System.out.println("Reservation successfully canceled!");
    }

    private static void displayAllReservations() {
        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
            return;
        }

        System.out.println("\n=== All Reservations ===");
        for (Reservation reservation : reservations) {
            System.out.println("Reservation ID: " + reservation.getReservationId());
            System.out.println("Customer Name: " + reservation.getCustomerName());
            System.out.println("Room Number: " + reservation.getRoomNumber());
            System.out.println("Check-in Date: " + reservation.getCheckInDate());
            System.out.println("Check-out Date: " + reservation.getCheckOutDate());
            System.out.println("----------------------------");
        }
    }
}
