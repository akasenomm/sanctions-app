package com.example.sanctions.web;

import com.example.sanctions.service.SanctionedNameService;
import com.example.sanctions.web.dto.SanctionedNameRequest;
import com.example.sanctions.web.dto.SanctionedNameResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping( "/api/sanctions" )
public class SanctionedNameController {

    private final SanctionedNameService service;

    public SanctionedNameController( SanctionedNameService service ) {
        this.service = service;
    }

    @GetMapping
    public List<SanctionedNameResponse> list() {
        return service.findAll( ).stream( )
                .map( SanctionedNameResponse::from )
                .toList( );
    }

    @GetMapping( "/{id}" )
    public SanctionedNameResponse get( @PathVariable Long id ) {
        return SanctionedNameResponse.from( service.findById( id ) );
    }

    @PostMapping
    @ResponseStatus( HttpStatus.CREATED )
    public SanctionedNameResponse create( @Valid @RequestBody SanctionedNameRequest request ) {
        return SanctionedNameResponse.from( service.create( request.fullName( ) ) );
    }

    @PutMapping( "/{id}" )
    public SanctionedNameResponse update( @PathVariable Long id,
                                          @Valid @RequestBody SanctionedNameRequest request ) {
        return SanctionedNameResponse.from( service.update( id, request.fullName( ) ) );
    }

    @DeleteMapping( "/{id}" )
    public ResponseEntity<Void> delete( @PathVariable Long id ) {
        service.delete( id );
        return ResponseEntity.noContent( ).build( );
    }
}
