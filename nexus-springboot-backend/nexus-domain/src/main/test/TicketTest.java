import com.nexus.domain.ticket.Ticket;
import com.nexus.domain.ticket.TicketPriority;
import com.nexus.domain.ticket.TicketStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class TicketTest {
    @Test
    void shouldCreateTicketWithOpenStatus(){
        Ticket ticket=Ticket.create(
                "Login Issue",
                "Unable to login the application",
                TicketPriority.HIGH
        );
        assertNotNull(ticket);
        assertEquals("Login Issue",ticket.getTitle());
        assertEquals("Unable to login the application",ticket.getDescription());
        assertEquals(TicketPriority.HIGH,ticket.getPriority());
        assertEquals(TicketStatus.OPEN,ticket.getStatus());
    }

    @Test
    void shouldThrowWhenTitleIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                Ticket.create("  ", "some description", TicketPriority.MEDIUM));
    }

    @Test
    void shouldResolveAnInProgressTicket() {
        Ticket ticket = Ticket.create("Login Issue", "Desc", TicketPriority.HIGH);
        ticket.startProgress();
        ticket.resolve();
        assertEquals(TicketStatus.RESOLVED, ticket.getStatus());
    }

    @Test
    void shouldThrowWhenResolvingAnOpenTicket() {
        Ticket ticket = Ticket.create("Login Issue", "Desc", TicketPriority.HIGH);
        // Don't call startProgress() — resolve directly
        IllegalStateException ex = assertThrows(IllegalStateException.class, ticket::resolve);
        assertTrue(ex.getMessage().contains("IN_PROGRESS"));
    }

    @Test
    void shouldThrowWhenStartingAnAlreadyInProgressTicket() {
        Ticket ticket = Ticket.create("Login Issue", "Desc", TicketPriority.HIGH);
        ticket.startProgress();
        assertThrows(IllegalStateException.class, ticket::startProgress);
    }
}
