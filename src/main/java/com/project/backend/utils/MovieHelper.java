package com.project.backend.utils;

import java.sql.*;

public class MovieHelper {
    private final String URL = "jdbc:postgresql://localhost:5433/alakazam_db";
    private final String USER = "alakazam";
    private final String PASSWORD = "alakazam";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public String insertMovieAndGetId(String title) {
        String sql = "INSERT INTO movies (title) VALUES (?) RETURNING movie_id";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, title);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("movie_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }
}