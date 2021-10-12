package org.example.siege;

import lombok.extern.slf4j.Slf4j;
import org.example.core.Event;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Component
@Slf4j
public class DataLoader {

    private final ReactiveRedisConnectionFactory factory;
    private final ReactiveRedisOperations<String, Event> eventOps;

    public DataLoader(ReactiveRedisConnectionFactory factory, ReactiveRedisOperations<String, Event> eventOps) {
        this.factory = factory;
        this.eventOps = eventOps;
    }

//    @PostConstruct
    public void loadData() {
        factory.getReactiveConnection().serverCommands().flushAll()
                .thenMany(Flux.just(new Event(UUID.randomUUID().toString(), "Test event", "http://cdn.image.co.kr/7788"),
                                new Event(UUID.randomUUID().toString(), "Welcome reactive", ""))
                        .flatMap(event -> eventOps.opsForValue().set("event:" + event.getId(), event)))
                .thenMany(eventOps.keys("event:*")
                        .flatMap(eventOps.opsForValue()::get))
                .subscribe(event -> log.info("Load event[{}]", event));
    }
}
