package com.example.dollcollectionproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {
    // This tells Java where the file is
    private static final String URL = "jdbc:sqlite:closet.db";

    // Method to connect to the database
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Method to add a Doll entry
    public void addDoll(String dollName, String imagePath) {
        String sql = "INSERT INTO items(name, image_path) VALUES(?, ?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dollName);
            pstmt.setString(2, imagePath);
            pstmt.executeUpdate();

            System.out.println("Doll '" + dollName + "' registered in SQL!");
        } catch (SQLException e) {
            System.out.println("Error saving doll: " + e.getMessage());
        }
    }
}
