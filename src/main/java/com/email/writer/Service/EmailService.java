package com.email.writer.Service;

import com.email.writer.Email_vapp.EmailRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class EmailService {

    private final WebClient webClient;
    @Value("${gemini.api.key}")
    private String geminiApiKey;
    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    public EmailService(WebClient.Builder webClient) {
        this.webClient = webClient.build();
    }
    public String GenerateEmailReply(EmailRequest emailRequest){
         //we have 4 stages
         //stage 1- Build prompt
         //stage 2- Craft Request
         //stage 3-Do request and return response
         //stage 4-Return Response

         String prompt=buildPrompt(emailRequest);
         //stage 2
         Map<String, Object> requestBody= Map.of("contents",new Object[]{
                 Map.of("parts",new Object[]{
                         Map.of("text",prompt)
                 })
         });
         //stage 3
        String response=webClient.post()
                   .uri(geminiApiUrl + geminiApiKey)
                   .header("Content-Type","application/json")
                   .bodyValue(requestBody)
                   .retrieve()
                   .bodyToMono(String.class)
                   .block();
      return extractResponseContent(response);
     }

    private String extractResponseContent(String response) {
        try {
            ObjectMapper objectMapper=new ObjectMapper();
            JsonNode rootNode=objectMapper.readTree(response);
            return rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        }
        catch (Exception e) {
        return "Error"+e.getMessage();
        }
    }

    private String buildPrompt(EmailRequest emailRequest) {
    StringBuilder prompt=new StringBuilder();
    prompt.append("Generate the professional email reply for the following email content without writing subject");
    if(emailRequest.getTone()!=null && !emailRequest.getTone().isEmpty()){
        prompt.append("Use tone").append(emailRequest.getTone()).append("for the reply");
    }
    prompt.append(" \n Originial email is: \n").append(emailRequest.getEmailContent());
    return prompt.toString();
    }

}
