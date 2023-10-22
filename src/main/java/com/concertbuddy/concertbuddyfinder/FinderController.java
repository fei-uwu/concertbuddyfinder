package com.concertbuddy.concertbuddyfinder;

import org.springframework.web.bind.annotation.*;

@RestController
public class FinderController {
    
    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }
}
