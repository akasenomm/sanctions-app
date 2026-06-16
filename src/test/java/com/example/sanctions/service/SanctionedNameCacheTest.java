package com.example.sanctions.service;

import com.example.sanctions.domain.SanctionedName;
import com.example.sanctions.matching.NameNormalizer;
import com.example.sanctions.repository.SanctionedNameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith( MockitoExtension.class )
class SanctionedNameCacheTest {

    @Mock
    private SanctionedNameRepository repository;

    private SanctionedNameCache cache;

    @BeforeEach
    void setUp() {
        cache = new SanctionedNameCache( repository, new NameNormalizer() );
    }

    @Test
    void rebuildPrecomputesTokens() {
        SanctionedName entry = new SanctionedName( "Osama Bin Laden" );
        entry.setId( 1L );
        when( repository.findAll() ).thenReturn( List.of( entry ) );

        cache.rebuild();

        assertThat( cache.getAll() ).containsExactly(
                new SanctionedNameCacheEntry( 1L, "Osama Bin Laden", List.of( "osama", "bin", "laden" ) ) );
    }

    @Test
    void upsertAndRemoveKeepCacheInSync() {
        SanctionedName first = new SanctionedName( "Osama Bin Laden" );
        first.setId( 1L );
        SanctionedName second = new SanctionedName( "Robert" );
        second.setId( 2L );
        when( repository.findAll() ).thenReturn( List.of( first ) );
        cache.rebuild();

        cache.insert( second );
        assertThat( cache.getAll() ).hasSize( 2 );

        cache.remove( 1L );
        assertThat( cache.getAll() ).containsExactly( new SanctionedNameCacheEntry( 2L, "Robert", List.of( "robert" ) ) );
    }
}
