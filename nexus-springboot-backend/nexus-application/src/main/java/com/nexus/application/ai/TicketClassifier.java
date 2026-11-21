package com.nexus.application.ai;

import com.nexus.domain.ticket.TicketPriority;

public interface TicketClassifier {
    TicketPriority classify(String text);
}
