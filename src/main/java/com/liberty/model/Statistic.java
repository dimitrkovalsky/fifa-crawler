package com.liberty.model;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 23.05.2016.
 */
@Data
public class Statistic {

  private long players;
  private long autoBuy;
  private boolean enabled;
  private int credits;
}
