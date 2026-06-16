package com.example.sanctions.service;

import com.example.sanctions.domain.SanctionedName;
import com.example.sanctions.repository.SanctionedNameRepository;
import com.example.sanctions.web.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SanctionedNameService {

    private final SanctionedNameRepository repository;
    private final SanctionedNameCache cache;

    public SanctionedNameService( final SanctionedNameRepository repository, final SanctionedNameCache cache ) {
        this.repository = repository;
        this.cache = cache;
    }

    @Transactional( readOnly = true )
    public List<SanctionedName> findAll() {
        return repository.findAll( );
    }

    @Transactional( readOnly = true )
    public SanctionedName findById( final Long id ) {
        return repository.findById( id )
                .orElseThrow( () -> new NotFoundException( "No sanctioned name with id " + id ) );
    }

    @Transactional
    public SanctionedName create( final String fullName ) {
        final SanctionedName saved = repository.save( new SanctionedName( fullName.trim( ) ) );
        cache.insert( saved );
        return saved;
    }

    @Transactional
    public SanctionedName update( final Long id, final String fullName ) {
        final SanctionedName existing = findById( id );
        existing.setFullName( fullName.trim( ) );
        final SanctionedName saved = repository.save( existing );
        cache.insert( saved );
        return saved;
    }

    @Transactional
    public void delete( final Long id ) {
        if ( !repository.existsById( id ) ) {
            throw new NotFoundException( "No sanctioned name with id " + id );
        }
        repository.deleteById( id );
        cache.remove( id );
    }
}
