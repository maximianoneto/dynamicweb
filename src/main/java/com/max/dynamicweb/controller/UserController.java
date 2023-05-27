package com.max.dynamicweb.controller;


import com.max.dynamicweb.crawller.WebCrawler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller
public class UserController {

    @PostMapping("/modify")
    public ResponseEntity<String> modifyClass(@RequestBody Map<String, String> payload) {
        String classContent = payload.get("classContent");
        if (classContent == null) {
            return new ResponseEntity<>("classContent is required", HttpStatus.BAD_REQUEST);
        }

        WebCrawler crawler = new WebCrawler(classContent);
        // Call the relevant methods of the WebCrawler
        // This will likely involve more complex processing and error handling

        // Assuming that the crawler updates the classContent
        String updatedClassContent = classContent; // Update this with the actual updated content

        return new ResponseEntity<>(updatedClassContent, HttpStatus.OK);
    }
}
