package com.nexus.api.ticket;

import com.nexus.application.ticket.CreateTicketCommand;
import com.nexus.application.ticket.CreateTicketUseCase;
import com.nexus.domain.ticket.TicketStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/tickets")
public class TicketController {
    private final CreateTicketUseCase createTicketUseCase;

    public TicketController(CreateTicketUseCase createTicketUseCase){
        this.createTicketUseCase=createTicketUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse createTicket(@RequestBody CreateTicketRequest request){
        CreateTicketCommand command= new CreateTicketCommand(
                request.title(),
                request.description(),
                request.priority()
        );

        UUID ticketId= createTicketUseCase.execute(command);

        return  new TicketResponse(
                ticketId,
                request.title(),
                request.description(),
                request.priority(),
                TicketStatus.OPEN,
                Instant.now()
        );
    }
}
