package com.max.dynamicweb.service;

import com.google.gson.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DynamicService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final Gson gson = new Gson();

    public String fetchFileContentAndPrint() {
        String content = restTemplate.getForObject(
                "https://raw.githubusercontent.com/maximianoneto/dynamicweb/master/src/main/java/com/max/dynamicweb/service/DynamicService.java",
                String.class);


        JsonObject body = new JsonObject();
        body.addProperty("model", "gpt-3.5-turbo");
        JsonObject messages = new JsonObject();
        messages.add("role", gson.toJsonTree("system"));
        messages.add("content", gson.toJsonTree("Alter the following Java class according to the new requirements: " + content));
        body.add("messages", gson.toJsonTree(new JsonObject[]{messages}));
        body.addProperty("temperature",0.7);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer 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");
        HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/chat/completions",
                request,
                String.class
        );

        JsonElement parser = JsonParser.parseString(response.getBody());
        String generatedText = parser.getAsJsonObject().get("choices").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString();

        System.out.println(generatedText.trim());
        return generatedText.trim();
    }
}
