package com.github.mkopylec.projectmanager.common.core;

import com.github.mkopylec.projectmanager.common.core.UniqueIdentifierGenerator;
import org.springframework.stereotype.Service;

import static java.util.UUID.randomUUID;

@Service
class UuidIdentifierGenerator implements UniqueIdentifierGenerator {

    @Override
    public String generateUniqueIdentifier() {
        return randomUUID().toString();
    }
}
