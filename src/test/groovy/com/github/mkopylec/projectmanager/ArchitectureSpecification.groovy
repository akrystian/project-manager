package com.github.mkopylec.projectmanager

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.library.Architectures
import spock.lang.Specification

class ArchitectureSpecification extends Specification {


    private final JavaClasses classes = new ClassFileImporter().importPackages("com.github.mkopylec.projectmanager");

    def checkDependencies() {
        when:
        def layers = Architectures.layeredArchitecture().consideringOnlyDependenciesInLayers()
            .layer("project").definedBy("com.github.mkopylec.projectmanager.project..")
            .layer("team").definedBy("com.github.mkopylec.projectmanager.team..")
            .layer("common").definedBy("com.github.mkopylec.projectmanager.common..")
            .whereLayer("project").mayOnlyAccessLayers("common")
            .whereLayer("team").mayOnlyAccessLayers("common")
            .whereLayer("common").mayNotAccessAnyLayer()

        then:
        layers.check(classes);
    }
}
