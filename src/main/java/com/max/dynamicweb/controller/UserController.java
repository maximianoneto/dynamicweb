package com.max.dynamicweb.controller;

import com.max.dynamicweb.service.DynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import java.util.Map;

@Controller
public class UserController {

    @Autowired
    DynamicService dynamicService;

    private final String url = "https://github.com/maximianoneto/dynamicweb/blob/master/src/main/java/com/max/dynamicweb/service/DynamicService.java";

    @PostMapping("/modify")
    public ResponseEntity<String> modifyClass(@RequestBody Map<String, String> payload) {
        String actualClass = null;
        try {

            String classContent = payload.get("classContent");
            if (classContent == null) {
                return new ResponseEntity<>("classContent is required", HttpStatus.BAD_REQUEST);
            }
            actualClass = dynamicService.fetchFileContentAndPrint(payload);

            return ResponseEntity.ok(actualClass);

        } catch (Exception e) {
            e.printStackTrace();
            return (ResponseEntity<String>) ResponseEntity.badRequest();
        }

    }
}
