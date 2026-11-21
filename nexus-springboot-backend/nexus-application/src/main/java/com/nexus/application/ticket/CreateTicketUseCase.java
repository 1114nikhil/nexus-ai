package com.nexus.application.ticket;

import com.nexus.application.ai.TicketClassifier;
import com.nexus.domain.ticket.Ticket;
import com.nexus.domain.ticket.TicketPriority;

import java.util.UUID;

public class CreateTicketUseCase {
    private final TicketRepository repository;
    private final TicketClassifier ticketClassifier;


    public CreateTicketUseCase(TicketRepository repository,TicketClassifier ticketClassifier) {
        this.repository = repository;
        this.ticketClassifier=ticketClassifier;
    }

    public UUID execute(CreateTicketCommand command){
//        Ask AI for the priority based on title +description
        String textToClassify = command.title()+" "+command.description();
        TicketPriority aiPriority= ticketClassifier.classify(textToClassify);

        //  Create the domain object using the AI priority
        Ticket ticket=Ticket.create(
                command.title(),
                command.description(),
                aiPriority
        );
        Ticket saved = repository.save(ticket);
        return saved.getId();
    }
}
