package com.transporte.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class StatusResource {

    @GetMapping("/")
    public Map<String, Object> status() {
        return Map.of(
                "api", "Transporte Sustentável - NepTune Labs",
                "status", "online",
                "recursos", new String[]{"/usuarios", "/transportes", "/cartoes", "/passagens", "/resgates"});
    }
}
