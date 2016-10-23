package com.liberty.service;

import com.liberty.model.PlayerInfo;
import com.liberty.model.Tag;

import java.util.List;

/**
 * User: Dimitr
 * Date: 22.10.2016
 * Time: 10:58
 */
public interface TagService {

  void executeUpdate();

  void addTag(Long playerId, String tag);

  void removeTag(Long playerId, String tag);

  void enableByTag(String tag);

  void markTags();

  List<PlayerInfo> getByTag(String tag);

  List<Tag> getAllTags();
}
