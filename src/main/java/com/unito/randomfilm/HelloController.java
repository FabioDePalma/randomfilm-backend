package com.unito.randomfilm;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/")
    public String ciao() {
        return "Ciao a tuttiiiiiiiiiii!!";
    }
}
