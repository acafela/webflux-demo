package org.example.armour;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class RouterConfiguration {

    @Bean
    public RouterFunction<ServerResponse> route(HomeHandler homeHandler) {
        return RouterFunctions
                .route(GET("/home").and(accept(MediaType.APPLICATION_JSON)), homeHandler::getHome)
                .andRoute(GET("/home/old").and(accept(MediaType.APPLICATION_JSON)), homeHandler::getHomeOld);
    }
}
