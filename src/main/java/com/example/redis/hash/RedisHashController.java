package com.example.redis.hash;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/redis/hash")
@RequiredArgsConstructor
public class RedisHashController {

    private final RedisHashCommandsService service;

    // ============================================================
    // 1. HSET - تنظیم یک فیلد
    // ============================================================
    @PostMapping("/set")
    public ResponseEntity<String> hSet(@RequestParam String key,
                                       @RequestParam String field,
                                       @RequestParam String value) {
        service.hSet(key, field, value);
        return ResponseEntity.ok("OK");
    }

    // ============================================================
    // 2. HSET با چند فیلد (Body JSON)
    // ============================================================
    @PostMapping("/set-all")
    public ResponseEntity<String> hSetAll(@RequestParam String key,
                                          @RequestBody Map<String, String> map) {
        service.hSetAll(key, map);
        return ResponseEntity.ok("OK");
    }

    // ============================================================
    // 3. HSETNX - تنظیم فقط اگه فیلد وجود نداشت
    // ============================================================
    @PostMapping("/set-if-absent")
    public ResponseEntity<Boolean> hSetIfAbsent(@RequestParam String key,
                                                @RequestParam String field,
                                                @RequestParam String value) {
        Boolean result = service.hSetIfAbsent(key, field, value);
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 4. HGET - دریافت یک فیلد
    // ============================================================
    @GetMapping("/get")
    public ResponseEntity<String> hGet(@RequestParam String key,
                                       @RequestParam String field) {
        String value = service.hGet(key, field);
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(value);
    }

    // ============================================================
    // 5. HMGET - دریافت چند فیلد
    // ============================================================
    @PostMapping("/multi-get")
    public ResponseEntity<List<Object>> hMultiGet(@RequestParam String key,
                                                  @RequestBody List<Object> fields) {
        List<Object> values = service.hMultiGet(key, fields);
        return ResponseEntity.ok(values);
    }

    // ============================================================
    // 6. HGETALL - دریافت همه‌ی فیلدها و مقادیر (فقط برای هش‌های کوچک!)
    // ============================================================
    @GetMapping("/get-all")
    public ResponseEntity<Map<Object, Object>> hGetAll(@RequestParam String key) {
        Map<Object, Object> entries = service.hGetAll(key);
        if (entries.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(entries);
    }

    // ============================================================
    // 7. HKEYS - گرفتن همه‌ی فیلدها
    // ============================================================
    @GetMapping("/keys")
    public ResponseEntity<Set<Object>> hKeys(@RequestParam String key) {
        Set<Object> keys = service.hKeys(key);
        return ResponseEntity.ok(keys);
    }

    // ============================================================
    // 8. HVALS - گرفتن همه‌ی مقادیر
    // ============================================================
    @GetMapping("/values")
    public ResponseEntity<List<Object>> hVals(@RequestParam String key) {
        List<Object> values = service.hVals(key);
        return ResponseEntity.ok(values);
    }

    // ============================================================
    // 9. HDEL - حذف فیلدها
    // ============================================================
    @DeleteMapping("/delete")
    public ResponseEntity<Long> hDelete(@RequestParam String key,
                                        @RequestParam List<String> fields) {
        Long deleted = service.hDelete(key, fields.toArray(new String[0]));
        return ResponseEntity.ok(deleted);
    }

    // ============================================================
    // 10. HEXISTS - بررسی وجود فیلد
    // ============================================================
    @GetMapping("/exists")
    public ResponseEntity<Boolean> hExists(@RequestParam String key,
                                           @RequestParam String field) {
        Boolean exists = service.hExists(key, field);
        return ResponseEntity.ok(exists);
    }

    // ============================================================
    // 11. HLEN - تعداد فیلدها
    // ============================================================
    @GetMapping("/size")
    public ResponseEntity<Long> hSize(@RequestParam String key) {
        Long size = service.hSize(key);
        return ResponseEntity.ok(size);
    }

    // ============================================================
    // 12. HINCRBY - افزایش عددی (تعداد)
    // ============================================================
    @PostMapping("/increment")
    public ResponseEntity<Long> hIncrement(@RequestParam String key,
                                           @RequestParam String field,
                                           @RequestParam(defaultValue = "1") long delta) {
        Long result = service.hIncrement(key, field, delta);
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 13. HINCRBYFLOAT - افزایش اعشاری
    // ============================================================
    @PostMapping("/increment-float")
    public ResponseEntity<Double> hIncrementFloat(@RequestParam String key,
                                                  @RequestParam String field,
                                                  @RequestParam double delta) {
        Double result = service.hIncrementFloat(key, field, delta);
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 14. HSTRLEN - طول مقدار یک فیلد خاص
    // ============================================================
    @GetMapping("/strlen")
    public ResponseEntity<Long> hStrLen(@RequestParam String key,
                                        @RequestParam String field) {
        Long length = service.hStrLen(key, field);
        return ResponseEntity.ok(length);
    }
}
