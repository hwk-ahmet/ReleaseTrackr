import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class ArchUnitTest {

    private val classes = ClassFileImporter().importPackages("org.releasetrackr")

    // <APPLICATION LAYER SEPARATION>

    // TODO: ("except itself" custom eval. needed)
    // Controllers should not access controllers directly
    // Drivers should not access drivers directly

    @Test
    fun `services should not access controllers`() {
        val rule = noClasses()
            .that().resideInAPackage("..service..")
            .should().accessClassesThat().resideInAPackage("..controller..")
        rule.check(classes)
    }

    @Test
    fun `drivers should not access controllers`() {
        val rule = noClasses()
            .that().resideInAPackage("..driver..")
            .should().accessClassesThat().resideInAPackage("..controller..")
        rule.check(classes)
    }

    @Test
    fun `drivers should not access services`() {
        val rule = noClasses()
            .that().resideInAPackage("..driver..")
            .should().accessClassesThat().resideInAPackage("..service..")
        rule.check(classes)
    }


    // <APPLICATION LAYER SEPARATION/>

    // <CLASS ANNOTATIONS>

    // <CLASS ANNOTATIONS/>


}
