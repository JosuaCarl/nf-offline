package qbic.offline.workflow

import groovy.util.logging.Slf4j
import nextflow.Session
import nextflow.config.spec.ConfigOption
import nextflow.config.spec.ConfigScope
import nextflow.config.spec.ScopeName
import nextflow.script.dsl.Description

@ScopeName('offline.workflow')
@Description('The `offline.workflow` scope allows you to configure the `nf-offline` plugin.')
@Slf4j
class WorkflowConfig implements ConfigScope  {
    @ConfigOption
    @Description('Whether or not to enable workflow inspection.')
    boolean enabled = true

    @ConfigOption
    @Description('Whether or not to download the workflows.')
    boolean download = true
    
    WorkflowConfig(Map<String, Object> workflowConfig, Session session){
        enabled = workflowConfig.get('enabled', true)
        download = workflowConfig.get('download', true)
    }
}
