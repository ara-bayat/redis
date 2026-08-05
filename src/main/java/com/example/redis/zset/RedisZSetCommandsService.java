package com.example.redis.zset;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisZSetCommandsService {

    private final StringRedisTemplate redisTemplate;

    // ============================================================
    // 1. ZADD - اضافه کردن یک عضو با امتیاز
    // ============================================================
    public Boolean zAdd(String key, String value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    // ============================================================
    // 2. ZADD - اضافه کردن چند عضو با امتیاز (با استفاده از TypedTuple)
    // ============================================================
    public Long zAddAll(String key, Set<ZSetOperations.TypedTuple<String>> tuples) {
        return redisTemplate.opsForZSet().add(key, tuples);
    }

    // ============================================================
    // 3. ZRANGE - گرفتن اعضا بر اساس رتبه (از کم به زیاد)
    // ============================================================
    public Set<String> zRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    // ============================================================
    // 4. ZRANGE WITHSCORES - گرفتن اعضا با امتیاز (از کم به زیاد)
    // ============================================================
    public Set<ZSetOperations.TypedTuple<String>> zRangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }

    // ============================================================
    // 5. ZREVRANGE - گرفتن اعضا بر اساس رتبه (از زیاد به کم) - Leaderboard!
    // ============================================================
    public Set<String> zReverseRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    // ============================================================
    // 6. ZREVRANGE WITHSCORES - گرفتن اعضا با امتیاز (از زیاد به کم)
    // ============================================================
    public Set<ZSetOperations.TypedTuple<String>> zReverseRangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }

    // ============================================================
    // 7. ZRANGEBYSCORE - گرفتن اعضا بر اساس محدوده‌ی امتیاز (از کم به زیاد)
    // ============================================================
    public Set<String> zRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    // ============================================================
    // 8. ZRANGEBYSCORE WITHSCORES
    // ============================================================
    public Set<ZSetOperations.TypedTuple<String>> zRangeByScoreWithScores(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max);
    }

    // ============================================================
    // 9. ZREVRANGEBYSCORE - گرفتن اعضا بر اساس محدوده‌ی امتیاز (از زیاد به کم)
    // ============================================================
    public Set<String> zReverseRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
    }

    // ============================================================
    // 10. ZREM - حذف یک یا چند عضو
    // ============================================================
    public Long zRemove(String key, String... values) {
        return redisTemplate.opsForZSet().remove(key, (Object[]) values);
    }

    // ============================================================
    // 11. ZCARD - تعداد اعضا (Cardinality)
    // ============================================================
    public Long zSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    // ============================================================
    // 12. ZCOUNT - تعداد اعضایی که امتیازشون در بازه است
    // ============================================================
    public Long zCount(String key, double min, double max) {
        return redisTemplate.opsForZSet().count(key, min, max);
    }

    // ============================================================
    // 13. ZSCORE - گرفتن امتیاز یک عضو خاص
    // ============================================================
    public Double zScore(String key, String value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    // ============================================================
    // 14. ZRANK - گرفتن رتبه (ایندکس) از کم به زیاد (۰ برای کمترین)
    // ============================================================
    public Long zRank(String key, String value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    // ============================================================
    // 15. ZREVRANK - گرفتن رتبه از زیاد به کم (۰ برای بیشترین) - خیلی پرکاربرد!
    // ============================================================
    public Long zReverseRank(String key, String value) {
        return redisTemplate.opsForZSet().reverseRank(key, value);
    }

    // ============================================================
    // 16. ZINCRBY - افزایش امتیاز یک عضو (با مقدار دلخواه)
    // ============================================================
    public Double zIncrementScore(String key, String value, double delta) {
        return redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    // ============================================================
    // 17. ZPOPMIN - برداشتن و حذف عضو با کمترین امتیاز (با امتیازش)
    // ============================================================
    public ZSetOperations.TypedTuple<String> zPopMin(String key) {
        return redisTemplate.opsForZSet().popMin(key);
    }

    // ============================================================
    // 18. ZPOPMIN با تعداد
    // ============================================================
    public Set<ZSetOperations.TypedTuple<String>> zPopMin(String key, long count) {
        return redisTemplate.opsForZSet().popMin(key, count);
    }

    // ============================================================
    // 19. ZPOPMAX - برداشتن و حذف عضو با بیشترین امتیاز
    // ============================================================
    public ZSetOperations.TypedTuple<String> zPopMax(String key) {
        return redisTemplate.opsForZSet().popMax(key);
    }

    // ============================================================
    // 20. ZPOPMAX با تعداد
    // ============================================================
    public Set<ZSetOperations.TypedTuple<String>> zPopMax(String key, long count) {
        return redisTemplate.opsForZSet().popMax(key, count);
    }

    // ============================================================
    // 21. ZREMRANGEBYRANK - حذف بر اساس محدوده‌ی رتبه (مثلاً ۱۰ تا اول)
    // ============================================================
    public Long zRemoveRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().removeRange(key, start, end);
    }

    // ============================================================
    // 22. ZREMRANGEBYSCORE - حذف بر اساس محدوده‌ی امتیاز
    // ============================================================
    public Long zRemoveRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

    // ============================================================
    // 23. ZSCAN - پیمایش اعضا با Cursor (برای ZSet های بزرگ)
    // ============================================================
    public Cursor<ZSetOperations.TypedTuple<String>> zScan(String key, ScanOptions options) {
        return redisTemplate.opsForZSet().scan(key, options);
    }
}