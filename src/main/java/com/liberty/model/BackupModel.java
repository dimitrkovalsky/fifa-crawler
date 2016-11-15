package com.liberty.model;

import lombok.Data;

import java.util.List;

/**
 * @author Dmytro_Kovalskyi.
 * @since 22.07.2016.
 */
@Data
public class BackupModel {

    private List<PlayerTradeStatus> tradeStatuses;
    private List<PlayerStatistic> statistics;
}
