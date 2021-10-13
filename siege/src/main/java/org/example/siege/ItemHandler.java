package org.example.siege;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.Item;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.example.siege.RedisConstants.ITEM_PREFIX;
import static org.example.siege.RedisConstants.RANKING_KEY;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
@RequiredArgsConstructor
@Slf4j
public class ItemHandler {

    private final ReactiveRedisOperations<String, Item> itemOps;
    private final ReactiveStringRedisTemplate stringRedisTemplate;

    public Mono<ServerResponse> getItem(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Item> itemMono = itemOps.opsForValue().get(ITEM_PREFIX + id);
        return itemMono.flatMap(item -> stringRedisTemplate.opsForZSet()
                        .incrementScore(RANKING_KEY, item.getId(), 10)
                        .then(ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromObject(item))))
        .switchIfEmpty(ServerResponse.notFound().build());
    }
}
