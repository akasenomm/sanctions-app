package com.example.sanctions.web;

import com.example.sanctions.service.ScreeningService;
import com.example.sanctions.web.dto.ScreeningRequest;
import com.example.sanctions.web.dto.ScreeningResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/screening" )
public class ScreeningController {

    private final ScreeningService screeningService;

    public ScreeningController( ScreeningService screeningService ) {
        this.screeningService = screeningService;
    }

    @PostMapping
    public ScreeningResponse screen( @Valid @RequestBody ScreeningRequest request ) {
        return screeningService.screen( request.name( ) );
    }
}
