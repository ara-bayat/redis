package com.example.redis.zset;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/redis/zset")
@RequiredArgsConstructor
public class RedisZSetController {

    private final RedisZSetCommandsService service;

    // --------------------- متد کمکی برای تبدیل Tuple به Map (برای پاسخ JSON) ---------------------
    private List<Map<String, Object>> convertTuplesToMap(Set<ZSetOperations.TypedTuple<String>> tuples) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (tuples != null) {
            for (ZSetOperations.TypedTuple<String> tuple : tuples) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("member", tuple.getValue());
                map.put("score", tuple.getScore());
                result.add(map);
            }
        }
        return result;
    }

    // ============================================================
    // 1. ZADD - اضافه کردن یک عضو
    // ============================================================
    @PostMapping("/add")
    public ResponseEntity<Boolean> zAdd(@RequestParam String key,
                                        @RequestParam String value,
                                        @RequestParam double score) {
        Boolean result = service.zAdd(key, value, score);
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 2. ZADD - اضافه کردن چند عضو با Body JSON
    // ============================================================
    @PostMapping("/add-all")
    public ResponseEntity<Long> zAddAll(@RequestParam String key,
                                        @RequestBody List<Map<String, Object>> members) {
        Set<ZSetOperations.TypedTuple<String>> tuples = new HashSet<>();
        for (Map<String, Object> item : members) {
            String value = (String) item.get("member");
            Double score = ((Number) item.get("score")).doubleValue();
            tuples.add(ZSetOperations.TypedTuple.of(value, score));
        }
        Long result = service.zAddAll(key, tuples);
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 3. ZRANGE - گرفتن بر اساس رتبه (کم به زیاد) - فقط اعضا
    // ============================================================
    @GetMapping("/range")
    public ResponseEntity<Set<String>> zRange(@RequestParam String key,
                                              @RequestParam long start,
                                              @RequestParam(defaultValue = "-1") long end) {
        Set<String> result = service.zRange(key, start, end);
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 4. ZRANGE WITHSCORES - گرفتن اعضا با امتیاز (برای نمایش کامل)
    // ============================================================
    @GetMapping("/range-with-scores")
    public ResponseEntity<List<Map<String, Object>>> zRangeWithScores(@RequestParam String key,
                                                                      @RequestParam long start,
                                                                      @RequestParam(defaultValue = "-1") long end) {
        Set<ZSetOperations.TypedTuple<String>> tuples = service.zRangeWithScores(key, start, end);
        return ResponseEntity.ok(convertTuplesToMap(tuples));
    }

    // ============================================================
    // 5. ZREVRANGE - گرفتن بر اساس رتبه (زیاد به کم) - فقط اعضا
    // ============================================================
    @GetMapping("/reverse-range")
    public ResponseEntity<Set<String>> zReverseRange(@RequestParam String key,
                                                     @RequestParam long start,
                                                     @RequestParam(defaultValue = "-1") long end) {
        Set<String> result = service.zReverseRange(key, start, end);
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 6. ZREVRANGE WITHSCORES - (همون Leaderboard اصلی!)
    // ============================================================
    @GetMapping("/reverse-range-with-scores")
    public ResponseEntity<List<Map<String, Object>>> zReverseRangeWithScores(@RequestParam String key,
                                                                             @RequestParam long start,
                                                                             @RequestParam(defaultValue = "-1") long end) {
        Set<ZSetOperations.TypedTuple<String>> tuples = service.zReverseRangeWithScores(key, start, end);
        return ResponseEntity.ok(convertTuplesToMap(tuples));
    }

    // ============================================================
    // 7. ZRANGEBYSCORE - گرفتن بر اساس محدوده‌ی امتیاز
    // ============================================================
    @GetMapping("/range-by-score")
    public ResponseEntity<Set<String>> zRangeByScore(@RequestParam String key,
                                                     @RequestParam double min,
                                                     @RequestParam double max) {
        Set<String> result = service.zRangeByScore(key, min, max);
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 8. ZREVRANGEBYSCORE - برعکس محدوده‌ی امتیاز
    // ============================================================
    @GetMapping("/reverse-range-by-score")
    public ResponseEntity<Set<String>> zReverseRangeByScore(@RequestParam String key,
                                                            @RequestParam double min,
                                                            @RequestParam double max) {
        Set<String> result = service.zReverseRangeByScore(key, min, max);
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 9. ZREM - حذف عضو
    // ============================================================
    @DeleteMapping("/remove")
    public ResponseEntity<Long> zRemove(@RequestParam String key,
                                        @RequestBody List<String> values) {
        Long result = service.zRemove(key, values.toArray(new String[0]));
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 10. ZCARD - تعداد اعضا
    // ============================================================
    @GetMapping("/size")
    public ResponseEntity<Long> zSize(@RequestParam String key) {
        Long size = service.zSize(key);
        return ResponseEntity.ok(size);
    }

    // ============================================================
    // 11. ZCOUNT - تعداد اعضای در بازه‌ی امتیاز
    // ============================================================
    @GetMapping("/count")
    public ResponseEntity<Long> zCount(@RequestParam String key,
                                       @RequestParam double min,
                                       @RequestParam double max) {
        Long count = service.zCount(key, min, max);
        return ResponseEntity.ok(count);
    }

    // ============================================================
    // 12. ZSCORE - گرفتن امتیاز یک عضو خاص
    // ============================================================
    @GetMapping("/score")
    public ResponseEntity<Double> zScore(@RequestParam String key,
                                         @RequestParam String value) {
        Double score = service.zScore(key, value);
        if (score == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(score);
    }

    // ============================================================
    // 13. ZRANK - رتبه از کم به زیاد
    // ============================================================
    @GetMapping("/rank")
    public ResponseEntity<Long> zRank(@RequestParam String key,
                                      @RequestParam String value) {
        Long rank = service.zRank(key, value);
        if (rank == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rank);
    }

    // ============================================================
    // 14. ZREVRANK - رتبه از زیاد به کم (مهم!)
    // ============================================================
    @GetMapping("/reverse-rank")
    public ResponseEntity<Long> zReverseRank(@RequestParam String key,
                                             @RequestParam String value) {
        Long rank = service.zReverseRank(key, value);
        if (rank == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rank);
    }

    // ============================================================
    // 15. ZINCRBY - افزایش امتیاز
    // ============================================================
    @PostMapping("/increment-score")
    public ResponseEntity<Double> zIncrementScore(@RequestParam String key,
                                                  @RequestParam String value,
                                                  @RequestParam double delta) {
        Double newScore = service.zIncrementScore(key, value, delta);
        return ResponseEntity.ok(newScore);
    }

    // ============================================================
    // 16. ZPOPMIN - گرفتن و حذف کمترین امتیاز
    // ============================================================
    @DeleteMapping("/pop-min")
    public ResponseEntity<Map<String, Object>> zPopMin(@RequestParam String key) {
        ZSetOperations.TypedTuple<String> tuple = service.zPopMin(key);
        if (tuple == null) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("member", tuple.getValue());
        result.put("score", tuple.getScore());
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 17. ZPOPMAX - گرفتن و حذف بیشترین امتیاز
    // ============================================================
    @DeleteMapping("/pop-max")
    public ResponseEntity<Map<String, Object>> zPopMax(@RequestParam String key) {
        ZSetOperations.TypedTuple<String> tuple = service.zPopMax(key);
        if (tuple == null) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("member", tuple.getValue());
        result.put("score", tuple.getScore());
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 18. ZREMRANGEBYRANK - حذف بر اساس محدوده‌ی رتبه
    // ============================================================
    @DeleteMapping("/remove-range")
    public ResponseEntity<Long> zRemoveRange(@RequestParam String key,
                                             @RequestParam long start,
                                             @RequestParam long end) {
        Long result = service.zRemoveRange(key, start, end);
        return ResponseEntity.ok(result);
    }

    // ============================================================
    // 19. ZREMRANGEBYSCORE - حذف بر اساس محدوده‌ی امتیاز
    // ============================================================
    @DeleteMapping("/remove-range-by-score")
    public ResponseEntity<Long> zRemoveRangeByScore(@RequestParam String key,
                                                    @RequestParam double min,
                                                    @RequestParam double max) {
        Long result = service.zRemoveRangeByScore(key, min, max);
        return ResponseEntity.ok(result);
    }
}