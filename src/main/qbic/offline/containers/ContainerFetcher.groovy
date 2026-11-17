package qbic.offline.containers

import groovy.util.logging.Slf4j
import nextflow.container.inspect.ContainersInspector
import nextflow.container.resolver.ContainerMeta
import nextflow.processor.TaskRun
import nextflow.script.ProcessDef
import nextflow.script.ScriptMeta
import org.codehaus.groovy.util.ListHashMap
import org.jsoup.Progress
import qbic.offline.display.ProgressBar

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

/**
 * A class to fetch containers of a pipeline run.
 */
@Slf4j
class ContainerFetcher extends ContainersInspector {
    ContainersConfig containersConfig
    
    ContainerFetcher(ContainersConfig containersConfig){
        super(containersConfig.download)
        withFormat(containersConfig.format)
        
        this.containersConfig = containersConfig
    }

    /**
     *
     * Wrapper method to fetch the data (and download) all containers of a run.
     * Similar to {@link ContainersInspector#getContainers()}.
     *
     * @param processes A set of processes for which containers are to be fetched.
     *                  Defaults to {@link ScriptMeta#allProcesses()}.
     * @return Metadata collection of task containers that were fetched.
     */
    List<ContainerMeta> fetchContainers(Set<ProcessDef> processes = ScriptMeta.allProcesses()) {

        // Collect tasks that have viable containers that need to be fetched
        List<TaskRun> tasks = new ArrayList<>()
        for( final process : processes ) {
            try {
                // get container preview
                final TaskRun task = process.createTaskProcessor().createTaskPreview()

                // Fetch name of image
                final String imageName = task.config.getContainer()
                
                if( imageName ) {
                    tasks.add(task)
                }
            }
            catch( Exception e ) {
                if( containersConfig.ignoreErrors )
                    log.warn "Unable to inspect container for task `$process.name` - cause: ${e.message}"
                else
                    throw e
            }
        }
        
        // Wait for containers to be downloaded
        if( containersConfig.download ) {
            await(tasks)
        }
        
        List<ContainerMeta> containerMetadata = tasks.collect( { TaskRun task -> task.containerMeta() } )
        
        return containerMetadata
    }


    protected void await(List<TaskRun> tasks) {
        if( !tasks )
            return
        final executor = Executors.newFixedThreadPool(10)
        CompletableFuture<Void>[] futures = new CompletableFuture<Void>[tasks.size()]
        for( int i=0; i<tasks.size(); i++ ) {
            final t = tasks[i]
            futures[i] = CompletableFuture.runAsync(() -> {
                try {
                    int c=0
                    while( !t.isContainerReady() ) {
                        Thread.sleep(c++< 10 ? 1_000 : 5_000)
                    }
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, executor)
        }

        // Wait for all tasks to complete
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures)

        ProgressBar progressBar = new ProgressBar(futures.size())
        while( !allFutures.isDone() ) {
            progressBar.setProgress( futures.count( { CompletableFuture<Void> future -> future.isDone() }) as int )
            allFutures.wait(1_000)
        }
        progressBar.done()
        
        // Block until all tasks are completed
        allFutures.join();  // This will wait until all tasks complete
        // Shut down the executor
        executor.shutdown()
    }
}