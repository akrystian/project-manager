package com.github.mkopylec.projectmanager.common.utils.databases

import org.mockito.Mockito
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import org.springframework.data.mongodb.core.query.Query

@Configuration
@Profile('unit-tests')
class InMemoryMongoConfiguration {

    @Bean
    @Primary
    MongoOperations mongoOperations(InMemoryMongoStorage storage) {
        def collections = storage.collections

        def operations = Mockito.mock(MongoOperations)

        Mockito.when(operations.save(Mockito.any())).thenAnswer {
            def document = it.getArgument(0)
            def collectionName = InMemoryMongoConfiguration.collectionName(document.class)
            def collection = collections.computeIfAbsent(collectionName) { new LinkedHashMap<>() }
            def id = InMemoryMongoConfiguration.readId(document)
            def current = collection.get(id)
            InMemoryMongoConfiguration.updateVersion(document, current)
            collection.put(id, document)
            document
        }

        Mockito.when(operations.findById(Mockito.any(), Mockito.any(Class))).thenAnswer {
            def id = it.getArgument(0)
            def entityClass = it.getArgument(1) as Class
            collections.getOrDefault(InMemoryMongoConfiguration.collectionName(entityClass), Collections.emptyMap()).get(id)
        }

        Mockito.when(operations.findAll(Mockito.any(Class))).thenAnswer {
            def entityClass = it.getArgument(0) as Class
            collections.getOrDefault(InMemoryMongoConfiguration.collectionName(entityClass), Collections.emptyMap()).values().toList()
        }

        Mockito.when(operations.getCollectionNames()).thenAnswer {
            collections.keySet()
        }

        Mockito.when(operations.remove(Mockito.any(Query), Mockito.anyString())).thenAnswer {
            def collectionName = it.getArgument(1) as String
            collections.get(collectionName)?.clear()
            null
        }
        Mockito.when(operations.remove(Mockito.any(Query), Mockito.any(Class))).thenAnswer {
            def entityClass = it.getArgument(1) as Class
            collections.get(InMemoryMongoConfiguration.collectionName(entityClass))?.clear()
            null
        }
        Mockito.when(operations.remove(Mockito.any(Query), Mockito.any(Class), Mockito.anyString())).thenAnswer {
            def collectionName = it.getArgument(2) as String
            collections.get(collectionName)?.clear()
            null
        }

        operations
    }

    @Bean
    InMemoryMongoStorage inMemoryMongoStorage() {
        new InMemoryMongoStorage()
    }

    protected static String collectionName(Class<?> type) {
        def document = type.getAnnotation(Document)
        if (document == null) {
            return type.simpleName
        }

        def fromCollection = document.collection()
        if (fromCollection != null && !fromCollection.isBlank()) {
            return fromCollection
        }

        def fromValue = document.value()
        return fromValue == null || fromValue.isBlank() ? type.simpleName : fromValue
    }

    protected static Object readId(Object object) {
        def field = findField(object.class) { it.isAnnotationPresent(MongoId) } ?: findField(object.class) { it.name == 'id' || it.name == 'name' }
        field.accessible = true
        field.get(object)
    }

    protected static void updateVersion(Object document, Object currentDocument) {
        def versionField = findField(document.class) { it.isAnnotationPresent(Version) }
        if (versionField == null) {
            return
        }
        versionField.accessible = true
        def currentVersion = currentDocument == null ? null : versionField.get(currentDocument) as Integer
        def incomingVersion = versionField.get(document) as Integer
        if (currentDocument != null && incomingVersion == null) {
            throw new DuplicateKeyException('Duplicate key')
        }
        if (currentDocument != null && incomingVersion != currentVersion) {
            throw new OptimisticLockingFailureException('Version mismatch')
        }
        versionField.set(document, currentVersion == null ? 0 : currentVersion + 1)
    }

    protected static java.lang.reflect.Field findField(Class<?> type, Closure<Boolean> matcher) {
        Class<?> current = type
        while (current != null && current != Object) {
            def field = current.declaredFields.find(matcher)
            if (field != null) {
                return field
            }
            current = current.superclass
        }
        return null
    }
}
