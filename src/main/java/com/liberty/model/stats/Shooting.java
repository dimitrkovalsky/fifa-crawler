package com.liberty.model.stats;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
@Data
public class Shooting {
    private int shooting;
    private int positioning;
    private int finishing;
    private int shotPower;
    private int longShots;
    private int volleys;
    private int penalties;
}
