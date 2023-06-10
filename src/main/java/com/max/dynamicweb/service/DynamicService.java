package com.max.dynamicweb.service;

import com.google.gson.*;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DynamicService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final Gson gson = new Gson();

    private final Dotenv dotenv = Dotenv.configure().load();

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
        body.addProperty("max_tokens",1000);
        body.addProperty("temperature",0);

        JsonArray messages = new JsonArray();
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", "This is your class repository, you are Java 17 SpringBoot 3.1.0 and your roleplay as WEBSERVICE: "
                +"the following class contains the code of your repository and the dependencies of your code(pom.xml), make new changes to the code to meet the user's expectations with the webservice application, You must generate only a changed class or a new class! Your code :"
                +"MAIN CLASS:\n "
                + mainClass + " USER CONTROLLER:\n "+  userControllerClass + "SERVICE CLASS:\n"+ dynamicServiceClass + "DEPENDENCIES:\n"+ pomXmlClass+
                "// REQUIREMENTS: " + payload);
        messages.add(systemMessage);
        body.add("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+ dotenv.get("OPENAI_API_KEY"));
        HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/chat/completions",
                request,
                String.class
        );

        JsonElement parser = JsonParser.parseString(response.getBody());
        //JsonElement choicesElement = parser.getAsJsonObject().get("choices");
        JsonArray choicesArray = parser.getAsJsonObject().get("choices").getAsJsonArray();

        // Iterate over the choices array
        for (JsonElement choiceElement : choicesArray) {
            // Get the 'message' object
            JsonElement messageElement = choiceElement.getAsJsonObject().get("message");

            // Get the 'content' string
            String content = messageElement.getAsJsonObject().get("content").getAsString();

            // Regex pattern to match class information
            String patternString = "(```java\\n)(.*?)(\\n```)";

            Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(content);

            while (matcher.find()) {
                // Assign the matched group to a String variable
                String classContent = matcher.group(2).trim();

                // Print the class content
                System.out.println(classContent);
            }
        }

        // Handle the case where the expected "choices" property is not present or not in the expected format
        System.out.println("Unexpected response from API: " + response.getBody());
        return null;

    }

}
