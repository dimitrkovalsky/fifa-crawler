package com.liberty.model.stats;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
@Data
public class Passing {

  private int passing;
  private int vision;
  private int crossing;
  private int freeKick;
  private int shortPassing;
  private int longPassing;
  private int curve;
}
