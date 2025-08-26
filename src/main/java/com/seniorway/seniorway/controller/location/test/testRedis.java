package com.seniorway.seniorway.controller.location.test;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class TestRedis implements CommandLineRunner {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void run(String... args) throws Exception {
        // Redis에 값 저장
        redisTemplate.opsForValue().set("test-key", "Hello Redis!");

        // Redis에서 값 읽기
        String value = redisTemplate.opsForValue().get("test-key");
        System.out.println("Redis에서 읽은 값: " + value);
    }
}
