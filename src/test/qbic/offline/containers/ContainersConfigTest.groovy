package qbic.offline.containers

import nextflow.Session
import spock.lang.Specification

class ContainersConfigTest extends Specification {
    def 'Test default config'() {
        when:
        Session session = Mock(Session)
        ContainersConfig config = new ContainersConfig(
            [:],
            session
        )
        
        then:
        config.download == true
        config.ignoreErrors == false
        config.format == 'json'
    }

    def 'Test custom config'() {
        when:
        Session session = Mock(Session)
        ContainersConfig config = new ContainersConfig(
            [download: false, ignoreErrors: true, format: 'config'],
            session
        )

        then:
        config.download == false
        config.ignoreErrors == true
        config.format == 'config'
    }
}
