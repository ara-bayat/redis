package com.example.redis.hash;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisHashCommandsService {

    private final StringRedisTemplate redisTemplate;

    // ============================================================
    // 1. HSET - تنظیم یک فیلد در هش
    // ============================================================
    public void hSet(String key, String field, String value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    // ============================================================
    // 2. HSET با چند فیلد (می‌تواند جایگزین HMSET شود)
    // ============================================================
    public void hSetAll(String key, Map<String, String> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    // ============================================================
    // 3. HSETNX - تنظیم فقط اگه فیلد وجود نداشت
    // ============================================================
    public Boolean hSetIfAbsent(String key, String field, String value) {
        return redisTemplate.opsForHash().putIfAbsent(key, field, value);
    }

    // ============================================================
    // 4. HGET - دریافت مقدار یک فیلد
    // ============================================================
    public String hGet(String key, String field) {
        Object value = redisTemplate.opsForHash().get(key, field);
        return value != null ? value.toString() : null;
    }

    // ============================================================
    // 5. HMGET - دریافت چند فیلد با هم
    // ============================================================
    public List<Object> hMultiGet(String key, List<Object> fields) {
        return redisTemplate.opsForHash().multiGet(key, fields);
    }

    // ============================================================
    // 6. HGETALL - دریافت همه‌ی فیلدها و مقادیر (برای هش‌های بزرگ استفاده نکن!)
    // ============================================================
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    // ============================================================
    // 7. HKEYS - دریافت همه‌ی فیلدها (کلیدها)
    // ============================================================
    public Set<Object> hKeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    // ============================================================
    // 8. HVALS - دریافت همه‌ی مقادیر
    // ============================================================
    public List<Object> hVals(String key) {
        return redisTemplate.opsForHash().values(key);
    }

    // ============================================================
    // 9. HDEL - حذف یک یا چند فیلد
    // ============================================================
    public Long hDelete(String key, String... fields) {
        return redisTemplate.opsForHash().delete(key, (Object[]) fields);
    }

    // ============================================================
    // 10. HEXISTS - بررسی وجود یک فیلد
    // ============================================================
    public Boolean hExists(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    // ============================================================
    // 11. HLEN - تعداد فیلدهای هش
    // ============================================================
    public Long hSize(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    // ============================================================
    // 12. HINCRBY - افزایش یک فیلد عددی با مقدار دلخواه
    // ============================================================
    public Long hIncrement(String key, String field, long delta) {
        return redisTemplate.opsForHash().increment(key, field, delta);
    }

    // ============================================================
    // 13. HINCRBYFLOAT - افزایش یک فیلد اعشاری
    // ============================================================
    public Double hIncrementFloat(String key, String field, double delta) {
        return redisTemplate.opsForHash().increment(key, field, delta);
    }

    // ============================================================
    // 14. HSCAN - پیمایش فیلدها با استفاده از Cursor (برای هش‌های بزرگ)
    // ============================================================
    public ScanOptions hScan(String key) {
        // این متد ScanOptions را برمی‌گرداند و خود پیمایش در کنترلر انجام می‌شود
        return ScanOptions.scanOptions().build();
    }

    // ============================================================
    // 15. HSTRLEN - طول مقدار یک فیلد خاص (از Redis 6.2 به بعد)
    // ============================================================
    public Long hStrLen(String key, String field) {
        // این متد به صورت مستقیم در opsForHash پشتیبانی نمی‌شود،
        // از متد execute با command مستقیم استفاده می‌کنیم.
        return redisTemplate.execute(
                (connection) -> connection.hStrLen(key.getBytes(), field.getBytes()),
                true
        );
    }
}