package com.nexus.api.config;

import com.nexus.application.ai.TicketClassifier;
import com.nexus.application.ticket.CreateTicketUseCase;
import com.nexus.application.ticket.TicketRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
    @Bean
    public CreateTicketUseCase createTicketUseCase (TicketRepository ticketRepository, TicketClassifier ticketClassifier){
        return new CreateTicketUseCase(ticketRepository,ticketClassifier);
    }
}
