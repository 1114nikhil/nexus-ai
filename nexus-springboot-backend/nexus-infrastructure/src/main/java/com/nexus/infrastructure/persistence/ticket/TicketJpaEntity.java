package com.nexus.infrastructure.persistence.ticket;

import com.nexus.domain.ticket.TicketPriority;
import com.nexus.domain.ticket.TicketStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="tickets")
public class TicketJpaEntity {
    @Id
    @Column(columnDefinition ="uuid",updatable = false,nullable = false)
    private UUID id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false,columnDefinition = "text")
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private TicketPriority priority;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private TicketStatus status;
    @Column(nullable = false, name = "created_at")
    private Instant createdAt;

    protected TicketJpaEntity(){}

    public TicketJpaEntity(UUID id, String title, String description, TicketPriority priority, TicketStatus status, Instant createdAt){
        this.id=id;
        this.title=title;
        this.description=description;
        this.priority=priority;
        this.status=status;
        this.createdAt=createdAt;
    }
    // Getters only — no setters, entity is immutable after construction
    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TicketStatus getStatus() { return status; }
    public TicketPriority getPriority() { return priority; }
    public Instant getCreatedAt() { return createdAt; }
}
