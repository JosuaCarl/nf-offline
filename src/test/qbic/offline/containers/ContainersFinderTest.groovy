package qbic.offline.containers

import nextflow.NF
import nextflow.container.ContainerConfig
import nextflow.container.resolver.ContainerInfo
import nextflow.container.resolver.ContainerResolver
import nextflow.processor.TaskConfig
import nextflow.processor.TaskProcessor
import nextflow.processor.TaskRun
import nextflow.script.BaseScript
import nextflow.script.ProcessDef
import nextflow.script.ScriptMeta
import spock.lang.Specification

class ContainersFinderTest extends Specification {
    def setup() {
        ScriptMeta.reset()
        NF.init()
    }

    def makeProcess(String name, def containerInfo) {
        final script = new BaseScript() {
            Object runScript() {}
        }
        final ProcessDef processDef = Mock(ProcessDef) {
            getName() >> name
            createTaskProcessor() >> Mock(TaskProcessor) {
                createTaskPreview() >> Mock(TaskRun) {
                    config >> Mock(TaskConfig) {
                        getContainer() >> containerInfo
                    }
                }
            }
        }
        ScriptMeta.get(script).addDefinition(processDef)
    }

    
    def 'Should not find container'() {
        given:
        makeProcess('proc1', false)

        when:
        new ContainerFinder(false).getContainers()

        then:
        final MissingPropertyException exception = thrown()
    }
}
