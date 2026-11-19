package groovy.qbic.offline

import nextflow.Session
import nextflow.trace.TraceObserverV2

class OfflineObserver implements TraceObserverV2 {
    OfflineConfig offlineConfig
    OfflineRunner offlineRunner
    
    OfflineObserver(OfflineConfig offlineConfig) {
        this.offlineConfig = offlineConfig
        this.offlineRunner = new OfflineRunner(offlineConfig)
    }

    @Override
    void onFlowCreate(Session session) {
        offlineRunner.run()
        
        if (offlineConfig.prepareOnly) {
            session.destroy()
        }
    }
}
