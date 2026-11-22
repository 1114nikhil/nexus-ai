package com.nexus.application.ticket;

import com.nexus.domain.ticket.Ticket;

import java.util.List;

public class ListTicketUseCase {

    private final TicketRepository ticketRepository;

    public ListTicketUseCase(TicketRepository ticketRepository){
        this.ticketRepository=ticketRepository;
    }

    public List<Ticket> execute(){
        return ticketRepository.findAll();
    }
}
