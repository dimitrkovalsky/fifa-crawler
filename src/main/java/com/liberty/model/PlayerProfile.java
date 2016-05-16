package com.liberty.model;

import com.liberty.model.stats.OverviewStats;
import com.liberty.model.stats.Stats;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
@Data
@Document(collection = "player_profile")
public class PlayerProfile {
    @Id
    private Integer id;
    private PlayerInfo info;
    private Stats stats;
    private Price price;
    private OverviewStats overviewStats;
}
