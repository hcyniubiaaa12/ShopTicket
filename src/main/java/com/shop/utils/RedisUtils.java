package com.shop.utils;

import cn.hutool.json.JSONUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class RedisUtils {
    private final StringRedisTemplate redisTemplate;
    private final RedissonClient redissonClient;

    public RedisUtils(StringRedisTemplate redisTemplate, RedissonClient redissonClient) {
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
    }

    /**
     * 缓存雪崩
     *
     * @param prefix
     * @param  keySuffix
     * @param value
     * @param baseTime
     */
    public void setWithRandomExpire(String prefix,  String keySuffix, String value, Integer baseTime, TimeUnit timeUnit) {
        Random random = new Random();
        int randomTime = random.nextInt(20);
        int expire = baseTime + randomTime;
        redisTemplate.opsForValue().set(prefix + ":" + keySuffix, value, expire, timeUnit);

    }


    /**
     * 缓存穿透面对单对象
     *
     * @param prefix
     * @param  keySuffix
     * @param clazz
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T cacheEmptyObject(String prefix, String keySuffix, Class<T> clazz, Supplier<T> supplier) {
        String value = redisTemplate.opsForValue().get(prefix + ":" +  keySuffix);
        if (value != null) {
            if (value.equals("NULL")) {
                return null;
            }
            return JSONUtil.toBean(value, clazz);

        }

        T t = supplier.get();
        if (t != null) {
            setWithRandomExpire(prefix,  keySuffix, JSONUtil.toJsonStr(t), 30, TimeUnit.SECONDS);

        } else {
            setWithRandomExpire(prefix,  keySuffix, "NULL", 3, TimeUnit.SECONDS);

        }

        return t;

    }

    /**
     * 缓存穿透面对列表
     *
     * @param prefix
     * @param  keySuffix
     * @param clazz
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> List<T> cacheEmptyList(String prefix, String  keySuffix, Class<T> clazz, Supplier<List<T>> supplier) {
        String value = redisTemplate.opsForValue().get(prefix + ":" +  keySuffix);
        if (value != null) {
            if (value.equals("NULL")) {
                return null;
            }
            return JSONUtil.toList(value, clazz);

        }

        List<T> t = supplier.get();
        if (t != null && !t.isEmpty()) {
            setWithRandomExpire(prefix,  keySuffix, JSONUtil.toJsonStr(t), 30, TimeUnit.SECONDS);
        } else {
            setWithRandomExpire(prefix,  keySuffix, "NULL", 3, TimeUnit.SECONDS);
        }

        return t;

    }

    /**
     * 缓存击穿
     *
     * @param prefix
     * @param  keySuffix
     * @param clazz
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T getWithLock(String prefix, String  keySuffix, Class<T> clazz, Supplier<T> supplier) {
        String value = redisTemplate.opsForValue().get(prefix + ":" +  keySuffix);
        if (value != null) {
            return JSONUtil.toBean(value, clazz);
        }
        RLock lock = redissonClient.getLock("lock:" + prefix + ":" +  keySuffix);
        try {
            boolean flag = lock.tryLock(3, 5, TimeUnit.SECONDS);
            if (flag) {
                try {
                    String data = redisTemplate.opsForValue().get(prefix + ":" +  keySuffix);
                    if (data != null) {
                        return JSONUtil.toBean(data, clazz);
                    }
                    T t = supplier.get();
                    setWithRandomExpire(prefix,  keySuffix, JSONUtil.toJsonStr(t), 30, TimeUnit.SECONDS);
                    return t;
                } finally {
                    lock.unlock();

                }
            } else {
                Thread.sleep(3000);
                return getWithLock(prefix,  keySuffix, clazz, supplier);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public <T> List<T> getWithLockList(String prefix, String keySuffix, Class<T> clazz, Supplier<List<T>> supplier) {
        String value = redisTemplate.opsForValue().get(prefix + ":" + keySuffix);
        if (value != null) {
            return JSONUtil.toList(value, clazz);
        }
        RLock lock = redissonClient.getLock("lock:" + prefix + ":" + keySuffix);
        try {
            boolean flag = lock.tryLock(3, 5, TimeUnit.SECONDS);
            if (flag) {
                try {
                    String data = redisTemplate.opsForValue().get(prefix + ":" +  keySuffix);
                    if (data != null) {
                        return JSONUtil.toList(data, clazz);
                    }
                    List<T> t = supplier.get();
                    setWithRandomExpire(prefix,  keySuffix, JSONUtil.toJsonStr(t), 30, TimeUnit.MINUTES);
                    return t;
                } finally {
                    lock.unlock();

                }
            } else {
                Thread.sleep(3000);
                return getWithLockList(prefix,  keySuffix, clazz, supplier);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
