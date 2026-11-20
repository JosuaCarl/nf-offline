package qbic.offline

import nextflow.Nextflow
import nextflow.Session
import nextflow.processor.TaskProcessor
import nextflow.processor.TaskRun
import nextflow.script.WorkflowMetadata
import nextflow.trace.TraceObserverV2
import nextflow.trace.event.TaskEvent

import java.nio.file.Path

class OfflineObserver implements TraceObserverV2 {
    OfflineConfig offlineConfig
    OfflineRunner offlineRunner
    Session session
    
    OfflineObserver(OfflineConfig offlineConfig) {
        this.offlineConfig = offlineConfig
        this.offlineRunner = new OfflineRunner(offlineConfig)
    }

    @Override
    void onFlowCreate(Session session) {
        Path baseDir = session.getBaseDir()
        WorkflowMetadata workflowMetadata = session.getWorkflowMetadata() // Contains projectDir = session.baseDir (where clone of workflow sits)
        this.session = session
    }

    @Override
    void onFlowBegin() {
        offlineRunner.run()

        if (offlineConfig.prepareOnly) {
            Nextflow.exit(0, "")
        }
    }

    @Override
    void onProcessCreate(TaskProcessor processor) {
        println(processor.name)
        TaskRun task = processor.createTaskPreview()
        println(task.getContainer())
    }
    @Override
    void onTaskPending(TaskEvent event) {
        println(event.handler.task.name)
    }
    @Override
    void onTaskSubmit(TaskEvent event) {
        println(event.handler.task.name)
    }
}
