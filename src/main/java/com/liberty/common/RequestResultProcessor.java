package com.liberty.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liberty.model.market.FifaError;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;

import static com.sun.corba.se.impl.activation.ServerMain.logError;

/**
 * @author Dmytro_Kovalskyi.
 * @since 27.10.2016.
 */
@Slf4j
public class RequestResultProcessor {

  private Runnable onSessionUpdate;

  public RequestResultProcessor(Runnable onSessionUpdate) {
    this.onSessionUpdate = onSessionUpdate;
  }

  private enum FifaRequestStatus {
    OK, SESSION_EXPIRED, FAILED
  }

  public <T, U> Optional<T> processResult(Optional<String> executionResult,
                                          Class<U> resultEntity,
                                          Supplier<Optional<T>> onSessionExpired) {
    if (!executionResult.isPresent()) {
      return Optional.empty();
    }
    String json = executionResult.get();
    FifaRequestStatus status = getStatus(json);

    switch (status) {
      case FAILED:
        return Optional.empty();
      case OK:
        return JsonHelper.toEntity(json, resultEntity);
      case SESSION_EXPIRED:
        return onSessionExpired.get();
      default:
        return Optional.empty();
    }
  }

  public <T> void processVoidResult(Optional<String> executionResult, Runnable
      onSessionExpired) {
    if (!executionResult.isPresent()) {
      return;
    }
    String json = executionResult.get();
    FifaRequestStatus status = getVoidStatus(json);

    switch (status) {
      case FAILED:
      case OK:
        break;
      case SESSION_EXPIRED:
        onSessionExpired.run();
        break;
    }
  }

  private FifaRequestStatus getVoidStatus(String json) {
    ObjectMapper objectMapper = JsonHelper.getObjectMapper();
    try {
      FifaError fifaError = objectMapper.readValue(json, FifaError.class);
      if (fifaError.getCode() == FifaError.ErrorCode.SESSION_EXPIRED) {
        logError("Session expired...");
        onSessionUpdate.run();
        return FifaRequestStatus.SESSION_EXPIRED;
      }
    } catch (Exception ignored) {

    }

    return FifaRequestStatus.OK;
  }


  private FifaRequestStatus getStatus(String json) {
    ObjectMapper objectMapper = JsonHelper.getObjectMapper();
    try {
      FifaError fifaError = objectMapper.readValue(json, FifaError.class);
      if (fifaError.getCode() == FifaError.ErrorCode.SESSION_EXPIRED) {
        logError("Session expired...");
        onSessionUpdate.run();
        return FifaRequestStatus.SESSION_EXPIRED;
      } else {
        log.error("FIFA ERROR : " + fifaError);
        return FifaRequestStatus.FAILED;
      }
    } catch (Exception ignored) {
      return FifaRequestStatus.OK;
    }
  }

  public <U, T> Boolean processBooleanResult(Optional<String> executionResult,
                                             Class<U> requestResult,
                                             Supplier<Boolean> onSessionExpired) {
    if (!executionResult.isPresent()) {
      return false;
    }
    String json = executionResult.get();
    FifaRequestStatus status = getStatus(json);

    switch (status) {
      case FAILED:
        return false;
      case OK:
        Optional<U> entity = JsonHelper.toEntity(json, requestResult);
        return entity.isPresent();
      case SESSION_EXPIRED:
        return onSessionExpired.get();
      default:
        return false;
    }
  }

  public <U, T> List<T> processListResult(Optional<String> executionResult,
                                          Class<U> requestResult,
                                          Function<U, List<T>> mapper,
                                          Supplier<List<T>> onSessionExpired) {
    if (!executionResult.isPresent()) {
      return Collections.emptyList();
    }
    String json = executionResult.get();
    FifaRequestStatus status = getStatus(json);

    switch (status) {
      case FAILED:
        return Collections.emptyList();
      case OK:
        Optional<U> entity = JsonHelper.toEntity(json, requestResult);
        return entity.map(mapper).orElse(Collections.emptyList());
      case SESSION_EXPIRED:
        return onSessionExpired.get();
      default:
        return Collections.emptyList();
    }
  }
}
