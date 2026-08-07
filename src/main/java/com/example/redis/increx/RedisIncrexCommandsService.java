package com.example.redis.increx;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.util.SafeEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisIncrexCommandsService {

    // INCREX is not in Jedis 5.2 Protocol.Command enum; send as custom command
    private static final ProtocolCommand INCREX = () -> SafeEncoder.encode("INCREX");

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

            Object rawResult = jedis.sendCommand(INCREX, args.toArray(new String[0]));

            if (!(rawResult instanceof List<?> rawList)) {
                throw new IllegalStateException("Unexpected result type: " + rawResult.getClass());
            }

            List<Long> result = new ArrayList<>(rawList.size());
            for (Object item : rawList) {
                result.add(toLong(item));
            }
            return result;
        }
    }

    private static Long toLong(Object item) {
        if (item instanceof Number number) {
            return number.longValue();
        }
        if (item instanceof byte[] bytes) {
            return Long.parseLong(SafeEncoder.encode(bytes));
        }
        if (item instanceof String value) {
            return Long.parseLong(value);
        }
        throw new IllegalStateException("Unexpected item type: " + item.getClass());
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
