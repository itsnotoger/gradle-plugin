package oger.util.java

import javafx.stage.Stage
import spock.lang.Specification

class JavaFxSpec extends Specification {
    def "pulls dependencies"() {
        when:
        new Stage()

        then:
        def e = thrown ExceptionInInitializerError
        e.cause.message == "This operation is permitted on the event thread only; currentThread = Test worker"
    }
}
