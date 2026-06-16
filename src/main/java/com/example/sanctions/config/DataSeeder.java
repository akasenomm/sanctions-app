package com.example.sanctions.config;

import com.example.sanctions.domain.SanctionedName;
import com.example.sanctions.repository.SanctionedNameRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds the in-memory database with the sanctioned-list names from the
 * assignment so the service demonstrates every matching scenario
 * out of the box.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final SanctionedNameRepository repository;

    public DataSeeder( final SanctionedNameRepository repository ) {
        this.repository = repository;
    }

    @Override
    public void run( final String... args ) {
        if ( repository.count( ) > 0 ) {
            return;
        }
        final List<String> seed = List.of(
                "Osama Bin Laden",
                "Robert",
                "Madis",
                "Joe Luis Webb",
                "Mr. John Smith" );
        seed.forEach( name -> repository.save( new SanctionedName( name ) ) );
    }
}
