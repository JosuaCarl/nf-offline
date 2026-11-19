package qbic.offline.logging

import groovy.util.logging.Slf4j

@Slf4j
class LoggerTestClass {

    static trace() {
        log.trace('Trace message')
    }

    static debug() {
        log.debug('Debug message')
    }

    static info() {
        log.info('Info message')
    }

    static warn() {
        log.warn( 'Warn message')
    }

    static error() {
        log.error('Error message')
    }

    static void main(String[] args) {
        trace()
        debug()
        info()
        warn()
        error()
    }
}
