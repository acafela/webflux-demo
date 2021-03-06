package org.example.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item implements BusinessModel {

    private String id;
    private String name;
    private double price;
    private int reviewCount;
}
