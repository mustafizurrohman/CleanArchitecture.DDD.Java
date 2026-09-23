package com.mustafizur.cleanarchitecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "com.mustafizur.cleanarchitecture",
        importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule domain_must_not_depend_on_spring_or_outer_layers = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("org.springframework..", "..application..", "..infrastructure..", "..api..");

    @ArchTest
    static final ArchRule core_must_not_depend_on_outer_layers = noClasses()
            .that().resideInAPackage("..core..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..domain..", "..application..", "..infrastructure..", "..api..", "org.springframework..");

    @ArchTest
    static final ArchRule application_must_not_depend_on_infrastructure_or_api = noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..infrastructure..", "..api..");
}
