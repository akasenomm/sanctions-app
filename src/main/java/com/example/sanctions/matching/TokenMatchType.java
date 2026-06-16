package com.example.sanctions.matching;

enum TokenMatchType {

    NONE( 0 ),
    FUZZY( 1 ),
    SUBSTRING( 2 ),
    INITIAL( 3 ),
    EXACT( 4 );

    private final int rank;

    TokenMatchType( final int rank ) {
        this.rank = rank;
    }

    int rank() {
        return rank;
    }
}
