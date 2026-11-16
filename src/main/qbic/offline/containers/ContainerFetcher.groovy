package qbic.offline.containers

import java.util.regex.Pattern

class ContainerFetcher {
    Map<String, String> containerRegistryURLs

    List<String> baseRegistryKeys 
    
    Map config

    String containerizationEngine

    ContainerFetcher(Map config, String containerizationEngine = 'apptainer'){
        this.config = config
        this.containerizationEngine = containerizationEngine
        
        this.containerRegistryURLs = [
                QUAY_IO: 'quay.io',
                SEQERA_DOCKER: 'community.wave.seqera.io/library',
                SEQERA_SINGULARITY: 'community-cr-prod.seqera.io/docker/registry/v2',
                GALAXY_SINGULARITY: 'depot.galaxyproject.org/singularity',
        ]

        this.baseRegistryKeys = [
                'docker.registry',
                'podman.registry',
                'apptainer.registry',
                'singularity.registry',
        ]
    }

    /**
     * Navigate though config and check out registry key content
     * @param config
     * @param registryKeys
     * @return
     */
    List<String> getConfigRegistries(List<String> registryKeys) {
        return registryKeys.collect(
                { String key -> config.navigate(key) as String }
        ).findAll(
                { String value -> value != null }
        ) as List<String>
    }
    
    static String cleanExtension(String fileName) {
        // Detect file extension
        String extension = ".img"
        if (fileName.contains(".sif")) {
            extension = ".sif"
            fileName = fileName.replace('.sif:', '-')
            fileName = fileName.replace('.sif', '')
        }

        // Strip : and / characters
        fileName = fileName.replace("/", "-").replace(":", "-")
        // Add file extension
        fileName = fileName + extension

        return fileName
    }

    static String getContainerFilename(String container, Set<String> registry){        
        // Strip URI prefix
        String fileName = container.replaceAll("^.*:${Pattern.quote('//')}", '')

        // Clean the file extension.
        fileName = cleanExtension(fileName)
        
        // Trim potential registries from the name for consistency.
        // This will allow pipelines to work offline without symlinked images,
        // if docker.registry / singularity.registry are set to empty strings at runtime, which can be included in the HPC config profiles easily.
        if (registry) {
            registry = registry.collect({ String reg ->
                "^${Pattern.quote(reg)}-?".replace("/", "[/-]")
            })
            String trimPattern = registry.join('|')
            fileName = fileName.replaceAll(trimPattern,  "")
        }

        return fileName
    }
    
    void fetchContainers() {
        Set<String> registryURLs
        List<String> registryKeys
        if (containerizationEngine in ['docker']) {
            registryURLs = ['QUAY_IO', 'SEQERA_DOCKER'].collect( { String key -> containerRegistryURLs.get(key) } ) as Set<String>
            registryKeys = this.baseRegistryKeys.subList(0, 2)
        }
        else {
            registryKeys = this.baseRegistryKeys
            registryURLs = containerRegistryURLs.values() as Set<String>
        }
        
        registryURLs.addAll( getConfigRegistries(registryKeys) )
        
        
        ...
    }

}
