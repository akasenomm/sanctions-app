package com.example.sanctions.service;

import com.example.sanctions.domain.SanctionedName;
import com.example.sanctions.matching.NameNormalizer;
import com.example.sanctions.repository.SanctionedNameRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Order( Integer.MAX_VALUE )
public class SanctionedNameCache implements ApplicationRunner {

    private final SanctionedNameRepository repository;
    private final NameNormalizer normalizer;

    private volatile Map<Long, SanctionedNameCacheEntry> cache = new ConcurrentHashMap<>();

    public SanctionedNameCache( final SanctionedNameRepository repository, final NameNormalizer normalizer ) {
        this.repository = repository;
        this.normalizer = normalizer;
    }

    @Override
    public void run( final ApplicationArguments args ) {
        rebuild();
    }

    public Collection<SanctionedNameCacheEntry> getAll() {
        return cache.values();
    }

    public void rebuild() {
        // Build a fresh ConcurrentHashMap in the background
        final Map<Long, SanctionedNameCacheEntry> freshCache = repository.findAll().stream()
                .collect( Collectors.toConcurrentMap(
                        SanctionedName::getId,
                        entity -> SanctionedNameCacheEntry.from( entity, normalizer )
                ) );

        // Swap the reference once it is fully built.
        // This ensures readers don't see an empty cache while it's rebuilding.
        this.cache = freshCache;
    }

    public void insert( final SanctionedName entity ) {
        cache.put( entity.getId(), SanctionedNameCacheEntry.from( entity, normalizer ) );
    }

    public void remove( final Long id ) {
        cache.remove( id );
    }
}