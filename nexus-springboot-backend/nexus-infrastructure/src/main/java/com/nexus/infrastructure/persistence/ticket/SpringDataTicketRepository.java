package com.nexus.infrastructure.persistence.ticket;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataTicketRepository extends JpaRepository<TicketJpaEntity, UUID> {
}
