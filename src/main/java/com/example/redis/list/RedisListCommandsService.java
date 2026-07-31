package com.example.redis.list;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisListCommandsService {

    private final StringRedisTemplate redisTemplate;

    // ============================================================
    // 1. LPUSH - اضافه کردن به چپ (ابتدا)
    // ============================================================
    public Long leftPush(String key, String value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    // ============================================================
    // 2. LPUSH با چند مقدار (varargs)
    // ============================================================
    public Long leftPushAll(String key, String... values) {
        return redisTemplate.opsForList().leftPushAll(key, values);
    }

    // ============================================================
    // 3. RPUSH - اضافه کردن به راست (انتهای لیست)
    // ============================================================
    public Long rightPush(String key, String value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    // ============================================================
    // 4. RPUSH با چند مقدار
    // ============================================================
    public Long rightPushAll(String key, String... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    // ============================================================
    // 5. LPOP - گرفتن و حذف از چپ
    // ============================================================
    public String leftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    // ============================================================
    // 6. LPOP با تعداد (برای برداشتن چند عنصر)
    // ============================================================
    public List<String> leftPop(String key, long count) {
        return redisTemplate.opsForList().leftPop(key, count);
    }

    // ============================================================
    // 7. RPOP - گرفتن و حذف از راست
    // ============================================================
    public String rightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    // ============================================================
    // 8. RPOP با تعداد
    // ============================================================
    public List<String> rightPop(String key, long count) {
        return redisTemplate.opsForList().rightPop(key, count);
    }

    // ============================================================
    // 9. LRANGE - گرفتن بازه‌ای از لیست
    // ============================================================
    public List<String> range(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    // ============================================================
    // 10. LLEN - طول لیست
    // ============================================================
    public Long size(String key) {
        return redisTemplate.opsForList().size(key);
    }

    // ============================================================
    // 11. LINDEX - گرفتن عنصر با ایندکس
    // ============================================================
    public String index(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    // ============================================================
    // 12. LREM - حذف عناصر تکراری
    // ============================================================
    public Long remove(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    // ============================================================
    // 13. LTRIM - برش لیست (نگه‌داشتن بازه‌ی مشخص)
    // ============================================================
    public void trim(String key, long start, long end) {
        redisTemplate.opsForList().trim(key, start, end);
    }

    // ============================================================
    // 14. SET - تنظیم مقدار یک عنصر در ایندکس مشخص
    // ============================================================
    public void set(String key, long index, String value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    // ============================================================
    // 15. BLPOP - بلوکینگ LPOP (با تایم‌اوت)
    // ============================================================
    public String leftPopBlocking(String key, long timeout, TimeUnit unit) {
        return redisTemplate.opsForList().leftPop(key, timeout, unit);
    }

    // ============================================================
    // 16. BRPOP - بلوکینگ RPOP
    // ============================================================
    public String rightPopBlocking(String key, long timeout, TimeUnit unit) {
        return redisTemplate.opsForList().rightPop(key, timeout, unit);
    }

    // ============================================================
    // 17. RPOPLPUSH - انتقال از انتهای یک لیست به ابتدای لیست دیگر
    // ============================================================
    public String rightPopAndLeftPush(String sourceKey, String destinationKey) {
        return redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey);
    }
}
