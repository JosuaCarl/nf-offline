package groovy.qbic.offline

import groovy.util.logging.Slf4j
import nextflow.Session
import nextflow.config.spec.ConfigOption
import nextflow.config.spec.ConfigScope
import nextflow.config.spec.ScopeName
import nextflow.script.WorkflowMetadata
import nextflow.script.dsl.Description
import groovy.qbic.offline.containers.ContainersConfig

import java.nio.file.Path

@ScopeName('offline')
@Description('The `offline` scope allows you to configure the `nf-offline` plugin.')
@Slf4j
class OfflineConfig implements ConfigScope {
    @ConfigOption
    @Description('Whether or not to only run preparation scripts or to continue with the run the plugin was started in.')
    boolean prepareOnly = true
    
    @ConfigOption
    @Description('Path to the output config.')
    Path configOutPath = null
    
    @ConfigOption
    @Description('Configuration of containers.')
    ContainersConfig containersConfig
    
    // For options parser
    OfflineConfig() {}
    
    OfflineConfig(Map<String, Object> sessionConfig, Session session) {
        Map<String, Object> offlineConfig = sessionConfig.navigate('offline') as Map<String, Object> ?: [:]
        WorkflowMetadata workflowMetadata = session.getWorkflowMetadata()

        prepareOnly = offlineConfig.get('prepareOnly', true)

        containersConfig = new ContainersConfig(
            offlineConfig.navigate('containers') as Map<String, Object> ?: [:],
            session
        )
    }
}
