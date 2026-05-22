package com.github.mkopylec.projectmanager.common.utils.databases

class InMemoryMongoStorage {
    final Map<String, Map<Object, Object>> collections = new LinkedHashMap<>()

    void clear() {
        collections.values().each { it.clear() }
    }
}
