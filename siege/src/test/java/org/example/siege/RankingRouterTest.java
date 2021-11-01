package org.example.siege;

import lombok.extern.slf4j.Slf4j;
import org.example.core.Event;
import org.example.core.Item;
import org.example.core.ItemRanking;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class RankingRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void getItemRanking() {
        List<ItemRanking> itemRankings = webTestClient.get().uri(V1_ITEM_ROOT + "/ranking")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ItemRanking.class)
                .returnResult()
                .getResponseBody();
        log.info("Ranking[{}]", itemRankings);
    }
}
