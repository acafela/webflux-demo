package org.example.siege;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.Event;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.example.core.TestHelper.delay;
import static org.example.siege.RedisConstants.EVENT_PREFIX;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventHandler {

    private final ReactiveRedisOperations<String, Event> eventOps;

    public Mono<ServerResponse> getAllEvents(ServerRequest request) {
        delay();
        Flux<Event> allEventsFlux = eventOps.keys("event:*")
                .flatMap(eventOps.opsForValue()::get);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(allEventsFlux, Event.class);
    }

    public Mono<ServerResponse> getEvent(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Event> event = eventOps.opsForValue().get(id);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(event, Event.class);
    }

    public Mono<ServerResponse> createEvent(ServerRequest request) {
        String generatedId = EVENT_PREFIX + UUID.randomUUID();
        Mono<Event> eventMono = request.bodyToMono(Event.class)
                .map(event -> {
                    event.setId(generatedId);
                    eventOps.opsForValue().set(generatedId, event);
                    return event;
                });
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(eventMono, Event.class);
    }

    public Mono<ServerResponse> createEventTest1(ServerRequest request) {
        String generatedId = EVENT_PREFIX + UUID.randomUUID();
//        String generatedId = "event:53a93470-7af1-42e0-80e1-2d5ee520a34a";
        return request.bodyToMono(Event.class)
                .flatMap(event -> {
                    event.setId(generatedId);
                    return eventOps.opsForValue().set(generatedId, event)
                            .flatMap(result -> {
                                ServerResponse.BodyBuilder bodyBuilder = ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON);
                                if (!result) {
                                    return bodyBuilder.body(fromObject("Failed..."));
                                }
                                return bodyBuilder.body(fromObject(event));
                            });
                });
    }

    public Mono<ServerResponse> deleteEvent(ServerRequest request) {
        String id = EVENT_PREFIX + request.pathVariable("id");
        return eventOps.delete(id).then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> updateEvent(ServerRequest request) {
        String id = EVENT_PREFIX + request.pathVariable("id");
        Mono<Event> eventMono = request.bodyToMono(Event.class);
        Mono<Event> updatedEventMono = eventMono.flatMap(newEvent ->
                eventOps.opsForValue().get(id)
                        .flatMap(event -> {
                            BeanUtils.copyProperties(newEvent, event);
                            return eventOps.opsForValue().set(id, event).thenReturn(event);
                        }));
        return updatedEventMono.flatMap(event -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromObject(event)))
                .switchIfEmpty(ServerResponse.notFound().build());
        // only method chain
//        return request.bodyToMono(Event.class)
//                .flatMap(newEvent ->
//                    eventOps.opsForValue().get(id)
//                            .flatMap(event -> {
//                                BeanUtils.copyProperties(newEvent, event);
//                                return eventOps.opsForValue().set(id, event).thenReturn(event);
//                            })
//                            .flatMap(event -> ServerResponse.ok()
//                                    .contentType(MediaType.APPLICATION_JSON)
//                                    .body(fromObject(event)))
//                            .switchIfEmpty(ServerResponse.notFound().build()));
    }

}
