package com.nexus.api.config;

import com.nexus.application.ai.TicketClassifier;
import com.nexus.application.ticket.CreateTicketUseCase;
import com.nexus.application.ticket.GetTicketUseCase;
import com.nexus.application.ticket.ListTicketUseCase;
import com.nexus.application.ticket.TicketRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
    @Bean
    public CreateTicketUseCase createTicketUseCase (TicketRepository ticketRepository, TicketClassifier ticketClassifier){
        return new CreateTicketUseCase(ticketRepository,ticketClassifier);
    }
    @Bean
    public GetTicketUseCase getTicketUseCase(TicketRepository ticketRepository){
        return  new GetTicketUseCase(ticketRepository);
    }
    @Bean
    public ListTicketUseCase listTicketUseCase(TicketRepository ticketRepository){
        return  new ListTicketUseCase(ticketRepository);
    }
}
