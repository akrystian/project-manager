package com.github.mkopylec.projectmanager.team.core;

import com.github.mkopylec.projectmanager.common.core.Value;

import static com.github.mkopylec.projectmanager.common.core.BusinessRuleViolation.requireNoBusinessRuleViolation;
import static com.github.mkopylec.projectmanager.common.core.BusinessRuleViolationProperties.properties;
import static com.github.mkopylec.projectmanager.common.support.StringUtils.isBlank;

public abstract sealed class JobPosition extends Value<String> {

    protected JobPosition(String value) {
        super(value);
    }

    public static JobPosition fromPersistentState(String value) {
        return requireNoBusinessRuleViolation(() -> JobPosition.valueOf(value)); // todo check empty  case
    }

    public static JobPosition valueOf(String value) {
        if (isBlank(value)) {
            throw new EmptyJobPosition();
        }
        return switch (value) {
            case SoftwareDeveloper.SOFTWARE_DEVELOPER -> new SoftwareDeveloper();
            case ScrumMaster.SCRUM_MASTER -> new ScrumMaster();
            case ProductOwner.PRODUCT_OWNER -> new ProductOwner();
            default -> throw new InvalidJobPosition(value);
        };
    }

    static final class SoftwareDeveloper extends JobPosition {

        private static final String SOFTWARE_DEVELOPER = "SOFTWARE_DEVELOPER";

        SoftwareDeveloper() {
            super(SOFTWARE_DEVELOPER);
        }
    }

    static final class ScrumMaster extends JobPosition {

        private static final String SCRUM_MASTER = "SCRUM_MASTER";

        ScrumMaster() {
            super(SCRUM_MASTER);
        }
    }

    static final class ProductOwner extends JobPosition {

        private static final String PRODUCT_OWNER = "PRODUCT_OWNER";

        ProductOwner() {
            super(PRODUCT_OWNER);
        }
    }

    static final class InvalidJobPosition extends TeamBusinessRuleViolation {

        private InvalidJobPosition(String position) {
            super(properties("position", position));
        }
    }

    static final class EmptyJobPosition extends TeamBusinessRuleViolation {

        private EmptyJobPosition() {
            super(properties("position", "empty"));
        }
    }
}
