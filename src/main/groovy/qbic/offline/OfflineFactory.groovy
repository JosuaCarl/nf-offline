package groovy.qbic.offline

import nextflow.Session
import nextflow.trace.TraceObserverFactoryV2
import nextflow.trace.TraceObserverV2
import groovy.qbic.offline.logging.LoggingAdapter

class OfflineFactory implements TraceObserverFactoryV2 {    
    /**
     * Creates and returns the configuration of this nf-offline run.
     *
     * @param session The Nextflow session
     * @return Collection of TraceObserverV2 (None)
     */
    @Override
    Collection<TraceObserverV2> create(Session session) {
        LoggingAdapter loggingAdapter = new LoggingAdapter()
        loggingAdapter.addRootAppendersToLogger()
        
        OfflineConfig offlineConfig = new OfflineConfig(session?.config ?: [:], session)
        
        OfflineObserver offlineObserver = new OfflineObserver(offlineConfig)
        
        return [offlineObserver]
    }
}
