package org.example.siege;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterConfiguration {

    @Bean
    public RouterFunction<ServerResponse> route(EventHandler eventHandler) {
        return RouterFunctions
                .route(GET("/v1/events").and(accept(MediaType.APPLICATION_JSON)), eventHandler::getAllEvents)
                .andRoute(GET("/v1/events/{id}").and(accept(MediaType.APPLICATION_JSON)), eventHandler::getEvent)
                .andRoute(POST("/v1/events").and(accept(MediaType.APPLICATION_JSON)), eventHandler::createEvent)
                .andRoute(DELETE("/v1/events/{id}").and(accept(MediaType.APPLICATION_JSON)), eventHandler::deleteEvent)
                .andRoute(PUT("/v1/events/{id}").and(accept(MediaType.APPLICATION_JSON)), eventHandler::updateEvent);
    }
}
