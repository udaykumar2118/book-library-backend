package com.library.booklibrarymanager.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping("/hello")
    public String hello() {
        System.out.println("=== DEBUG: GET /api/test/hello called ===");
        return "Hello World!";
    }
    
    @PostMapping("/echo")
    public String echo(@RequestBody String body) {
        System.out.println("=== DEBUG: POST /api/test/echo called ===");
        System.out.println("Body: " + body);
        return "Echo: " + body;
    }
    
    @PostMapping("/signup-test")
    public String signupTest(@RequestBody String body) {
        System.out.println("=== DEBUG: POST /api/test/signup-test called ===");
        System.out.println("Body: " + body);
        return "Signup test: " + body;
    }
}