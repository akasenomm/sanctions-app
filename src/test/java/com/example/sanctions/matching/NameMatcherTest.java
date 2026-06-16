package com.example.sanctions.matching;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class NameMatcherTest {

    private static final String SANCTIONED = "Osama Bin Laden";

    private final NameMatcher matcher = new NameMatcher( new NameNormalizer( ), new TokenMatcher( ) );

    @ParameterizedTest(name = "should flag \"{0}\"")
    @ValueSource(strings = {
            "Osama Laden",
            "Osama Bin Laden",
            "Bin Laden, Osama",
            "Laden Osama Bin",
            "to the osama bin laden",
            "osama and bin laden"
    })
    void flagsBaselineExamples(String query) {
        MatchResult result = matcher.match(query, SANCTIONED);
        assertThat(result.match())
                .as("query \"%s\" scored %.3f", query, result.score())
                .isTrue();
    }

    @Test
    void flagsFuzzyTypos() {
        MatchResult result = matcher.match("Ben Osama Ladn", SANCTIONED);
        assertThat(result.match())
                .as("scored %.3f", result.score())
                .isTrue();
    }

    @Test
    void doesNotFlagPhoneticOnlySpelling() {
        MatchResult result = matcher.match("Ladn the Asoma", SANCTIONED);
        assertThat(result.match())
                .as("phonetic-only variant scored %.3f", result.score())
                .isFalse();
    }

    @Test
    void flagsAbbreviatedMiddleName() {
        MatchResult result = matcher.match("Joe L. Webb", "Joe Luis Webb");
        assertThat(result.match())
                .as("abbreviation scored %.3f", result.score())
                .isTrue();
    }

    @Test
    void flagsNoiseWordTitle() {
        MatchResult result = matcher.match("John Smith", "Mr. John Smith");
        assertThat(result.match()).isTrue();
    }

    @Test
    void flagsSpellingError() {
        MatchResult result = matcher.match("Madus", "Madis");
        assertThat(result.match()).isTrue();
    }

    @ParameterizedTest(name = "should NOT flag \"{0}\"")
    @ValueSource(strings = {
            "Barack Obama",
            "Saddam Hussein",
            "John Smith",
            "Angela Merkel",
            "Vladimir Putin"
    })
    void doesNotFlagUnrelatedNames(String query) {
        MatchResult result = matcher.match(query, SANCTIONED);
        assertThat(result.match())
                .as("query \"%s\" scored %.3f", query, result.score())
                .isFalse();
    }

    @Test
    void substringMatchingFlagsContainedToken() {
        MatchResult result = matcher.match("Bert", "Robert");
        assertThat(result.match())
                .as("substring scored %.3f", result.score())
                .isTrue();
    }

    @Test
    void substringMatchingDoesNotFlagSingleTokenAgainstLongerName() {
        MatchResult result = matcher.match("Bert", "Robert Mueller");
        assertThat(result.match())
                .as("single-token substring scored %.3f", result.score())
                .isFalse();
    }
}
