package com.example.LT_Web2.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class WebController {
    @GetMapping("/welcome")
    public String home() {
        return "Welcome to LT_Web2 Application!";
    }
}
