package com.liberty.service;

import com.liberty.model.PlayerInfo;
import com.liberty.model.Tag;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

  Map<String, Integer> getTagDistribution();

  List<PlayerInfo> getByTag(String tag);

  List<Tag> getAllTags();

  void disableByTag(String tag, Set<String> activeTags);

  void addNewTag(String tag);
}
