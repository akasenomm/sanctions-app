package com.example.sanctions.matching;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Compares two person names and decides whether the query should be flagged
 * against a sanctions list entry.
 *
 * <p>Each name is first normalized and tokenized (lower-casing, stripping punctuation, and dropping noise words.
 * Token order does not matter.
 *
 * <p>Matching compares the shorter tokenized name to the longer one. Every token
 * in the shorter list must pair with a token in the longer list (value from TokenMatchType).
 *
 * <p>The returned score ranks multiple hits for the API; it is not used for
 * the match decision itself.</p>
 */
@Component
public class NameMatcher {

    private final NameNormalizer normalizer;
    private final TokenMatcher tokenMatcher;

    public NameMatcher( final NameNormalizer normalizer, final TokenMatcher tokenMatcher ) {
        this.normalizer = normalizer;
        this.tokenMatcher = tokenMatcher;
    }

    public MatchResult match( final String query, final String sanctioned ) {
        return match( normalizer.tokenize( query ), normalizer.tokenize( sanctioned ) );
    }

    public MatchResult match( final List<String> queryTokens, final List<String> sanctionedTokens ) {
        if ( queryTokens.isEmpty( ) || sanctionedTokens.isEmpty( ) ) {
            return new MatchResult( false, 0.0 );
        }

        final List<String> smaller = queryTokens.size( ) <= sanctionedTokens.size( ) ? queryTokens : sanctionedTokens;
        final List<String> larger = smaller == queryTokens ? sanctionedTokens : queryTokens;

        final List<TokenMatch> tokenMatches = new ArrayList<>( smaller.size( ) );
        for ( String token : smaller ) {
            TokenMatch best = TokenMatch.none( );
            for ( String candidate : larger ) {
                best = TokenMatch.bestOf( best, tokenMatcher.match( token, candidate ) );
            }
            tokenMatches.add( best );
        }

        final double score = aggregateScore( tokenMatches, smaller.size( ), larger.size( ) );
        final boolean match = isMatch( tokenMatches, smaller.size( ), larger.size( ) );

        return new MatchResult( match, score );
    }

    private boolean isMatch( final List<TokenMatch> tokenMatches, final int smallerSize, final int largerSize ) {
        if ( tokenMatches.stream( ).anyMatch( m -> m.type( ) == TokenMatchType.NONE ) ) {
            return false;
        }

        if ( smallerSize == 1 && largerSize > 1 ) {
            return tokenMatches.getFirst().type( ) == TokenMatchType.EXACT;
        }

        return true;
    }

    private double aggregateScore( final List<TokenMatch> tokenMatches, final int smallerSize, final int largerSize ) {
        if ( tokenMatches.isEmpty( ) || largerSize == 0 ) {
            return 0.0;
        }
        final double averageConfidence = tokenMatches.stream( )
                .mapToDouble( TokenMatch::confidence )
                .average( )
                .orElse( 0.0 );
        return averageConfidence * ( (double) smallerSize / largerSize );
    }
}
