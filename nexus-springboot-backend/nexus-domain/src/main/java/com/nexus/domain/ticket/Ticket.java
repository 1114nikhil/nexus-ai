package com.nexus.domain.ticket;

import java.util.UUID;

public class Ticket {
    private final UUID id;
    private final String title;
    private final String description;
    private final TicketPriority priority;

    private TicketStatus status;

    private Ticket(
            UUID id,
            String title,
            String description,
            TicketPriority priority,
            TicketStatus status
    ){
        this.id=id;
        this.title=title;
        this.description=description;
        this.priority=priority;
        this.status=status;
    }

//    Static factory method
    public static Ticket create(String title,String description,TicketPriority priority){
        if(title==null||title.isBlank()){
            throw new IllegalArgumentException("Ticket title must not be blank!");
        }
        if(description==null||description.isBlank()){
            throw new IllegalArgumentException("Ticket Description must not be blank!");
        }
        if(priority==null){
            throw new IllegalArgumentException("Ticket priority must not be null!");
        }
        return new Ticket(
                UUID.randomUUID(),
                title,
                description,
                priority,
                TicketStatus.OPEN
        );
    }

//    Business operation:Start Operation
    public void startProgress(){
        if(this.status!=TicketStatus.OPEN){
            throw new IllegalStateException("Ticket is not in OPEN status!");
        }
        this.status=TicketStatus.IN_PROGRESS;
    }

//    Business operation:Resolve ticket
    public void resolve(){
        if(this.status!=TicketStatus.IN_PROGRESS){
            throw new IllegalStateException( "Cannot resolve a ticket in status " + this.status + ". Expected IN_PROGRESS.");
        }
        this.status=TicketStatus.RESOLVED;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public TicketStatus getStatus() {
        return status;
    }
}
