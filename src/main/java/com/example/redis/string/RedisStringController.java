package com.example.redis.string;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/redis/string")
@RequiredArgsConstructor
public class RedisStringController {

    private final RedisStringCommandsService service;

    // ============================================================
    // 1. SET - ذخیره‌سازی مقدار
    // ============================================================
    @PostMapping("/set")
    public ResponseEntity<String> set(@RequestParam String key,
                                      @RequestParam String value) {
        service.set(key, value);
        return ResponseEntity.ok("OK");
    }

    // ============================================================
    // 2. SET با زمان انقضا (معادل SETEX)
    // ============================================================
    @PostMapping("/set-with-expire")
    public ResponseEntity<String> setWithExpire(@RequestParam String key,
                                                @RequestParam String value,
                                                @RequestParam long timeout,
                                                @RequestParam(defaultValue = "SECONDS") String unit) {
        TimeUnit timeUnit = TimeUnit.valueOf(unit.toUpperCase());
        service.setWithExpire(key, value, timeout, timeUnit);
        return ResponseEntity.ok("OK");
    }

    // ============================================================
    // 3. GET - دریافت مقدار
    // ============================================================
    @GetMapping("/get")
    public ResponseEntity<String> get(@RequestParam String key) {
        String value = service.get(key);
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(value);
    }

    // ============================================================
    // 4. DEL - حذف کلید
    // ============================================================
    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> delete(@RequestParam String key) {
        Boolean result = service.delete(key);
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 5. EXISTS - بررسی وجود کلید
    // ============================================================
    @GetMapping("/exists")
    public ResponseEntity<Boolean> exists(@RequestParam String key) {
        Boolean result = service.exists(key);
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 6. EXPIRE - تنظیم زمان انقضا روی کلید موجود
    // ============================================================
    @PostMapping("/expire")
    public ResponseEntity<Boolean> expire(@RequestParam String key,
                                          @RequestParam long timeout,
                                          @RequestParam(defaultValue = "SECONDS") String unit) {
        TimeUnit timeUnit = TimeUnit.valueOf(unit.toUpperCase());
        Boolean result = service.expire(key, timeout, timeUnit);
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 7. TTL - مشاهده‌ی زمان باقی‌مانده
    // ============================================================
    @GetMapping("/ttl")
    public ResponseEntity<Long> getTTL(@RequestParam String key,
                                       @RequestParam(defaultValue = "SECONDS") String unit) {
        TimeUnit timeUnit = TimeUnit.valueOf(unit.toUpperCase());
        Long ttl = service.getTTL(key, timeUnit);
        return ResponseEntity.ok(ttl);
    }

    // ============================================================
    // 8. SETNX - تنظیم فقط اگه کلید وجود نداشت
    // ============================================================
    @PostMapping("/set-if-absent")
    public ResponseEntity<Boolean> setIfAbsent(@RequestParam String key,
                                               @RequestParam String value) {
        Boolean result = service.setIfAbsent(key, value);
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 9. SETNX با زمان انقضا (برای قفل توزیع‌شده)
    // ============================================================
    @PostMapping("/set-if-absent-with-expire")
    public ResponseEntity<Boolean> setIfAbsentWithExpire(@RequestParam String key,
                                                         @RequestParam String value,
                                                         @RequestParam long timeout,
                                                         @RequestParam(defaultValue = "SECONDS") String unit) {
        TimeUnit timeUnit = TimeUnit.valueOf(unit.toUpperCase());
        Boolean result = service.setIfAbsentWithExpire(key, value, timeout, timeUnit);
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 10. MGET - گرفتن چند کلید با هم
    // ============================================================
    @PostMapping("/multi-get")
    public ResponseEntity<List<String>> multiGet(@RequestBody List<String> keys) {
        List<String> values = service.multiGet(keys);
        return ResponseEntity.ok(values);
    }

    // ============================================================
    // 11. MSET - تنظیم چند کلید با هم
    // ============================================================
    @PostMapping("/multi-set")
    public ResponseEntity<String> multiSet(@RequestBody Map<String, String> map) {
        service.multiSet(map);
        return ResponseEntity.ok("OK");
    }

    // ============================================================
    // 12. INCR - افزایش ۱ واحدی
    // ============================================================
    @PostMapping("/increment")
    public ResponseEntity<Long> increment(@RequestParam String key) {
        Long result = service.increment(key);
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 13. INCRBY - افزایش با مقدار دلخواه
    // ============================================================
    @PostMapping("/increment-by")
    public ResponseEntity<Long> incrementBy(@RequestParam String key,
                                            @RequestParam long delta) {
        Long result = service.incrementBy(key, delta);
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 14. DECR - کاهش ۱ واحدی
    // ============================================================
    @PostMapping("/decrement")
    public ResponseEntity<Long> decrement(@RequestParam String key) {
        Long result = service.decrement(key);
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 15. DECRBY - کاهش با مقدار دلخواه
    // ============================================================
    @PostMapping("/decrement-by")
    public ResponseEntity<Long> decrementBy(@RequestParam String key,
                                            @RequestParam long delta) {
        Long result = service.decrementBy(key, delta);
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 16. APPEND - اضافه کردن به انتهای مقدار
    // ============================================================
    @PostMapping("/append")
    public ResponseEntity<Integer> append(@RequestParam String key,
                                          @RequestParam String value) {
        Integer newLength = service.append(key, value);
        return ResponseEntity.ok(newLength);
    }

    // ============================================================
    // 17. STRLEN - طول مقدار
    // ============================================================
    @GetMapping("/size")
    public ResponseEntity<Long> size(@RequestParam String key) {
        Long length = service.size(key);
        return ResponseEntity.ok(length);
    }

    // ============================================================
    // 18. GETRANGE - زیررشته
    // ============================================================
    @GetMapping("/get-range")
    public ResponseEntity<String> getRange(@RequestParam String key,
                                           @RequestParam long start,
                                           @RequestParam long end) {
        String result = service.getRange(key, start, end);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }
}
