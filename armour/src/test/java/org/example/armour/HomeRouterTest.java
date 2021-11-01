package org.example.armour;

import lombok.extern.slf4j.Slf4j;
import org.example.core.BusinessModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collection;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@Slf4j
public class HomeRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void getHome() {
        long st = System.currentTimeMillis();
        Map<String, Collection<BusinessModel>> homeResponse = webTestClient.get().uri("/home")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .returnResult()
                .getResponseBody();
        long end = System.currentTimeMillis();
        log.info("Elapsed time {}", (end - st) / 1000.0);
        log.info("Home[{}]", homeResponse);
    }
}
