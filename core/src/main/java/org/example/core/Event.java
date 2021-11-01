package org.example.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event implements BusinessModel {

    private String id;
    private String subject;
    private String imgUrl;
}
