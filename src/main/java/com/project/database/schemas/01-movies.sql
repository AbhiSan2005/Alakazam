CREATE TABLE IF NOT EXISTS movies (
    movie_id VARCHAR(8) PRIMARY KEY DEFAULT substring(gen_random_uuid()::text, 1, 8),
    title VARCHAR NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);