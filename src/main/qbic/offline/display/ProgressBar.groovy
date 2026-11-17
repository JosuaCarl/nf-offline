package qbic.offline.display

import java.math.RoundingMode

class ProgressBar {
    BigDecimal total
    BigDecimal progress
    BigDecimal stepSize

    int totalSteps
    int completedSteps
    int remainingSteps
    
    StringBuilder currentProgressBar
    
    ProgressBar(BigDecimal total, int numSteps = 20) {
        this.total = total
        this.progress = 0
        
        this.stepSize = total / numSteps
        this.completedSteps = 0
        this.remainingSteps = numSteps
        this.totalSteps = numSteps
        this.currentProgressBar = new StringBuilder(numSteps + 6).append(" " * numSteps)
    }
    
    int determineSteps(){
        completedSteps = (progress / stepSize).setScale(0, RoundingMode.FLOOR) as Integer
        remainingSteps = totalSteps - completedSteps
    }
    
    int determinePercentage() {
       return (100 * progress / total).setScale(0, RoundingMode.CEILING) as Integer
    }
    
    void setProgress(BigDecimal progress, boolean render=true) {
        int difference = progress - this.progress
        this.progress = progress
        
        if (render || difference >= stepSize) {
            renderProgress()
        }
    }
    
    String renderProgress() {
        determineSteps()
        
        currentProgressBar = new StringBuilder(totalSteps + 7)
                .append('|')
                .append('=' * completedSteps)
                .append(' ' * remainingSteps)
                .append('| ')
                .append(determinePercentage())
                .append('%')
                .append('\r')
        
        String progressBar = currentProgressBar.toString()
        print(progressBar)
        
        return progressBar
    }
    
    void done() {
        print("|${'=' * totalSteps}| 100%\n")
        print("\n")
    }
}
