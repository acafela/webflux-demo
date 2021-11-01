package org.example.siege;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.core.Event;
import org.example.core.Item;
import org.example.core.ItemRanking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.example.core.TestHelper.delay;
import static org.example.siege.RedisConstants.ITEM_PREFIX;
import static org.example.siege.RedisConstants.RANKING_KEY;

@Component
@RequiredArgsConstructor
public class RankingHandler {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ReactiveRedisOperations<String, Item> itemOps;

    public Mono<ServerResponse> getRanking(ServerRequest request) {
        delay();
        long from = Long.parseLong(request.queryParam("from").orElse("0"));
        long to = Long.parseLong(request.queryParam("to").orElse("2"));
        Range<Long> range = Range.from(Range.Bound.inclusive(from)).to(Range.Bound.inclusive(to));
        Flux<ZSetOperations.TypedTuple<String>> itemRankingTupleFlux = redisTemplate.opsForZSet()
                .reverseRangeWithScores(RANKING_KEY, range);
        Flux<ItemRanking> rankingFlux = itemRankingTupleFlux.flatMap(t ->
                itemOps.opsForValue().get(ITEM_PREFIX + t.getValue())
                        .map(item -> {
                            ItemRanking itemRanking = new ItemRanking(item);
                            itemRanking.setCount(t.getScore().longValue());
                            return itemRanking;
                        })
        );
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(rankingFlux, ItemRanking.class);
    }

}
