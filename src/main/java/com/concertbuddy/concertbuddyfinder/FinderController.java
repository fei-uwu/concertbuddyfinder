package com.concertbuddy.concertbuddyfinder;

import org.springframework.web.bind.annotation.*;

@RestController
public class FinderController {
    
    @PostMapping("api/v1/finder/find")
    public String index() {
        return "Greetings from Spring Boot!";
    }
}
