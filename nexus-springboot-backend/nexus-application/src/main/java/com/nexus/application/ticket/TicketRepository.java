package com.nexus.application.ticket;

import com.nexus.domain.ticket.Ticket;

public interface TicketRepository {

    Ticket save(Ticket ticket);
}
