package com.example.redis.stream;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.util.SafeEncoder;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RedisStreamCommandsService {

    private final JedisPool jedisPool;

    // ============================================================
    // 1. XADD - اضافه کردن پیام به Stream
    //    برگرداننده: شناسه‌ی پیام (مثل "1691234567890-0")
    // ============================================================
    public String xAdd(String streamKey, Map<String, String> body) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> args = new ArrayList<>();
            args.add(streamKey);
            args.add("*"); // شناسه‌ی خودکار
            for (Map.Entry<String, String> entry : body.entrySet()) {
                args.add(entry.getKey());
                args.add(entry.getValue());
            }

            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("XADD"),
                    args.toArray(new String[0])
            );

            return result != null ? result.toString() : null;
        }
    }

    // ============================================================
    // 2. XREAD - خواندن از Stream (بدون گروه)
    //    برگرداننده: لیست پیام‌ها با شناسه و فیلدها
    // ============================================================
    public List<Map<String, Object>> xRead(String streamKey, String lastId, int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("XREAD"),
                    "COUNT", String.valueOf(count),
                    "STREAMS", streamKey, lastId
            );

            return parseStreamReadResult(result);
        }
    }

    // ============================================================
    // 3. XREADGROUP - خواندن با گروه مصرف‌کننده
    //    برگرداننده: لیست پیام‌ها با شناسه و فیلدها
    // ============================================================
    public List<Map<String, Object>> xReadGroup(String streamKey, String groupName, String consumerName,
                                                String lastId, int count, boolean autoAcknowledge) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> args = new ArrayList<>();
            args.add("GROUP");
            args.add(groupName);
            args.add(consumerName);
            args.add("COUNT");
            args.add(String.valueOf(count));
            if (autoAcknowledge) {
                args.add("NOACK");
            }
            args.add("STREAMS");
            args.add(streamKey);
            args.add(lastId);

            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("XREADGROUP"),
                    args.toArray(new String[0])
            );

            return parseStreamReadResult(result);
        }
    }

    // ============================================================
    // 4. XACK - تأیید پردازش پیام
    //    برگرداننده: تعداد پیام‌های تأیید شده
    // ============================================================
    public Long xAck(String streamKey, String groupName, String... recordIds) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> args = new ArrayList<>();
            args.add(streamKey);
            args.add(groupName);
            args.addAll(Arrays.asList(recordIds));

            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("XACK"),
                    args.toArray(new String[0])
            );

            return result instanceof Long ? (Long) result : null;
        }
    }

    // ============================================================
    // 5. XGROUP CREATE - ایجاد گروه مصرف‌کننده
    //    برگرداننده: "OK" در صورت موفقیت
    // ============================================================
    public String xGroupCreate(String streamKey, String groupName, String startId, boolean makeStream) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> args = new ArrayList<>();
            args.add("CREATE");
            args.add(streamKey);
            args.add(groupName);
            args.add(startId);
            if (makeStream) {
                args.add("MKSTREAM");
            }

            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("XGROUP"),
                    args.toArray(new String[0])
            );

            return result != null ? result.toString() : null;
        }
    }

    // ============================================================
    // 6. XGROUP DESTROY - حذف گروه مصرف‌کننده
    //    برگرداننده: true/false
    // ============================================================
    public Boolean xGroupDestroy(String streamKey, String groupName) {
        try (Jedis jedis = jedisPool.getResource()) {
            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("XGROUP"),
                    "DESTROY", streamKey, groupName
            );

            return result instanceof Long && ((Long) result) == 1;
        }
    }

    // ============================================================
    // 7. XPENDING - مشاهده‌ی پیام‌های معلق (خلاصه)
    //    برگرداننده: Map شامل تعداد، کمترین و بیشترین ID
    // ============================================================
    public Map<String, Object> xPendingSummary(String streamKey, String groupName) {
        try (Jedis jedis = jedisPool.getResource()) {
            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("XPENDING"),
                    streamKey, groupName
            );

            return parsePendingSummary(result);
        }
    }

    // ============================================================
    // 8. XPENDING با جزئیات (بازه و تعداد)
    //    برگرداننده: لیست پیام‌های معلق با شناسه، مصرف‌کننده، زمان و ...
    // ============================================================
    public List<Map<String, Object>> xPendingDetailed(String streamKey, String groupName,
                                                      String startId, String endId, int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("XPENDING"),
                    streamKey, groupName, startId, endId, String.valueOf(count)
            );

            return parsePendingDetailed(result);
        }
    }

    // ============================================================
    // 9. XTRIM - کوتاه کردن طول Stream
    //    برگرداننده: تعداد عناصر حذف‌شده
    // ============================================================
    public Long xTrim(String streamKey, long maxLen) {
        try (Jedis jedis = jedisPool.getResource()) {
            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("XTRIM"),
                    streamKey, "MAXLEN", String.valueOf(maxLen)
            );

            return result instanceof Long ? (Long) result : null;
        }
    }

    // ============================================================
    // 10. XDEL - حذف پیام‌ها با شناسه
    //     برگرداننده: تعداد پیام‌های حذف‌شده
    // ============================================================
    public Long xDelete(String streamKey, String... recordIds) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> args = new ArrayList<>();
            args.add(streamKey);
            args.addAll(Arrays.asList(recordIds));

            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("XDEL"),
                    args.toArray(new String[0])
            );

            return result instanceof Long ? (Long) result : null;
        }
    }

    // ============================================================
    // 11. XLEN - طول Stream (تعداد کل پیام‌ها)
    //     برگرداننده: تعداد پیام‌ها
    // ============================================================
    public Long xLen(String streamKey) {
        try (Jedis jedis = jedisPool.getResource()) {
            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("XLEN"),
                    streamKey
            );

            return result instanceof Long ? (Long) result : null;
        }
    }

    // ============================================================
    // 12. XRANGE - مشاهده‌ی بازه‌ای از پیام‌ها (از ابتدا به انتها)
    //     برگرداننده: لیست پیام‌ها با شناسه و فیلدها
    // ============================================================
    public List<Map<String, Object>> xRange(String streamKey, String startId, String endId) {
        try (Jedis jedis = jedisPool.getResource()) {
            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("XRANGE"),
                    streamKey, startId, endId
            );

            return parseStreamRangeResult(result);
        }
    }

    // ============================================================
    // 13. XREVRANGE - مشاهده‌ی بازه‌ای از پیام‌ها (از انتها به ابتدا)
    //     برگرداننده: لیست پیام‌ها با شناسه و فیلدها
    // ============================================================
    public List<Map<String, Object>> xReverseRange(String streamKey, String endId, String startId, int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("XREVRANGE"),
                    streamKey, endId, startId, "COUNT", String.valueOf(count)
            );

            return parseStreamRangeResult(result);
        }
    }

    // ============================================================
    // 14. XINFO STREAM - اطلاعات کامل در مورد Stream
    //     برگرداننده: Map با اطلاعات
    // ============================================================
    public Map<String, Object> xInfoStream(String streamKey) {
        try (Jedis jedis = jedisPool.getResource()) {
            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("XINFO"),
                    "STREAM", streamKey
            );

            return parseInfoResult(result);
        }
    }

    // ============================================================
    // 15. XINFO GROUPS - اطلاعات گروه‌های مصرف‌کننده
    //     برگرداننده: لیست گروه‌ها با جزئیات
    // ============================================================
    public List<Map<String, Object>> xInfoGroups(String streamKey) {
        try (Jedis jedis = jedisPool.getResource()) {
            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("XINFO"),
                    "GROUPS", streamKey
            );

            return parseInfoGroupsResult(result);
        }
    }

    // ============================================================
    // 16. XINFO CONSUMERS - اطلاعات مصرف‌کننده‌های یک گروه
    //     برگرداننده: لیست مصرف‌کننده‌ها با جزئیات
    // ============================================================
    public List<Map<String, Object>> xInfoConsumers(String streamKey, String groupName) {
        try (Jedis jedis = jedisPool.getResource()) {
            Object result = jedis.sendCommand(
                    () -> SafeEncoder.encode("XINFO"),
                    "CONSUMERS", streamKey, groupName
            );

            return parseInfoGroupsResult(result);
        }
    }

    // ============================================================
    // ================= متدهای کمکی برای پردازش خروجی =============
    // ============================================================

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> parseStreamReadResult(Object result) {
        List<Map<String, Object>> messages = new ArrayList<>();
        if (!(result instanceof List)) return messages;

        List<Object> rawList = (List<Object>) result;
        for (Object streamData : rawList) {
            if (!(streamData instanceof List)) continue;
            List<Object> streamEntry = (List<Object>) streamData;
            if (streamEntry.size() < 2) continue;

            // streamEntry: [streamKey, [ [id1, [field1, val1, field2, val2]], [id2, ...] ]]
            Object entriesObj = streamEntry.get(1);
            if (!(entriesObj instanceof List)) continue;

            List<Object> entries = (List<Object>) entriesObj;
            for (Object entry : entries) {
                if (!(entry instanceof List)) continue;
                List<Object> entryData = (List<Object>) entry;
                if (entryData.size() < 2) continue;

                String id = entryData.get(0).toString();
                Object fieldsObj = entryData.get(1);
                if (!(fieldsObj instanceof List)) continue;

                List<Object> fieldsList = (List<Object>) fieldsObj;
                Map<String, Object> messageMap = new LinkedHashMap<>();
                messageMap.put("id", id);
                for (int i = 0; i < fieldsList.size() - 1; i += 2) {
                    String key = fieldsList.get(i).toString();
                    Object value = fieldsList.get(i + 1);
                    messageMap.put(key, value != null ? value.toString() : null);
                }
                messages.add(messageMap);
            }
        }
        return messages;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> parseStreamRangeResult(Object result) {
        List<Map<String, Object>> messages = new ArrayList<>();
        if (!(result instanceof List)) return messages;

        List<Object> entries = (List<Object>) result;
        for (Object entry : entries) {
            if (!(entry instanceof List)) continue;
            List<Object> entryData = (List<Object>) entry;
            if (entryData.size() < 2) continue;

            String id = entryData.get(0).toString();
            Object fieldsObj = entryData.get(1);
            if (!(fieldsObj instanceof List)) continue;

            List<Object> fieldsList = (List<Object>) fieldsObj;
            Map<String, Object> messageMap = new LinkedHashMap<>();
            messageMap.put("id", id);
            for (int i = 0; i < fieldsList.size() - 1; i += 2) {
                String key = fieldsList.get(i).toString();
                Object value = fieldsList.get(i + 1);
                messageMap.put(key, value != null ? value.toString() : null);
            }
            messages.add(messageMap);
        }
        return messages;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parsePendingSummary(Object result) {
        Map<String, Object> summary = new LinkedHashMap<>();
        if (!(result instanceof List)) return summary;
        List<Object> raw = (List<Object>) result;
        if (raw.size() >= 3) {
            summary.put("total", raw.get(0));
            summary.put("minId", raw.get(1).toString());
            summary.put("maxId", raw.get(2).toString());
        }
        return summary;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> parsePendingDetailed(Object result) {
        List<Map<String, Object>> details = new ArrayList<>();
        if (!(result instanceof List)) return details;

        List<Object> rawList = (List<Object>) result;
        for (Object item : rawList) {
            if (!(item instanceof List)) continue;
            List<Object> entry = (List<Object>) item;
            if (entry.size() >= 4) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", entry.get(0).toString());
                map.put("consumer", entry.get(1).toString());
                map.put("deliveryCount", entry.get(2));
                map.put("lastDeliveryTime", entry.get(3));
                details.add(map);
            }
        }
        return details;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseInfoResult(Object result) {
        Map<String, Object> info = new LinkedHashMap<>();
        if (!(result instanceof List)) return info;
        List<Object> raw = (List<Object>) result;
        for (int i = 0; i < raw.size() - 1; i += 2) {
            String key = raw.get(i).toString();
            Object value = raw.get(i + 1);
            info.put(key, value);
        }
        return info;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> parseInfoGroupsResult(Object result) {
        List<Map<String, Object>> groups = new ArrayList<>();
        if (!(result instanceof List)) return groups;

        List<Object> rawList = (List<Object>) result;
        for (Object groupObj : rawList) {
            if (!(groupObj instanceof List)) continue;
            List<Object> groupData = (List<Object>) groupObj;
            Map<String, Object> groupMap = new LinkedHashMap<>();
            for (int i = 0; i < groupData.size() - 1; i += 2) {
                String key = groupData.get(i).toString();
                Object value = groupData.get(i + 1);
                groupMap.put(key, value);
            }
            groups.add(groupMap);
        }
        return groups;
    }

    // ============================================================
    // متدهای کمکی برای حذف کلید
    // ============================================================
    public Boolean delete(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.del(key) > 0;
        }
    }
}
