package com.unito.randomfilm.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        log.info("Test ping endpoint called");
        return ResponseEntity.ok("pong");
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> handleOptions() {
        log.info("OPTIONS request received");
        return ResponseEntity.ok().build();
    }
}