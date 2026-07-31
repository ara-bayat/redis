package com.example.redis.string;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisStringCommandsService {

    private final StringRedisTemplate redisTemplate;

    // ============================================================
    // 1. SET (ذخیره‌سازی مقدار)
    // ============================================================
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // ============================================================
    // 2. SET با زمان انقضا (معادل SETEX)
    // ============================================================
    public void setWithExpire(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    // ============================================================
    // 3. GET (دریافت مقدار)
    // ============================================================
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // ============================================================
    // 4. DEL (حذف کلید)
    // ============================================================
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    // ============================================================
    // 5. EXISTS (بررسی وجود کلید)
    // ============================================================
    public Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    // ============================================================
    // 6. EXPIRE (تنظیم زمان انقضا روی کلید موجود)
    // ============================================================
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    // ============================================================
    // 7. TTL (مشاهده‌ی زمان باقی‌مانده)
    // ============================================================
    public Long getTTL(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    // ============================================================
    // 8. SETNX (تنظیم فقط اگه کلید وجود نداشت)
    // ============================================================
    public Boolean setIfAbsent(String key, String value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    // ============================================================
    // 9. SETNX با زمان انقضا (برای قفل توزیع‌شده)
    // ============================================================
    public Boolean setIfAbsentWithExpire(String key, String value, long timeout, TimeUnit unit) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
    }

    // ============================================================
    // 10. MGET (گرفتن چند کلید با هم)
    // ============================================================
    public List<String> multiGet(List<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    // ============================================================
    // 11. MSET (تنظیم چند کلید با هم)
    // ============================================================
    public void multiSet(java.util.Map<String, String> map) {
        redisTemplate.opsForValue().multiSet(map);
    }

    // ============================================================
    // 12. INCR (افزایش ۱ واحدی)
    // ============================================================
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    // ============================================================
    // 13. INCRBY (افزایش با مقدار دلخواه)
    // ============================================================
    public Long incrementBy(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    // ============================================================
    // 14. DECR (کاهش ۱ واحدی)
    // ============================================================
    public Long decrement(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    // ============================================================
    // 15. DECRBY (کاهش با مقدار دلخواه)
    // ============================================================
    public Long decrementBy(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    // ============================================================
    // 16. APPEND (اضافه کردن به انتهای مقدار)
    // ============================================================
    public Integer append(String key, String value) {
        return redisTemplate.opsForValue().append(key, value);
    }

    // ============================================================
    // 17. STRLEN (طول مقدار)
    // ============================================================
    public Long size(String key) {
        return redisTemplate.opsForValue().size(key);
    }

    // ============================================================
    // 18. GETRANGE (زیررشته) - معادل SUBSTR یا GETRANGE در Redis
    // ============================================================
    public String getRange(String key, long start, long end) {
        return redisTemplate.opsForValue().get(key, start, end);
    }
}
