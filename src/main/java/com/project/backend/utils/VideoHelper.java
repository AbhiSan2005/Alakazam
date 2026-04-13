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

    public void insertVideoHashes(List<FrameFingerprint> frames) {
        String sql = "INSERT INTO video_hashes (movie_id, frame_timestamp, phash) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            int count = 0;
            for (FrameFingerprint frame : frames) {
                pstmt.setString(1, frame.getVideoID());
                pstmt.setInt(2, frame.getTimestamp());
                pstmt.setLong(3, frame.getHash());
                pstmt.addBatch();
                if (++count % 10000 == 0)
                    pstmt.executeBatch();
            }
            pstmt.executeBatch();
            conn.commit();
            System.out.println("Successfully stored " + frames.size() + " video hashes.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MatchResult findBestMatch(List<FrameFingerprint> queryFrames) {
        if (queryFrames == null || queryFrames.isEmpty()) {
            return new MatchResult("No Match Found", 0.0, 0, 0);
        }

        int targetCount = 15;
        int step = Math.max(1, queryFrames.size() / targetCount);

        Map<String, Integer> sequenceVotes = new HashMap<>();
        String sql = "SELECT movie_id, frame_timestamp FROM video_hashes WHERE bit_count((phash # ?)::bit(64)) <= 15";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < queryFrames.size(); i += step) {
                FrameFingerprint queryFrame = queryFrames.get(i);
                pstmt.setLong(1, queryFrame.getHash());

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String dbMovieId = rs.getString("movie_id");
                        int dbFrame = rs.getInt("frame_timestamp");
                        int offset = dbFrame - queryFrame.getTimestamp();

                        String voteKey = dbMovieId + "::" + offset;
                        sequenceVotes.put(voteKey, sequenceVotes.getOrDefault(voteKey, 0) + 1);
                    }
                }

                if (i / step >= targetCount - 1)
                    break;
            }

            int highestVotes = 0;
            String winningId = "No Match Found";
            int winningOffset = 0;

            for (Map.Entry<String, Integer> entry : sequenceVotes.entrySet()) {
                if (entry.getValue() > highestVotes) {
                    highestVotes = entry.getValue();
                    String[] parts = entry.getKey().split("::");
                    winningId = parts[0];
                    winningOffset = Integer.parseInt(parts[1]);
                }
            }

            if (highestVotes >= 3 && !winningId.equals("No Match Found")) {
                double confidence = ((double) highestVotes / targetCount) * 100.0;
                return new MatchResult(winningId, Math.min(confidence, 100.0), highestVotes, winningOffset);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new MatchResult("No Match Found", 0.0, 0, 0);
    }
}