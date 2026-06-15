package com.example.sanctions.service;

import com.example.sanctions.domain.SanctionedName;
import com.example.sanctions.matching.NameNormalizer;

import java.util.List;

public record SanctionedNameCacheEntry( Long id, String fullName, List<String> tokens ) {

    public static SanctionedNameCacheEntry from( final SanctionedName entity, final NameNormalizer normalizer ) {
        return new SanctionedNameCacheEntry(
                entity.getId(),
                entity.getFullName(),
                normalizer.tokenize( entity.getFullName() )
        );
    }

}
