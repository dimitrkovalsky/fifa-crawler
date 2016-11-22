package com.liberty.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Dmytro_Kovalskyi.
 * @since 22.11.2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MinerStrategy {
    private Integer id;
    private String name;
    private Boolean active;
}
