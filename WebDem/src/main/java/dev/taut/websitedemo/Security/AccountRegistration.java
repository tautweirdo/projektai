package dev.taut.websitedemo.Security;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AccountRegistration {

    private static final String INSERT_USERS_SQL = "INSERT INTO account" +
            "  (username, password) VALUES " +
            " (?, ?);";

    public static void main(String[] args) {

        String dbURL = "jdbc:mariadb://localhost:3306/transactiondb";
        String dbUser = "root";
        String dbPassword = "root";

        try (Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/transactiondb", "root", "root");
             PreparedStatement preparedStatement = conn.prepareStatement(INSERT_USERS_SQL)) {

            preparedStatement.setString(1, "testuser");
            preparedStatement.setString(2, "testpassword");

            int row = preparedStatement.executeUpdate();
            if (row > 0) {
                System.out.println("Account " + "testuser" + " " + "testpassword" + " added.");
            }

        } catch (SQLException e) {
            System.out.println("Duomenų bazės prisijungimo klaida.");
            e.printStackTrace();
        }
    }
}


