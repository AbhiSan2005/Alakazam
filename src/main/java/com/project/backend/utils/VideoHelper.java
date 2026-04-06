package com.project.backend.utils;

import com.project.core.FrameFingerprint;
import com.project.core.MatchResult;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoHelper {

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

    public void insertVideoHashes(List<FrameFingerprint> frames) {
        String sql = "INSERT INTO video_hashes (movie_id, frame_timestamp, phash, chunk_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false); 

            for (FrameFingerprint frame : frames) {
                pstmt.setString(1, frame.getVideoID()); 
                pstmt.setInt(2, frame.getTimestamp());
                pstmt.setLong(3, frame.getHash());
                pstmt.setInt(4, frame.getChunkID());
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            conn.commit();
            System.out.println("Successfully stored " + frames.size() + " hashes.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MatchResult findBestMatch(List<FrameFingerprint> queryFrames) {
        String sql = "SELECT m.title, v.frame_timestamp " +
                     "FROM video_hashes v " +
                     "JOIN movies m ON v.movie_id = m.movie_id " +
                     "WHERE bit_count((v.phash # ?) :: bit(64)) <= 9";
        
        Map<String, Integer> sequenceVotes = new HashMap<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (FrameFingerprint queryFrame : queryFrames) {
                pstmt.setLong(1, queryFrame.getHash());
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String movieTitle = rs.getString("title");
                        int dbFrame = rs.getInt("frame_timestamp");
                        
                        int offset = dbFrame - queryFrame.getTimestamp();
                        String voteKey = movieTitle + "::" + offset;
                        
                        sequenceVotes.put(voteKey, sequenceVotes.getOrDefault(voteKey, 0) + 1);
                    }
                }
            }

            int highestVotes = 0;
            String winningTitle = "Unknown";

            for (Map.Entry<String, Integer> entry : sequenceVotes.entrySet()) {
                if (entry.getValue() > highestVotes) {
                    highestVotes = entry.getValue();
                    winningTitle = entry.getKey().split("::")[0];
                }
            }

            if (highestVotes >= 3) {
                double confidence = ((double) highestVotes / queryFrames.size()) * 100.0;
                
                return new MatchResult(winningTitle, Math.min(confidence, 100.0));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new MatchResult("No Match Found", 0.0);
    }
}