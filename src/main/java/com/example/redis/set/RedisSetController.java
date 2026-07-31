package com.example.redis.set;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/redis/set")
@RequiredArgsConstructor
public class RedisSetController {

    private final RedisSetCommandsService service;

    // ============================================================
    // 1. SADD - اضافه کردن اعضا
    // ============================================================
    @PostMapping("/add")
    public ResponseEntity<Long> sAdd(@RequestParam String key,
                                     @RequestBody List<String> values) {
        Long result = service.sAdd(key, values.toArray(new String[0]));
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 2. SMEMBERS - گرفتن همه‌ی اعضا
    // ============================================================
    @GetMapping("/members")
    public ResponseEntity<Set<String>> sMembers(@RequestParam String key) {
        Set<String> members = service.sMembers(key);
        return ResponseEntity.ok(members);
    }

    // ============================================================
    // 3. SISMEMBER - بررسی وجود عضو
    // ============================================================
    @GetMapping("/is-member")
    public ResponseEntity<Boolean> sIsMember(@RequestParam String key,
                                             @RequestParam String value) {
        Boolean result = service.sIsMember(key, value);
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 4. SREM - حذف اعضا
    // ============================================================
    @DeleteMapping("/remove")
    public ResponseEntity<Long> sRemove(@RequestParam String key,
                                        @RequestBody List<String> values) {
        Long result = service.sRemove(key, values.toArray(new String[0]));
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 5. SCARD - تعداد اعضا
    // ============================================================
    @GetMapping("/size")
    public ResponseEntity<Long> sSize(@RequestParam String key) {
        Long size = service.sSize(key);
        return ResponseEntity.ok(size);
    }

    // ============================================================
    // 6. SPOP - برداشتن یک عضو تصادفی (حذف می‌شود)
    // ============================================================
    @DeleteMapping("/pop")
    public ResponseEntity<String> sPop(@RequestParam String key) {
        String value = service.sPop(key);
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(value);
    }

    // ============================================================
    // 7. SPOP با تعداد
    // ============================================================
    @DeleteMapping("/pop-many")
    public ResponseEntity<List<String>> sPop(@RequestParam String key,
                                             @RequestParam long count) {
        List<String> values = service.sPop(key, count);
        return ResponseEntity.ok(values);
    }

    // ============================================================
    // 8. SRANDMEMBER - گرفتن عضو تصادفی (بدون حذف)
    // ============================================================
    @GetMapping("/random-member")
    public ResponseEntity<String> sRandomMember(@RequestParam String key) {
        String value = service.sRandomMember(key);
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(value);
    }

    // ============================================================
    // 9. SRANDMEMBER با تعداد
    // ============================================================
    @GetMapping("/random-members")
    public ResponseEntity<List<String>> sRandomMembers(@RequestParam String key,
                                                       @RequestParam long count) {
        List<String> values = service.sRandomMembers(key, count);
        return ResponseEntity.ok(values);
    }

    // ============================================================
    // 10. SINTER - اشتراک (می‌توانید به جای Body از چند پارامتر استفاده کنید)
    // ============================================================
    @PostMapping("/inter")
    public ResponseEntity<Set<String>> sInter(@RequestParam String key,
                                              @RequestBody List<String> otherKeys) {
        Set<String> result = service.sInter(key, otherKeys.toArray(new String[0]));
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 11. SINTERSTORE - ذخیره‌ی اشتراک
    // ============================================================
    @PostMapping("/inter-store")
    public ResponseEntity<Long> sInterStore(@RequestParam String destination,
                                            @RequestParam String key,
                                            @RequestBody List<String> otherKeys) {
        Long result = service.sInterStore(destination, key, otherKeys.toArray(new String[0]));
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 12. SUNION - اجتماع
    // ============================================================
    @PostMapping("/union")
    public ResponseEntity<Set<String>> sUnion(@RequestParam String key,
                                              @RequestBody List<String> otherKeys) {
        Set<String> result = service.sUnion(key, otherKeys.toArray(new String[0]));
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 13. SUNIONSTORE - ذخیره‌ی اجتماع
    // ============================================================
    @PostMapping("/union-store")
    public ResponseEntity<Long> sUnionStore(@RequestParam String destination,
                                            @RequestParam String key,
                                            @RequestBody List<String> otherKeys) {
        Long result = service.sUnionStore(destination, key, otherKeys.toArray(new String[0]));
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 14. SDIFF - تفاضل
    // ============================================================
    @PostMapping("/diff")
    public ResponseEntity<Set<String>> sDiff(@RequestParam String key,
                                             @RequestBody List<String> otherKeys) {
        Set<String> result = service.sDiff(key, otherKeys.toArray(new String[0]));
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 15. SDIFFSTORE - ذخیره‌ی تفاضل
    // ============================================================
    @PostMapping("/diff-store")
    public ResponseEntity<Long> sDiffStore(@RequestParam String destination,
                                           @RequestParam String key,
                                           @RequestBody List<String> otherKeys) {
        Long result = service.sDiffStore(destination, key, otherKeys.toArray(new String[0]));
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 16. SMOVE - انتقال عضو به Set دیگر
    // ============================================================
    @PostMapping("/move")
    public ResponseEntity<Boolean> sMove(@RequestParam String sourceKey,
                                         @RequestParam String destinationKey,
                                         @RequestParam String value) {
        Boolean result = service.sMove(sourceKey, destinationKey, value);
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 17. SSCAN - پیمایش اعضا (برای Set های بزرگ)
    // ============================================================
    @GetMapping("/scan")
    public ResponseEntity<List<String>> sScan(@RequestParam String key,
                                              @RequestParam(defaultValue = "0") int count) {
        List<String> result = new ArrayList<>();
        try (Cursor<String> cursor = service.sScan(key, ScanOptions.scanOptions().count(count).build())) {
            while (cursor.hasNext()) {
                result.add(cursor.next());
            }
        }
        return ResponseEntity.ok(result);
    }
}