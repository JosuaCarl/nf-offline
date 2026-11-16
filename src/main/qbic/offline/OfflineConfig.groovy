package qbic.offline

import groovy.util.logging.Slf4j
import nextflow.config.spec.ConfigOption
import nextflow.config.spec.ConfigScope
import nextflow.config.spec.ScopeName
import nextflow.script.dsl.Description

@ScopeName('offline')
@Description('The `offline` scope allows you to configure the `nf-offline` plugin.')
@Slf4j
class OfflineConfig implements ConfigScope{
    @ConfigOption
    @Description('Path to the output config.')
    String configOutPath
    
    OfflineConfig(Map<String, Object> configMap) {
        
    }
}
