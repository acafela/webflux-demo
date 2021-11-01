package org.example.siege;

import lombok.extern.slf4j.Slf4j;
import org.example.core.Event;
import org.example.core.Item;
import org.example.core.ItemRanking;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.stream.Collectors;

import static org.example.siege.RedisConstants.ITEM_PREFIX;
import static org.example.siege.RedisConstants.RANKING_KEY;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@Slf4j
public class RedisTemplateTest {

    @Autowired
    private ReactiveStringRedisTemplate redisTemplate;

    @Autowired
    private ReactiveRedisOperations<String, Item> itemOps;

    @Test
    public void increase() {
        redisTemplate.opsForZSet().incrementScore("item:ranking", "2", 5)
                .block();
    }

    @Test
    public void range() {
        Range<Long> range = Range.from(Range.Bound.inclusive(0L)).to(Range.Bound.inclusive(3L));
        Flux<String> valueFlux = redisTemplate.opsForZSet().reverseRange(RANKING_KEY, range);
        StepVerifier.create(valueFlux.log())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void rangeWithScores() {
        Range<Long> range = Range.from(Range.Bound.inclusive(0L)).to(Range.Bound.inclusive(3L));
        Flux<ZSetOperations.TypedTuple<String>> tupleFlux = redisTemplate.opsForZSet()
                                                                        .reverseRangeWithScores(RANKING_KEY, range);
        tupleFlux.subscribe((t) -> System.out.println(t.getScore() + "/" + t.getValue()));
    }

    @Test
    public void itemRanking() {
        Range<Long> range = Range.from(Range.Bound.inclusive(0L)).to(Range.Bound.inclusive(2L));
        Flux<ZSetOperations.TypedTuple<String>> tupleFlux = redisTemplate.opsForZSet()
                .reverseRangeWithScores(RANKING_KEY, range);
//        Flux<Item> allItemsFlux = itemOps.keys(ITEM_PREFIX)
//                .flatMap(itemOps.opsForValue()::get);
        Flux<ItemRanking> itemFlux = tupleFlux.flatMap(t ->
            itemOps.opsForValue().get(ITEM_PREFIX + t.getValue())
                    .map(item -> {
                        ItemRanking itemRanking = new ItemRanking(item);
                        itemRanking.setCount(t.getScore().longValue());
                        return itemRanking;
                    })
        );
        StepVerifier.create(itemFlux.log())
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }
}
