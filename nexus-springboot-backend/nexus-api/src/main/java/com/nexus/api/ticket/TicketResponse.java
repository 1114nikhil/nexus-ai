package com.nexus.api.ticket;

import com.nexus.domain.ticket.TicketPriority;
import com.nexus.domain.ticket.TicketStatus;

import java.time.Instant;
import java.util.UUID;

public record TicketResponse(
        UUID id,
        String title,
        String description,
        TicketPriority priority,
        TicketStatus status,
        Instant createdAt
) {
}
