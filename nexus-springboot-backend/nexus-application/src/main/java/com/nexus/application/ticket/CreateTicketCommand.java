package com.nexus.application.ticket;

import com.nexus.domain.ticket.TicketPriority;

public record CreateTicketCommand(String title, String description, TicketPriority priority) {
}
