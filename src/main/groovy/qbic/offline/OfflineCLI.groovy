package qbic.offline

import groovy.util.logging.Slf4j

@Slf4j
class OfflineCLI {
    static String downloadCmd = 'download'
    
    static List<String> commands = [downloadCmd]
    
    static int download(Map<String, Object> parsedArgs, Map configMap=[:]) {
        
        return 0
    }
}
