package com.project.backend.utils;

import com.project.core.MovieMetaData;
import java.sql.*;

public class MovieHelper {
    private final String URL = "jdbc:postgresql://localhost:5433/alakazam_db";
    private final String USER = "alakazam";
    private final String PASSWORD = "alakazam";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public String insertMovieAndGetId(String title, String genre, int duration, int yearOfRelease) {
        String sql = "INSERT INTO movies (title, genre, duration, year_of_release) VALUES (?, ?, ?, ?) RETURNING movie_id";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, genre);
            pstmt.setInt(3, duration);
            pstmt.setInt(4, yearOfRelease);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("movie_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MovieMetaData getMovieDetails(String movieId) {
        String sql = "SELECT title, genre, duration, year_of_release FROM movies WHERE movie_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, movieId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new MovieMetaData(
                            rs.getString("title"),
                            rs.getString("genre"),
                            rs.getInt("duration"),
                            rs.getInt("year_of_release"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}