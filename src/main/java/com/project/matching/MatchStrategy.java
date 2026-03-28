package com.project.matching;
import com.project.core.Fingerprint;
import com.project.core.MatchResult;
import com.project.database.MediaDatabase;

public interface MatchStrategy {
    MatchResult match(Fingerprint input, MediaDatabase db);
}