package com.liberty.service.impl;

import com.liberty.model.PlayerProfile;
import com.liberty.repositories.PlayerProfileRepository;
import com.liberty.service.PlayerProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Dmytro_Kovalskyi.
 * @since 12.10.2016.
 */
@Service
public class PlayerProfileServiceImpl implements PlayerProfileService {

    public static final int MAX_PLAYERS = 30;

    @Autowired
    private PlayerProfileRepository repository;

    @Autowired
    private MongoTemplate template;

    @Override
    public List<PlayerProfile> searchByPhrase(String phrase) {
        Query query = new Query();
        Criteria criteria = Criteria.where("name").regex(phrase, "i");
        query.limit(MAX_PLAYERS);
        query.addCriteria(criteria);
        query.with(new Sort(Sort.Direction.DESC, "rating"));

        List<PlayerProfile> playerProfiles = template.find(query, PlayerProfile.class);
        if (playerProfiles == null) {
            return Collections.emptyList();
        }
        return playerProfiles;
    }

    @Override
    public PlayerProfile findOne(Long id) {
        return repository.findOne(id);
    }

    @Override
    public List<PlayerProfile> getAll(List<Long> ids) {
        return makeList(repository.findAll(ids));
    }

    private <E> List<E> makeList(Iterable<E> iter) {
        List<E> list = new ArrayList<E>();
        for (E item : iter) {
            list.add(item);
        }
        return list;
    }
}
