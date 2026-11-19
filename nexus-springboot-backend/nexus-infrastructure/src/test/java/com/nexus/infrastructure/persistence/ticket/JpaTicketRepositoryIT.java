package com.nexus.infrastructure.persistence.ticket;

import com.nexus.domain.ticket.Ticket;
import com.nexus.domain.ticket.TicketPriority;
import com.nexus.domain.ticket.TicketStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;


import static org.junit.jupiter.api.Assertions.*;

@Testcontainers(disabledWithoutDocker = true)
@DataJpaTest
@Import(JpaTicketRepository.class)
class JpaTicketRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private JpaTicketRepository repository;

    @Test
    void shouldSaveAndReloadTicket() {
        Ticket ticket = Ticket.create("Test Ticket", "Test description", TicketPriority.HIGH);
        Ticket saved = repository.save(ticket);

        assertNotNull(saved.getId());
        assertEquals("Test Ticket", saved.getTitle());
        assertEquals(TicketStatus.OPEN, saved.getStatus());
        assertEquals(TicketPriority.HIGH, saved.getPriority());
        assertEquals(ticket.getCreatedAt(), saved.getCreatedAt());
    }
}