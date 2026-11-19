package groovy.qbic.offline.display

import spock.lang.Specification

class ProgressBarTest extends Specification {
    
    def 'Test display of progress bar'(){
        given:
        ProgressBar progressBar = new ProgressBar(10.0)
        
        when:
        progressBar.setProgress(0.0)
        progressBar.setProgress(1.0)
        progressBar.setProgress(7.5)
        progressBar.done()
        
        then:
        progressBar.total == 10
        progressBar.progress == 7.5
        progressBar.stepSize == 0.5

        progressBar.totalSteps  == 20
        progressBar.completedSteps  == 15
        progressBar.remainingSteps  == 5
        
        progressBar.currentProgressBar.toString() == "|===============     | 75%\r"
    }
}
