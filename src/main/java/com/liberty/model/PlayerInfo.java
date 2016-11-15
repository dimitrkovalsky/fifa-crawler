package com.liberty.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Dmytro_Kovalskyi.
 * @since 12.10.2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerInfo {

    private PlayerTradeStatus tradeStatus;
    private PlayerProfile profile;
}
