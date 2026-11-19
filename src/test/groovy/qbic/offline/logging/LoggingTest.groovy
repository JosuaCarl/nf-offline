package qbic.offline.logging

import qbic.offline.TestHelpers.LogChecker
import spock.lang.Specification
import spock.lang.Stepwise


/**
 * Test the Logging  (especially the definition of a Duplication TurboFilter in logback-test.xml)
 */
@Stepwise
class LoggingTest extends Specification {
    def 'Should return three logs (debug and trace are dropped)' () {
        when:
        LogChecker logChecker = new LogChecker(LoggerTestClass)
        LoggerTestClass.main()

        then:
        // Additional warnings are blocked
        logChecker.checkLogs(3)
    }
}

