package com.perfectdart.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccueilController {
    @GetMapping("/")
    public String accueil() {
        return "Bienvenue !";
    }
}