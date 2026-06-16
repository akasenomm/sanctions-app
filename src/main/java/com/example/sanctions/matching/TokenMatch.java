package com.example.sanctions.matching;

record TokenMatch( TokenMatchType type, double confidence ) {

    static TokenMatch none() {
        return new TokenMatch( TokenMatchType.NONE, 0.0 );
    }

    static TokenMatch bestOf( final TokenMatch a, final TokenMatch b ) {
        if ( a.type( ).rank( ) != b.type( ).rank( ) ) {
            return a.type( ).rank( ) > b.type( ).rank( ) ? a : b;
        }
        return a.confidence( ) >= b.confidence( ) ? a : b;
    }
}
