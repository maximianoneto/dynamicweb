package com.max.dynamicweb.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class DynamicService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final Gson gson = new Gson();

    public String fetchFileContentAndPrint(Map<String, String> payload) {
        String dynamicServiceClass = restTemplate.getForObject(
                "https://raw.githubusercontent.com/maximianoneto/dynamicweb/master/src/main/java/com/max/dynamicweb/service/DynamicService.java",
                String.class);

        String userControllerClass = restTemplate.getForObject(
                "https://raw.githubusercontent.com/maximianoneto/dynamicweb/master/src/main/java/com/max/dynamicweb/controller/UserController.java",
                String.class);

        String mainClass = restTemplate.getForObject(
                "https://raw.githubusercontent.com/maximianoneto/dynamicweb/master/src/main/java/com/max/dynamicweb/DynamicWebserviceApplication.java",
                String.class);

        String pomXmlClass = restTemplate.getForObject(
                "https://raw.githubusercontent.com/maximianoneto/dynamicweb/master/pom.xml",
                String.class);

        JsonObject body = new JsonObject();
        body.addProperty("model", "gpt-4");
        JsonObject messages = new JsonObject();
        messages.add("role", gson.toJsonTree("system"));
        messages.add("content",
                gson.toJsonTree("This is your class repository, you are Java 17 SpringBoot 3.1.0 and your roleplay as WEBSERVICE: "
                +"the following class contains the code of your repository and the dependencies of your code(pom.xml), make new changes to the code to meet the user's expectations with the webservice application, generate only a changed class or a new class or method to be added to some class of the repository. Your code :"
                +"MAIN CLASS:\n "
                + mainClass + " USER CONTROLLER:\n "+  userControllerClass + "SERVICE CLASS:\n"+ dynamicServiceClass + "DEPENDENCIES:\n"+ pomXmlClass+
                "// REQUIREMENTS: " + payload));
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
