package com.example.redis.pipeline;

import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedisPipelineService{

    private final StringRedisTemplate stringRedisTemplate;

    public RedisPipelineService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public List<Object> pipelineExample() {
        int batchSize = 1000;

        List<Object> results = stringRedisTemplate.executePipelined(
                (RedisCallback<Object>) connection -> {
                    // connection را به StringRedisConnection تبدیل می‌کنیم
                    StringRedisConnection stringRedisConn = (StringRedisConnection) connection;

                    // اضافه کردن ۱۰۰۰ کلید به صورت پایپلاین
                    for (int i = 0; i < batchSize; i++) {
                        stringRedisConn.set("pipeline:key:" + i, "value:" + i);
                    }

                    // مقدار برگشتی باید null باشد
                    return null;
                }
        );

        // results شامل لیست پاسخ‌های تمام دستورات SET است
        System.out.println("تعداد پاسخ‌ها: " + results.size());
        return results;
    }
}
