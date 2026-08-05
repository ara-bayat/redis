package com.example.redis.hyperloglog;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisHyperLogLogCommandsService {

    private final StringRedisTemplate redisTemplate;

    // ============================================================
    // 1. PFADD - اضافه کردن یک یا چند عضو به HyperLogLog
    // (برمی‌گردونه: 1 اگر حداقل یکی از اعضا جدید بود، در غیر این صورت 0)
    // ============================================================
    public Long pfAdd(String key, String... values) {
        return redisTemplate.opsForHyperLogLog().add(key, values);
    }

    // ============================================================
    // 2. PFCOUNT - گرفتن تعداد تخمینی اعضای یکتا (برای یک کلید)
    // ============================================================
    public Long pfCount(String key) {
        return redisTemplate.opsForHyperLogLog().size(key);
    }

    // ============================================================
    // 3. PFCOUNT - گرفتن تعداد تخمینی اعضای یکتا (برای چند کلید با هم - Union)
    // ============================================================
    public Long pfCount(String... keys) {
        return redisTemplate.opsForHyperLogLog().size(keys);
    }

    // ============================================================
    // 4. PFMERGE - ادغام کردن چند HyperLogLog در یک کلید جدید
    // (مفید برای ترکیب آمار روزهای مختلف)
    // ============================================================
    public Long pfMerge(String destination, String... sourceKeys) {
        return redisTemplate.opsForHyperLogLog().union(destination, sourceKeys);
    }

    // ============================================================
    // 5. حذف (مثل همیشه با DEL معمولی)
    // ============================================================
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }
}

