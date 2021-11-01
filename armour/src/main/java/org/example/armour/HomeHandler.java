package org.example.armour;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.BusinessModel;
import org.example.core.Event;
import org.example.core.Item;
import org.example.core.ItemRanking;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class HomeHandler {

    private final WebClient webClient;
    private final List<ServiceSpec> serviceSpecs = Arrays.asList(
            new ServiceSpec("/v1/items", "items", Item.class),
            new ServiceSpec("/v1/items/ranking", "ranking", ItemRanking.class),
            new ServiceSpec("/v1/events", "events", Event.class)
    );

    public Mono<ServerResponse> getHome(ServerRequest request) {
        Flux<BusinessModel> serviceResponse = Flux.fromIterable(serviceSpecs)
                .flatMap(serviceSpec -> webClient.get()
                        .uri(serviceSpec.getUrl())
                        .retrieve()
                        .bodyToFlux(serviceSpec.getModelClass()));

        Mono<Map<String, Collection<BusinessModel>>> collectedServiceResponse = serviceResponse.collectMultimap(e -> {
            Class<?> itemClazz = e.getClass();
            if (itemClazz == Event.class) {
                return "event";
            } else if (itemClazz == Item.class) {
                return "item";
            } else if (itemClazz == ItemRanking.class) {
                return "ranking";
            }
            return "unknown";
        });
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(collectedServiceResponse, Map.class);
    }

    public Mono<ServerResponse> getHomeOld(ServerRequest request) {
        Flux<ItemRanking> itemRankingFlux = webClient.get()
                .uri("/v1/items/ranking")
                .retrieve()
                .bodyToFlux(ItemRanking.class)
                .log("Ranking : ");
        Flux<Item> itemFlux = webClient.get()
                .uri("/v1/items")
                .retrieve()
                .bodyToFlux(Item.class)
                .log("Items : ");
        Flux<Event> eventFlux = webClient.get()
                .uri("/v1/events")
                .retrieve()
                .bodyToFlux(Event.class)
                .log("events : ");
        Flux<BusinessModel> serviceResponse = Flux.merge(itemFlux, itemRankingFlux, eventFlux);

        Mono<Map<String, Collection<BusinessModel>>> collectedServiceResponse = serviceResponse.collectMultimap(e -> {
            Class<?> itemClazz = e.getClass();
            if (itemClazz == Event.class) {
                return "event";
            } else if (itemClazz == Item.class) {
                return "item";
            } else if (itemClazz == ItemRanking.class) {
                return "ranking";
            }
            return "unknown";
        });

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(collectedServiceResponse, Map.class);
    }
}
