package com.example.sanctions.matching;

import java.util.Set;

final class MatchingConstants {

    /** Ignore substring hits shorter than this. */
    static final int MIN_SUBSTRING_LENGTH = 3;
    static final Set<String> NOISE_WORDS = Set.of(
            "the", "to", "an", "a", "and", "of", "mr", "mrs", "ms", "miss", "dr" );

    private MatchingConstants() {
    }
}
