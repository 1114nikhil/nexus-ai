package com.nexus.infrastructure.persistence.ticket;

import com.nexus.domain.ticket.Ticket;
import com.nexus.domain.ticket.TicketPriority;
import com.nexus.domain.ticket.TicketStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(FlywayAutoConfiguration.class)
@Import(JpaTicketRepository.class)
@Testcontainers
class JpaTicketRepositoryIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16"));

//    @DynamicPropertySource
//    static void configure(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", postgres::getJdbcUrl);
//        registry.add("spring.datasource.username", postgres::getUsername);
//        registry.add("spring.datasource.password", postgres::getPassword);
//    }

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