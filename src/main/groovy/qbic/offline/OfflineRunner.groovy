package groovy.qbic.offline

import groovy.qbic.offline.containers.ContainerFetcher

class OfflineRunner {
    OfflineConfig offlineConfig
    
    OfflineRunner(OfflineConfig offlineConfig) {
        this.offlineConfig = offlineConfig
    }
    
    void run() {
        // Container fetching
        if(offlineConfig.containersConfig.enabled) {
            ContainerFetcher containerFetcher = new ContainerFetcher(offlineConfig.containersConfig)
            containerFetcher.fetchContainers()
        }

        // Plugins fetching

        // Datasets fetching
    }
}
