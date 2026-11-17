package qbic.offline.containers

import groovy.util.logging.Slf4j
import nextflow.Session
import nextflow.config.spec.ConfigOption
import nextflow.config.spec.ConfigScope
import nextflow.config.spec.ScopeName
import nextflow.container.ContainerConfig
import nextflow.container.ContainerHandler
import nextflow.script.dsl.Description


@ScopeName('offline.containers')
@Description('The `offline.containers` scope allows you to configure the `nf-offline` plugin.')
@Slf4j
class ContainersConfig implements ConfigScope {
    ContainerHandler handler

    @ConfigOption
    @Description('Whether or not to download the containers.')
    boolean download = true

    @ConfigOption
    @Description('How to return the resolved containers as a string.')
    String format = 'json'

    @ConfigOption
    @Description('How to return the resolved containers as a string.')
    boolean ignoreErrors = false
    
    ContainersConfig(Map<String, Object> containersConfig, Session session){
        ContainerConfig containerConfig = session.getContainerConfig()
        handler = new ContainerHandler(containerConfig)
        
        download = containersConfig.containsKey('download') ? containersConfig.get('download') : true
        download = containersConfig.containsKey('format') ? containersConfig.get('format') : 'json'
        download = containersConfig.containsKey('ignoreErrors') ? containersConfig.get('ignoreErrors') : false
    }
}
