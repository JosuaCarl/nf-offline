package qbic.offline.containers

import nextflow.processor.TaskRun

/**
 * Wrapper class of {@link TaskRun} to enable time tracking during container download.
 */
class TaskWrapper extends TaskRun {    
    @Override
    boolean isContainerReady() {
        // Fetches container
        boolean success = containerKey
                ? containerResolver().isContainerReady(containerKey)
                : true

        notify()

        return success
    }
}
