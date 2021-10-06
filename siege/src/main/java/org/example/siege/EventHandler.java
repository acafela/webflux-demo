package org.example.siege;

import org.example.core.Event;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class EventHandler {

    private final ReactiveRedisOperations<String, Event> eventOps;

    public EventHandler(ReactiveRedisOperations<String, Event> eventOps) {
        this.eventOps = eventOps;
    }

    public Mono<ServerResponse> getAllEvents(ServerRequest request) {
        Flux<Event> allEventsFlux = eventOps.keys("event:*")
                .flatMap(eventOps.opsForValue()::get);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(allEventsFlux, Event.class);
    }
}
