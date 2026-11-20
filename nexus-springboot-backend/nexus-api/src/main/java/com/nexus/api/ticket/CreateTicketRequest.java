package com.nexus.api.ticket;

import com.nexus.domain.ticket.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTicketRequest(
        @NotBlank(message ="Title is Required")
        String title,
        @NotBlank(message ="Description is required")
        String description,
        @NotNull(message = "Priority is required")
        TicketPriority priority
) { }
