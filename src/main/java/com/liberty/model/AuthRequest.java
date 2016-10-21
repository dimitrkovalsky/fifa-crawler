package com.liberty.model;

import lombok.Data;

/**
 * User: Dimitr Date: 04.06.2016 Time: 16:16
 */
@Data
public class AuthRequest {

  String nucleusPersonaPlatform = "pc";
  int priorityLevel = 4;
  Identification Identification = new Identification();
  String gameSku = "FFA17PCC";
  String locale = "en-US";
  String sku = "FUT17WEB";
  String method = "authcode";
  Integer clientVersion = 1;
  Boolean isReadOnly = false;
  long nuc = 2311254984L;
  long nucleusPersonaId = 228045231L;
  String nucleusPersonaDisplayName = "dimitrkovalsky";

  public AuthRequest(long nuc, long nucleusPersonaId) {
    this.nuc = nuc;
    this.nucleusPersonaId = nucleusPersonaId;
  }

  @Data
  static class Identification {

    private String authCode = "";
  }
}

