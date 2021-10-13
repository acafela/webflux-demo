package org.example.siege;

import com.fasterxml.jackson.databind.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.core.Event;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.example.siege.EndPoint.V1_ITEM_ROOT;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

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

    @Test
    public void createEvent() {
        webTestClient.post().uri("/v1/events")
                .body(Mono.just(new Event(null, "20211012", "https://gg.com")), Event.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.subject").isEqualTo("20211012")
                .jsonPath("$.id").isNotEmpty();
    }

    @Test
    public void deleteEvent() {
        String id = "53a93470-7af1-42e0-80e1-2d5ee520a34a";
        webTestClient.delete().uri("/v1/events/" + id)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void updateEvent() {
        String id = "a691933b-daa8-488b-a775-f4c1f5c5c921";
        String subject = "new event(update) 3";
        webTestClient.put().uri("/v1/events/" + id)
                .body(fromObject(new Event(id, subject, null)))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.subject").isEqualTo(subject)
                .jsonPath("$.imgUrl").doesNotExist();
    }

    @Test
    public void updateEventNotFound() {
        String id = "blahblah";
        String subject = "coca cola";
        webTestClient.put().uri("/v1/events/" + id)
                .body(fromObject(new Event(id, subject, null)))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void getItem() {
        String itemId = "1";
        webTestClient.get().uri(V1_ITEM_ROOT + "/" +itemId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(itemId)
                .jsonPath("$.name", "$.price", "$.reviewCount").isNotEmpty();
    }

}
