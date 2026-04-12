package com.project.backend.utils;

import com.project.core.FrameFingerprint;
import com.project.core.MatchResult;

import java.sql.*;
import java.util.*;

public class AudioHelper {

    private final String URL = "jdbc:postgresql://localhost:5433/alakazam_db";
    private final String USER = "alakazam";
    private final String PASSWORD = "alakazam";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void insertAudioHashes(List<FrameFingerprint> frames) {
        String sql = "INSERT INTO audio_hashes (movie_id, time_offset, hash_code) VALUES (?, ?, ?)";
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
            System.out.println("Successfully stored " + frames.size() + " audio hashes.");

            try (Statement stmt = conn.createStatement()) {
                System.out.println("Updating database statistics (ANALYZE)...");
                stmt.execute("ANALYZE audio_hashes");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MatchResult findBestMatch(List<FrameFingerprint> queryFrames) {
        if (queryFrames == null || queryFrames.isEmpty()) {
            return new MatchResult("No Match Found", 0.0, 0, 0);
        }

        int targetPayloadSize = 400;
        int step = Math.max(1, queryFrames.size() / targetPayloadSize);

        Map<Long, List<Integer>> queryHashMap = new HashMap<>();
        List<Long> hashList = new ArrayList<>();

        for (int i = 0; i < queryFrames.size(); i += step) {
            FrameFingerprint f = queryFrames.get(i);
            if (f.getHash() != 0L) {
                queryHashMap.computeIfAbsent(f.getHash(), k -> new ArrayList<>()).add(f.getTimestamp());
                hashList.add(f.getHash());
            }
        }

        if (hashList.isEmpty()) {
            return new MatchResult("No Match Found", 0.0, 0, 0);
        }

        Long[] hashArray = hashList.toArray(new Long[0]);

        String sql = "SELECT movie_id, time_offset, hash_code FROM audio_hashes WHERE hash_code = ANY(?::bigint[])";

        Map<String, Map<Integer, Integer>> histogram = new HashMap<>();

        try (Connection conn = getConnection()) {

            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SET enable_seqscan = off;");
                stmt.execute("SET enable_hashjoin = off;");
                stmt.execute("SET enable_mergejoin = off;");
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setArray(1, conn.createArrayOf("bigint", hashArray));

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String dbMovieId = rs.getString("movie_id");
                        int dbTime = rs.getInt("time_offset");
                        long dbHash = rs.getLong("hash_code");

                        List<Integer> queryTimes = queryHashMap.get(dbHash);
                        if (queryTimes != null) {
                            for (int qTime : queryTimes) {
                                // Calculate the relative offset (db_time - query_time)
                                int offset = dbTime - qTime;
                                histogram.computeIfAbsent(dbMovieId, k -> new HashMap<>())
                                        .merge(offset, 1, Integer::sum);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int highestVotes = 0;
        String winningId = "No Match Found";
        int winningOffset = 0;

        for (Map.Entry<String, Map<Integer, Integer>> movieEntry : histogram.entrySet()) {
            for (Map.Entry<Integer, Integer> offsetEntry : movieEntry.getValue().entrySet()) {
                if (offsetEntry.getValue() > highestVotes) {
                    highestVotes = offsetEntry.getValue();
                    winningId = movieEntry.getKey();
                    winningOffset = offsetEntry.getKey();
                }
            }
        }

        if (highestVotes >= 15 && !winningId.equals("No Match Found")) {
            double confidence = Math.min((highestVotes / 50.0) * 100.0, 100.0);
            return new MatchResult(winningId, confidence, highestVotes, winningOffset);
        }

        return new MatchResult("No Match Found", 0.0, 0, 0);
    }
}