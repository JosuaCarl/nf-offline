package qbic.offline

import groovy.util.logging.Slf4j
import nextflow.Session
import nextflow.config.spec.ConfigOption
import nextflow.config.spec.ConfigScope
import nextflow.config.spec.ScopeName
import nextflow.script.WorkflowMetadata
import nextflow.script.dsl.Description
import qbic.offline.containers.ContainersConfig

import java.nio.file.Path

@ScopeName('offline')
@Description('The `offline` scope allows you to configure the `nf-offline` plugin.')
@Slf4j
class OfflineConfig implements ConfigScope {

    @ConfigOption
    @Description('Path to the output config.')
    Path configOutPath = null
    
    @ConfigOption
    @Description('Configuration of containers.')
    ContainersConfig containersConfig
    
    OfflineConfig(Map<String, Object> sessionConfig, Session session) {
        Map<String, Object> offlineConfig = sessionConfig.navigate('offline') as Map<String, Object>
        WorkflowMetadata workflowMetadata = session.getWorkflowMetadata()

        containersConfig = new ContainersConfig(
            offlineConfig.navigate('containers') as Map<String, Object>,
            session
        )
    }
}
