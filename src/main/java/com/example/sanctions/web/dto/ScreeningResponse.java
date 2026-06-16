package com.example.sanctions.web.dto;

import java.util.List;

public record ScreeningResponse(
        boolean match,
        List<Match> matches ) {

    public record Match( Long id, String fullName, double score ) {
    }
}
