package com.liberty.model.stats;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
@Data
public class Stats {

    private Defending defending;
    private Dribbling dribbling;
    private Pace pace;
    private Passing passing;
    private Shooting shooting;
    private Physical physical;

}
