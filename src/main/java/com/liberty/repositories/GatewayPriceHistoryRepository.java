package com.liberty.repositories;

import com.liberty.common.Platform;
import com.liberty.model.PriceHistory;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * @author Dmytro_Kovalskyi.
 * @since 06.12.2016.
 */
@Component
public class GatewayPriceHistoryRepository {
    private static String COLLECTION_NAME = "price_history";

    @Autowired
    private MongoTemplate template;


    public void save(PriceHistory history) {
        DBObject dbObject = toDbObject(history);
        getCollection().save(dbObject);
    }

    private DBObject toDbObject(PriceHistory history) {
        DBObject dbObject = new BasicDBObject();
        template.getConverter().write(history, dbObject);
        return dbObject;
    }

    public List<PriceHistory> getAll() {
        return null;
    }

    public Optional<PriceHistory> findOne() {
        return Optional.empty();
    }

    private DBCollection getCollection() {
        Platform platform = getPlatform();
        String collectionName;
        switch (platform) {
            case PS:
                collectionName = "ps_" + COLLECTION_NAME;
            case XBOX:
                collectionName = "xb_" + COLLECTION_NAME;
            default:
                collectionName = COLLECTION_NAME;
        }
        return template.getCollection(collectionName);
    }

    private Platform getPlatform() {
        return Platform.PC;
    }
}
