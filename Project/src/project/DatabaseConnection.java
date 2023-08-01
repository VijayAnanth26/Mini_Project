package project;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hotel";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1001Vijay1001"; // Replace with your actual password
    private Connection connection;

    public DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error connecting to the database.");
            e.printStackTrace();
        }
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM rooms";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int roomNumber = resultSet.getInt("room_number");
                String roomType = resultSet.getString("room_type");
                boolean isAvailable = resultSet.getBoolean("is_available");
                double price = resultSet.getDouble("price");

                // Use HotelRoom to instantiate the room
                HotelRoom room = new HotelRoom(roomNumber, roomType, isAvailable, price);
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rooms;
    }


    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservations";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int reservationId = resultSet.getInt("reservation_id");
                String customerName = resultSet.getString("customer_name");
                int roomNumber = resultSet.getInt("room_number");
                String checkInDateStr = resultSet.getString("check_in_date");
                String checkOutDateStr = resultSet.getString("check_out_date");

                LocalDate checkInDate = LocalDate.parse(checkInDateStr);
                LocalDate checkOutDate = LocalDate.parse(checkOutDateStr);

                Reservation reservation = new Reservation(reservationId, customerName, roomNumber, checkInDate, checkOutDate);
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservations;
    }

    public void insertReservation(Reservation reservation) {
        String query = "INSERT INTO reservations (reservation_id, customer_name, room_number, check_in_date, check_out_date) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, reservation.getReservationId());
            preparedStatement.setString(2, reservation.getCustomerName());
            preparedStatement.setInt(3, reservation.getRoomNumber());
            preparedStatement.setString(4, reservation.getCheckInDate().toString());
            preparedStatement.setString(5, reservation.getCheckOutDate().toString());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cancelReservation(int reservationId) {
        String query = "DELETE FROM reservations WHERE reservation_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, reservationId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
