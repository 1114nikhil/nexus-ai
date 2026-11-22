package com.nexus.application.ticket;

import com.nexus.domain.ticket.Ticket;

import java.util.UUID;

public class GetTicketUseCase {

    private final TicketRepository ticketRepository;

    public GetTicketUseCase(TicketRepository ticketRepository){
        this.ticketRepository=ticketRepository;
    }

    public Ticket execute(UUID id){
        return ticketRepository.fundById(id).orElseThrow(()-> new IllegalArgumentException("Ticket not found with the Id:"+id));
    }
}
