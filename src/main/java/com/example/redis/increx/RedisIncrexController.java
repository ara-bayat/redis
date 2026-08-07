package com.example.redis.increx;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/redis/increx")
public class RedisIncrexController {

    private final RedisIncrexCommandsService service;

    public RedisIncrexController(RedisIncrexCommandsService service) {
        this.service = service;
    }

    // ============================================================
    // 1. INCREX ساده
    // ============================================================
    @PostMapping("/increx")
    public ResponseEntity<Map<String, Object>> increx(@RequestParam String key) {
        List<Long> result = service.increx(key);
        return buildResponse(result);
    }

    // ============================================================
    // 2. INCREX با Bound (حداقل و حداکثر)
    // ============================================================
    @PostMapping("/increx-with-bounds")
    public ResponseEntity<Map<String, Object>> increxWithBounds(
            @RequestParam String key,
            @RequestParam(required = false) Long lowerBound,
            @RequestParam(required = false) Long upperBound,
            @RequestParam(required = false) Long expireSeconds) {

        List<Long> result = service.increxWithBounds(key, lowerBound, upperBound, expireSeconds);
        return buildResponse(result);
    }

    // ============================================================
    // 3. INCREX با افزایش دلخواه
    // ============================================================
    @PostMapping("/increx-by-int")
    public ResponseEntity<Map<String, Object>> increxByInt(
            @RequestParam String key,
            @RequestParam Long increment,
            @RequestParam(required = false) Long lowerBound,
            @RequestParam(required = false) Long upperBound,
            @RequestParam(required = false) Long expireSeconds) {

        List<Long> result = service.increxByInt(key, increment, lowerBound, upperBound, expireSeconds);
        return buildResponse(result);
    }

    // ============================================================
    // 4. INCREX با SATURATE
    // ============================================================
    @PostMapping("/increx-with-saturate")
    public ResponseEntity<Map<String, Object>> increxWithSaturate(
            @RequestParam String key,
            @RequestParam(required = false) Long lowerBound,
            @RequestParam Long upperBound,
            @RequestParam(required = false) Long expireSeconds) {

        List<Long> result = service.increxWithSaturate(key, lowerBound, upperBound, expireSeconds);
        return buildResponse(result);
    }

    // ============================================================
    // 5. INCREX با ENX (فقط در صورت عدم وجود کلید)
    // ============================================================
    @PostMapping("/increx-if-not-exists")
    public ResponseEntity<Map<String, Object>> increxIfNotExists(
            @RequestParam String key,
            @RequestParam(required = false) Long lowerBound,
            @RequestParam(required = false) Long upperBound,
            @RequestParam(required = false) Long expireSeconds) {

        List<Long> result = service.increxIfNotExists(key, lowerBound, upperBound, expireSeconds);
        return buildResponse(result);
    }

    // ============================================================
    // 6. GET - خواندن مقدار فعلی شمارنده
    // ============================================================
    @GetMapping("/value")
    public ResponseEntity<Map<String, Object>> getCurrentValue(@RequestParam String key) {
        Long value = service.getCurrentValue(key);
        Map<String, Object> response = new HashMap<>();
        if (value == null) {
            response.put("exists", false);
            response.put("message", "Key does not exist or has no value.");
        } else {
            response.put("exists", true);
            response.put("value", value);
            // TTL را هم اضافه می‌کنیم
            Long ttl = service.getTTL(key, TimeUnit.SECONDS);
            response.put("ttl", ttl != null ? ttl : -1);
        }
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // 7. DELETE - حذف کلید (ریست کردن شمارنده)
    // ============================================================
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> delete(@RequestParam String key) {
        Boolean deleted = service.delete(key);
        Map<String, Object> response = new HashMap<>();
        response.put("deleted", deleted);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // متد کمکی برای ساخت پاسخ یکسان
    // ============================================================
    private ResponseEntity<Map<String, Object>> buildResponse(List<Long> result) {
        Map<String, Object> response = new HashMap<>();
        if (result == null || result.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Command failed or returned empty result.");
            return ResponseEntity.badRequest().body(response);
        }

        Long currentValue = result.get(0);
        Long status = result.size() > 1 ? result.get(1) : null;

        response.put("currentValue", currentValue);
        response.put("status", status);
        response.put("statusDescription", status != null && status == 1 ? "SUCCESS (Allowed)" : "REJECTED (Rate limit exceeded)");

        return ResponseEntity.ok(response);
    }
}