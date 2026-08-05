package com.example.redis.hyperloglog;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/redis/hll")
@RequiredArgsConstructor
public class RedisHyperLogLogController {

    private final RedisHyperLogLogCommandsService service;

    // ============================================================
    // 1. PFADD - اضافه کردن بازدیدکننده
    // ============================================================
    @PostMapping("/add")
    public ResponseEntity<Long> pfAdd(@RequestParam String key,
                                      @RequestBody List<String> values) {
        Long result = service.pfAdd(key, values.toArray(new String[0]));
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 2. PFCOUNT - گرفتن تعداد تخمینی بازدیدکننده‌های یکتا (برای یک روز)
    // ============================================================
    @GetMapping("/count")
    public ResponseEntity<Long> pfCount(@RequestParam String key) {
        Long count = service.pfCount(key);
        return ResponseEntity.ok(count);
    }

    // ============================================================
    // 3. PFCOUNT - گرفتن تعداد تخمینی بازدیدکننده‌های یکتا برای چند روز (مجموع)
    // ============================================================
    @PostMapping("/count-multi")
    public ResponseEntity<Long> pfCountMulti(@RequestBody List<String> keys) {
        Long count = service.pfCount(keys.toArray(new String[0]));
        return ResponseEntity.ok(count);
    }

    // ============================================================
    // 4. PFMERGE - ادغام چند HyperLogLog (مثلاً برای گزارش ماهانه)
    // ============================================================
    @PostMapping("/merge")
    public ResponseEntity<Long> pfMerge(@RequestParam String destination,
                                        @RequestBody List<String> sourceKeys) {
        // قبل از ادغام، اگه کلید مقصد قبلاً وجود داشت، پاکش کن (اختیاری)
        // service.delete(destination);
        Long result = service.pfMerge(destination, sourceKeys.toArray(new String[0]));
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 5. DELETE - حذف کلید
    // ============================================================
    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> delete(@RequestParam String key) {
        Boolean result = service.delete(key);
        return ResponseEntity.ok(result);
    }
}