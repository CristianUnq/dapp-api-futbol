package com.dapp.api_futbol.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.domain.JavaClasses;
import org.junit.jupiter.api.Test;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

public class ArchitectureTests {

    // --- REGLAS DE CAPAS (LAYERING) ---

    public static final ArchRule layer_dependencies_are_respected = layeredArchitecture()
            .consideringOnlyDependenciesInAnyPackage("com.dapp.api_futbol..")
            // Definimos las capas y los paquetes que les corresponden
            .layer("Controllers").definedBy("..controller..", "..actuator..", "..aop..")
            .layer("Services").definedBy("..service..", "..security..")
            .layer("Repositories").definedBy("..repository..")
            .layer("Metrics").definedBy("..metrics..")
            .layer("Config").definedBy("..config..")

            // Definimos las reglas de acceso entre capas
            .whereLayer("Controllers").mayNotBeAccessedByAnyLayer()
            .whereLayer("Services").mayOnlyBeAccessedByLayers("Controllers", "Services", "Config")
            .whereLayer("Repositories").mayOnlyBeAccessedByLayers("Services", "Config", "Metrics")
            .whereLayer("Metrics").mayOnlyBeAccessedByLayers("Services");


    // --- REGLAS DE CONVENCIONES DE NOMBRADO ---

    public static final ArchRule services_should_be_annotated_and_named_correctly =
            classes()
                    .that().resideInAPackage("..service..")
                    .should().beAnnotatedWith(Service.class)
                    .andShould().haveSimpleNameEndingWith("Service");

    public static final ArchRule controllers_should_be_named_correctly =
            classes()
                    .that().resideInAPackage("..controller..")
                    .should().beAnnotatedWith(RestController.class)
                    .andShould().haveSimpleNameEndingWith("Controller");


    // --- REGLAS DE DEPENDENCIAS ESPECÍFICAS ---

    public static final ArchRule services_should_not_depend_on_controllers =
            noClasses()
                    .that().resideInAPackage("..service..")
                    .should().dependOnClassesThat().resideInAPackage("..controller..");


    // --- REGLAS CONTRA DEPENDENCIAS CÍCLICAS ---

    public static final ArchRule no_cyclic_dependencies =
            slices()
                    .matching("com.dapp.api_futbol.(*)..")
                    .should().beFreeOfCycles();

    @Test
    void runArchitectureRules() {
        JavaClasses imported = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages(
                        "com.dapp.api_futbol.controller",
                        "com.dapp.api_futbol.actuator",
                        "com.dapp.api_futbol.aop",
                        "com.dapp.api_futbol.service",
                        "com.dapp.api_futbol.security",
                        "com.dapp.api_futbol.repository",
                        "com.dapp.api_futbol.metrics",
                        "com.dapp.api_futbol.config"
                );

        layer_dependencies_are_respected.check(imported);
        services_should_be_annotated_and_named_correctly.check(imported);
        controllers_should_be_named_correctly.check(imported);
        services_should_not_depend_on_controllers.check(imported);
        no_cyclic_dependencies.check(imported);
    }
}