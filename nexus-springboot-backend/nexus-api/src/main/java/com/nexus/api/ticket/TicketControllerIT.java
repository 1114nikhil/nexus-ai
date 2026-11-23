//package com.nexus.api.ticket;
//
//import com.nexus.application.ai.TicketClassifier;
//import com.nexus.domain.ticket.TicketPriority;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
//import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import tools.jackson.databind.ObjectMapper;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Testcontainers
//public class TicketControllerIT {
//
//    @Container
//    @ServiceConnection
//    static PostgreSQLContainer<?> postgres=new PostgreSQLContainer<>("postgres:16");
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockitoBean
//    private TicketClassifier ticketClassifier;
//
//    @Test
//    void shouldCreateTicketViaApi() throws Exception{
//        when(ticketClassifier.classify(any(String.class)).thenReturn(TicketPriority.CRITICAL));
//    }
//}
