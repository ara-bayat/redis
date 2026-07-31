package com.example.redis;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/redis")
public class RedisController {

    private final RedisService redisService;

    public RedisController(RedisService redisService) {
        this.redisService = redisService;
    }

    @PostMapping("/set")
    public ResponseEntity<Map<String, String>> set(@RequestBody Map<String, String> body) {
        String key = body.get("key");
        String value = body.get("value");

        if (key == null || value == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "key and value are required"));
        }

        redisService.set(key, value);
        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "key", key,
                "value", value
        ));
    }

    @GetMapping("/get/{key}")
    public ResponseEntity<Map<String, String>> get(@PathVariable String key) {
        String value = redisService.get(key);

        if (value == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(Map.of(
                "key", key,
                "value", value
        ));
    }
}
