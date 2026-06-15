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

    public SanctionedNameCache( SanctionedNameRepository repository ) {
        this.repository = repository;
        // TODO
    }

    @Override
    public void run( ApplicationArguments args ) throws Exception {

    }
}