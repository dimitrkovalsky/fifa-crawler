package com.liberty.repositories;

import com.liberty.model.Tag;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TagRepository extends MongoRepository<Tag, ObjectId> {

    Tag findOneByName(String name);
}
