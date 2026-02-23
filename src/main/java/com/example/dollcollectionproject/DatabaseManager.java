package com.example.dollcollectionproject;

import com.example.dollcollectionproject.model.Doll;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    // This tells Java where the file is
    private static final String URL = "jdbc:sqlite:closet.db";

    // Method to connect to the database
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Method to add a Doll entry
    public int addDoll(String name, String path) {
        String sql = "INSERT INTO items(name, image_path) VALUES(?, ?)";
        int generatedId = -1;

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, path);
            pstmt.executeUpdate();

            // This grabs the ID that was just created
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }

            System.out.println("Doll '" + name + "' registered in SQL!");
        } catch (SQLException e) {
            System.out.println("Error saving doll: " + e.getMessage());
        }
        return generatedId;    // This sends the ID back to the Controller
    }

    // Deleting Dolls by ID, because that is pro way, not searching by some variable
    public void deleteDollById(int id) {
        String sql = "DELETE FROM items WHERE id = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Delete Error: " + e.getMessage());
        }
    }

    //
    public List<Doll> getAllDolls() {
        List<Doll> dolls = new ArrayList<>();
        // MUST INCLUDE "id" in the SELECT statement
        String sql = "SELECT * FROM items";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // 2. Grab the REAL ID from the database row
                int id = rs.getInt("id");
                String imagePath = rs.getString("image_path");
                String name = rs.getString("name");
                String hint = rs.getString("hint");
                String description = rs.getString("description");
                String brand = rs.getString("brand");
                String model = rs.getString("model");
                int year = rs.getInt("year");


                // 8-argument constructor (id, imagePath, name, hint, description, brand, model, year)
                Doll doll = new Doll(
                        id,
                        imagePath,
                        name,
                        hint,
                        description,
                        brand,
                        model,
                        year

                );
                dolls.add(doll);
            }
        } catch (SQLException e) {
            System.out.println("SQL Load Error: " + e.getMessage());
        }
        return dolls;
    }

    //
    public void updateFullDollDetails(int id, String name, String hint, String description, String brand, String model, int year) {
        // We use the ID to find the right doll, then update every other column
        String sql = "UPDATE items SET name = ?, hint = ?, description = ?, brand = ?, model = ?, year = ? WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, hint);
            pstmt.setString(3, description);
            pstmt.setString(4, brand);
            pstmt.setString(5, model);
            pstmt.setInt(6, year);
            pstmt.setInt(7, id); // The anchor

            pstmt.executeUpdate();
            System.out.println("SQL: Data successfully synced for ID " + id);
        } catch (SQLException e) {
            System.out.println("SQL Update Error: " + e.getMessage());
        }
    }
}
