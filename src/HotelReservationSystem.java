import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.Scanner;

public class HotelReservationSystem {
    private static final String url ="jdbc:mysql://127.0.0.1:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "Arun@2306";
    public static void main(String[] args) throws ClassNotFoundException,SQLException  {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");  // Load Driver
        } catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection connection = DriverManager.getConnection(url,username,password); // Obtain Connection
            while (true) {
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1. Reserver a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get room number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.println("Choose an option: ");

                int choice = scanner.nextInt();
                switch(choice){
                    case 1:
                        reserveRoom(connection, scanner);
                        break;
                    
                    case 2:
                        viewReservations(connection);
                        break;
                    
                    case 3:
                        getRoomNumber(connection, scanner);
                        break;

                    case 4:
                        updateReservation(connection, scanner);
                        break;

                    case 5: 
                        deleteReservation(connection, scanner);
                        break;

                    case 0:
                        exit();
                        scanner.close();
                        return;
                    
                        default:
                            System.out.println("Invalid choice. Try again. ");
                }
            }
        } catch(SQLException e){
            System.out.println(e.getMessage());
        } catch(InterruptedException e){
            throw new RuntimeException(e);
        }
    }
    
        private static void reserveRoom(Connection connection, Scanner scanner){
            try{
                System.out.println(("Enter guest name"));
                String guestName = scanner.next();
                scanner.nextLine();

                System.out.println("Enter room number");
                int roomNumber = scanner.nextInt();

                System.out.println("Enter contact number");
                int contactNumber = scanner.nextInt();

                String sql = "insert into reservations(guest_name, room_number, contact_number)" + "values('"+guestName + "',"+ roomNumber +",'"+contactNumber +"')";

                try(Statement statement = connection.createStatement()){ // Obtain a Statement
                    int affectedRows = statement.executeUpdate(sql); // Execute Query

                    if(affectedRows > 0){
                        System.out.println("Reservation succesfull");
                    }
                    else{
                        System.out.println("Reservation failed");
                    }
                }
            } catch(SQLException e){
                e.printStackTrace();
            }
        }

        private static void viewReservations(Connection connection) throws SQLException{
            String sql = "select reservation_id, guest_name, room_number, contact_number, reservation_date from reservations";

            try(Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql))
                {
                    System.out.println("Current Reservation");
                    System.out.println("+----------------+------------+---------------+-------------------+------------------+");
                    System.out.println("| Reservation ID | Guest      | Room Number   | Contact Number    | Reservation_date |");
                    System.out.println("+----------------+------------+---------------+-------------------+------------------+");

                    while(resultSet.next()){
                        int reservationId = resultSet.getInt("reservation_id");
                        String guestName  = resultSet.getString("guest_name");
                        int roomNumber = resultSet.getInt("room_number");
                        String contactNumber = resultSet.getString("contact_number");
                        String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                        System.out.printf("|%-14d |%-15s |%-13d|%-20s|%-19s |\n", reservationId, guestName, roomNumber, contactNumber, reservationDate);
                    }
                    System.out.println("+--------------+---------------+-------------+--------------------+-------------------+");
                }
        }

        private static void getRoomNumber(Connection connection, Scanner scanner){
        try{
            System.out.println("Enter reservation_id: ");
            int reservationId = scanner.nextInt();

            System.out.println("Enter guest name ");
            String guestName = scanner.next();

            String sql = "Select room_number from reservation "+
                        "WHERE reservation_id = "+ reservationId +
                        " AND guest_name = '" + guestName + "'";

            try(Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)){

                    if(resultSet.next()){
                        int roomNumber = resultSet.getInt("room_number");
                        System.out.println("Room number for reservation ID " + reservationId + 
                        "and Guest " + guestName + "is: " + roomNumber);
                    }
                    else{
                        System.out.println("Reservation not found for the given ID and guest name");
                    }
                }
            } catch(SQLException e){
                e.printStackTrace();
            }
        }

        public static void updateReservation(Connection connection, Scanner scanner){
            try{
                System.out.println("Enter reservation ID to update: ");
                int reservationId = scanner.nextInt();
                scanner.nextLine();
                
                if(!reservationExists(connection, reservationId)){
                    System.out.println("Reservation not found for the given ID.");
                    return;
                }

                System.out.println("Enter new guest name : ");
                String newGuestName = scanner.nextLine();

                System.out.println("Enter new room number : ");
                int newRoomNumber = scanner.nextInt();

                System.out.println("Enter new contact number : ");
                String newContactNumber = scanner.next();

                String sql = "update reservations set guest_name = '" + newGuestName + "',"+
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' "+
                    "where reservation_id = " + reservationId;

                    try(Statement statement = connection.createStatement()){
                        int affectedRows = statement.executeUpdate(sql);

                        if(affectedRows>0){
                            System.out.println("Reservation updated succesfully!");
                        }
                        else{
                            System.out.println("Reservation update failed.");
                        }
                    }
            } catch(SQLException e){
                e.printStackTrace();
            }
        }


        private static void deleteReservation(Connection connection, Scanner scanner){
            try{
                System.out.println("Enter reservation ID to delete : ");
                int reservationId = scanner.nextInt();

                if(!reservationExists(connection, reservationId)){
                    System.out.println("Reservation not found for the given ID.");
                    return;
                }

                String sql = "delete from reservations where reservation_id = " + reservationId;

                try(Statement statement = connection.createStatement()){
                    int affectedRows = statement.executeUpdate(sql);

                    if(affectedRows>0){
                        System.out.println("Reservation deleted succesfully");
                    }
                    else{
                        System.out.println("Reservation deletion failed");
                    }
                }
            } catch(SQLException e){
                e.printStackTrace();
            }
        }

        private static boolean reservationExists(Connection connection, int reservationId){
            try{
                String sql = "select reservation_id from reservations where reservation_id = " +reservationId;

                try(Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql)){

                    return resultSet.next();
                }
            } catch(SQLException e){
                e.printStackTrace();
                return false;
            }
        }

        public static void exit() throws InterruptedException{
            System.out.println("Exiting system");
            int i=5;
            while(i!=0){
                System.out.println(".");
                Thread.sleep(450);
                i--;
            }
            System.out.println();
            System.out.println("Thank You for using Hotel Reservation System!!!");
        }
}
