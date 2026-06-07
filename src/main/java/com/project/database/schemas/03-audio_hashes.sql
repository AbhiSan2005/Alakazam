CREATE TABLE IF NOT EXISTS audio_hashes(
    hash_id BIGSERIAL PRIMARY KEY,
    movie_id VARCHAR(8) NOT NULL,
    time_offset INT NOT NULL,
    hash_code BIGINT NOT NULL,

    CONSTRAINT fk_audio_movie
        FOREIGN KEY (movie_id)
        REFERENCES movies(movie_id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_audio_movie_id ON audio_hashes(movie_id);
CREATE INDEX IF NOT EXISTS idx_audio_hash_code ON audio_hashes(hash_code);