package com.nexus.application.ticket;

import com.nexus.application.ticket.CreateTicketCommand;
import com.nexus.application.ticket.CreateTicketUseCase;
import com.nexus.application.ticket.TicketRepository;
import com.nexus.domain.ticket.Ticket;
import com.nexus.domain.ticket.TicketPriority;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class CreateTicketUseCaseTest {

    @Test
    void shouldSaveTicketAndReturnId(){
        TicketRepository fakTicketRepository= new TicketRepository() {
            @Override
            public Ticket save(Ticket ticket) {
                return ticket;
            }
        };

        CreateTicketUseCase useCase=new CreateTicketUseCase(fakTicketRepository);
        CreateTicketCommand command=new CreateTicketCommand(
                "Login Issue","cannot Login", TicketPriority.HIGH
        );

        UUID id = useCase.execute(command);
        assertNotNull(id);
    }

    @Test
    void shouldPropagateValidationErrorFromDomain(){
        TicketRepository fakeTicketRepository=ticket -> ticket;
        CreateTicketUseCase useCase = new CreateTicketUseCase(fakeTicketRepository);
        CreateTicketCommand command = new CreateTicketCommand("","Cannot login",TicketPriority.HIGH);
        assertThrows(IllegalArgumentException.class,()->useCase.execute(command));
    }
}
