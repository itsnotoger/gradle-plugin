package oger.util.java

import spock.lang.Specification

class GDriveLocatorSpec extends Specification {
    def "finds gdrive on this system"() {
        given:
        def locator = GDriveLocator.@INSTANCE

        when:
        def result = locator.locate()

        then:
        result != null
    }
}
