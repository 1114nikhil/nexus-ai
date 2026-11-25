//package com.nexus.infrastructure.ai;
////
////import com.nexus.application.ai.TicketClassifier;
////import com.nexus.domain.ticket.TicketPriority;
////import org.springframework.http.MediaType;
////import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
////import org.springframework.stereotype.Component;
////import org.springframework.web.client.RestClient;
////
////@Component
////public class HttpTicketClassifier implements TicketClassifier {
////
////    private final RestClient restClient;
////
////    public HttpTicketClassifier(){
//////        this.restClient=RestClient.builder().baseUrl("http://localhost:8000").build();
//////        this.restClient = RestClient.builder()
//////                .baseUrl("http://localhost:8000")
//////                .configureMessageConverters(builder -> {
//////                    // Key: First register default converters
//////                    builder.registerDefaults();
//////                    // Then add Jackson JSON converter (ensure JSON serialization)
//////                    builder.withJsonConverter(new JacksonJsonHttpMessageConverter());
//////                })
//////                .build();
////        this.restClient=RestClient.builder()
////                .baseUrl("http://localhost:8000")
////                .configureMessageConverters(builder -> {
////                    builder.registerDefaults();              // <-- REQUIRED
////                    builder.withJsonConverter(new JacksonJsonHttpMessageConverter());
////                })
////                .build();
////    }
////    @Override
////    public TicketPriority classify(String text) {
////        try{
////            ClassifyRequest request = new ClassifyRequest(text);
////            System.out.println("Sending to Python: " + request);
////            ClassifyResponse response= restClient.post()
////                    .uri("/classify")
////                    .contentType(MediaType.APPLICATION_JSON)
////                    .body(new ClassifyRequest(text))
////                    .retrieve()
////                    .body(ClassifyResponse.class);
////
////            if(response !=null){
////                return TicketPriority.valueOf(response.priority());
////            }
////        }catch (Exception e){
////            System.err.println("AI Service unavailable, defaulting to MEDIUM: " + e.getMessage());
////        }
////        return TicketPriority.MEDIUM;
////    }
////
////    public record ClassifyRequest(String text){}
////    public record ClassifyResponse(String priority,double confidence){}
////}
package com.nexus.infrastructure.ai;

import com.nexus.application.ai.TicketClassifier;
import com.nexus.domain.ticket.TicketPriority;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class HttpTicketClassifier implements TicketClassifier {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String PYTHON_URL = "http://localhost:8000/classify";

    @Override
    public TicketPriority classify(String text) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = Map.of("text", text);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            System.out.println("Sending to Python: " + body);

            Map<?, ?> response = restTemplate.postForObject(PYTHON_URL, request, Map.class);

            if (response != null && response.get("priority") != null) {
                return TicketPriority.valueOf(response.get("priority").toString());
            }
        } catch (Exception e) {
            System.err.println("AI Service unavailable, defaulting to MEDIUM: " + e.getMessage());
        }
        return TicketPriority.MEDIUM;
    }
}