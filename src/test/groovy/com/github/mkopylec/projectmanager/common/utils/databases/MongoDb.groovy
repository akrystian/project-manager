package com.github.mkopylec.projectmanager.common.utils.databases

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component

@Component
class MongoDb {

    private MongoOperations database
    @Autowired(required = false)
    private InMemoryMongoStorage storage

    MongoDb(MongoOperations database) {
        this.database = database
    }

    void clear() {
        if (storage != null) {
            storage.clear()
            return
        }
        database.collectionNames.each { database.remove(new Query(), it as String) }
    }
}
