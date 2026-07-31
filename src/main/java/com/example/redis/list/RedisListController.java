package com.example.redis.list;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/redis/list")
@RequiredArgsConstructor
public class RedisListController {

    private final RedisListCommandsService service;

    // ============================================================
    // 1. LPUSH - اضافه به چپ
    // ============================================================
    @PostMapping("/left-push")
    public ResponseEntity<Long> leftPush(@RequestParam String key,
                                         @RequestParam String value) {
        Long result = service.leftPush(key, value);
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 2. LPUSH با چند مقدار (Array)
    // ============================================================
    @PostMapping("/left-push-all")
    public ResponseEntity<Long> leftPushAll(@RequestParam String key,
                                            @RequestBody List<String> values) {
        Long result = service.leftPushAll(key, values.toArray(new String[0]));
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 3. RPUSH - اضافه به راست
    // ============================================================
    @PostMapping("/right-push")
    public ResponseEntity<Long> rightPush(@RequestParam String key,
                                          @RequestParam String value) {
        Long result = service.rightPush(key, value);
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 4. RPUSH با چند مقدار
    // ============================================================
    @PostMapping("/right-push-all")
    public ResponseEntity<Long> rightPushAll(@RequestParam String key,
                                             @RequestBody List<String> values) {
        Long result = service.rightPushAll(key, values.toArray(new String[0]));
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 5. LPOP - گرفتن و حذف از چپ
    // ============================================================
    @DeleteMapping("/left-pop")
    public ResponseEntity<String> leftPop(@RequestParam String key) {
        String value = service.leftPop(key);
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(value);
    }

    // ============================================================
    // 6. LPOP با تعداد
    // ============================================================
    @DeleteMapping("/left-pop-many")
    public ResponseEntity<List<String>> leftPopMany(@RequestParam String key,
                                                    @RequestParam long count) {
        List<String> values = service.leftPop(key, count);
        return ResponseEntity.ok(values);
    }

    // ============================================================
    // 7. RPOP - گرفتن و حذف از راست
    // ============================================================
    @DeleteMapping("/right-pop")
    public ResponseEntity<String> rightPop(@RequestParam String key) {
        String value = service.rightPop(key);
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(value);
    }

    // ============================================================
    // 8. RPOP با تعداد
    // ============================================================
    @DeleteMapping("/right-pop-many")
    public ResponseEntity<List<String>> rightPopMany(@RequestParam String key,
                                                     @RequestParam long count) {
        List<String> values = service.rightPop(key, count);
        return ResponseEntity.ok(values);
    }

    // ============================================================
    // 9. LRANGE - گرفتن بازه‌ای از لیست
    // ============================================================
    @GetMapping("/range")
    public ResponseEntity<List<String>> range(@RequestParam String key,
                                              @RequestParam(defaultValue = "0") long start,
                                              @RequestParam(defaultValue = "-1") long end) {
        List<String> result = service.range(key, start, end);
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 10. LLEN - طول لیست
    // ============================================================
    @GetMapping("/size")
    public ResponseEntity<Long> size(@RequestParam String key) {
        Long size = service.size(key);
        return ResponseEntity.ok(size);
    }

    // ============================================================
    // 11. LINDEX - گرفتن عنصر با ایندکس
    // ============================================================
    @GetMapping("/index")
    public ResponseEntity<String> index(@RequestParam String key,
                                        @RequestParam long index) {
        String value = service.index(key, index);
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(value);
    }

    // ============================================================
    // 12. LREM - حذف عناصر تکراری
    // ============================================================
    @DeleteMapping("/remove")
    public ResponseEntity<Long> remove(@RequestParam String key,
                                       @RequestParam long count,
                                       @RequestParam String value) {
        Long removed = service.remove(key, count, value);
        return ResponseEntity.ok(removed);
    }

    // ============================================================
    // 13. LTRIM - برش لیست
    // ============================================================
    @PostMapping("/trim")
    public ResponseEntity<String> trim(@RequestParam String key,
                                       @RequestParam long start,
                                       @RequestParam long end) {
        service.trim(key, start, end);
        return ResponseEntity.ok("OK");
    }

    // ============================================================
    // 14. SET - تنظیم مقدار یک عنصر در ایندکس مشخص
    // ============================================================
    @PutMapping("/set")
    public ResponseEntity<String> set(@RequestParam String key,
                                      @RequestParam long index,
                                      @RequestParam String value) {
        service.set(key, index, value);
        return ResponseEntity.ok("OK");
    }

    // ============================================================
    // 15. BLPOP - بلوکینگ LPOP
    // ============================================================
    @GetMapping("/blocking-left-pop")
    public ResponseEntity<String> leftPopBlocking(@RequestParam String key,
                                                        @RequestParam long timeout,
                                                        @RequestParam(defaultValue = "SECONDS") String unit) {
        TimeUnit timeUnit = TimeUnit.valueOf(unit.toUpperCase());
        String result = service.leftPopBlocking(key, timeout, timeUnit);
        if (result == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 16. BRPOP - بلوکینگ RPOP
    // ============================================================
    @GetMapping("/blocking-right-pop")
    public ResponseEntity<String> rightPopBlocking(@RequestParam String key,
                                                         @RequestParam long timeout,
                                                         @RequestParam(defaultValue = "SECONDS") String unit) {
        TimeUnit timeUnit = TimeUnit.valueOf(unit.toUpperCase());
        String result = service.rightPopBlocking(key, timeout, timeUnit);
        if (result == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 17. RPOPLPUSH - انتقال از انتهای یک لیست به ابتدای لیست دیگر
    // ============================================================
    @PostMapping("/right-pop-left-push")
    public ResponseEntity<String> rightPopLeftPush(@RequestParam String sourceKey,
                                                   @RequestParam String destinationKey) {
        String value = service.rightPopAndLeftPush(sourceKey, destinationKey);
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(value);
    }
}
