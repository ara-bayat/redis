package com.example.redis.array;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.util.SafeEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RedisArrayCommandsService {

    private final JedisPool jedisPool;

    // ============================================================
    // 1. ARSET - تنظیم یک یا چند مقدار متوالی در آرایه
    //    برگرداننده: تعداد خانه‌های جدید (قبلاً خالی) که پر شده‌اند
    // ============================================================
    public Long arSet(String key, long index, String... values) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> args = new ArrayList<>();
            args.add(key);
            args.add(String.valueOf(index));
            Collections.addAll(args, values);

            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("ARSET"),
                    args.toArray(new String[0])
            );

            if (result instanceof Long) {
                return (Long) result;
            }
            throw new IllegalStateException("Unexpected result type: " + result.getClass());
        }
    }

    // ============================================================
    // 2. ARMSET - تنظیم چند جفت (ایندکس، مقدار) غیرمتوالی در آرایه
    //    برگرداننده: تعداد خانه‌های جدید (قبلاً خالی) که پر شده‌اند
    // ============================================================
    public Long arMSet(String key, Map<Long, String> indexValuePairs) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> args = new ArrayList<>();
            args.add(key);
            for (Map.Entry<Long, String> entry : indexValuePairs.entrySet()) {
                args.add(String.valueOf(entry.getKey()));
                args.add(entry.getValue());
            }

            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("ARMSET"),
                    args.toArray(new String[0])
            );

            if (result instanceof Long) {
                return (Long) result;
            }
            throw new IllegalStateException("Unexpected result type: " + result.getClass());
        }
    }

    // ============================================================
    // 3. ARGET - دریافت مقدار یک ایندکس خاص از آرایه
    //    برگرداننده: مقدار رشته‌ای، یا null اگر ایندکس خالی باشد
    // ============================================================
    public String arGet(String key, long index) {
        try (Jedis jedis = jedisPool.getResource()) {
            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("ARGET"),
                    key,
                    String.valueOf(index)
            );

            if (result == null) {
                return null;
            }
            if (result instanceof byte[]) {
                return new String((byte[]) result, java.nio.charset.StandardCharsets.UTF_8);
            }
            return result.toString();
        }
    }

    // ============================================================
    // 4. ARGETRANGE - دریافت بازه‌ای از مقادیر آرایه
    //    برگرداننده: لیست مقادیر (خانه‌های خالی به صورت null برمی‌گردند)
    // ============================================================
    public List<String> arGetRange(String key, long start, long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("ARGETRANGE"),
                    key,
                    String.valueOf(start),
                    String.valueOf(end)
            );

            if (result instanceof List) {
                List<?> rawList = (List<?>) result;
                List<String> resultList = new ArrayList<>();
                for (Object item : rawList) {
                    if (item == null) {
                        resultList.add(null);
                    } else if (item instanceof byte[]) {
                        resultList.add(new String((byte[]) item, java.nio.charset.StandardCharsets.UTF_8));
                    } else {
                        resultList.add(item.toString());
                    }
                }
                return resultList;
            }
            throw new IllegalStateException("Unexpected result type: " + result.getClass());
        }
    }

    // ============================================================
    // 5. ARLEN - طول آرایه (حداکثر ایندکس + 1)
    //    برگرداننده: طول آرایه (حتی اگر خانه‌ها خالی باشند)
    // ============================================================
    public Long arLen(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("ARLEN"),
                    key
            );

            if (result instanceof Long) {
                return (Long) result;
            }
            throw new IllegalStateException("Unexpected result type: " + result.getClass());
        }
    }

    // ============================================================
    // 6. ARCOUNT - تعداد خانه‌های غیرخالی آرایه
    //    برگرداننده: تعداد عناصری که مقدار دارند
    // ============================================================
    public Long arCount(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("ARCOUNT"),
                    key
            );

            if (result instanceof Long) {
                return (Long) result;
            }
            throw new IllegalStateException("Unexpected result type: " + result.getClass());
        }
    }

    // ============================================================
    // 7. ARDEL - حذف عناصر در ایندکس‌های مشخص
    //    برگرداننده: تعداد عناصر حذف‌شده
    // ============================================================
    public Long arDel(String key, long... indices) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> args = new ArrayList<>();
            args.add(key);
            for (long index : indices) {
                args.add(String.valueOf(index));
            }

            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("ARDEL"),
                    args.toArray(new String[0])
            );

            if (result instanceof Long) {
                return (Long) result;
            }
            throw new IllegalStateException("Unexpected result type: " + result.getClass());
        }
    }

    // ============================================================
    // 8. ARDELRANGE - حذف عناصر در یک یا چند بازه
    //    برگرداننده: تعداد عناصر حذف‌شده
    // ============================================================
    public Long arDelRange(String key, List<long[]> ranges) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> args = new ArrayList<>();
            args.add(key);
            for (long[] range : ranges) {
                if (range.length != 2) {
                    throw new IllegalArgumentException("Each range must have exactly 2 elements: start and end");
                }
                args.add(String.valueOf(range[0]));
                args.add(String.valueOf(range[1]));
            }

            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("ARDELRANGE"),
                    args.toArray(new String[0])
            );

            if (result instanceof Long) {
                return (Long) result;
            }
            throw new IllegalStateException("Unexpected result type: " + result.getClass());
        }
    }

    // ============================================================
    // 9. ARINSERT - درج یک یا چند مقدار در ایندکس‌های متوالی
    //    برگرداننده: تعداد خانه‌های جدید پر شده
    // ============================================================
    public Long arInsert(String key, long index, String... values) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> args = new ArrayList<>();
            args.add(key);
            args.add(String.valueOf(index));
            Collections.addAll(args, values);

            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("ARINSERT"),
                    args.toArray(new String[0])
            );

            if (result instanceof Long) {
                return (Long) result;
            }
            throw new IllegalStateException("Unexpected result type: " + result.getClass());
        }
    }

    // ============================================================
    // 10. ARGREP - جستجوی مقدار در بازه‌ای از آرایه
    //     برگرداننده: لیست ایندکس‌هایی که با الگو مطابقت دارند
    // ============================================================
    public List<Long> arGrep(String key, long start, long end, String pattern) {
        try (Jedis jedis = jedisPool.getResource()) {
            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("ARGREP"),
                    key,
                    String.valueOf(start),
                    String.valueOf(end),
                    pattern
            );

            if (result instanceof List) {
                List<?> rawList = (List<?>) result;
                List<Long> resultList = new ArrayList<>();
                for (Object item : rawList) {
                    if (item instanceof Long) {
                        resultList.add((Long) item);
                    } else if (item instanceof Integer) {
                        resultList.add(((Integer) item).longValue());
                    } else {
                        throw new IllegalStateException("Unexpected item type: " + item.getClass());
                    }
                }
                return resultList;
            }
            throw new IllegalStateException("Unexpected result type: " + result.getClass());
        }
    }

    // ============================================================
    // 11. ARRING - استفاده از آرایه به عنوان Ring Buffer (حافظه حلقوی)
    //     برگرداننده: تعداد کل عناصر نوشته‌شده (شامل عناصر بازنویسی‌شده)
    // ============================================================
    public Long arRing(String key, long capacity, String... values) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> args = new ArrayList<>();
            args.add(key);
            args.add(String.valueOf(capacity));
            Collections.addAll(args, values);

            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("ARRING"),
                    args.toArray(new String[0])
            );

            if (result instanceof Long) {
                return (Long) result;
            }
            throw new IllegalStateException("Unexpected result type: " + result.getClass());
        }
    }

    // ============================================================
    // 12. ARLASTITEMS - دریافت آخرین N عنصر از Ring Buffer
    //     برگرداننده: لیست آخرین عناصر نوشته‌شده
    // ============================================================
    public List<String> arLastItems(String key, long count) {
        try (Jedis jedis = jedisPool.getResource()) {
            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("ARLASTITEMS"),
                    key,
                    String.valueOf(count)
            );

            if (result instanceof List) {
                List<?> rawList = (List<?>) result;
                List<String> resultList = new ArrayList<>();
                for (Object item : rawList) {
                    if (item == null) {
                        resultList.add(null);
                    } else if (item instanceof byte[]) {
                        resultList.add(new String((byte[]) item, java.nio.charset.StandardCharsets.UTF_8));
                    } else {
                        resultList.add(item.toString());
                    }
                }
                return resultList;
            }
            throw new IllegalStateException("Unexpected result type: " + result.getClass());
        }
    }

    // ============================================================
    // 13. حذف کلید (برای پاک کردن کامل آرایه)
    // ============================================================
    public Boolean delete(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.del(key) > 0;
        }
    }
}