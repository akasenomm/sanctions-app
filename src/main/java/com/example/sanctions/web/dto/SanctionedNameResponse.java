package com.example.sanctions.web.dto;

import com.example.sanctions.domain.SanctionedName;

public record SanctionedNameResponse( Long id, String fullName ) {

    public static SanctionedNameResponse from( SanctionedName entity ) {
        return new SanctionedNameResponse( entity.getId( ), entity.getFullName( ) );
    }
}
