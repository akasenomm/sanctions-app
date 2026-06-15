package com.example.sanctions.matching;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Turns a raw name string into a clean,
 * lower-cased list of tokens with letter markings removed (õ -> o), punctuation
 * stripped, and noise words removed.
 */
@Component
public class NameNormalizer {

    private static final Pattern MARKS_PATTERN = Pattern.compile( "\\p{M}+" );
    private static final Pattern PUNCTUATION_PATTERN = Pattern.compile( "[.,;:'\"()\\[\\]/\\\\-]" );
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile( "\\s+" );
    private static final Pattern SPLIT_PATTERN = Pattern.compile( " " );

    private final Set<String> noiseWords = MatchingConstants.NOISE_WORDS;

    public List<String> tokenize( final String raw ) {
        if ( raw == null || raw.isBlank( ) ) {
            return List.of( );
        }

        final List<String> allTokens = SPLIT_PATTERN.splitAsStream( normalize( raw ) )
                .filter( Predicate.not( String::isBlank ) )
                .toList( );

        final List<String> significant = allTokens.stream( )
                .filter( t -> !noiseWords.contains( t ) )
                .toList( );

        return significant.isEmpty( ) ? allTokens : significant;
    }

    public String normalize( String raw ) {
        String folded = Normalizer.normalize( raw, Normalizer.Form.NFD ).toLowerCase( Locale.ROOT );
        folded = MARKS_PATTERN.matcher( folded ).replaceAll( "" );
        folded = PUNCTUATION_PATTERN.matcher( folded ).replaceAll( " " );
        folded = WHITESPACE_PATTERN.matcher( folded ).replaceAll( " " );
        return folded.trim( );
    }
}