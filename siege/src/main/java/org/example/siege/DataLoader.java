package org.example.siege;

import lombok.extern.slf4j.Slf4j;
import org.example.core.Event;
import org.example.core.Item;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class DataLoader {

    private final ReactiveRedisConnectionFactory factory;
    private final ReactiveRedisOperations<String, Event> eventOps;
    private final ReactiveRedisOperations<String, Item> itemOps;

    public DataLoader(ReactiveRedisConnectionFactory factory,
                      ReactiveRedisOperations<String, Event> eventOps,
                      ReactiveRedisOperations<String, Item> itemOps) {
        this.factory = factory;
        this.eventOps = eventOps;
        this.itemOps = itemOps;
    }

//    @PostConstruct
    public void loadData() {
        AtomicInteger eventId = new AtomicInteger();
        factory.getReactiveConnection().serverCommands().flushAll()
                .thenMany(Flux.just(new Event(String.valueOf(eventId.incrementAndGet()), "Test event", "http://cdn.image.co.kr/7788"),
                                new Event(String.valueOf(eventId.incrementAndGet()), "Welcome reactive", ""),
                                new Event(String.valueOf(eventId.incrementAndGet()), "CPN", "http://cpn.com/1123"))
                        .flatMap(event -> eventOps.opsForValue().set(RedisConstants.EVENT_PREFIX + event.getId(), event)))
                .thenMany(eventOps.keys(RedisConstants.EVENT_PREFIX)
                        .flatMap(eventOps.opsForValue()::get))
                .subscribe(event -> log.info("Load event[{}]", event));

        AtomicInteger itemId = new AtomicInteger();
        factory.getReactiveConnection().serverCommands().flushAll()
                .thenMany(Flux.just(new Item(String.valueOf(itemId.incrementAndGet()), "씨그램 350ml 라임 x 20", 19000.0, 5),
                                new Item(String.valueOf(itemId.incrementAndGet()), "비말차단 덴탈마스크 x 50", 5900.0, 1),
                                new Item(String.valueOf(itemId.incrementAndGet()), "Best Eleven 10월호", 8900.0, 0),
                                new Item(String.valueOf(itemId.incrementAndGet()), "A", 9000.0, 0),
                                new Item(String.valueOf(itemId.incrementAndGet()), "B", 8000.0, 0),
                                new Item(String.valueOf(itemId.incrementAndGet()), "C", 12000.0, 0),
                                new Item(String.valueOf(itemId.incrementAndGet()), "D", 11300.0, 11),
                                new Item(String.valueOf(itemId.incrementAndGet()), "E", 5300.0, 1))
                        .flatMap(item -> itemOps.opsForValue().set(RedisConstants.ITEM_PREFIX + item.getId(), item)))
                .thenMany(itemOps.keys(RedisConstants.ITEM_PREFIX)
                        .flatMap(itemOps.opsForValue()::get))
                .subscribe(event -> log.info("Load items[{}]", event));
    }
}
