CREATE TABLE IF NOT EXISTS video_hashes(
    hash_id BIGSERIAL PRIMARY KEY,
    movie_id VARCHAR(8) NOT NULL,
    frame_timestamp INT NOT NULL,
    phash BIGINT NOT NULL,

    CONSTRAINT fk_movie
        FOREIGN KEY (movie_id)
        REFERENCES movies(movie_id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_video_movie_id ON video_hashes(movie_id);
CREATE INDEX IF NOT EXISTS idx_phash ON video_hashes(phash);