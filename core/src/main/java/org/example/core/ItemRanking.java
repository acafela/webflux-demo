package org.example.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRanking implements BusinessModel {

    private String id;
    private String name;
    private int order;
    private long count;

    public ItemRanking(Item item) {
        id = item.getId();
        name = item.getName();
    }
}
