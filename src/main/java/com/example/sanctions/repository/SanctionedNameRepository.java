package com.example.sanctions.repository;

import com.example.sanctions.domain.SanctionedName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SanctionedNameRepository extends JpaRepository<SanctionedName, Long> {
}
