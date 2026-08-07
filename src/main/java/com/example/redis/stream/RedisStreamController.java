package com.example.redis.stream;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/redis/stream")
@RequiredArgsConstructor
public class RedisStreamController {

    private final RedisStreamCommandsService service;

    // ============================================================
    // 1. XADD - افزودن پیام
    // ============================================================
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> xAdd(
            @RequestParam String key,
            @RequestBody Map<String, String> body) {

        String id = service.xAdd(key, body);
        return ResponseEntity.ok(Map.of("status", "OK", "id", id));
    }

    // ============================================================
    // 2. XREAD - خواندن بدون گروه
    // ============================================================
    @GetMapping("/read")
    public ResponseEntity<List<Map<String, Object>>> xRead(
            @RequestParam String key,
            @RequestParam(defaultValue = "0") String lastId,
            @RequestParam(defaultValue = "10") int count) {

        List<Map<String, Object>> messages = service.xRead(key, lastId, count);
        return ResponseEntity.ok(messages);
    }

    // ============================================================
    // 3. XREADGROUP - خواندن با گروه
    // ============================================================
    @GetMapping("/read-group")
    public ResponseEntity<List<Map<String, Object>>> xReadGroup(
            @RequestParam String key,
            @RequestParam String group,
            @RequestParam String consumer,
            @RequestParam(defaultValue = ">") String lastId,
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(defaultValue = "false") boolean autoAck) {

        List<Map<String, Object>> messages = service.xReadGroup(key, group, consumer, lastId, count, autoAck);
        return ResponseEntity.ok(messages);
    }

    // ============================================================
    // 4. XACK - تأیید پیام
    // ============================================================
    @PostMapping("/ack")
    public ResponseEntity<Map<String, Object>> xAck(
            @RequestParam String key,
            @RequestParam String group,
            @RequestBody List<String> ids) {

        Long count = service.xAck(key, group, ids.toArray(new String[0]));
        return ResponseEntity.ok(Map.of("status", "OK", "acknowledgedCount", count));
    }

    // ============================================================
    // 5. XGROUP CREATE - ایجاد گروه
    // ============================================================
    @PostMapping("/create-group")
    public ResponseEntity<Map<String, Object>> xGroupCreate(
            @RequestParam String key,
            @RequestParam String group,
            @RequestParam(defaultValue = "0") String startId,
            @RequestParam(defaultValue = "true") boolean makeStream) {

        String result = service.xGroupCreate(key, group, startId, makeStream);
        return ResponseEntity.ok(Map.of("status", result));
    }

    // ============================================================
    // 6. XGROUP DESTROY - حذف گروه
    // ============================================================
    @DeleteMapping("/destroy-group")
    public ResponseEntity<Map<String, Object>> xGroupDestroy(
            @RequestParam String key,
            @RequestParam String group) {

        Boolean destroyed = service.xGroupDestroy(key, group);
        return ResponseEntity.ok(Map.of("destroyed", destroyed));
    }

    // ============================================================
    // 7. XPENDING - خلاصه پیام‌های معلق
    // ============================================================
    @GetMapping("/pending-summary")
    public ResponseEntity<Map<String, Object>> xPendingSummary(
            @RequestParam String key,
            @RequestParam String group) {

        Map<String, Object> summary = service.xPendingSummary(key, group);
        return ResponseEntity.ok(summary);
    }

    // ============================================================
    // 8. XPENDING با جزئیات
    // ============================================================
    @GetMapping("/pending-detail")
    public ResponseEntity<List<Map<String, Object>>> xPendingDetail(
            @RequestParam String key,
            @RequestParam String group,
            @RequestParam(defaultValue = "-") String start,
            @RequestParam(defaultValue = "+") String end,
            @RequestParam(defaultValue = "10") int count) {

        List<Map<String, Object>> details = service.xPendingDetailed(key, group, start, end, count);
        return ResponseEntity.ok(details);
    }

    // ============================================================
    // 9. XTRIM - کوتاه کردن Stream
    // ============================================================
    @DeleteMapping("/trim")
    public ResponseEntity<Map<String, Object>> xTrim(
            @RequestParam String key,
            @RequestParam long maxLen) {

        Long removed = service.xTrim(key, maxLen);
        return ResponseEntity.ok(Map.of("status", "OK", "removedCount", removed));
    }

    // ============================================================
    // 10. XDEL - حذف پیام‌ها
    // ============================================================
    @DeleteMapping("/delete-messages")
    public ResponseEntity<Map<String, Object>> xDelete(
            @RequestParam String key,
            @RequestBody List<String> ids) {

        Long deleted = service.xDelete(key, ids.toArray(new String[0]));
        return ResponseEntity.ok(Map.of("status", "OK", "deletedCount", deleted));
    }

    // ============================================================
    // 11. XLEN - طول Stream
    // ============================================================
    @GetMapping("/length")
    public ResponseEntity<Map<String, Object>> xLen(@RequestParam String key) {
        Long length = service.xLen(key);
        return ResponseEntity.ok(Map.of("length", length));
    }

    // ============================================================
    // 12. XRANGE - بازه‌ی پیام‌ها (از ابتدا)
    // ============================================================
    @GetMapping("/range")
    public ResponseEntity<List<Map<String, Object>>> xRange(
            @RequestParam String key,
            @RequestParam(defaultValue = "-") String start,
            @RequestParam(defaultValue = "+") String end) {

        List<Map<String, Object>> messages = service.xRange(key, start, end);
        return ResponseEntity.ok(messages);
    }

    // ============================================================
    // 13. XREVRANGE - بازه‌ی پیام‌ها (از انتها)
    // ============================================================
    @GetMapping("/reverse-range")
    public ResponseEntity<List<Map<String, Object>>> xReverseRange(
            @RequestParam String key,
            @RequestParam(defaultValue = "+") String end,
            @RequestParam(defaultValue = "-") String start,
            @RequestParam(defaultValue = "10") int count) {

        List<Map<String, Object>> messages = service.xReverseRange(key, end, start, count);
        return ResponseEntity.ok(messages);
    }

    // ============================================================
    // 14. XINFO STREAM - اطلاعات Stream
    // ============================================================
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> xInfo(@RequestParam String key) {
        Map<String, Object> info = service.xInfoStream(key);
        return ResponseEntity.ok(info);
    }

    // ============================================================
    // 15. XINFO GROUPS - اطلاعات گروه‌ها
    // ============================================================
    @GetMapping("/info-groups")
    public ResponseEntity<List<Map<String, Object>>> xInfoGroups(@RequestParam String key) {
        List<Map<String, Object>> groups = service.xInfoGroups(key);
        return ResponseEntity.ok(groups);
    }

    // ============================================================
    // 16. XINFO CONSUMERS - اطلاعات مصرف‌کننده‌ها
    // ============================================================
    @GetMapping("/info-consumers")
    public ResponseEntity<List<Map<String, Object>>> xInfoConsumers(
            @RequestParam String key,
            @RequestParam String group) {

        List<Map<String, Object>> consumers = service.xInfoConsumers(key, group);
        return ResponseEntity.ok(consumers);
    }

    // ============================================================
    // 17. DELETE - حذف کل Stream
    // ============================================================
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> delete(@RequestParam String key) {
        Boolean deleted = service.delete(key);
        return ResponseEntity.ok(Map.of("deleted", deleted));
    }
}
