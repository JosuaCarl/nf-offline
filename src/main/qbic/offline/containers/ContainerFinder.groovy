package qbic.offline.containers

import groovy.util.logging.Slf4j
import nextflow.container.resolver.ContainerInfo
import nextflow.container.resolver.ContainerResolver
import nextflow.container.resolver.ContainerResolverProvider
import nextflow.processor.TaskRun
import nextflow.script.ScriptMeta
import org.codehaus.groovy.util.ListHashMap

@Slf4j
class ContainerFinder{
    boolean ignoreErrors
    
    ContainerFinder(boolean ignoreErrors=false) {
        this.ignoreErrors = ignoreErrors
    }

    static ContainerResolver getContainerResolver() {
        return ContainerResolverProvider.load()
    }
    
    Map<String, ContainerInfo> getContainers() {
        final containers = new ListHashMap<String, ContainerInfo>()
        
        for( final process : ScriptMeta.allProcesses() ) {
            try {
                // Get container preview
                final TaskRun task = process.createTaskProcessor().createTaskPreview()
                
                // Fetch name of image
                def imageName = task.config.getContainer()
                if( imageName == false ) {
                    String message = "Task ${task.name} missed a container."
                    log.error(message)
                    throw new MissingPropertyException(message)
                }
                if( !imageName ) { imageName = null }

                // Resolve name against container loader
                ContainerInfo containerInfo = getContainerResolver().resolveImage(task, imageName as String)

                containers[process.name] = containerInfo
            }
            
            catch( Exception e ) {
                if( ignoreErrors ) {
                    log.warn "Unable to inspect container for task `$process.name` - cause: ${e.message}"
                }
                else {
                    throw e
                }
            }
        }

        return containers
    }
}
