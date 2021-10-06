package org.example.siege;

import lombok.extern.slf4j.Slf4j;
import org.example.core.Event;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@Slf4j
public class RouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void getAllEvents() {
        List<Event> events = webTestClient.get().uri("/v1/events")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Event.class)
                .returnResult()
                .getResponseBody();
        log.info("Events[{}]", events);
    }
}
