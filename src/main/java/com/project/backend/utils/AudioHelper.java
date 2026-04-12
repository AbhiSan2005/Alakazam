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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MatchResult findBestMatch(List<FrameFingerprint> queryFrames) {
        if (queryFrames == null || queryFrames.isEmpty()) {
            return new MatchResult("No Match Found", 0.0, 0, 0);
        }

        Set<Long> uniqueHashes = new HashSet<>();
        for (FrameFingerprint f : queryFrames) {
            if (f.getHash() != 0) {
                uniqueHashes.add(f.getHash());
            }
        }

        if (uniqueHashes.size() < 30) {
            System.out.println("Audio Rejected: Insufficient acoustic diversity (Silence detected).");
            return new MatchResult("No Match Found", 0.0, 0, 0);
        }

        Map<Long, Integer> hashFrequency = new HashMap<>();
        for (FrameFingerprint f : queryFrames) {
            hashFrequency.put(f.getHash(), hashFrequency.getOrDefault(f.getHash(), 0) + 1);
        }

        Map<Long, List<Integer>> queryHashMap = new HashMap<>();
        List<Long> validHashesList = new ArrayList<>();

        for (FrameFingerprint f : queryFrames) {
            if (hashFrequency.get(f.getHash()) <= 10 && f.getHash() != 0) {
                queryHashMap.computeIfAbsent(f.getHash(), k -> new ArrayList<>()).add(f.getTimestamp());
                validHashesList.add(f.getHash());
            }
        }

        if (validHashesList.isEmpty()) {
            System.out.println("Audio Rejected: Clip contained only static or repetitive noise.");
            return new MatchResult("No Match Found", 0.0, 0, 0);
        }

        long[] hashArray = validHashesList.stream().mapToLong(l -> l).toArray();
        Map<String, Map<Integer, Integer>> histogram = new HashMap<>();

        String sql = "SELECT movie_id, time_offset, hash_code FROM audio_hashes WHERE hash_code = ANY(?)";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            Array sqlArray = conn.createArrayOf("bigint", Arrays.stream(hashArray).boxed().toArray(Long[]::new));
            pstmt.setArray(1, sqlArray);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String dbMovieId = rs.getString("movie_id");
                    int dbTime = rs.getInt("time_offset");
                    long dbHash = rs.getLong("hash_code");

                    List<Integer> queryTimes = queryHashMap.get(dbHash);
                    if (queryTimes != null) {
                        for (int qTime : queryTimes) {
                            int offset = dbTime - qTime;
                            histogram.computeIfAbsent(dbMovieId, k -> new HashMap<>())
                                    .merge(offset, 1, Integer::sum);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int highestVotes = 0;
        int secondHighestVotes = 0;
        String winningId = "No Match Found";
        int winningOffset = 0;

        for (Map.Entry<String, Map<Integer, Integer>> movieEntry : histogram.entrySet()) {
            for (Map.Entry<Integer, Integer> offsetEntry : movieEntry.getValue().entrySet()) {
                int votes = offsetEntry.getValue();
                if (votes > highestVotes) {
                    secondHighestVotes = highestVotes;
                    highestVotes = votes;
                    winningId = movieEntry.getKey();
                    winningOffset = offsetEntry.getKey();
                } else if (votes > secondHighestVotes) {
                    secondHighestVotes = votes;
                }
            }
        }

        boolean isClearWinner = (secondHighestVotes == 0) || (highestVotes > (secondHighestVotes * 3));

        if (highestVotes >= 50 && isClearWinner && !winningId.equals("No Match Found")) {
            double ratio = (double) highestVotes / (secondHighestVotes == 0 ? 1 : secondHighestVotes);
            double confidence = Math.min((ratio / 10.0) * 100.0, 100.0);

            return new MatchResult(winningId, confidence, highestVotes, winningOffset);
        }

        return new MatchResult("No Match Found", 0.0, 0, 0);
    }
}