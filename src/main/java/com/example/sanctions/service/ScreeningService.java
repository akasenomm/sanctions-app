package com.example.sanctions.service;

import com.example.sanctions.matching.MatchResult;
import com.example.sanctions.matching.NameMatcher;
import com.example.sanctions.matching.NameNormalizer;
import com.example.sanctions.web.dto.ScreeningResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ScreeningService {

    private final SanctionedNameCache cache;
    private final NameNormalizer normalizer;
    private final NameMatcher nameMatcher;

    public ScreeningService( final SanctionedNameCache cache,
                             final NameNormalizer normalizer,
                             final NameMatcher nameMatcher ) {
        this.cache = cache;
        this.normalizer = normalizer;
        this.nameMatcher = nameMatcher;
    }

    public ScreeningResponse screen( final String name ) {
        final List<String> queryTokens = normalizer.tokenize( name );
        final List<ScreeningResponse.Match> matches = new ArrayList<>( );

        for ( SanctionedNameCacheEntry entry : cache.getAll( ) ) {
            final MatchResult result = nameMatcher.match( queryTokens, entry.tokens( ) );
            if ( result.match( ) ) {
                matches.add( new ScreeningResponse.Match(
                        entry.id( ),
                        entry.fullName( ),
                        round( result.score( ) ) ) );
            }
        }

        matches.sort( Comparator.comparingDouble( ScreeningResponse.Match::score ).reversed( ) );
        return new ScreeningResponse( !matches.isEmpty( ), matches );
    }

    private double round( final double value ) {
        return Math.round( value * 10000.0 ) / 10000.0;
    }
}
