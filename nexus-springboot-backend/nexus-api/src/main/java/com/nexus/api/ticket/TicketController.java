package com.nexus.api.ticket;

import com.nexus.application.ticket.*;
import com.nexus.domain.ticket.Ticket;
import com.nexus.domain.ticket.TicketStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/tickets")
public class TicketController {
    private final CreateTicketUseCase createTicketUseCase;
    private final GetTicketUseCase getTicketUseCase;
    private final ListTicketUseCase listTicketUseCase;

    public TicketController(CreateTicketUseCase createTicketUseCase,GetTicketUseCase getTicketUseCase,ListTicketUseCase listTicketUseCase){
        this.createTicketUseCase=createTicketUseCase;
        this.getTicketUseCase=getTicketUseCase;
        this.listTicketUseCase=listTicketUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse createTicket(@RequestBody CreateTicketRequest request) {
        CreateTicketCommand command = new CreateTicketCommand(
                request.title(), request.description(), request.priority());
        UUID ticketId = createTicketUseCase.execute(command);

        // Fetch the actual saved ticket — the priority here is the AI's, not the request's
        Ticket ticket = getTicketUseCase.execute(ticketId);
        return toResponse(ticket);
    }

    @GetMapping("/{id}")
    public TicketResponse getTicket(@PathVariable UUID id){
        Ticket ticket=getTicketUseCase.execute(id);
        return toResponse(ticket);
    }

    @GetMapping
    public List<TicketResponse> getAllTickets(){
        return listTicketUseCase.execute().stream().map(this::toResponse).toList();
    }

    private TicketResponse toResponse(Ticket ticket){
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getPriority(),
                ticket.getStatus(),
                ticket.getCreatedAt()
        );
    }
}
