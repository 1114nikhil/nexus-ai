package com.nexus.application.ticket;

import com.nexus.domain.ticket.Ticket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository {

    Ticket save(Ticket ticket);
    Optional<Ticket> fundById(UUID id);
    List<Ticket> findAll();
}
