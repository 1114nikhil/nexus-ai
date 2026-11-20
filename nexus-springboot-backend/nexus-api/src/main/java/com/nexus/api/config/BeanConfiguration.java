package com.nexus.api.config;

import com.nexus.application.ticket.CreateTicketUseCase;
import com.nexus.application.ticket.TicketRepository;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

@Configurable
public class BeanConfiguration {
    @Bean
    public CreateTicketUseCase createTicketUseCase (TicketRepository ticketRepository){
        return new CreateTicketUseCase(ticketRepository);
    }
}
