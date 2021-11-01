package org.example.armour;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.core.Event;
import org.example.core.Item;
import org.example.core.ItemRanking;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeResponse {

    private List<Event> events;
    private List<ItemRanking> rankings;
    private List<Item> items;

    public HomeResponse addEvent(Event event) {
        if (isNull(events)) {
            events = new ArrayList<>();
        }
        events.add(event);
        return this;
    }

    public HomeResponse addRanking(ItemRanking ranking) {
        if (isNull(rankings)) {
            rankings = new ArrayList<>();
        }
        rankings.add(ranking);
        return this;
    }

    public HomeResponse addItem(Item item) {
        if (isNull(items)) {
            items = new ArrayList<>();
        }
        items.add(item);
        return this;
    }
}
