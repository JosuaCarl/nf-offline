package qbic.offline

import groovy.util.logging.Slf4j
import nextflow.Const
import nextflow.Session
import nextflow.config.spec.ConfigOption
import nextflow.config.spec.ConfigScope
import nextflow.config.spec.ScopeName
import nextflow.script.WorkflowMetadata
import nextflow.script.dsl.Description
import qbic.offline.containers.ContainersConfig
import qbic.offline.workflow.WorkflowConfig

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
    @Description('Directory for output of cached / pre-fetched files.')
    Path applicationCacheDir = null

    @ConfigOption
    @Description('Configuration of containers.')
    ContainersConfig containersConfig

    @ConfigOption
    @Description('Configuration of workflow.')
    WorkflowConfig workflowConfig

    // For options parser
    OfflineConfig() {}
    
    OfflineConfig(Map<String, Object> sessionConfig, Session session) {
        Map<String, Object> offlineConfig = sessionConfig.get('offline', [:]) as Map<String, Object>

        applicationCacheDir = Path.of( offlineConfig.get('applicationCacheDir', Const.getAppCacheDir()) as String)
        prepareOnly = offlineConfig.get('prepareOnly', true)

        workflowConfig = new WorkflowConfig(
            offlineConfig.get('workflow', [:])  as Map<String, Object>,
            session
        )
        
        containersConfig = new ContainersConfig(
            offlineConfig.get('containers', [:]) as Map<String, Object>,
            session
        )
    }
}
