package com.liberty.rest.request;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 17.06.2016.
 */
@Data
public class AutobuyRequest {

  private Boolean enabled;
  private Integer purchases;
}
