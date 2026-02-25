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

    // This method adds doll with its path (that will be updated anyway) into sql db, return int ID
    public int addDoll(String name, String path) {
        String sql = "INSERT INTO items(name, image_path) VALUES(?, ?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);
            pstmt.setString(2, path);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);    // returns the ID (autoincrement)
            }
        } catch (SQLException e) {
            System.out.println("Error saving doll: " + e.getMessage());
        }
        return -1;
    }

    // This method updates the path once the file is renamed to "doll_ID.jpg"
    public void updateImagePath(int id, String fileName) {
        String sql = "UPDATE items SET image_path = ? WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fileName);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating path: " + e.getMessage());
        }
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

    // This method completely DELETES ALL DATA IN SQL AND 'CLOSET' FOLDER!!!!
    public void fullWipeOut() {
        // this part deletes SQL Data
        String deleteSql = "DELETE FROM items";                                  // deletes all data in rows
        String resetIdSql = "DELETE FROM sqlite_sequence WHERE name='items'";    // resets counter, id starts from 1

        try (Connection conn = this.connect();
             java.sql.PreparedStatement pstmt1 = conn.prepareStatement(deleteSql);
             java.sql.PreparedStatement pstmt2 = conn.prepareStatement(resetIdSql)) {
            pstmt1.executeUpdate();    // 1st stmt where all data is deleted
            pstmt2.executeUpdate();    // 2nd stmt is the one that resets inner counter of sql to 0, and IDs start from 1 again
            System.out.println("SQL Table cleared and ID counter reset to 1.");
        } catch (java.sql.SQLException e) {
            System.out.println("SQL Wipe Error: " + e.getMessage());
        }

        // this part deletes images in the 'closet' folder
        java.io.File closetFolder = new java.io.File("src/main/resources/com/example/dollcollectionproject/closet/");
        java.io.File[] files = closetFolder.listFiles();

        if (files != null) {
            for (java.io.File file : files) {
                // for safety, we only delete if it starts with 'doll_'
                if (file.getName().startsWith("doll_")) {
                    if (file.delete()) {
                        System.out.println("Deleted file: " + file.getName());
                    }
                }
            }
        }
    }
}
