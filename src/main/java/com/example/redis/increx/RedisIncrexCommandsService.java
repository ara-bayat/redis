package com.example.redis.increx;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisIncrexCommandsService {

    private final JedisPool jedisPool;

    private List<Long> executeIncrex(String key, Long increment, Long lowerBound, Long upperBound,
                                     Long expireSeconds, Boolean enx, Boolean saturate) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> args = new ArrayList<>();
            args.add(key);

            if (increment != null) {
                args.add("BYINT");
                args.add(increment.toString());
            }
            if (lowerBound != null) {
                args.add("LBOUND");
                args.add(lowerBound.toString());
            }
            if (upperBound != null) {
                args.add("UBOUND");
                args.add(upperBound.toString());
            }
            if (expireSeconds != null) {
                args.add("EX");
                args.add(expireSeconds.toString());
            }
            if (saturate != null && saturate) {
                args.add("SATURATE");
            }
            if (enx != null && enx) {
                args.add("ENX");
            }

            // ارسال دستور INCREX با Jedis
            Object rawResult = jedis.sendCommand(
                    Protocol.Command.valueOf("INCREX"),
                    args.toArray(new String[0])
            );

            if (rawResult instanceof List) {
                List<?> rawList = (List<?>) rawResult;
                List<Long> result = new ArrayList<>();
                for (Object item : rawList) {
                    if (item instanceof Number) {
                        result.add(((Number) item).longValue());
                    } else {
                        throw new IllegalStateException("Unexpected item type: " + item.getClass());
                    }
                }
                return result;
            } else {
                throw new IllegalStateException("Unexpected result type: " + rawResult.getClass());
            }
        }
    }

    // متدهای عمومی (همان‌های قبلی)
    public List<Long> increx(String key) {
        return executeIncrex(key, null, null, null, null, null, false);
    }

    public List<Long> increxWithBounds(String key, Long lowerBound, Long upperBound, Long expireSeconds) {
        return executeIncrex(key, null, lowerBound, upperBound, expireSeconds, null, false);
    }

    public List<Long> increxByInt(String key, Long increment, Long lowerBound, Long upperBound, Long expireSeconds) {
        return executeIncrex(key, increment, lowerBound, upperBound, expireSeconds, null, false);
    }

    public List<Long> increxWithSaturate(String key, Long lowerBound, Long upperBound, Long expireSeconds) {
        return executeIncrex(key, null, lowerBound, upperBound, expireSeconds, null, true);
    }

    public List<Long> increxIfNotExists(String key, Long lowerBound, Long upperBound, Long expireSeconds) {
        return executeIncrex(key, null, lowerBound, upperBound, expireSeconds, true, false);
    }

    public Long getCurrentValue(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            String value = jedis.get(key);
            return value != null ? Long.parseLong(value) : null;
        }
    }

    public Long getTTL(String key, TimeUnit unit) {
        try (Jedis jedis = jedisPool.getResource()) {
            Long seconds = jedis.ttl(key);
            if (seconds == null || seconds < 0) return seconds;
            return unit.convert(seconds, TimeUnit.SECONDS);
        }
    }

    public Boolean delete(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.del(key) > 0;
        }
    }
}
