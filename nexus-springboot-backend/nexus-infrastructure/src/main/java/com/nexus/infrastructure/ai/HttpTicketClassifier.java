package com.nexus.infrastructure.ai;

import com.nexus.application.ai.TicketClassifier;
import com.nexus.domain.ticket.TicketPriority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class HttpTicketClassifier implements TicketClassifier {

    private final RestClient restClient;

    public HttpTicketClassifier(){
        this.restClient=RestClient.builder().baseUrl("http://localhost:8080").build();
    }
    @Override
    public TicketPriority classify(String text) {
        try{
            ClassifyResponse response= restClient.post()
                    .uri("/classify")
                    .body(new ClassifyRequest(text))
                    .retrieve()
                    .body(ClassifyResponse.class);

            if(response !=null){
                return TicketPriority.valueOf(response.priority());
            }
        }catch (Exception e){
            System.err.println("AI Service unavailable, defaulting to MEDIUM: " + e.getMessage());
        }
        return TicketPriority.MEDIUM;
    }

    private record ClassifyRequest(String text){}
    private record ClassifyResponse(String priority,double confidence){}
}
