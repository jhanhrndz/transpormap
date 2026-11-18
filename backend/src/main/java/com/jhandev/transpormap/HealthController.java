package com.jhandev.transpormap;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class HealthController {
    @GetMapping("/v1/health")
    Map<String, String> health() {
        return Map.of("status", "up");
    }
}