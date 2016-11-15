package com.liberty.service;

import com.liberty.model.PlayerProfile;

import java.util.List;

/**
 * @author Dmytro_Kovalskyi.
 * @since 12.10.2016.
 */

public interface PlayerProfileService {

    List<PlayerProfile> searchByPhrase(String phrase);

    PlayerProfile findOne(Long id);

    List<PlayerProfile> getAll(List<Long> ids);
}
