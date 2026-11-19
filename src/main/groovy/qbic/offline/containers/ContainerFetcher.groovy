package qbic.offline.containers

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import nextflow.container.inspect.ContainersInspector
import nextflow.container.resolver.ContainerMeta
import nextflow.processor.TaskRun
import nextflow.script.ProcessDef
import nextflow.script.ScriptMeta
import qbic.offline.display.ProgressBar

import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.IntStream

/**
 * A class to fetch containers of a pipeline run.
 */
@Slf4j
@CompileStatic
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
        List<ContainerMeta> containersMetadata = new ArrayList<>()
        for( final process : processes ) {
            try {
                // get container preview
                final TaskRun task = process.createTaskProcessor().createTaskPreview()

                // Fetch name of image
                final String imageName = task.config.getContainer()
                
                if( imageName ) {
                    tasks.add(task)
                    containersMetadata.add(task.containerMeta())
                }
            }
            catch( Exception e ) {
                if( containersConfig.ignoreErrors )
                    log.warn( "Unable to inspect container for task `$process.name` - cause: ${e.message}" )
                else
                    throw e
            }
        }
        
        log.info( "Found the following containers: ${tasks.collect( { TaskRun task -> task.config.getContainer() } )}" )
        
        // Wait for containers to be downloaded
        if( containersConfig.download ) {
            downloadContainers(tasks, containersMetadata)
        }
        
        return containersMetadata
    }

    /**
     * Inspired by {@link ContainersInspector#await()} with progress bar.
     * 
     * @param tasks 
     */
    static protected void downloadContainers(List<TaskRun> tasks, List<ContainerMeta> containersMetadata) {
        // Track progress
        ProgressBar progressBar = new ProgressBar(tasks.size())
        AtomicInteger completedTasks = new AtomicInteger(0)
        
        // Process downloads in parallel
        IntStream.range(0, tasks.size()).parallel().forEach({ Integer i ->
            final TaskRun task = tasks[i]
            final ContainerMeta containerMeta = containersMetadata[i]
            try {
                log.debug( "Fetching ${containerMeta.buildId}: [${containerMeta.sourceImage} -> ${containerMeta.targetImage}]" )
                
                int c = 0
                while (!task.isContainerReady()) {
                    Thread.sleep(c++ < 10 ? 1_000 : 5_000)
                }
                
                log.debug( "Finished fetching ${containerMeta.buildId}" )
                int currentlyCompletedTasks = completedTasks.getAndAdd(1)
                progressBar.setProgress(currentlyCompletedTasks)
            }
            catch (InterruptedException ignore) {
                Thread.currentThread().interrupt()
            }
        })

        // Complete progress
        progressBar.done()
    }

    void run() {
        nextflow.container.inspect.ContainerInspectMode.activate(true)
        // configure quiet mode
        nextflow.util.LoggerHelper.setQuiet(true)
        // setup the target run command
        final target = new CmdRun()
        target.launcher = this.launcher
        target.args = args
        target.profile = this.profile
        target.revision = this.revision
        target.runConfig = this.runConfig
        target.params = this.params
        target.paramsFile = this.paramsFile
        target.preview = true
        target.previewAction = this.&applyInspect
        target.ansiLog = false
        target.skipHistoryFile = true
        // run it
        target.run()
    }
}