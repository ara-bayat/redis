package com.example.redis.array;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/redis/array")
@RequiredArgsConstructor
public class RedisArrayController {

    private final RedisArrayCommandsService service;

    // ============================================================
    // 1. ARSET - تنظیم مقدار(های) متوالی
    // ============================================================
    @PostMapping("/set")
    public ResponseEntity<Map<String, Object>> arSet(
            @RequestParam String key,
            @RequestParam long index,
            @RequestBody List<String> values) {

        Long result = service.arSet(key, index, values.toArray(new String[0]));
        return buildResponse(result);
    }

    // ============================================================
    // 2. ARMSET - تنظیم چند جفت (ایندکس، مقدار) غیرمتوالی
    // ============================================================
    @PostMapping("/mset")
    public ResponseEntity<Map<String, Object>> arMSet(
            @RequestParam String key,
            @RequestBody Map<Long, String> indexValuePairs) {

        Long result = service.arMSet(key, indexValuePairs);
        return buildResponse(result);
    }

    // ============================================================
    // 3. ARGET - دریافت مقدار یک ایندکس
    // ============================================================
    @GetMapping("/get")
    public ResponseEntity<Map<String, Object>> arGet(
            @RequestParam String key,
            @RequestParam long index) {

        String value = service.arGet(key, index);
        Map<String, Object> response = new HashMap<>();
        if (value == null) {
            response.put("exists", false);
            response.put("message", "Index is empty or does not exist.");
        } else {
            response.put("exists", true);
            response.put("value", value);
        }
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // 4. ARGETRANGE - دریافت بازه‌ای از مقادیر
    // ============================================================
    @GetMapping("/range")
    public ResponseEntity<Map<String, Object>> arGetRange(
            @RequestParam String key,
            @RequestParam long start,
            @RequestParam long end) {

        List<String> values = service.arGetRange(key, start, end);
        Map<String, Object> response = new HashMap<>();
        response.put("values", values);
        response.put("count", values.size());
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // 5. ARLEN - طول آرایه
    // ============================================================
    @GetMapping("/len")
    public ResponseEntity<Map<String, Object>> arLen(@RequestParam String key) {
        Long length = service.arLen(key);
        Map<String, Object> response = new HashMap<>();
        response.put("length", length);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // 6. ARCOUNT - تعداد خانه‌های غیرخالی
    // ============================================================
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> arCount(@RequestParam String key) {
        Long count = service.arCount(key);
        Map<String, Object> response = new HashMap<>();
        response.put("nonEmptyCount", count);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // 7. ARDEL - حذف عناصر در ایندکس‌های مشخص
    // ============================================================
    @DeleteMapping("/del")
    public ResponseEntity<Map<String, Object>> arDel(
            @RequestParam String key,
            @RequestBody List<Long> indices) {

        long[] indicesArray = indices.stream().mapToLong(Long::longValue).toArray();
        Long result = service.arDel(key, indicesArray);
        return buildResponse(result);
    }

    // ============================================================
    // 8. ARDELRANGE - حذف عناصر در بازه‌ها
    // ============================================================
    @DeleteMapping("/del-range")
    public ResponseEntity<Map<String, Object>> arDelRange(
            @RequestParam String key,
            @RequestBody List<List<Long>> ranges) {

        List<long[]> rangeArray = ranges.stream()
                .map(r -> new long[]{r.getFirst(), r.get(1)})
                .toList();

        Long result = service.arDelRange(key, rangeArray);
        return buildResponse(result);
    }

    // ============================================================
    // 9. ARINSERT - درج مقدار(های) جدید در ایندکس مشخص
    // ============================================================
    @PostMapping("/insert")
    public ResponseEntity<Map<String, Object>> arInsert(
            @RequestParam String key,
            @RequestParam long index,
            @RequestBody List<String> values) {

        Long result = service.arInsert(key, index, values.toArray(new String[0]));
        return buildResponse(result);
    }

    // ============================================================
    // 10. ARGREP - جستجوی الگو در بازه
    // ============================================================
    @GetMapping("/grep")
    public ResponseEntity<Map<String, Object>> arGrep(
            @RequestParam String key,
            @RequestParam long start,
            @RequestParam long end,
            @RequestParam String pattern) {

        List<Long> indices = service.arGrep(key, start, end, pattern);
        Map<String, Object> response = new HashMap<>();
        response.put("matchingIndices", indices);
        response.put("count", indices.size());
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // 11. ARRING - Ring Buffer (حافظه حلقوی)
    // ============================================================
    @PostMapping("/ring")
    public ResponseEntity<Map<String, Object>> arRing(
            @RequestParam String key,
            @RequestParam long capacity,
            @RequestBody List<String> values) {

        Long result = service.arRing(key, capacity, values.toArray(new String[0]));
        Map<String, Object> response = new HashMap<>();
        response.put("totalWritten", result);
        response.put("status", "SUCCESS");
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // 12. ARLASTITEMS - دریافت آخرین N عنصر Ring Buffer
    // ============================================================
    @GetMapping("/last-items")
    public ResponseEntity<Map<String, Object>> arLastItems(
            @RequestParam String key,
            @RequestParam long count) {

        List<String> items = service.arLastItems(key, count);
        Map<String, Object> response = new HashMap<>();
        response.put("items", items);
        response.put("count", items.size());
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // 13. DELETE - حذف کلید (پاک کردن کامل آرایه)
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
    private ResponseEntity<Map<String, Object>> buildResponse(Long result) {
        Map<String, Object> response = new HashMap<>();
        if (result == null) {
            response.put("status", "error");
            response.put("message", "Command failed.");
            return ResponseEntity.badRequest().body(response);
        }
        response.put("status", "SUCCESS");
        response.put("filledSlots", result);
        return ResponseEntity.ok(response);
    }
}
