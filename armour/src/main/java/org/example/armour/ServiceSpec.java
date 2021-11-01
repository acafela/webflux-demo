package org.example.armour;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.core.BusinessModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceSpec {
    private String url;
    private String jsonPath;
    private Class<? extends BusinessModel> modelClass;
}
