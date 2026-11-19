package qbic.offline.containers

import nextflow.Session
import nextflow.container.resolver.ContainerMeta
import nextflow.processor.TaskConfig
import nextflow.processor.TaskProcessor
import nextflow.processor.TaskRun
import nextflow.script.ProcessDef
import spock.lang.Specification

class ContainerFetcherTest extends Specification {
    def 'Test fetching empty'(){
        given:
        Session session = Mock(Session) {

        }
        ContainersConfig containersConfig = new ContainersConfig([:], session)
        ContainerFetcher containerFetcher = new ContainerFetcher(containersConfig)

        when:
        List<ContainerMeta> metas = containerFetcher.fetchContainers()

        then:
        metas.size() == 0
    }
    
    def 'Test fetching mocked process containers'(){
        given:
        Session session = Mock(Session) {}
        
        ProcessDef process = Mock(ProcessDef) {
            createTaskProcessor() >> Mock(TaskProcessor) {
                createTaskPreview() >> Mock(TaskRun) {
                    config >> Mock(TaskConfig) {
                        getContainer() >> 'Mock container'
                    }
                    containerMeta() >> Mock(ContainerMeta) {
                        targetImage >> 'Mock container'
                    }
                }
            }
        }
        Set<ProcessDef> processes = [process]
        ContainersConfig containersConfig = new ContainersConfig([download: false], session)
        ContainerFetcher containerFetcher = new ContainerFetcher(containersConfig)

        when:
        List<ContainerMeta> metas = containerFetcher.fetchContainers(processes)

        then:
        metas.collect( { ContainerMeta meta -> meta.targetImage}) == ['Mock container']
    }
    
    def 'Test await'() {
        given:
        Session session = Mock(Session) {}
        ContainersConfig containersConfig = new ContainersConfig([:], session)
        ContainerFetcher containerFetcher = new ContainerFetcher(containersConfig)
        TaskRun taskRun = Mock(TaskRun) {
            isContainerReady() >> { () ->
                sleep(5_000)
                return true
            }
        }
        List<TaskRun> tasks  = [taskRun] * 20
        
        when:
        containerFetcher.await(tasks)
        
        then:
        true
    }
}
