package org.example.siege;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalTime;

import static org.example.siege.RedisConstants.RANKING_KEY;

@RestController
public class RankingController {

    @Autowired
    private ReactiveStringRedisTemplate redisTemplate;

    @GetMapping("/stream-sse")
    public Flux<ServerSentEvent<String>> streamEvents() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> ServerSentEvent.<String> builder()
                        .comment("my comment")
                        .id(String.valueOf(sequence))
                        .event("periodic-event")
                        .data("SSE - " + LocalTime.now().toString())
                        .build());
    }

    @GetMapping("/rankingtest")
    public Flux<ServerSentEvent<String>> rankingtest() {
        long from = 0;
        long to = 2;
        Range<Long> range = Range.from(Range.Bound.inclusive(from)).to(Range.Bound.inclusive(to));
        return redisTemplate.opsForZSet().reverseRange(RANKING_KEY, range)
                .map(s -> ServerSentEvent.builder(s).build());
    }

}
