package com.liberty.model;

import com.liberty.common.PriceHelper;
import lombok.Data;

import java.util.List;

/**
 * @author Dmytro_Kovalskyi.
 * @since 03.11.2016.
 */
@Data
public class FullSquad {

    private Long squadId;
    private PriceHelper.HistoryPoint price;
    private List<SquadPlayer> players;
    private String date;
    private String squadGroup;
    private String squadName;
}
