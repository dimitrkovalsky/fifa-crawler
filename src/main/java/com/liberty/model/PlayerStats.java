package com.liberty.model;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
@Data
public class PlayerStats {
    private int total;
    private int pace;
    private int shooting;
    private int passing;
    private int dribbling;
    private int defending;
    private int heading;
    private int league;

    private int skillMoves;
    private int weakFoot;
    private String strongFoot;
}
