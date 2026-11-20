package qbic.offline.workflow

import nextflow.exception.AbortOperationException
import nextflow.plugin.Plugins
import nextflow.scm.AssetManager

/**
 * 
 */
class WorkflowFetcher {
    WorkflowConfig workflowConfig
    
    WorkflowFetcher(WorkflowConfig workflowConfig) {
        this.workflowConfig = workflowConfig
    }
    
    def fetchWorkflows(String pipelineName) {
        Plugins.init()

        final AssetManager manager = new AssetManager(pipelineName)
        AssetManager.
        
        // the target directory is the second parameter
        // otherwise default the current pipeline name
        def target = new File(args.size()> 1 ? args[1] : manager.getBaseName())
        if( target.exists() ) {
            if( target.isFile() )
                throw new AbortOperationException("A file with the same name already exists: $target")
            if( !target.empty() )
                throw new AbortOperationException("Clone target directory must be empty: $target")
        }
        else if( !target.mkdirs() ) {
            throw new AbortOperationException("Cannot create clone target directory: $target")
        }

        manager.checkValidRemoteRepo()
        print "Cloning ${manager.getProjectWithRevision()} ..."
        manager.clone(target, revision, deep)
        print "\r"
        println "${manager.getProjectWithRevision()} cloned to: $target"    

    }
}
