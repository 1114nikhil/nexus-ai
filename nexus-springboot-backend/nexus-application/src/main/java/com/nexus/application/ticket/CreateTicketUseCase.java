package com.nexus.application.ticket;

import com.nexus.domain.ticket.Ticket;

import java.util.UUID;

public class CreateTicketUseCase {
    private final TicketRepository repository;


    public CreateTicketUseCase(TicketRepository repository) {
        this.repository = repository;
    }

    public UUID execute(CreateTicketCommand command){
        Ticket ticket=Ticket.create(
                command.title(),
                command.description(),
                command.priority()
        );
        Ticket saved = repository.save(ticket);
        return saved.getId();
    }
}
