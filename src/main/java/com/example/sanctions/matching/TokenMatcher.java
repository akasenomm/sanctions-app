package com.example.sanctions.matching;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Component;

/**
 * Classifies how two tokens relate using rules. Using Jaro Winkler and Levenshtein.
 * Confidence values are derived from token lengths (and
 * Jaro-Winkler for typos).
 *
 */
@Component
public class TokenMatcher {

    private final JaroWinklerSimilarity jaroWinkler = new JaroWinklerSimilarity( );
    private final LevenshteinDistance levenshtein = new LevenshteinDistance( );

    public TokenMatch match( final String a, final String b ) {
        if ( a.equals( b ) ) {
            return new TokenMatch( TokenMatchType.EXACT, 1.0 );
        }
        if ( isInitialMatch( a, b ) ) {
            return new TokenMatch( TokenMatchType.INITIAL, initialConfidence( a, b ) );
        }
        if ( isSubstringMatch( a, b ) ) {
            return new TokenMatch( TokenMatchType.SUBSTRING, substringConfidence( a, b ) );
        }
        if ( isFuzzyMatch( a, b ) ) {
            return new TokenMatch( TokenMatchType.FUZZY, jaroWinkler.apply( a, b ) );
        }
        return TokenMatch.none( );
    }

    private double initialConfidence( final String a, final String b ) {
        final int longerLen = Math.max( a.length( ), b.length( ) );
        return 1.0 - 1.0 / longerLen;
    }

    private double substringConfidence( final String a, final String b ) {
        final int shorterLen = Math.min( a.length( ), b.length( ) );
        final int longerLen = Math.max( a.length( ), b.length( ) );
        return (double) shorterLen / longerLen;
    }

    /**
     * Typos are allowed in proportion to token length: one edit per four
     * characters at minimum, one edit for very short tokens.
     */
    private boolean isFuzzyMatch( final String a, final String b ) {
        final int minLen = Math.min( a.length( ), b.length( ) );
        if ( minLen < 2 ) {
            return false;
        }
        final int allowedEdits = Math.max( 1, minLen / 4 );
        return levenshtein.apply( a, b ) <= allowedEdits;
    }

    private boolean isInitialMatch( final String a, final String b ) {
        if ( a.isEmpty( ) || b.isEmpty( ) ) {
            return false;
        }
        final boolean oneInitial = ( a.length( ) == 1 && b.length( ) > 1 )
                || ( b.length( ) == 1 && a.length( ) > 1 );
        return oneInitial && a.charAt( 0 ) == b.charAt( 0 );
    }

    private boolean isSubstringMatch( final String a, final String b ) {
        final String shorter = a.length( ) <= b.length( ) ? a : b;
        final String longer = a.length( ) <= b.length( ) ? b : a;
        return shorter.length( ) >= MatchingConstants.MIN_SUBSTRING_LENGTH && longer.contains( shorter );
    }
}
