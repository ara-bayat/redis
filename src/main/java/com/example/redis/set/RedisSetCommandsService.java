package com.example.redis.set;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisSetCommandsService {

    private final StringRedisTemplate redisTemplate;

    // ============================================================
    // 1. SADD - اضافه کردن یک یا چند عضو به Set
    // ============================================================
    public Long sAdd(String key, String... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    // ============================================================
    // 2. SMEMBERS - گرفتن همه‌ی اعضای Set (فقط برای Set های کوچک!)
    // ============================================================
    public Set<String> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    // ============================================================
    // 3. SISMEMBER - بررسی وجود یک عضو در Set
    // ============================================================
    public Boolean sIsMember(String key, String value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    // ============================================================
    // 4. SREM - حذف یک یا چند عضو از Set
    // ============================================================
    public Long sRemove(String key, String... values) {
        return redisTemplate.opsForSet().remove(key, (Object[]) values);
    }

    // ============================================================
    // 5. SCARD - تعداد اعضای Set (Cardinality)
    // ============================================================
    public Long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    // ============================================================
    // 6. SPOP - برداشتن و حذف یک عضو تصادفی
    // ============================================================
    public String sPop(String key) {
        return redisTemplate.opsForSet().pop(key);
    }

    // ============================================================
    // 7. SPOP با تعداد - برداشتن و حذف چند عضو تصادفی
    // ============================================================
    public List<String> sPop(String key, long count) {
        return redisTemplate.opsForSet().pop(key, count);
    }

    // ============================================================
    // 8. SRANDMEMBER - گرفتن یک عضو تصادفی (بدون حذف)
    // ============================================================
    public String sRandomMember(String key) {
        return redisTemplate.opsForSet().randomMember(key);
    }

    // ============================================================
    // 9. SRANDMEMBER با تعداد (بدون حذف)
    // ============================================================
    public List<String> sRandomMembers(String key, long count) {
        return redisTemplate.opsForSet().randomMembers(key, count);
    }

    // ============================================================
    // 10. SINTER - اشتراک (Intersection) بین چند Set
    // ============================================================
    public Set<String> sInter(String key, String... otherKeys) {
        return redisTemplate.opsForSet().intersect(key, List.of(otherKeys));
    }

    // ============================================================
    // 11. SINTERSTORE - ذخیره‌ی اشتراک در یک Set جدید
    // ============================================================
    public Long sInterStore(String destination, String key, String... otherKeys) {
        return redisTemplate.opsForSet().intersectAndStore(key, List.of(otherKeys), destination);
    }

    // ============================================================
    // 12. SUNION - اجتماع (Union) بین چند Set
    // ============================================================
    public Set<String> sUnion(String key, String... otherKeys) {
        return redisTemplate.opsForSet().union(key, List.of(otherKeys));
    }

    // ============================================================
    // 13. SUNIONSTORE - ذخیره‌ی اجتماع در یک Set جدید
    // ============================================================
    public Long sUnionStore(String destination, String key, String... otherKeys) {
        return redisTemplate.opsForSet().unionAndStore(key, List.of(otherKeys), destination);
    }

    // ============================================================
    // 14. SDIFF - تفاضل (Difference) - اعضای key1 که در key2 نیستند
    // ============================================================
    public Set<String> sDiff(String key, String... otherKeys) {
        return redisTemplate.opsForSet().difference(key, List.of(otherKeys));
    }

    // ============================================================
    // 15. SDIFFSTORE - ذخیره‌ی تفاضل در یک Set جدید
    // ============================================================
    public Long sDiffStore(String destination, String key, String... otherKeys) {
        return redisTemplate.opsForSet().differenceAndStore(key, List.of(otherKeys), destination);
    }

    // ============================================================
    // 16. SMOVE - انتقال یک عضو از Set ای به Set دیگر (اتمی)
    // ============================================================
    public Boolean sMove(String sourceKey, String destinationKey, String value) {
        return redisTemplate.opsForSet().move(sourceKey, value, destinationKey);
    }

    // ============================================================
    // 17. SSCAN - پیمایش اعضای Set با Cursor (برای Set های بزرگ)
    // ============================================================
    public Cursor<String> sScan(String key, ScanOptions options) {
        return redisTemplate.opsForSet().scan(key, options);
    }
}
