package com.nexus.infrastructure.persistence.ticket;

import com.nexus.application.ticket.TicketRepository;
import com.nexus.domain.ticket.Ticket;
import org.springframework.stereotype.Component;

@Component
public class JpaTicketRepository implements TicketRepository {

    private final SpringDataTicketRepository springDataTicketRepository;

    public JpaTicketRepository(SpringDataTicketRepository springDataTicketRepository){
        this.springDataTicketRepository=springDataTicketRepository;
    }
    @Override
    public Ticket save(Ticket ticket) {
        TicketJpaEntity ticketJpaEntity= toEntity(ticket);
        TicketJpaEntity saved=springDataTicketRepository.save(ticketJpaEntity);
        return toDomain(saved);
    }

    private Ticket toDomain(TicketJpaEntity entity) {
        return Ticket.reconstitute(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getPriority(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    private TicketJpaEntity toEntity(Ticket ticket) {
        return new TicketJpaEntity(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getPriority(),
                ticket.getStatus(),
                ticket.getCreatedAt()
        );
    }
}
