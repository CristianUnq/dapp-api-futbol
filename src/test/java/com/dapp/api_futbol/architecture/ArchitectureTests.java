package com.dapp.api_futbol.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchUnitExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

// Le dice a ArchUnit qué paquetes escanear para las pruebas
@ExtendWith(ArchUnitExtension.class)
@AnalyzeClasses(packages = "com.dapp.api_futbol", importOptions = {ImportOption.DoNotIncludeTests.class})
public class ArchitectureTests {

    // --- REGLAS DE CAPAS (LAYERING) ---

    @ArchTest
    public static final ArchRule layer_dependencies_are_respected = layeredArchitecture()
            .consideringOnlyDependenciesInAnyPackage("com.dapp.api_futbol..")
            // Definimos las capas y los paquetes que les corresponden
            .layer("Controllers").definedBy("..controller..")
            .layer("Services").definedBy("..service..")
            .layer("Repositories").definedBy("..repository..")
            .layer("Metrics").definedBy("..metrics..")
            .layer("Config").definedBy("..config..")

            // Definimos las reglas de acceso entre capas
            .whereLayer("Controllers").mayNotBeAccessedByAnyLayer()
            .whereLayer("Services").mayOnlyBeAccessedByLayers("Controllers", "Services", "Config")
            .whereLayer("Repositories").mayOnlyBeAccessedByLayers("Services")
            .whereLayer("Metrics").mayOnlyBeAccessedByLayers("Services");


    // --- REGLAS DE CONVENCIONES DE NOMBRADO ---

    @ArchTest
    public static final ArchRule services_should_be_annotated_and_named_correctly =
            classes()
                    .that().resideInAPackage("..service..")
                    .should().beAnnotatedWith(Service.class)
                    .andShould().haveSimpleNameEndingWith("Service");

    @ArchTest
    public static final ArchRule controllers_should_be_named_correctly =
            classes()
                    .that().resideInAPackage("..controller..")
                    .should().beAnnotatedWith(RestController.class)
                    .andShould().haveSimpleNameEndingWith("Controller");


    // --- REGLAS DE DEPENDENCIAS ESPECÍFICAS ---

    @ArchTest
    public static final ArchRule services_should_not_depend_on_controllers =
            noClasses()
                    .that().resideInAPackage("..service..")
                    .should().dependOnClassesThat().resideInAPackage("..controller..");


    // --- REGLAS CONTRA DEPENDENCIAS CÍCLICAS ---

    @ArchTest
    public static final ArchRule no_cyclic_dependencies =
            slices()
                    .matching("com.dapp.api_futbol.(*)..")
                    .should().beFreeOfCycles();
}