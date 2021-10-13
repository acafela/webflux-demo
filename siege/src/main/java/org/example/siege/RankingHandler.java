package org.example.siege;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

public class RankingHandler {

    @Autowired
    private ReactiveStringRedisTemplate redisTemplate;


}
